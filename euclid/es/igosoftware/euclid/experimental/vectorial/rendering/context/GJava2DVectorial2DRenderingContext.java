

package es.igosoftware.euclid.experimental.vectorial.rendering.context;


public class GJava2DVectorial2DRenderingContext
         implements
            IVectorial2DRenderingContext {


   private final IVectorial2DDrawer                _drawer;
   private final IVectorial2DRenderingScaleContext _scaler;


   public GJava2DVectorial2DRenderingContext(final IVectorial2DRenderingScaleContext scaler,
                                             final IVectorial2DDrawer drawer) {
      _scaler = scaler;
      _drawer = drawer;
   }


   @Override
   public IVectorial2DDrawer getDrawer() {
      return _drawer;
   }


   @Override
   public IVectorial2DRenderingScaleContext getScaler() {
      return _scaler;
   }


}
