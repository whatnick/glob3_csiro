

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GVectorial2DRenderingScaler
         implements
            IVectorial2DRenderingScaler {


   private final IVector2              _scale;
   private final GAxisAlignedRectangle _region;
   private final GProjection           _projection;


   public GVectorial2DRenderingScaler(final GAxisAlignedRectangle region,
                                      final GProjection projection,
                                      final int imageWidth,
                                      final int imageHeight) {
      GAssert.notNull(region, "region");
      GAssert.notNull(projection, "projection");
      GAssert.isPositive(imageWidth, "imageWidth");
      GAssert.isPositive(imageHeight, "imageHeight");

      _region = region;
      _projection = projection;

      _scale = new GVector2D(imageWidth, imageHeight).div(region.getExtent());
   }


   @Override
   public final GProjection getProjection() {
      return _projection;
   }


   @Override
   public final IVector2 scaleExtent(final IVector2 extent) {
      return extent.scale(_scale);
   }


   @Override
   public final IVector2 scaleAndTranslatePoint(final IVector2 point) {
      return point.sub(_region._lower).scale(_scale);
   }


   @Override
   public final GAWTPoints toScaledAndTranslatedPoints(final IPointsContainer<IVector2> polygon) {
      final int nPoints = polygon.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      for (int i = 0; i < nPoints; i++) {
         final IVector2 point = scaleAndTranslatePoint(polygon.getPoint(i));

         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());
      }

      return new GAWTPoints(xPoints, yPoints);
   }


}
