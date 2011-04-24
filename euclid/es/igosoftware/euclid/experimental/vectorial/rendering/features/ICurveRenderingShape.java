

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface ICurveRenderingShape<

GeometryT extends ICurve2D<? extends IFiniteBounds<IVector2, ?>>

>
         extends
            IRenderingShape<GeometryT> {

}
