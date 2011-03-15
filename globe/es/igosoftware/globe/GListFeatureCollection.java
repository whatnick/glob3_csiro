

package es.igosoftware.globe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.layers.IGlobeFeature;
import es.igosoftware.util.GAssert;


public class GListFeatureCollection
         implements
            IGlobeFeatureCollection {


   private final GProjection         _projection;
   private final List<IGlobeFeature> _features;


   public GListFeatureCollection(final GProjection projection,
                                 final List<IGlobeFeature> features) {
      GAssert.notNull(projection, "projection");
      GAssert.notEmpty(features, "features");

      _projection = projection;
      _features = new ArrayList<IGlobeFeature>(features); // creates a copy of the list to protect the modifications from outside
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public List<IGlobeFeature> getFeatures() {
      return Collections.unmodifiableList(_features);
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor visitor) {
      try {
         for (final IGlobeFeature feature : _features) {
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
   public Iterator<IGlobeFeature> iterator() {
      return _features.iterator();
   }


   @Override
   public IGlobeFeature get(final long index) {
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

}
