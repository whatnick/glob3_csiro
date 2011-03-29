

package es.igosoftware.globe;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector2;


public interface IGlobeVector2Layer<

GeometryT extends IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>

>
         extends
            IGlobeVectorLayer<IVector2<?>, GeometryT> {

}
