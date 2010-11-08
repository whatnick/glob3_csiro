/*
 * Cáceres 3D
 * 
 * Copyright (c) 2008 Junta de Extremadura.
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Funded by European Union. FEDER program.
 * Developed by: IGO SOFTWARE, S.L.
 * 
 * For more information, contact: 
 * 
 *    Junta de Extremadura
 *    Consejería de Cultura y Turismo
 *    C/ Almendralejo 14 Mérida
 *    06800 Badajoz
 *    SPAIN
 * 
 *    Tel: +34 924007009
 *    http://www.culturaextremadura.com
 * 
 *   or
 * 
 *    IGO SOFTWARE, S.L.
 *    Calle Santiago Caldera Nro 4
 *    Cáceres
 *    Spain
 *    Tel: +34 927 629 436
 *    e-mail: support@igosoftware.es
 *    http://www.igosoftware.es
 */

package es.igosoftware.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class LRUCache<KeyT, ValueT, ExceptionT extends Exception> {
   private static final long serialVersionUID = 1L;


   public static interface ValueFactory<KeyT, ValueT, ExceptionT extends Exception> {
      public ValueT create(final KeyT key) throws ExceptionT;
   }


   public static class Entry<KeyT, ValueT, ExceptionT extends Exception> {
      private static final long serialVersionUID = 1L;

      private final KeyT        _key;
      private final ValueT      _value;
      private final ExceptionT  _exception;


      private Entry(final KeyT key,
                    final ValueT value,
                    final ExceptionT exception) {
         _key = key;
         _value = value;
         _exception = exception;
      }


      @Override
      public String toString() {
         if (_exception != null) {
            return "[" + _key + " -> Exception: " + _exception + "]";
         }
         return "[" + _key + " -> " + _value + "]";
      }


      public KeyT getKey() {
         return _key;
      }


      @SuppressWarnings("cast")
      public ValueT getValue() throws ExceptionT {
         if (_exception != null) {
            throw (ExceptionT) _exception;
         }
         return _value;
      }


      public ExceptionT getException() {
         return _exception;
      }
   }


   public static interface SizePolicy<KeyT, ValueT, ExceptionT extends Exception> {
      public boolean isOversized(final List<LRUCache.Entry<KeyT, ValueT, ExceptionT>> entries);
   }


   private final static class DefaultSizePolicy<KeyT, ValueT, ExceptionT extends Exception>
            implements
               SizePolicy<KeyT, ValueT, ExceptionT> {
      private static final long serialVersionUID = 1L;

      private final int         _maximumSize;


      private DefaultSizePolicy(final int maximumSize) {
         _maximumSize = maximumSize;
      }


      @Override
      public boolean isOversized(final List<Entry<KeyT, ValueT, ExceptionT>> entries) {
         return entries.size() > _maximumSize;
      }

   }


   private final SizePolicy<KeyT, ValueT, ExceptionT>                 _sizePolicy;
   private final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT>      _factory;

   private final LinkedList<LRUCache.Entry<KeyT, ValueT, ExceptionT>> _entries;

   private int                                                        _hits   = 0;
   private int                                                        _misses = 0;


   public LRUCache(final int maximumSize,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory) {
      this(maximumSize, factory, null);
   }


   public LRUCache(final int maximumSize,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final List<GTriplet<KeyT, ValueT, ExceptionT>> initialValues) {
      this(new LRUCache.DefaultSizePolicy<KeyT, ValueT, ExceptionT>(maximumSize), factory, initialValues);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory) {
      this(sizePolicy, factory, null);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final List<GTriplet<KeyT, ValueT, ExceptionT>> initialValues) {
      _sizePolicy = sizePolicy;
      _factory = factory;

      if (initialValues == null) {
         _entries = new LinkedList<LRUCache.Entry<KeyT, ValueT, ExceptionT>>();
      }
      else {
         _entries = new LinkedList<Entry<KeyT, ValueT, ExceptionT>>();
         for (final GTriplet<KeyT, ValueT, ExceptionT> initialValue : initialValues) {
            _entries.add(new LRUCache.Entry<KeyT, ValueT, ExceptionT>(initialValue._first, initialValue._second,
                     initialValue._third));
         }
      }
   }


   @SuppressWarnings("unchecked")
   public synchronized ValueT get(final KeyT key) throws ExceptionT {
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         if (entry != null) {
            if (entry._key.equals(key)) {
               _entries.remove(entry);
               _entries.addFirst(entry);
               _hits++;
               return entry.getValue();
            }
         }
      }

      _misses++;

      ValueT value = null;
      ExceptionT exception = null;
      try {
         value = _factory.create(key);
      }
      catch (final Exception e) {
         exception = (ExceptionT) e;

      }

      final LRUCache.Entry<KeyT, ValueT, ExceptionT> newEntry = new LRUCache.Entry<KeyT, ValueT, ExceptionT>(key, value,
               exception);

      while (_sizePolicy.isOversized(Collections.unmodifiableList(_entries))) {
         _entries.removeLast();
      }

      _entries.addFirst(newEntry);

      return newEntry.getValue();
   }


   public int getCallsCount() {
      return (_hits + _misses);
   }


   public double getHitsRatio() {
      return (double) _hits / getCallsCount();
   }


   public int getHitsCount() {
      return _hits;
   }


   public List<GTriplet<KeyT, ValueT, ExceptionT>> getValues() {
      final List<GTriplet<KeyT, ValueT, ExceptionT>> result = new ArrayList<GTriplet<KeyT, ValueT, ExceptionT>>(_entries.size());

      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         result.add(new GTriplet<KeyT, ValueT, ExceptionT>(entry._key, entry._value, entry._exception));
      }

      return result;
   }

}
