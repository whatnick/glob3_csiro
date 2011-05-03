

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;


public interface IGeometry2DSymbolizerExpression<GeometryT extends IGeometry2D> {


   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler);


   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features);


   public void preRenderImage(final BufferedImage image);


   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> evaluate(final GeometryT geometry,
                                                                                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                               final IVectorial2DRenderingScaler scaler);


   public void postRenderImage(final BufferedImage image);

}
