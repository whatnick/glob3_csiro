

package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface ISurface2D<BoundsT extends IBounds<IVector2, BoundsT>>
         extends
            ISurface<IVector2, BoundsT> {

}
