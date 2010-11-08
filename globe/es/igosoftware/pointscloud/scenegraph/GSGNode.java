package es.igosoftware.pointscloud.scenegraph;

import java.awt.Color;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.pointscloud.GPointsCloudLayer;
import es.igosoftware.util.GMath;
import es.igosoftware.utils.GGlobeCache;
import es.igosoftware.utils.GPositionBox;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

public abstract class GSGNode {

   private static final GGlobeCache<GSGNode, Cylinder> EXTENTS_CACHE;

   static {
      EXTENTS_CACHE = new GGlobeCache<GSGNode, Cylinder>(new GGlobeCache.Factory<GSGNode, Cylinder>() {
         @Override
         public Cylinder create(final GSGNode node,
                                final Globe globe,
                                final double verticalExaggeration) {
            final GPositionBox box = node._box;

            return Cylinder.computeVerticalBoundingCylinder(globe, verticalExaggeration, box._sector, box._lower.elevation,
                     GMath.nextUp(box._upper.elevation));
         }
      });
   }


   private final GPositionBox                          _box;
   protected final GPointsCloudLayer                   _layer;

   private boolean                                     _forceComputationOfProjectedPixels = true;
   private float                                       _computedProjectedPixels           = -1;
   private float                                       _priority                          = Float.NEGATIVE_INFINITY;


   public GSGNode(final GAxisAlignedBox bounds,
                  final GProjection projection,
                  final GPointsCloudLayer layer) {
      _box = new GPositionBox(bounds, projection);
      _layer = layer;
      //      System.out.println("Box: " + _box);
   }


   protected final Cylinder getExtent(final DrawContext dc) {
      final Globe globe = dc.getView().getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      return EXTENTS_CACHE.get(this, globe, verticalExaggeration);
   }


   protected final boolean isVisible(final DrawContext dc) {
      final Cylinder extent = getExtent(dc);
      final Frustum frustum = dc.getView().getFrustumInModelCoordinates();
      return extent.intersects(frustum);
   }


   protected float getProjectedPixels(final DrawContext dc) {
      if (_forceComputationOfProjectedPixels) {
         computeProjectedPixels(dc);
      }

      return _computedProjectedPixels;
   }


   private void computeProjectedPixels(final DrawContext dc) {
      //      double minX = Double.POSITIVE_INFINITY;
      //      double minY = Double.POSITIVE_INFINITY;
      //      double maxX = Double.NEGATIVE_INFINITY;
      //      double maxY = Double.NEGATIVE_INFINITY;

      // calculate the minimum enclosing rectangle
      //      for (final Position boundsVertice : _box.getVertices()) {
      final GPositionBox box = getBoxForProjectedPixels();
      final Position[] vertices = box.getVertices();
      //      if (vertices.length == 0) {
      //         _computedProjectedPixels = 0;
      //         return;
      //      }

      final Vec4 firstProjectedVertex = dc.getScreenPoint(vertices[0]);
      double minX = firstProjectedVertex.x;
      double maxX = firstProjectedVertex.x;
      double minY = firstProjectedVertex.y;
      double maxY = firstProjectedVertex.y;

      for (int i = 1; i < vertices.length; i++) {
         final Position vertex = vertices[i];
         final Vec4 projected = dc.getScreenPoint(vertex);
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


   protected GPositionBox getBoxForProjectedPixels() {
      return _box;
   }


   private boolean isBigEnough(final DrawContext dc) {
      return (getProjectedPixels(dc) > 0);
   }


   public final void preRender(final DrawContext dc,
                               final boolean changed) {
      //      if (!isVisible(dc)) {
      //         return;
      //      }
      //
      //      if (!isBigEnough(dc)) {
      //         return;
      //      }

      _forceComputationOfProjectedPixels = true;

      if (!isVisible(dc) || !isBigEnough(dc)) {
         setPriority(Integer.MIN_VALUE);
      }

      doPreRender(dc, changed);
   }


   protected void setPriority(final float priority) {
      _priority = priority;
   }


   public float getPriority() {
      return _priority;
   }


   public final int render(final DrawContext dc) {
      if (!isVisible(dc)) {
         return 0;
      }

      if (!isBigEnough(dc)) {
         return 0;
      }

      if (_layer.isShowExtents()) {
         getExtent(dc).render(dc);
      }

      return doRender(dc);
   }


   protected abstract void doPreRender(final DrawContext dc,
                                       final boolean changed);


   protected abstract int doRender(final DrawContext dc);


   public abstract void initialize(final DrawContext dc);


   public abstract void setColorFromElevation(final boolean colorFromElevation);


   public abstract void reload();


   public abstract Position getHomePosition();


   public GPositionBox getBox() {
      return _box;
   }


   public abstract void setPointsColor(final Color pointsColor);

}
