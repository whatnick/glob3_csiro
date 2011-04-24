

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;


public interface INodeRenderingShape {


   public void draw(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                    final IRenderingStyle2D renderingStyle,
                    final IVectorial2DRenderingScaler scaler,
                    final IVectorial2DDrawer drawer);


}
