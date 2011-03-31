

package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GGeometryQuadtree<ElementT>
         extends
            GGeometryNTree<IVector2, GAxisAlignedRectangle, ElementT> {


   public GGeometryQuadtree(final String name,
                            final GAxisAlignedRectangle bounds,
                            final Iterable<? extends ElementT> elements,
                            final ITransformer<ElementT, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> transformer,
                            final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Quadtree";
   }

}
