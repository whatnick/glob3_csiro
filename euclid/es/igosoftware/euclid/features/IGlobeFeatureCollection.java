

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>,

TypeT extends IGlobeFeatureCollection<VectorT, FeatureGeometryT, TypeT>

>
         extends
            Iterable<IGlobeFeature<VectorT, FeatureGeometryT>> {


   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;

   }


   public static interface IFeatureVisitor<

   VectorT extends IVector<VectorT, ?, ?>,

   FeatureGeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

   > {

      public void visit(final IGlobeFeature<VectorT, FeatureGeometryT> feature,
                        final long index) throws IGlobeFeatureCollection.AbortVisiting;

   }


   public GVectorLayerType getShapeType();


   public List<GField> getFields();


   public GProjection getProjection();


   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureGeometryT> visitor);


   public IGlobeFeature<VectorT, FeatureGeometryT> get(final long index);


   public boolean isEmpty();


   public long size();


   public String getUniqueID();


   public GAxisAlignedOrthotope<VectorT, ?> getBounds();


}
