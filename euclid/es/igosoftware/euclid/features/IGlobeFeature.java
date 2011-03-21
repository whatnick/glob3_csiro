

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeature<

VectorT extends IVector<VectorT, ?, ?>,

FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>

> {

   public IBoundedGeometry<VectorT, ?, FeatureBoundsT> getDefaultGeometry();


   public List<Object> getAttributes();


   public Object getAttribute(final int index);

}
