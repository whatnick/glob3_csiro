

package es.igosoftware.experimental.ndimensional.vectorial;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.GPolygon2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRenderingAttributes;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GGlobeCache;
import es.igosoftware.utils.GPositionBox;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Position;
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

import javax.swing.Icon;


public class GPolygon2DLayer
         extends
            AbstractLayer
         implements
            IGlobeLayer {

   private static final GGlobeCache<GPolygon2DLayer, Cylinder> EXTENTS_CACHE;

   static {
      EXTENTS_CACHE = new GGlobeCache<GPolygon2DLayer, Cylinder>(new GGlobeCache.Factory<GPolygon2DLayer, Cylinder>() {
         @Override
         public Cylinder create(final GPolygon2DLayer node,
                                final Globe globe,
                                final double verticalExaggeration) {
            final GPositionBox box = node._polygonBox;

            return Cylinder.computeVerticalBoundingCylinder(globe, verticalExaggeration, box._sector, box._lower.elevation,
                     GMath.nextUp(box._upper.elevation));
         }
      });
   }


   private final List<IPolygon2D<?>>                           _polygons;
   private final GProjection                                   _projection;
   private final GPositionBox                                  _polygonBox;

   private boolean                                             _showExtents                       = false;

   private float                                               _computedProjectedPixels;
   private boolean                                             _forceComputationOfProjectedPixels = true;
   private final SurfaceImage                                  _surfaceImage;


   public GPolygon2DLayer(final List<IPolygon2D<?>> polygons,
                          final GProjection projection) {
      _projection = projection;

      _polygons = new ArrayList<IPolygon2D<?>>(polygons);

      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(polygons);
      final GAxisAlignedRectangle bounds = (GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(polygonsBounds),
               polygonsBounds._center);

      final GVector3D lower3D = new GVector3D(bounds._lower, 0);
      final GVector3D upper3D = new GVector3D(bounds._upper, 0);
      _polygonBox = new GPositionBox(lower3D, upper3D, projection);


      final GPolygon2DRenderer renderer = new GPolygon2DRenderer(polygons);


      final boolean renderLeafs = true;
      final boolean renderLODIgnores = true;
      final float borderWidth = 0.5f;
      final Color fillColor = new Color(0.5f, 0.5f, 1, 0.75f);
      final Color borderColor = Color.BLACK;
      final double lodMinSize = 5;
      final boolean debugLODRendering = true;
      final int textureDimension = 2048;
      final boolean renderBounds = true;

      final GRenderingAttributes attributes = new GRenderingAttributes(renderLeafs, renderLODIgnores, borderWidth, fillColor,
               borderColor, lodMinSize, debugLODRendering, textureDimension, renderBounds);


      final BufferedImage renderedImage = renderer.render(bounds, attributes);

      _surfaceImage = new SurfaceImage(renderedImage, getExtent());
   }


   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> centerBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                                                       final VectorT center) {
      final VectorT delta = bounds.getCenter().sub(center);
      return bounds.translatedBy(delta.negated());
   }


   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> multipleOfSmallestDimention(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds._extent;

      double smallestExtension = Double.POSITIVE_INFINITY;
      for (byte i = 0; i < bounds.dimensions(); i++) {
         final double ext = extent.get(i);
         if (ext < smallestExtension) {
            smallestExtension = ext;
         }
      }

      final VectorT newExtent = smallestBiggerMultipleOf(extent, smallestExtension);
      final VectorT newUpper = bounds._lower.add(newExtent);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   @SuppressWarnings("unchecked")
   private static <VectorT extends IVector<VectorT, ?>> VectorT smallestBiggerMultipleOf(final VectorT lower,
                                                                                         final double smallestExtension) {

      final byte dimensionsCount = lower.dimensions();

      final double[] dimensionsValues = new double[dimensionsCount];
      for (byte i = 0; i < dimensionsCount; i++) {
         dimensionsValues[i] = smallestBiggerMultipleOf(lower.get(i), smallestExtension);
      }

      return (VectorT) GVectorUtils.createD(dimensionsValues);
   }


   private static double smallestBiggerMultipleOf(final double value,
                                                  final double multiple) {
      if (GMath.closeTo(value, multiple)) {
         return multiple;
      }

      final int times = (int) (value / multiple);

      double result = times * multiple;
      if (value < 0) {
         if (result > value) {
            result -= multiple;
         }
      }
      else {
         if (result < value) {
            result += multiple;
         }
      }

      return result;
   }


   @Override
   public String getName() {
      return "Polygons";
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return application.getIcon("pointscloud.png");
   }


   @Override
   public Sector getExtent() {
      return _polygonBox._sector;
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void setProjection(final GProjection proj) {

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


   private Cylinder getExtent(final DrawContext dc) {
      final Globe globe = dc.getView().getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      return EXTENTS_CACHE.get(this, globe, verticalExaggeration);
   }


   private boolean isVisible(final DrawContext dc) {
      final Cylinder extent = getExtent(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      return extent.intersects(frustum);
   }


   public void setShowExtents(final boolean showExtents) {
      _showExtents = showExtents;
      redraw();
   }


   public boolean isShowExtents() {
      return _showExtents;
   }


   @Override
   public final void doPreRender(final DrawContext dc) {
      _forceComputationOfProjectedPixels = true;
   }


   private void computeProjectedPixels(final DrawContext dc) {
      //      double minX = Double.POSITIVE_INFINITY;
      //      double minY = Double.POSITIVE_INFINITY;
      //      double maxX = Double.NEGATIVE_INFINITY;
      //      double maxY = Double.NEGATIVE_INFINITY;

      // calculate the minimum enclosing rectangle
      //      for (final Position boundsVertice : _box.getVertices()) {
      final GPositionBox box = _polygonBox;
      final Position[] vertices = box.getVertices();
      //      if (vertices.length == 0) {
      //         _computedProjectedPixels = 0;
      //         return;
      //      }


      final Vec4 firstProjectedVertex = GWWUtils.getScreenPoint(dc, vertices[0]);
      double minX = firstProjectedVertex.x;
      double maxX = firstProjectedVertex.x;
      double minY = firstProjectedVertex.y;
      double maxY = firstProjectedVertex.y;

      for (int i = 1; i < vertices.length; i++) {
         final Position vertex = vertices[i];
         final Vec4 projected = GWWUtils.getScreenPoint(dc, vertex);
         final double x = projected.x;
         final double y = projected.y;

         if (x < minX) {
            minX = x;
         }
         if (y < minY) {
            minY = y;
         }
         if (x > maxX) {
            maxX = x;
         }
         if (y > maxY) {
            maxY = y;
         }
      }

      // calculate the area of the rectangle
      final double width = maxX - minX;
      final double height = maxY - minY;
      final double area = width * height;
      //_computedProjectedPixels = Math.round((float) area);
      _computedProjectedPixels = (float) area;
   }


   private float getProjectedPixels(final DrawContext dc) {
      if (_forceComputationOfProjectedPixels) {
         computeProjectedPixels(dc);
      }

      return _computedProjectedPixels;
   }


   private boolean isBigEnough(final DrawContext dc) {
      return (getProjectedPixels(dc) > 0);
   }


   @Override
   protected void doRender(final DrawContext dc) {
      if (!isVisible(dc)) {
         return;
      }

      if (!isBigEnough(dc)) {
         return;
      }

      if (isShowExtents()) {
         getExtent(dc).render(dc);
      }

      _surfaceImage.render(dc);
   }

}
