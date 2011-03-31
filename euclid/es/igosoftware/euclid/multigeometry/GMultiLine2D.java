

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.shape.ILineal2D;


public class GMultiLine2D
         extends
            GMultiGeometry2D<ILineal2D> {


   private static final long serialVersionUID = 1L;


   public GMultiLine2D(final ILineal2D... children) {
      super(children);
   }


   public GMultiLine2D(final List<ILineal2D> children) {
      super(children);
   }


}
