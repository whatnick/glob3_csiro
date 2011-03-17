

package es.igosoftware.globe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.globe.layers.IGlobeFeature;
import es.igosoftware.util.GAssert;


public class GListFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends IFiniteBounds<VectorT, BoundsT>

>
         implements
            IGlobeFeatureCollection<VectorT, BoundsT> {


   private final GProjection                           _projection;
   private final List<IGlobeFeature<VectorT, BoundsT>> _features;


   public GListFeatureCollection(final GProjection projection,
                                 final List<IGlobeFeature<VectorT, BoundsT>> features) {
      GAssert.notNull(projection, "projection");
      GAssert.notEmpty(features, "features");

      _projection = projection;
      _features = new ArrayList<IGlobeFeature<VectorT, BoundsT>>(features); // creates a copy of the list to protect the modifications from outside
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, BoundsT> visitor) {
      try {
         for (final IGlobeFeature<VectorT, BoundsT> feature : _features) {
            visitor.visit(feature);
         }
      }
      catch (final IGlobeFeatureCollection.AbortVisiting e) {
         // ignore exception, just exit the for loop
      }
   }


   @Override
   public String toString() {
      return "GListFeatureCollection [projection=" + _projection + ", features=" + _features.size() + "]";
   }


   @Override
   public Iterator<IGlobeFeature<VectorT, BoundsT>> iterator() {
      return _features.iterator();
   }


   @Override
   public IGlobeFeature<VectorT, BoundsT> get(final long index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("index #" + index + " is begative");
      }
      if (index > Integer.MAX_VALUE) {
         throw new IndexOutOfBoundsException("index #" + index + " is bigger that Integer.MAX_VALUE");
      }


      final int intIndex = (int) index; // safe to cast here as the bounds was just checked
      return _features.get(intIndex);
   }


   @Override
   public boolean isEmpty() {
      return _features.isEmpty();
   }


   @Override
   public long size() {
      return _features.size();
   }


   //   public static void main(final String[] args) {
   //      final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(new GVector2F(0, 1), new GVector2F(0, 1));
   //      System.out.println(bounds.getCenter());
   //   }
}
