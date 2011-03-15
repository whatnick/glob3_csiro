

package es.igosoftware.globe;

import java.util.List;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.layers.IGlobeFeature;


public interface IGlobeFeatureCollection
         extends
            Iterable<IGlobeFeature> {


   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;

   }


   public static interface IFeatureVisitor {
      public void visit(final IGlobeFeature feature) throws IGlobeFeatureCollection.AbortVisiting;
   }


   public GProjection getProjection();


   public List<IGlobeFeature> getFeatures();


   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor visitor);


   public IGlobeFeature get(final long index);


   public boolean isEmpty();


   public long size();


}
