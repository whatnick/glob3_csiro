

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
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
   public IMeasure<GArea> getPointSize(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature);


   public IColor getPointColor(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature);


   public float getPointOpacity(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature);


   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final GVectorialRenderingContext rc);


   public GSymbol getPointSymbol(final IVector2 point,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                 final GVectorialRenderingContext rc);

}
