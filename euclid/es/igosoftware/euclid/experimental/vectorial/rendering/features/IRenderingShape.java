

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;


public interface IRenderingShape<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

>
         extends
            IRenderingFeature<GeometryT> {


}
