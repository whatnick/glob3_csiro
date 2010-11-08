package es.igosoftware.util;


public final class GIntHolder {
   private int _value;


   public GIntHolder(final int value) {
      _value = value;
   }


   public void clear() {
      set(0);
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
      final GIntHolder other = (GIntHolder) obj;
      if (_value != other._value) {
         return false;
      }
      return true;
   }


   public int get() {
      return _value;
   }


   public int getAndIncrement() {
      return _value++;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _value;
      return result;
   }


   public void increment() {
      _value++;
   }


   public void increment(final int delta) {
      _value += delta;
   }


   public int incrementAndGet() {
      return ++_value;
   }


   public void set(final int value) {
      _value = value;
   }


   @Override
   public String toString() {
      return "GIntHolder [" + valueToString() + "]";
   }


   public String valueToString() {
      return Integer.toString(_value);
   }

}
