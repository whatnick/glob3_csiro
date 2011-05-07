

package es.igosoftware.euclid.experimental.algorithms.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GPair;
import es.igosoftware.util.IFunction;


public class TestFeatureCollection<VectorT extends IVector<VectorT, ?>>
         implements
            Iterable<GPair<IBoundedGeometry<VectorT, ?>, Double>>,
            IFunction<GPair<IBoundedGeometry<VectorT, ?>, Double>, GPair<IBoundedGeometry<VectorT, ?>, Double>> {

   private final List<GPair<IBoundedGeometry<VectorT, ?>, Double>> _geoms = new ArrayList<GPair<IBoundedGeometry<VectorT, ?>, Double>>();


   @Override
   public GPair<IBoundedGeometry<VectorT, ?>, Double> apply(final GPair<IBoundedGeometry<VectorT, ?>, Double> element) {
      return element;
   }


   @Override
   public Iterator<GPair<IBoundedGeometry<VectorT, ?>, Double>> iterator() {
      return _geoms.iterator();
   }


   public void add(final IBoundedGeometry<VectorT, ?> geom) {
      add(geom, 1);
   }


   public void add(final IBoundedGeometry<VectorT, ?> geom,
                   final double weight) {
      _geoms.add(new GPair<IBoundedGeometry<VectorT, ?>, Double>(geom, new Double(weight)));
   }

}
