

package es.igosoftware.euclid.experimental.vectorial.rendering.feautures;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GRenderingShape<

GeometryT extends IBoundedGeometry<IVector2, GAxisAlignedRectangle>

>
         extends
            GRenderingFeature<GeometryT> {


}
