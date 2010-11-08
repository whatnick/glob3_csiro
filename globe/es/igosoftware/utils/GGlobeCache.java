package es.igosoftware.utils;

import es.igosoftware.util.GMath;
import gov.nasa.worldwind.globes.Globe;

import java.util.HashMap;
import java.util.Map;


/**
 * An utility class to implements cache per globe/verticalExaggeration changes.
 * 
 * @param <KeyT>
 * @param <ValueT>
 */
public class GGlobeCache<KeyT, ValueT> {

   private static class Entry<ValueT> {
      private final Globe  _globe;
      private final double _verticalExaggeration;
      private final ValueT _value;


      private Entry(final Globe globe,
                    final double verticalExaggeration,
                    final ValueT value) {
         _globe = globe;
         _verticalExaggeration = verticalExaggeration;
         _value = value;
      }
   }


   public static interface Factory<KeyT, ValueT> {
      public ValueT create(final KeyT key,
                           final Globe globe,
                           final double verticalExaggeration);
   }


   private final GGlobeCache.Factory<KeyT, ValueT>    _factory;
   private final Map<KeyT, GGlobeCache.Entry<ValueT>> _values;


   public GGlobeCache(final GGlobeCache.Factory<KeyT, ValueT> factory) {
      _factory = factory;
      _values = new HashMap<KeyT, GGlobeCache.Entry<ValueT>>();
   }


   public ValueT get(final KeyT key,
                     final Globe globe,
                     final double verticalExaggeration) {
      final GGlobeCache.Entry<ValueT> entry = _values.get(key);
      if ((entry != null) && (entry._globe == globe) && (GMath.closeTo(entry._verticalExaggeration, verticalExaggeration))) {
         // cache hit
         return entry._value;
      }

      final ValueT newValue = _factory.create(key, globe, verticalExaggeration);
      final GGlobeCache.Entry<ValueT> newEntry = new GGlobeCache.Entry<ValueT>(globe, verticalExaggeration, newValue);
      _values.put(key, newEntry);
      return newValue;
   }

}
