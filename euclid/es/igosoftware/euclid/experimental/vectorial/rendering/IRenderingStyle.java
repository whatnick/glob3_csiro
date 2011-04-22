

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingStyle {

   /* general */
   public boolean isDebugRendering();


   public boolean isRenderLODIgnores();


   public double getLODMinSize();


   public IColor getLODColor();


   public String uniqueName();


   public IVector2 increment(final IVector2 position,
                             final GProjection projection,
                             final double deltaEasting,
                             final double deltaNorthing);


   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features);


   public void preRenderImage(final BufferedImage renderedImage);


   public void postRenderImage(final BufferedImage renderedImage);


   public IMeasure<GArea> getMaximumSize();


   /* point style */
   public IMeasure<GArea> getPointSize(final IVector2 point,
                                       final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                       final IVectorial2DRenderingContext rc);


   public IMeasure<GLength> getPointBorderSize(final IVector2 point,
                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                               final IVectorial2DRenderingContext rc);


   public IColor getPointColor(final IVector2 point,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final IVectorial2DRenderingContext rc);


   public IColor getPointBorderColor(final IVector2 point,
                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                     final IVectorial2DRenderingContext rc);


   public float getPointOpacity(final IVector2 point,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IVectorial2DRenderingContext rc);


   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final IVectorial2DRenderingContext rc);


   public GRenderingSymbol getPointSymbol(final IVector2 point,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                          final IVectorial2DRenderingContext rc);


   /* surfaces */
   public IMeasure<GLength> getSurfaceBorderSize(final ISurface2D<?> surface,
                                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                 final IVectorial2DRenderingContext rc);


   public IColor getSurfaceColor(final ISurface2D<?> surface,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                 final IVectorial2DRenderingContext rc);


   public IColor getSurfaceBorderColor(final ISurface2D<?> surface,
                                       final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                       final IVectorial2DRenderingContext rc);


   public float getSurfaceOpacity(final ISurface2D<?> surface,
                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                  final IVectorial2DRenderingContext rc);


   public GRenderingShape<? extends ISurface2D<?>> getSurfaceShape(final ISurface2D<?> surface,
                                                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                                   final IVectorial2DRenderingContext rc);


   public void drawSurface(final ISurface2D<?> surface,
                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                           final IVectorial2DRenderingContext rc);


   /* curves */
   public IMeasure<GLength> getCurveBorderSize(final ICurve2D<?> curve,
                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                               final IVectorial2DRenderingContext rc);


   public IColor getCurveColor(final ICurve2D<?> curve,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final IVectorial2DRenderingContext rc);


   public float getCurveOpacity(final ICurve2D<?> curve,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IVectorial2DRenderingContext rc);


   public GRenderingShape<? extends ICurve2D<?>> getCurveShape(final ICurve2D<?> curve,
                                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                               final IVectorial2DRenderingContext rc);


   public void drawCurve(final ICurve2D<?> curve,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final IVectorial2DRenderingContext rc);


}
