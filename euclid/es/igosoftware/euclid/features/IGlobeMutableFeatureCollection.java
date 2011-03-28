

package es.igosoftware.euclid.features;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeMutableFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>,

TypeT extends IGlobeMutableFeatureCollection<VectorT, FeatureGeometryT, TypeT>

>
         extends
            IGlobeFeatureCollection<VectorT, FeatureGeometryT, TypeT>,
            IMutable<TypeT> {


   public void set(final long index,
                   final IGlobeFeature<VectorT, FeatureGeometryT> value);


   public void add(final IGlobeFeature<VectorT, FeatureGeometryT> value);


   public IGlobeFeature<VectorT, FeatureGeometryT> remove(final long index);


   public boolean remove(final IGlobeFeature<VectorT, FeatureGeometryT> value);


   public void clear();


}
