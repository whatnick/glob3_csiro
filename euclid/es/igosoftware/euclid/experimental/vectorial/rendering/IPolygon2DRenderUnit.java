

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.shape.IPolygon2D;


public interface IPolygon2DRenderUnit {

   public BufferedImage render(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes);

}
