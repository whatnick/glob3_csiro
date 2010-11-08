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
