

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;


public interface ICurveRenderingShape<

GeometryT extends ICurve2D<? extends IFinite2DBounds<?>>

>
         extends
            IRenderingShape<GeometryT> {

}
