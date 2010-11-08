package es.igosoftware.util;

import java.io.Serializable;


public class GPair<T1, T2>
         implements
            Serializable {
   private static final long serialVersionUID = 1L;


   public final T1           _first;
   public final T2           _second;


   public GPair(final T1 first,
                final T2 second) {
      _first = first;
      _second = second;
   }


   @Override
   public String toString() {
      return "[" + _first + ", " + _second + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_first == null) ? 0 : _first.hashCode());
      result = prime * result + ((_second == null) ? 0 : _second.hashCode());
      return result;
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
      final GPair<?, ?> other = (GPair<?, ?>) obj;
      if (_first == null) {
         if (other._first != null) {
            return false;
         }
      }
      else if (!_first.equals(other._first)) {
         return false;
      }
      if (_second == null) {
         if (other._second != null) {
            return false;
         }
      }
      else if (!_second.equals(other._second)) {
         return false;
      }
      return true;
   }


   public T1 getFirst() {
      return _first;
   }


   public T2 getSecond() {
      return _second;
   }

}
