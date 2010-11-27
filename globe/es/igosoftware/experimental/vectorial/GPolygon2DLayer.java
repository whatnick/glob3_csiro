

package es.igosoftware.experimental.vectorial;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.GPolygon2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRenderingAttributes;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.IPredicate;
import es.igosoftware.util.LRUCache;
import es.igosoftware.util.LRUCache.Entry;
import es.igosoftware.utils.GGlobeStateKeyCache;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.SurfaceImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.media.opengl.GL;
import javax.swing.Icon;


public class GPolygon2DLayer
         extends
            AbstractLayer
         implements
            IGlobeLayer {


   //   private static final double DEFAULT_LOG10_RESOLUTION_TARGET = 1.3;

   private static final int BYTES_PER_PIXEL = 4 /* rgba */
                                            * 4 /* 4 bytes x integer*/;


   private static final class RenderingKey {
      private final GPolygon2DLayer       _layer;
      private final GAxisAlignedRectangle _tileBounds;
      private final GRenderingAttributes  _renderingAttributes;


      private RenderingKey(final GPolygon2DLayer layer,
                           final GAxisAlignedRectangle tileSectorBounds,
                           final GRenderingAttributes renderingAttributes) {
         _layer = layer;
         _tileBounds = tileSectorBounds;
         _renderingAttributes = renderingAttributes;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((_renderingAttributes == null) ? 0 : _renderingAttributes.hashCode());
         result = prime * result + ((_layer == null) ? 0 : _layer.hashCode());
         result = prime * result + ((_tileBounds == null) ? 0 : _tileBounds.hashCode());
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final RenderingKey other = (RenderingKey) obj;
         if (_renderingAttributes == null) {
            if (other._renderingAttributes != null) {
               return false;
            }
         }
         else if (!_renderingAttributes.equals(other._renderingAttributes)) {
            return false;
         }
         if (_layer == null) {
            if (other._layer != null) {
               return false;
            }
         }
         else if (!_layer.equals(other._layer)) {
            return false;
         }
         if (_tileBounds == null) {
            if (other._tileBounds != null) {
               return false;
            }
         }
         else if (!_tileBounds.equals(other._tileBounds)) {
            return false;
         }
         return true;
      }


      @Override
      public String toString() {
         return "RenderingKey [layer=" + _layer + ", tileBounds=" + _tileBounds + ", renderingAttributes=" + _renderingAttributes
                + "]";
      }
   }

   private static final LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException> IMAGES_CACHE;

   static {
      final LRUCache.SizePolicy<RenderingKey, Future<BufferedImage>, RuntimeException> sizePolicy;
      sizePolicy = new LRUCache.SizePolicy<GPolygon2DLayer.RenderingKey, Future<BufferedImage>, RuntimeException>() {
         final private long _maxImageCacheSizeInBytes = Runtime.getRuntime().maxMemory() / 5;


         @Override
         public boolean isOversized(final List<Entry<RenderingKey, Future<BufferedImage>, RuntimeException>> entries) {

            long totalBytes = 0;

            for (final Entry<RenderingKey, Future<BufferedImage>, RuntimeException> entry : entries) {
               final Future<BufferedImage> futureImage = entry.getValue();
               if (futureImage.isDone()) {
                  try {
                     final BufferedImage image = futureImage.get();
                     totalBytes += image.getWidth() * image.getHeight() * BYTES_PER_PIXEL;
                  }
                  catch (final InterruptedException e) {
                  }
                  catch (final ExecutionException e) {
                  }
               }
            }

            return (totalBytes > _maxImageCacheSizeInBytes);
         }
      };

      final LRUCache.ValuesVisitor<RenderingKey, Future<BufferedImage>, RuntimeException> removedListener;
      removedListener = new LRUCache.ValuesVisitor<RenderingKey, Future<BufferedImage>, RuntimeException>() {
         @Override
         public void visit(final RenderingKey key,
                           final Future<BufferedImage> value,
                           final RuntimeException exception) {
            //            System.out.println("Removed " + key);
         }
      };

      IMAGES_CACHE = new LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException>(sizePolicy,
               new LRUCache.ValueFactory<RenderingKey, Future<BufferedImage>, RuntimeException>() {
                  @Override
                  public Future<BufferedImage> create(final RenderingKey key) {

                     return GConcurrent.getDefaultExecutor().submit(new Callable<BufferedImage>() {
                        @Override
                        public BufferedImage call() throws Exception {
                           final GPolygon2DLayer layer = key._layer;
                           final GPolygon2DRenderer renderer = layer._renderer;
                           final BufferedImage renderedImage = renderer.render(key._tileBounds, key._renderingAttributes);
                           layer.redraw();
                           return renderedImage;
                        }
                     });

                     // return _renderer.render(key._tileSectorBounds, key._attributes);
                  }
               }, removedListener, 0);
   }


   private final class Tile {

      private final Tile                  _parent;

      private final Sector                _tileSector;
      private final LatLon[]              _tileSectorCorners;
      private final GAxisAlignedRectangle _tileBounds;
      private final IVector2<?>           _tileBoundsExtent;

      private final double                _log10CellSize;
      private final int                   _density = 20;

      private SurfaceImage                _surfaceImage;

      private BufferedImage               _ancestorContribution;


      private Tile(final Tile parent,
                   final Globe globe,
                   final Sector tileSector) {

         _parent = parent;
         //         _level = (parent == null) ? 0 : parent._level + 1;

         _tileSector = tileSector;
         _tileSectorCorners = _tileSector.getCorners();

         _tileBounds = GWWUtils.toBoundingRectangle(tileSector, _projection);
         _tileBoundsExtent = _tileBounds.getExtent();

         final double cellSize = tileSector.getDeltaLatRadians() * globe.getRadius() / _density;

         _log10CellSize = Math.log10(cellSize);
      }


      private Box getBox(final DrawContext dc) {
         // return Sector.computeBoundingBox(globe, verticalExaggeration, _sector);

         return BOX_CACHE.get(dc, _tileSector);
      }


      private boolean atBestResolution(final DrawContext dc) {
         final Globe globe = dc.getGlobe();

         final double best = globe.getElevationModel().getBestResolution(_tileSector)
                             * globe.getRadiusAt(_tileSector.getCentroid());

         return _log10CellSize <= Math.log10(best);
      }


      private float computeProjectedPixels(final DrawContext dc) {
         final Vec4 firstProjected = GWWUtils.getScreenPoint(dc, _tileSectorCorners[0]);
         double minX = firstProjected.x;
         double maxX = firstProjected.x;
         double minY = firstProjected.y;
         double maxY = firstProjected.y;

         for (int i = 1; i < _tileSectorCorners.length; i++) {
            final Vec4 projected = GWWUtils.getScreenPoint(dc, _tileSectorCorners[i]);

            if (projected.x < minX) {
               minX = projected.x;
            }
            if (projected.y < minY) {
               minY = projected.y;
            }
            if (projected.x > maxX) {
               maxX = projected.x;
            }
            if (projected.y > maxY) {
               maxY = projected.y;
            }
         }


         // calculate the area of the rectangle
         final double width = maxX - minX;
         final double height = maxY - minY;
         final double area = width * height;
         return (float) area;
      }


      private boolean needToSplit(final DrawContext dc) {
         return computeProjectedPixels(dc) > (_attributes._textureWidth * _attributes._textureHeight * 2);
         //         final Globe globe = dc.getGlobe();
         //         final double verticalExaggeration = dc.getVerticalExaggeration();
         //
         //         final Vec4[] corners = _tileSector.computeCornerPoints(globe, verticalExaggeration);
         //         final Vec4 centerPoint = _tileSector.computeCenterPoint(globe, verticalExaggeration);
         //
         //         final Vec4 eyePoint = dc.getView().getEyePoint();
         //         final double d1 = eyePoint.distanceTo3(corners[0]);
         //         final double d2 = eyePoint.distanceTo3(corners[1]);
         //         final double d3 = eyePoint.distanceTo3(corners[2]);
         //         final double d4 = eyePoint.distanceTo3(corners[3]);
         //         final double d5 = eyePoint.distanceTo3(centerPoint);
         //
         //         double minDistance = d1;
         //         if (d2 < minDistance) {
         //            minDistance = d2;
         //         }
         //         if (d3 < minDistance) {
         //            minDistance = d3;
         //         }
         //         if (d4 < minDistance) {
         //            minDistance = d4;
         //         }
         //         if (d5 < minDistance) {
         //            minDistance = d5;
         //         }
         //
         //         final double logDist = Math.log10(minDistance);
         //         final double target = computeTileResolutionTarget(dc);
         //
         //         final boolean useTile = _log10CellSize <= (logDist - target);
         //         return !useTile;
      }


      //      private double computeTileResolutionTarget(final DrawContext dc) {
      //         // Compute the log10 detail target for the specified tile. Apply the elevation model's detail hint to the
      //         // default detail target.
      //
      //         return DEFAULT_LOG10_RESOLUTION_TARGET + dc.getGlobe().getElevationModel().getDetailHint(_tileSector);
      //      }


      private Tile[] slit(final DrawContext dc) {
         final Globe globe = dc.getGlobe();

         //         final GAxisAlignedRectangle[] sectors = _tileBounds.subdivideAtCenter();
         //
         //         final Tile[] subTiles = new Tile[4];
         //         subTiles[0] = new Tile(this, globe, GWWUtils.toSector(sectors[0], _projection));
         //         subTiles[1] = new Tile(this, globe, GWWUtils.toSector(sectors[1], _projection));
         //         subTiles[2] = new Tile(this, globe, GWWUtils.toSector(sectors[2], _projection));
         //         subTiles[3] = new Tile(this, globe, GWWUtils.toSector(sectors[3], _projection));

         final Sector[] sectors = _tileSector.subdivide();

         final Tile[] subTiles = new Tile[4];
         subTiles[0] = new Tile(this, globe, sectors[0]);
         subTiles[1] = new Tile(this, globe, sectors[1]);
         subTiles[2] = new Tile(this, globe, sectors[2]);
         subTiles[3] = new Tile(this, globe, sectors[3]);

         return subTiles;
      }


      private void preRender(final DrawContext dc) {
         if ((_surfaceImage == null) || (_ancestorContribution != null)) {
            //            final BufferedImage renderedImage = _renderer.render(_tileSectorBounds, _attributes);

            final RenderingKey key = createRenderingKey();
            final Future<BufferedImage> renderedImageFuture = IMAGES_CACHE.get(key);

            if (renderedImageFuture.isDone()) {
               try {
                  final BufferedImage renderedImage = renderedImageFuture.get();
                  if (_surfaceImage == null) {
                     _surfaceImage = new SurfaceImage(renderedImage, _tileSector);
                  }
                  else {
                     _surfaceImage.setImageSource(renderedImage, _tileSector);
                  }
                  _ancestorContribution = null;
               }
               catch (final InterruptedException e) {
               }
               catch (final ExecutionException e) {
               }
            }
         }


         if ((_surfaceImage == null) && (_ancestorContribution == null)) {
            _ancestorContribution = calculateAncestorContribution();
            if (_ancestorContribution != null) {
               _surfaceImage = new SurfaceImage(_ancestorContribution, _tileSector);
            }
         }

         if (_surfaceImage != null) {
            _surfaceImage.preRender(dc);
         }
      }


      private BufferedImage calculateAncestorContribution() {
         final Tile ancestor = findNearestAncestorWithImage();

         if (ancestor == null) {
            return null;
         }

         final Future<BufferedImage> ancestorImageFuture = IMAGES_CACHE.getValueOrNull(ancestor.createRenderingKey());

         if (ancestorImageFuture == null) {
            return null;
         }

         if (!ancestorImageFuture.isDone()) {
            return null;
         }


         try {
            final BufferedImage ancestorBufferedImage = ancestorImageFuture.get();

            final IVector2<?> scale = _tileBoundsExtent.div(ancestor._tileBoundsExtent);

            final GVector2D textureExtent = new GVector2D(_attributes._textureWidth, _attributes._textureHeight);

            final IVector2<?> topLeft = _tileBounds._lower.sub(ancestor._tileBounds._lower).scale(scale).div(_tileBoundsExtent).scale(
                     textureExtent);

            final IVector2<?> widthAndHeight = textureExtent.scale(scale);

            final int width = (int) widthAndHeight.x();
            final int height = (int) widthAndHeight.y();
            final int x = (int) topLeft.x();
            final int y = (int) -(topLeft.y() + height - _attributes._textureHeight);

            return markImageAsWorkInProgress(ancestorBufferedImage.getSubimage(x, y, width, height));
         }
         catch (final InterruptedException e) {
         }
         catch (final ExecutionException e) {
         }


         return null;
      }


      private BufferedImage markImageAsWorkInProgress(final BufferedImage image) {
         //         final Image workingIcon = GGlobeApplication.instance().getImage("working.png");

         final int TODO_flag_the_image_as_work_in_progress;

         return image;
      }


      private RenderingKey createRenderingKey() {
         return new RenderingKey(GPolygon2DLayer.this, _tileBounds, _attributes);
      }


      private Tile findNearestAncestorWithImage() {
         Tile ancestor = _parent;
         while (ancestor != null) {
            final RenderingKey ancestorKey = ancestor.createRenderingKey();
            final Future<BufferedImage> futureImage = IMAGES_CACHE.getValueOrNull(ancestorKey);
            if ((futureImage != null) && futureImage.isDone()) {
               // if (ancestor._surfaceImage != null) {
               return ancestor;
            }

            ancestor = ancestor._parent;
         }
         return null;
      }


      private void render(final DrawContext dc) {
         if (_surfaceImage != null) {
            _surfaceImage.render(dc);
         }
      }


      private void renderExtent(final DrawContext dc) {
         GWWUtils.renderSector(dc, _tileSector, 1, 1, 0);
      }

   }


   private static final GGlobeStateKeyCache<Sector, Box> BOX_CACHE;

   static {
      BOX_CACHE = new GGlobeStateKeyCache<Sector, Box>(new GGlobeStateKeyCache.Factory<Sector, Box>() {
         @Override
         public Box create(final DrawContext dc,
                           final Sector sector) {
            final Globe globe = dc.getView().getGlobe();
            final double verticalExaggeration = dc.getVerticalExaggeration();

            return Sector.computeBoundingBox(globe, verticalExaggeration, sector);
         }
      });
   }


   private final GProjection                             _projection;

   private final Sector                                  _sector;
   private final LatLon[]                                _sectorCorners;

   private final GPolygon2DRenderer                      _renderer;
   private final GRenderingAttributes                    _attributes;

   private Globe                                         _lastGlobe;
   private List<Tile>                                    _topTiles;
   private final List<Tile>                              _currentTiles = new ArrayList<Tile>();

   private boolean                                       _showExtents  = false;


   //   private final HashSet<Tile>                           _ancestorsToRender = new HashSet<Tile>();


   public GPolygon2DLayer(final List<IPolygon2D<?>> polygons,
                          final GProjection projection) {
      _projection = projection;


      //      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(project(polygons, projection2));
      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(polygons);

      _sector = GWWUtils.toSector(polygonsBounds, projection);
      _sectorCorners = _sector.getCorners();

      _renderer = new GPolygon2DRenderer(polygons);

      _attributes = createRenderingAttributes(polygonsBounds);
   }


   private static GRenderingAttributes createRenderingAttributes(@SuppressWarnings("unused") final GAxisAlignedRectangle polygonsBounds) {
      final boolean renderLODIgnores = true;
      final float borderWidth = 1;
      final Color fillColor = new Color(1, 0, 0, 0.5f);
      final Color borderColor = Color.BLACK;
      //      final Color borderColor = fillColor.darker().darker().darker();
      final double lodMinSize = 5;
      final boolean debugLODRendering = true;
      //      final int textureDimension = 256;
      final int textureWidth = 256;
      final int textureHeight = 256;
      final boolean renderBounds = true;


      //      final int textureWidth;
      //      final int textureHeight;
      //
      //      final IVector2<?> extent = polygonsBounds.getExtent();
      //
      //      if (extent.x() > extent.y()) {
      //         textureHeight = textureDimension;
      //         textureWidth = (int) Math.round(extent.x() / extent.y() * textureDimension);
      //      }
      //      else {
      //         textureWidth = textureDimension;
      //         textureHeight = (int) Math.round(extent.y() / extent.x() * textureDimension);
      //      }

      return new GRenderingAttributes(renderLODIgnores, borderWidth, fillColor, borderColor, lodMinSize, debugLODRendering,
               textureWidth, textureHeight, renderBounds);
   }


   private static List<Sector> createTopLevelSectors(final Sector polygonsSector) {
      final int latitudeSubdivisions = 5;
      final int longitudeSubdivisions = 10;
      final List<Sector> allTopLevelSectors = GWWUtils.createTopLevelSectors(latitudeSubdivisions, longitudeSubdivisions);

      return GCollections.select(allTopLevelSectors, new IPredicate<Sector>() {
         @Override
         public boolean evaluate(final Sector element) {
            return element.intersects(polygonsSector);
         }
      });
   }


   @Override
   public String getName() {
      return "Polygons";
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return application.getIcon("vectorial.png");
   }


   @Override
   public Sector getExtent() {
      return _sector;
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void setProjection(final GProjection proj) {
      throw new RuntimeException("Operation not supported!");
   }


   @Override
   public void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      application.zoomToSector(getExtent());
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }


   private Box getBox(final DrawContext dc) {
      // return Sector.computeBoundingBox(globe, verticalExaggeration, _sector);

      return BOX_CACHE.get(dc, _sector);
   }


   public void setShowExtents(final boolean showExtents) {
      _showExtents = showExtents;
      redraw();
   }


   public boolean isShowExtents() {
      return _showExtents;
   }


   private float computeProjectedPixels(final DrawContext dc) {
      final Vec4 firstProjected = GWWUtils.getScreenPoint(dc, _sectorCorners[0]);
      double minX = firstProjected.x;
      double maxX = firstProjected.x;
      double minY = firstProjected.y;
      double maxY = firstProjected.y;

      for (int i = 1; i < _sectorCorners.length; i++) {
         final Vec4 projected = GWWUtils.getScreenPoint(dc, _sectorCorners[i]);

         if (projected.x < minX) {
            minX = projected.x;
         }
         if (projected.y < minY) {
            minY = projected.y;
         }
         if (projected.x > maxX) {
            maxX = projected.x;
         }
         if (projected.y > maxY) {
            maxY = projected.y;
         }
      }


      // calculate the area of the rectangle
      final double width = maxX - minX;
      final double height = maxY - minY;
      final double area = width * height;
      return (float) area;
   }


   private void selectVisibleTiles(final DrawContext dc,
                                   final Tile tile,
                                   final Frustum currentFrustum,
                                   final int currentLevel) {
      if (!tile._tileSector.intersects(_sector)) {
         return;
      }

      if (!tile.getBox(dc).intersects(currentFrustum)) {
         return;
      }

      final int maxLevel = 20;
      if ((currentLevel < maxLevel - 1) && !tile.atBestResolution(dc) && tile.needToSplit(dc)) {
         final Tile[] subtiles = tile.slit(dc);
         for (final Tile child : subtiles) {
            selectVisibleTiles(dc, child, currentFrustum, currentLevel + 1);
         }
         return;
      }
      _currentTiles.add(tile);
   }


   private void calculateCurrentTiles(final DrawContext dc) {
      final Globe globe = dc.getGlobe();

      if ((_topTiles == null) || (_lastGlobe != globe)) {
         _lastGlobe = globe;

         final List<Sector> topLevelSectors = createTopLevelSectors(_sector);

         _topTiles = new ArrayList<Tile>(topLevelSectors.size());
         for (final Sector sector : topLevelSectors) {
            _topTiles.add(new Tile(null, globe, sector));
         }
      }


      _currentTiles.clear();
      final Frustum currentFrustum = dc.getView().getFrustumInModelCoordinates();
      for (final Tile tile : _topTiles) {
         selectVisibleTiles(dc, tile, currentFrustum, 0);
      }
   }


   @Override
   public boolean isLayerInView(final DrawContext dc) {
      final Box extent = getBox(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      if (!extent.intersects(frustum)) {
         return false;
      }

      final boolean bigEnough = (computeProjectedPixels(dc) >= 25);
      return bigEnough;
   }


   @Override
   protected final void doPreRender(final DrawContext dc) {
      calculateCurrentTiles(dc);

      for (final Tile tile : _currentTiles) {
         tile.preRender(dc);
      }

      //      _ancestorsToRender.clear();
      //      for (final Tile tile : _currentTiles) {
      //         final Tile ancestor = tile.preRender(dc);
      //         if (ancestor != null) {
      //            _ancestorsToRender.add(ancestor);
      //         }
      //      }

      //      for (final Tile ancestor : _ancestorsToRender) {
      //         ancestor.preRender(dc);
      //      }
   }


   @Override
   protected final void doRender(final DrawContext dc) {
      final boolean bigEnough = (computeProjectedPixels(dc) >= 25);
      if (!bigEnough) {
         return;
      }


      if (_showExtents) {
         renderExtents(dc);
      }

      //      for (final Tile ancestor : _ancestorsToRender) {
      //         ancestor.render(dc);
      //      }

      for (final Tile tile : _currentTiles) {
         tile.render(dc);
      }

   }


   private void renderExtents(final DrawContext dc) {
      //      getBox(dc).render(dc);

      final GL gl = dc.getGL();
      GWWUtils.pushOffset(gl);

      for (final Tile tile : _currentTiles) {
         tile.renderExtent(dc);
      }

      GWWUtils.popOffset(gl);
   }


   public static void main(final String[] args) {
      GUtils.showMemoryInfo();
   }
}
