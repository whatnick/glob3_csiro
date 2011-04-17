

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public interface IVectorial2DRenderUnit {


   public BufferedImage render(final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                               final GAxisAlignedRectangle region,
                               final int imageWidth,
                               final int imageHeight,
                               final GVectorialRenderingAttributes attributes,
                               final IRenderingStyle renderingStyle);


}
