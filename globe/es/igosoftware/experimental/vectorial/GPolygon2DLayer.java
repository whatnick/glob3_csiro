

package es.igosoftware.experimental.vectorial;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.GPolygon2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRenderingAttributes;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.IPredicate;
import es.igosoftware.util.LRUCache;
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


   private static final double DEFAULT_LOG10_RESOLUTION_TARGET = 1.3;


   private static final class RenderingKey {
      private final GAxisAlignedRectangle _tileSectorBounds;
      private final GRenderingAttributes  _attributes;


      private RenderingKey(final GAxisAlignedRectangle tileSectorBounds,
                           final GRenderingAttributes attributes) {
         _tileSectorBounds = tileSectorBounds;
         _attributes = attributes;
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((_attributes == null) ? 0 : _attributes.hashCode());
         result = prime * result + ((_tileSectorBounds == null) ? 0 : _tileSectorBounds.hashCode());
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
         if (_attributes == null) {
            if (other._attributes != null) {
               return false;
            }
         }
         else if (!_attributes.equals(other._attributes)) {
            return false;
         }

         if (_tileSectorBounds == null) {
            if (other._tileSectorBounds != null) {
               return false;
            }
         }
         else if (!_tileSectorBounds.equals(other._tileSectorBounds)) {
            return false;
         }

         return true;
      }
   }

   private final LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException> imagesCache;

   {
      imagesCache = new LRUCache<RenderingKey, Future<BufferedImage>, RuntimeException>(128,
               new LRUCache.ValueFactory<RenderingKey, Future<BufferedImage>, RuntimeException>() {
                  @Override
                  public Future<BufferedImage> create(final RenderingKey key) {

                     return GConcurrent.getDefaultExecutor().submit(new Callable<BufferedImage>() {
                        @Override
                        public BufferedImage call() throws Exception {
                           return _renderer.render(key._tileSectorBounds, key._attributes);
                        }
                     });

                     // return _renderer.render(key._tileSectorBounds, key._attributes);
                  }
               });
   }


   private final class Tile {

      private final Sector                _tileSector;
      private final GAxisAlignedRectangle _tileBounds;

      private final double                _log10CellSize;
      private final int                   _density = 20;

      private SurfaceImage                _surfaceImage;


      private Tile(final Globe globe,
                   final Sector tileSector) {

         //         _parent = parent;
         //         _level = (parent == null) ? 0 : parent._level + 1;

         _tileSector = tileSector;

         _tileBounds = GWWUtils.toBoundingRectangle(tileSector, _projection);

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


      private boolean needToSplit(final DrawContext dc) {
         final Globe globe = dc.getGlobe();
         final double verticalExaggeration = dc.getVerticalExaggeration();

         final Vec4[] corners = _tileSector.computeCornerPoints(globe, verticalExaggeration);
         final Vec4 centerPoint = _tileSector.computeCenterPoint(globe, verticalExaggeration);

         final Vec4 eyePoint = dc.getView().getEyePoint();
         final double d1 = eyePoint.distanceTo3(corners[0]);
         final double d2 = eyePoint.distanceTo3(corners[1]);
         final double d3 = eyePoint.distanceTo3(corners[2]);
         final double d4 = eyePoint.distanceTo3(corners[3]);
         final double d5 = eyePoint.distanceTo3(centerPoint);

         double minDistance = d1;
         if (d2 < minDistance) {
            minDistance = d2;
         }
         if (d3 < minDistance) {
            minDistance = d3;
         }
         if (d4 < minDistance) {
            minDistance = d4;
         }
         if (d5 < minDistance) {
            minDistance = d5;
         }

         final double logDist = Math.log10(minDistance);
         final double target = computeTileResolutionTarget(dc);

         final boolean useTile = _log10CellSize <= (logDist - target);
         return !useTile;
      }


      private double computeTileResolutionTarget(final DrawContext dc) {
         // Compute the log10 detail target for the specified tile. Apply the elevation model's detail hint to the
         // default detail target.

         return DEFAULT_LOG10_RESOLUTION_TARGET + dc.getGlobe().getElevationModel().getDetailHint(_tileSector);
      }


      private Tile[] slit(final DrawContext dc) {
         final Sector[] sectors = _tileSector.subdivide();

         final Globe globe = dc.getGlobe();

         final Tile[] subTiles = new Tile[4];
         subTiles[0] = new Tile(globe, sectors[0]);
         subTiles[1] = new Tile(globe, sectors[1]);
         subTiles[2] = new Tile(globe, sectors[2]);
         subTiles[3] = new Tile(globe, sectors[3]);

         return subTiles;
      }


      private void preRender(final DrawContext dc) {
         if (_surfaceImage == null) {
            //            final BufferedImage renderedImage = _renderer.render(_tileSectorBounds, _attributes);

            final RenderingKey key = new RenderingKey(_tileBounds, _attributes);
            final Future<BufferedImage> renderedImageFuture = imagesCache.get(key);

            if (renderedImageFuture.isDone()) {
               try {
                  final BufferedImage renderedImage = renderedImageFuture.get();
                  _surfaceImage = new SurfaceImage(renderedImage, _tileSector);
               }
               catch (final InterruptedException e) {
                  e.printStackTrace();
               }
               catch (final ExecutionException e) {
                  e.printStackTrace();
               }
            }
         }

         if (_surfaceImage != null) {
            _surfaceImage.preRender(dc);
         }
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
      final int latitudeSubdivisions = 5 * 1;
      final int longitudeSubdivisions = 10 * 1;
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


   @Override
   protected final void doPreRender(final DrawContext dc) {

      calculateCurrentTiles(dc);


      for (final Tile surfaceImage : _currentTiles) {
         surfaceImage.preRender(dc);
      }
   }


   private void calculateCurrentTiles(final DrawContext dc) {
      final Globe globe = dc.getGlobe();

      if ((_topTiles == null) || (_lastGlobe != globe)) {
         _lastGlobe = globe;

         final List<Sector> topLevelSectors = createTopLevelSectors(_sector);

         _topTiles = new ArrayList<Tile>(topLevelSectors.size());
         for (final Sector sector : topLevelSectors) {
            _topTiles.add(new Tile(globe, sector));
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
      return extent.intersects(frustum);
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
}
