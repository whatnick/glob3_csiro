

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;


public interface IPolygon2DRenderUnit {


   public BufferedImage render(final GRenderingQuadtree quadtree,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes);

}
