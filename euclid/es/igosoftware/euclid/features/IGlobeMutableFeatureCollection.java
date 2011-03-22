

package es.igosoftware.euclid.features;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeMutableFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ?, FeatureBoundsT>,

FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>,

TypeT extends IGlobeMutableFeatureCollection<VectorT, FeatureGeometryT, FeatureBoundsT, TypeT>

>
         extends
            IGlobeFeatureCollection<VectorT, FeatureGeometryT, FeatureBoundsT, TypeT>,
            IMutable<TypeT> {


   public void set(final long index,
                   final IGlobeFeature<VectorT, FeatureGeometryT, FeatureBoundsT> value);


   public void add(final IGlobeFeature<VectorT, FeatureGeometryT, FeatureBoundsT> value);


   public IGlobeFeature<VectorT, FeatureGeometryT, FeatureBoundsT> remove(final long index);


   public boolean remove(final IGlobeFeature<VectorT, FeatureGeometryT, FeatureBoundsT> value);


   public void clear();


}
