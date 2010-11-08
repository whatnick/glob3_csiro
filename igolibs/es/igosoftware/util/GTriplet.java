package es.igosoftware.util;

public class GTriplet<T1, T2, T3>
         extends
            GPair<T1, T2> {

   private static final long serialVersionUID = 1L;


   public final T3           _third;


   public GTriplet(final T1 first,
                   final T2 second,
                   final T3 third) {
      super(first, second);
      _third = third;
   }


   public T3 getThird() {
      return _third;
   }


   @Override
   public String toString() {
      return "[" + _first + ", " + _second + ", " + _third + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((_third == null) ? 0 : _third.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GTriplet<?, ?, ?> other = (GTriplet<?, ?, ?>) obj;
      if (_third == null) {
         if (other._third != null) {
            return false;
         }
      }
      else if (!_third.equals(other._third)) {
         return false;
      }
      return true;
   }

}
