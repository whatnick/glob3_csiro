

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ICurveRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.IRenderingSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ISurfaceRenderingShape;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingStyle {

   /* general */
   public boolean isDebugRendering();


   public boolean isRenderLODIgnores();


   public double getLODMinSize();


   public IColor getLODColor();


   public String uniqueName();


   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features);


   public void preRenderImage(final BufferedImage renderedImage);


   public void postRenderImage(final BufferedImage renderedImage);


   public IMeasure<GArea> getMaximumSize();


   /* point style */
   public IMeasure<GArea> getPointSize(final IVector2 point,
                                       final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                       final IVectorial2DRenderingScaler scaler);


   public IMeasure<GLength> getPointBorderSize(final IVector2 point,
                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                               final IVectorial2DRenderingScaler scaler);


   public IColor getPointColor(final IVector2 point,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final IVectorial2DRenderingScaler scaler);


   public IColor getPointBorderColor(final IVector2 point,
                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                     final IVectorial2DRenderingScaler scaler);


   public float getPointOpacity(final IVector2 point,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IVectorial2DRenderingScaler scaler);


   public IRenderingSymbol getPointSymbol(final IVector2 point,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                          final IVectorial2DRenderingScaler scaler);


   /* surfaces */
   public IMeasure<GLength> getSurfaceBorderSize(final ISurface2D<?> surface,
                                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                 final IVectorial2DRenderingScaler scaler);


   public IColor getSurfaceColor(final ISurface2D<?> surface,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                 final IVectorial2DRenderingScaler scaler);


   public IColor getSurfaceBorderColor(final ISurface2D<?> surface,
                                       final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                       final IVectorial2DRenderingScaler scaler);


   public float getSurfaceOpacity(final ISurface2D<?> surface,
                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                  final IVectorial2DRenderingScaler scaler);


   public ISurfaceRenderingShape<ISurface2D<? extends IFiniteBounds<IVector2, ?>>> getSurfaceShape(final ISurface2D<? extends IFiniteBounds<IVector2, ?>> surface,
                                                                                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                                                                   final IVectorial2DRenderingScaler scaler);


   /* curves */
   public IMeasure<GLength> getCurveBorderSize(final ICurve2D<?> curve,
                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                               final IVectorial2DRenderingScaler scaler);


   public IColor getCurveColor(final ICurve2D<?> curve,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final IVectorial2DRenderingScaler scaler);


   public float getCurveOpacity(final ICurve2D<?> curve,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IVectorial2DRenderingScaler scaler);


   public ICurveRenderingShape<ICurve2D<? extends IFiniteBounds<IVector2, ?>>> getCurveShape(final ICurve2D<? extends IFiniteBounds<IVector2, ?>> curve,
                                                                                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                                                             final IVectorial2DRenderingScaler scaler);


}
