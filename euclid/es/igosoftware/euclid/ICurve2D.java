

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface ICurve2D<BoundsT extends IBounds<IVector2, BoundsT>>
         extends
            ICurve<IVector2, BoundsT>,
            IBoundedGeometry2D<BoundsT> {

}
