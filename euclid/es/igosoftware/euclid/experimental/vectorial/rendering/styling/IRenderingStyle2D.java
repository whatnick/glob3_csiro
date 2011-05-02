

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingStyle2D {


   /* -------------------------------------------------------------------------------------- */
   /* general */
   public boolean isDebugRendering();


   public double getLODMinSize();


   public boolean isRenderLODIgnores();


   public String uniqueName();


   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features);


   public void preRenderImage(final BufferedImage renderedImage);


   public void postRenderImage(final BufferedImage renderedImage);


   public IMeasure<GArea> getMaximumSize();


   public boolean isClusterSymbols();


   /* -------------------------------------------------------------------------------------- */
   /* nodes */
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getNodeSymbols(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                                                                                                     final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* point style */
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getPointSymbols(final IVector2 point,
                                                                                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                      final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* surfaces */
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                                                                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                        final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* curves */
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getCurveSymbols(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                                                                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                      final IVectorial2DRenderingScaler scaler);


}
