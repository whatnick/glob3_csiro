

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeature<

VectorT extends IVector<VectorT, ?, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

> {

   public GeometryT getDefaultGeometry();


   public List<Object> getAttributes();


   public Object getAttribute(final int index);

}
