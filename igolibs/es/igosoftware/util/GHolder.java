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

import java.util.Arrays;


public final class GHolder<T> {
   private T _value;


   public GHolder(final T value) {
      _value = value;
   }


   public void clear() {
      set(null);
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GHolder<?> other = (GHolder<?>) obj;
      if (_value == null) {
         if (other._value != null) {
            return false;
         }
      }
      else if (!_value.equals(other._value)) {
         return false;
      }
      return true;
   }


   public T get() {
      return _value;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      return prime + ((_value == null) ? 0 : _value.hashCode());
   }


   public void set(final T value) {
      _value = value;
   }


   @Override
   public String toString() {
      return "GHolder [" + valueToString() + "]";
   }


   public String valueToString() {
      if (_value == null) {
         return "null";
      }

      if (_value instanceof Object[]) {
         return Arrays.toString((Object[]) _value);
      }

      return _value.toString();
   }


   public boolean isEmpty() {
      return (_value == null);
   }


   public boolean hasValue() {
      return (_value != null);
   }

}
