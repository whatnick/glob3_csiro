

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingShape<

GeometryT extends IBoundedGeometry<IVector2, GAxisAlignedRectangle>

>
         extends
            IRenderingFeature<GeometryT> {


}
