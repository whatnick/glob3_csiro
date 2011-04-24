

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;


public interface IColorizer {


   public void preprocessFeatures(IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features);


   public IColor getColor(final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature);


   public void preRenderImage(final BufferedImage renderedImage);


   public void postRenderImage(final BufferedImage renderedImage);


}
