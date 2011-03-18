

package es.igosoftware.euclid.features;

import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeMutableFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>,

TypeT extends IGlobeMutableFeatureCollection<VectorT, FeatureBoundsT, TypeT>

>
         extends
            IGlobeFeatureCollection<VectorT, FeatureBoundsT, TypeT>,
            IMutable<TypeT> {


   public void set(final long index,
                   final IGlobeFeature<VectorT, FeatureBoundsT> value);


   public void add(final IGlobeFeature<VectorT, FeatureBoundsT> value);


   public IGlobeFeature<VectorT, FeatureBoundsT> remove(final long index);


   public boolean remove(final IGlobeFeature<VectorT, FeatureBoundsT> value);


}
