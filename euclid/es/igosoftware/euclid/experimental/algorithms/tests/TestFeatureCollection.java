

package es.igosoftware.euclid.experimental.algorithms.tests;

import java.util.ArrayList;
import java.util.Iterator;

import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;


public class TestFeatureCollection<VectorT extends IVector<VectorT, ?>>
         implements
            Iterable<GPair<IGeometry<VectorT>, Double>>,
            IFunction<GPair<IGeometry<VectorT>, Double>, GPair<IGeometry<VectorT>, Double>> {

   private final ArrayList<GPair<IGeometry<VectorT>, Double>> geoms = new ArrayList<GPair<IGeometry<VectorT>, Double>>();


   @Override
   public GPair<IGeometry<VectorT>, Double> apply(final GPair<IGeometry<VectorT>, Double> element) {
      return element;
   }


   @Override
   public Iterator<GPair<IGeometry<VectorT>, Double>> iterator() {
      return geoms.iterator();
   }


   public void add(final IGeometry<VectorT> geom) {
      add(geom, 1);
   }


   public void add(final IGeometry<VectorT> geom,
                   final double weight) {
      this.geoms.add(new GPair<IGeometry<VectorT>, Double>(geom, new Double(weight)));
   }

}
