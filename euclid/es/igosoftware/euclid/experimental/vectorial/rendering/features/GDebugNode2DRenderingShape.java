

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GDebugNode2DRenderingShape
         implements
            INode2DRenderingShape {

   private static final Color                       LEAF_NODE_BOUND_COLOR   = new Color(0f, 1f, 0f, 0.5f);
   private static final Color                       INNER_NODES_BOUND_COLOR = LEAF_NODE_BOUND_COLOR.darker().darker();

   private static final BasicStroke                 LEAF_NODES_STROKE       = new BasicStroke(1, BasicStroke.CAP_ROUND,
                                                                                     BasicStroke.JOIN_ROUND, 10, new float[] {
            2,
            2                                                                       }, 0);
   private static final BasicStroke                 INNER_NODES_STROKE      = new BasicStroke(1);


   private final GAxisAlignedOrthotope<IVector2, ?> _scaledBounds;


   public GDebugNode2DRenderingShape(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                     final IVectorial2DRenderingScaler scaler) {
      GAssert.notNull(node, "node");
      GAssert.notNull(scaler, "scaler");

      _scaledBounds = scaler.scaleAndTranslate(node.getBounds());
   }


   @Override
   public void draw(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                    final IRenderingStyle2D renderingStyle,
                    final IVectorial2DRenderingScaler scaler,
                    final IVectorial2DDrawer drawer) {
      final boolean isInner = (node instanceof GGTInnerNode);

      final Color color = isInner ? INNER_NODES_BOUND_COLOR : LEAF_NODE_BOUND_COLOR;
      final BasicStroke stroke = isInner ? INNER_NODES_STROKE : LEAF_NODES_STROKE;

      drawer.drawRect(_scaledBounds, color, stroke);
   }


}
