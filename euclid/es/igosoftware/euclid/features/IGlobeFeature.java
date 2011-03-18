

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeature<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends IFiniteBounds<VectorT, BoundsT>

> {

   public IBoundedGeometry<VectorT, ?, BoundsT> getDefaultGeometry();


   public List<Object> getAttributes();


   public Object getAttribute(final int index);

}
