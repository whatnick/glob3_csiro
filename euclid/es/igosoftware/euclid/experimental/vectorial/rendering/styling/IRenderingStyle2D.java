

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;
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


   /* -------------------------------------------------------------------------------------- */
   /* nodes  */

   public boolean processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                              final IVectorial2DRenderingScaler scaler,
                              final IVectorial2DDrawer drawer);


   public GStyled2DGeometry<? extends IGeometry2D> getNodeSymbol(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                                                 final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* point style */
   public GStyled2DGeometry<? extends IGeometry2D> getPointStyledSurface(final IVector2 point,
                                                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                         final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* surfaces */
   public GStyled2DGeometry<? extends IGeometry2D> getStyledSurface(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                    final IVectorial2DRenderingScaler scaler);


   /* -------------------------------------------------------------------------------------- */
   /* curves */
   public GStyled2DGeometry<? extends IGeometry2D> getStyledCurve(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                  final IVectorial2DRenderingScaler scaler);


}
