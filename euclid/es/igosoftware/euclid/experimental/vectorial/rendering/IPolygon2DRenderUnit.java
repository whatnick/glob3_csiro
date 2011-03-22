

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public interface IPolygon2DRenderUnit {


   public BufferedImage render(final GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle> quadtree,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes);


}
