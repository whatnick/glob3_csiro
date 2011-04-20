

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


class GVectorial2DRenderUnit
         implements
            IVectorial2DRenderUnit {


   @Override
   public void render(final BufferedImage renderedImage,
                      final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                      final GProjection projection,
                      final GAxisAlignedRectangle region,
                      final GVectorialRenderingAttributes attributes,
                      final IRenderingStyle renderingStyle) {

      final int imageWidth = renderedImage.getWidth();
      final int imageHeight = renderedImage.getHeight();

      final IVector2 scale = new GVector2D(imageWidth, imageHeight).div(region.getExtent());

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      //      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

      if (renderingStyle.isDebugRendering()) {
         g2d.setColor(Color.YELLOW);
         g2d.setStroke(new BasicStroke(1));

         g2d.drawRect(0, 0, imageWidth, imageHeight);

         //         g2d.drawString(region._lower.toString(), 10, 10);
         //         g2d.drawString(" " + region._upper, 10, 20);
      }

      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -imageHeight));

      g2d.setTransform(transformFlipY);


      final GAxisAlignedRectangle extendedRegion = calculateExtendedRegion(region, projection, renderingStyle);
      final GVectorialRenderingContext rc = new GVectorialRenderingContext(scale, region, extendedRegion, attributes,
               renderingStyle, projection, g2d, renderedImage);

      processNode(quadtree.getRoot(), rc);

   }


   private static GAxisAlignedRectangle calculateExtendedRegion(final GAxisAlignedRectangle region,
                                                                final GProjection projection,
                                                                final IRenderingStyle renderingStyle) {
      final IMeasure<GArea> maximumSize = renderingStyle.getMaximumSize();

      final double areaInSquaredMeters = maximumSize.getValueInReferenceUnits();
      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 lower = renderingStyle.increment(region._lower, projection, -extent, -extent);
      final IVector2 upper = renderingStyle.increment(region._upper, projection, extent, extent);

      return new GAxisAlignedRectangle(lower, upper);
   }


   private void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                            final GVectorialRenderingContext rc) {

      final GAxisAlignedRectangle nodeBounds = node.getMinimumBounds().asRectangle();

      if (!nodeBounds.touches(rc._extendedRegion)) {
         return;
      }


      final IVector2 scaledNodeExtent = rc.scaleExtent(nodeBounds.getExtent());
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize < rc._renderingStyle.getLODMinSize()) {
         if (rc._renderingStyle.isRenderLODIgnores() || rc._renderingStyle.isDebugRendering()) {
            final Color color = rc._renderingStyle.isDebugRendering() ? Color.RED : rc._renderingStyle.getLODColor().asAWTColor();

            final IVector2 projectedCenter = rc.scaleAndTranslatePoint(nodeBounds.getCenter());
            rc.setPixel(projectedCenter, color);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> child : inner.getChildren()) {
            processNode(child, rc);
         }
      }

      renderNodeGeometries(node, rc);
   }


   private void renderNodeGeometries(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                                     final GVectorialRenderingContext rc) {


      if (rc._renderingStyle.isDebugRendering()) {
         final GAxisAlignedOrthotope<IVector2, ?> nodeBounds = node.getBounds();

         final IVector2 nodeLower = rc.scaleAndTranslatePoint(nodeBounds._lower);
         final IVector2 nodeUpper = rc.scaleAndTranslatePoint(nodeBounds._upper);

         //         g2d.setStroke(new BasicStroke(0.25f));
         final boolean isInner = (node instanceof GGTInnerNode);
         rc.setStroke(new BasicStroke(1));
         rc.setColor(isInner ? Color.GREEN.darker().darker().darker().darker().darker() : Color.GREEN);

         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         //         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         //         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         final int width = (int) (nodeUpper.x() - nodeLower.x());
         final int height = (int) (nodeUpper.y() - nodeLower.y());
         rc.drawRect(x, y, width, height);
      }


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> pair : node.getElements()) {
         final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry = pair.getGeometry();
         renderGeometry(geometry, pair.getElement(), rc);
      }

   }


   private void renderGeometry(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final GVectorialRenderingContext rc) {


      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(rc._extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) geometry;
         for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
            renderGeometry(child, feature, rc);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;
         renderPoint(point, feature, rc);
      }
      else {
         // size validation only for non-points
         final GAxisAlignedOrthotope<IVector2, ?> geometryBounds = geometry.getBounds().asAxisAlignedOrthotope();
         final IVector2 scaledGeometryExtent = rc.scaleExtent(geometryBounds.getExtent());
         final double projectedSize = scaledGeometryExtent.x() * scaledGeometryExtent.y();
         if (projectedSize < rc._renderingStyle.getLODMinSize()) {
            if (rc._renderingStyle.isRenderLODIgnores() || rc._renderingStyle.isDebugRendering()) {
               final Color color = rc._renderingStyle.isDebugRendering() ? Color.MAGENTA
                                                                        : rc._renderingStyle.getLODColor().asAWTColor();

               final IVector2 projectedCenter = rc.scaleAndTranslatePoint(geometryBounds.getCenter());
               rc.setPixel(projectedCenter, color);
            }

            return;
         }


         if (geometry instanceof IPolygonalChain2D) {
            drawPolyline(rc.getPoints((IPolygonalChain2D) geometry), feature, rc);
         }
         else if (geometry instanceof IPolygon2D) {
            final IPolygon2D polygon = (IPolygon2D) geometry;
            if (polygon instanceof IComplexPolygon2D) {
               final IComplexPolygon2D complexPolygon = (IComplexPolygon2D) polygon;

               final Area complexShape = rc.getPoints(complexPolygon.getHull()).asArea();

               for (final ISimplePolygon2D hole : complexPolygon.getHoles()) {
                  // complexShape.exclusiveOr(getPoints(hole, scale, region).asArea());
                  complexShape.subtract(rc.getPoints(hole).asArea());
               }

               drawShape(complexShape, feature, rc);
            }
            else {
               drawShape(rc.getPoints(polygon).asShape(), feature, rc);
            }
         }
         else {
            System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
         }

      }
   }


   private static void renderPoint(final IVector2 point,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final GVectorialRenderingContext rc) {
      rc._renderingStyle.drawPoint(point, feature, rc);
   }


   private static void drawPolyline(final GAWTPoints points,
                                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                    final GVectorialRenderingContext rc) {
      // render border
      if (rc._attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = rc._attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            rc.setStroke(borderStroke);
            rc.setColor(rc._attributes._borderColor);
            rc.drawPolyline(points);
         }
      }
   }


   private static void drawShape(final Shape shape,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                 final GVectorialRenderingContext rc) {
      // fill polygon
      rc.setColor(rc._attributes._fillColor);
      rc.fill(shape);


      // render border
      if (rc._attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = rc._attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            rc.setStroke(borderStroke);
            rc.setColor(rc._attributes._borderColor);
            rc.draw(shape);
         }
      }
   }


}
