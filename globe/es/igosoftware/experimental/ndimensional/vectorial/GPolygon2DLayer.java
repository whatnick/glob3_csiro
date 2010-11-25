

package es.igosoftware.experimental.ndimensional.vectorial;

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
import es.igosoftware.utils.GGlobeCache;
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

import javax.media.opengl.GL;
import javax.swing.Icon;


public class GPolygon2DLayer
         extends
            AbstractLayer
         implements
            IGlobeLayer {

   private static final GGlobeCache<GPolygon2DLayer, Box> BOX_CACHE;

   static {
      BOX_CACHE = new GGlobeCache<GPolygon2DLayer, Box>(new GGlobeCache.Factory<GPolygon2DLayer, Box>() {
         @Override
         public Box create(final GPolygon2DLayer layer,
                           final Globe globe,
                           final double verticalExaggeration) {
            return Sector.computeBoundingBox(globe, verticalExaggeration, layer._sector);
         }
      });
   }


   private final GProjection                              _projection;

   private final Sector                                   _sector;
   private final LatLon[]                                 _sectorCorners;

   private final List<SurfaceImage>                       _surfaceImages;

   private boolean                                        _showExtents = false;


   public GPolygon2DLayer(final List<IPolygon2D<?>> polygons,
                          final GProjection projection) {
      _projection = projection;


      //      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(project(polygons, projection2));
      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(polygons);

      _sector = GWWUtils.toSector(polygonsBounds, projection);
      _sectorCorners = _sector.getCorners();

      final GPolygon2DRenderer renderer = new GPolygon2DRenderer(polygons);

      final GRenderingAttributes attributes = createRenderingAttributes();


      final List<Sector> topLevelSectors = createTopLevelSectors(_sector);

      _surfaceImages = new ArrayList<SurfaceImage>(topLevelSectors.size());
      for (final Sector sector : topLevelSectors) {
         final GAxisAlignedRectangle sectorBounds = GWWUtils.toBoundingRectangle(sector, projection);
         final BufferedImage renderedImage = renderer.render(sectorBounds, attributes);
         _surfaceImages.add(new SurfaceImage(renderedImage, sector));
      }

      //      final GAxisAlignedRectangle[] rectangles = rectangle.subdivideAtCenter();
      //
      //      _surfaceImages = new ArrayList<SurfaceImage>(rectangles.length);
      //      for (final GAxisAlignedRectangle sector : rectangles) {
      //         final BufferedImage renderedImage = renderer.render(sector, attributes);
      //         _surfaceImages.add(new SurfaceImage(renderedImage, GWWUtils.toSector(sector, projection)));
      //      }
   }


   //   private static List<IPolygon2D<?>> project(final List<IPolygon2D<?>> polygons,
   //                                              final GProjection projection) {
   //
   //      final IVectorTransformer<IVector2<?>> transformer = new IVectorTransformer<IVector2<?>>() {
   //
   //         @Override
   //         public IVector2<?> transform(final IVector2<?> point) {
   //            return point.reproject(projection, GProjection.EPSG_4326);
   //         }
   //      };
   //
   //      return GCollections.collect(polygons, new ITransformer<IPolygon2D<?>, IPolygon2D<?>>() {
   //         @Override
   //         public IPolygon2D<?> transform(final IPolygon2D<?> polygon) {
   //            return (IPolygon2D<?>) polygon.transformedBy(transformer);
   //         }
   //      });
   //
   //   }


   private static GRenderingAttributes createRenderingAttributes() {
      final boolean renderLODIgnores = true;
      final float borderWidth = 0.5f;
      final Color fillColor = new Color(0.5f, 0, 1, 0.5f);
      final Color borderColor = Color.BLACK;
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
      final int latitudeSubdivisions = 5 * 8;
      final int longitudeSubdivisions = 10 * 8;

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

      final Globe globe = dc.getView().getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      return BOX_CACHE.get(this, globe, verticalExaggeration);
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


   @Override
   public final void doPreRender(final DrawContext dc) {
      for (final SurfaceImage surfaceImage : _surfaceImages) {
         surfaceImage.preRender(dc);
      }
   }


   @Override
   public boolean isLayerInView(final DrawContext dc) {
      final Box extent = getBox(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      return extent.intersects(frustum);
   }


   @Override
   protected void doRender(final DrawContext dc) {
      final boolean bigEnough = (computeProjectedPixels(dc) >= 25);
      if (!bigEnough) {
         return;
      }


      if (_showExtents) {
         renderExtents(dc);
      }


      for (final SurfaceImage surfaceImage : _surfaceImages) {
         surfaceImage.render(dc);
      }

   }


   private void renderExtents(final DrawContext dc) {
      //      getBox(dc).render(dc);

      final GL gl = dc.getGL();
      GWWUtils.pushOffset(gl);

      for (final SurfaceImage surfaceImage : _surfaceImages) {
         final Sector sector = surfaceImage.getSector();
         GWWUtils.renderSector(dc, sector, 1, 1, 0);
      }

      GWWUtils.popOffset(gl);
   }
}
