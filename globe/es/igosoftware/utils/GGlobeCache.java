/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


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
