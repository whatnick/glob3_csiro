

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingShape<

GeometryT extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>

>
         extends
            IRenderingFeature<GeometryT> {


}
