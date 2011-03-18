

package es.igosoftware.euclid.features;

import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;


public interface IGlobeFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>,

TypeT extends IGlobeFeatureCollection<VectorT, FeatureBoundsT, TypeT>

>
         extends
            Iterable<IGlobeFeature<VectorT, FeatureBoundsT>> {


   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;

   }


   public static interface IFeatureVisitor<

   VectorT extends IVector<VectorT, ?, ?>,

   FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>

   > {

      public void visit(final IGlobeFeature<VectorT, FeatureBoundsT> feature) throws IGlobeFeatureCollection.AbortVisiting;

   }


   public GVectorLayerType getShapeType();


   public List<GField> getFields();


   public GProjection getProjection();


   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureBoundsT> visitor);


   public IGlobeFeature<VectorT, FeatureBoundsT> get(final long index);


   public boolean isEmpty();


   public long size();


   public String getUniqueID();


   public GAxisAlignedOrthotope<VectorT, ?> getBounds();


}
