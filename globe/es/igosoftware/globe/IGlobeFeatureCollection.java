

package es.igosoftware.globe;

import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.globe.layers.IGlobeFeature;


public interface IGlobeFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends IFiniteBounds<VectorT, BoundsT>

>
         extends
            Iterable<IGlobeFeature<VectorT, BoundsT>> {


   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;

   }


   public static interface IFeatureVisitor<VectorT extends IVector<VectorT, ?, ?>, BoundsT extends IFiniteBounds<VectorT, BoundsT>> {
      public void visit(final IGlobeFeature<VectorT, BoundsT> feature) throws IGlobeFeatureCollection.AbortVisiting;
   }


   public GProjection getProjection();


   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, BoundsT> visitor);


   public IGlobeFeature<VectorT, BoundsT> get(final long index);


   public boolean isEmpty();


   public long size();


}
