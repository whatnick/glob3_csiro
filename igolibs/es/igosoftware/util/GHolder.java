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

}
