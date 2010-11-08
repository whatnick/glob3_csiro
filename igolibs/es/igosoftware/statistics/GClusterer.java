package es.igosoftware.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GClusterer {

   private GClusterer() {}

   public interface NeighborhoodCalculator<T> {
      public Collection<T> getNeighborhood(final T value);
   }


   public static <T> Collection<Set<T>> getClusters(final Collection<T> values,
                                                    final NeighborhoodCalculator<T> neighborhoodCalculator) {

      final Collection<Set<T>> clusters = new ArrayList<Set<T>>();

      final Set<T> processed = new HashSet<T>(values.size());
      for (final T value : values) {
         if (processed.contains(value)) {
            continue;
         }

         // data not processed, create a new cluster starting from this
         final Set<T> cluster = new HashSet<T>();
         clusters.add(cluster);

         final LinkedList<T> toProcess = new LinkedList<T>();
         toProcess.add(value);

         while (!toProcess.isEmpty()) {
            final T valueInProcess = toProcess.removeFirst();

            if (!processed.contains(valueInProcess)) {
               processed.add(valueInProcess);

               cluster.add(valueInProcess);

               toProcess.addAll(neighborhoodCalculator.getNeighborhood(valueInProcess));
            }
         }

      }

      return clusters;
   }
}
