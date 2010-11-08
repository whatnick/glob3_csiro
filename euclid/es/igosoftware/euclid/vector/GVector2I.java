package es.igosoftware.euclid.vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;

public class GVector2I
         implements
            IVectorI2<GVector2I> {

   private static final long     serialVersionUID   = 1L;


   public static final GVector2I UNIT               = new GVector2I(1, 1);
   public static final GVector2I ZERO               = new GVector2I(0, 0);

   public static final GVector2I NEGATIVE_MIN_VALUE = new GVector2I(Integer.MIN_VALUE, Integer.MIN_VALUE);
   public static final GVector2I POSITIVE_MAX_VALUE = new GVector2I(Integer.MAX_VALUE, Integer.MAX_VALUE);


   public static final GVector2I X_UP               = new GVector2I(1, 0);
   public static final GVector2I Y_UP               = new GVector2I(0, 1);
   public static final GVector2I X_DOWN             = new GVector2I(-1, 0);
   public static final GVector2I Y_DOWN             = new GVector2I(0, -1);


   public final IVectorI2<?> load(final DataInputStream input) throws IOException {
      final int x = input.readInt();
      final int y = input.readInt();
      return new GVector2I(x, y);
   }


   public final void save(final DataOutputStream output) throws IOException {
      output.writeInt(_x);
      output.writeInt(_y);
   }


   public final int _x;
   public final int _y;


   public GVector2I(final int x,
                    final int y) {
      GAssert.notNan(x, "x");
      GAssert.notNan(y, "y");
      _x = x;
      _y = y;
   }


   @Override
   public final int x() {

      return _x;
   }


   @Override
   public final int y() {

      return _y;
   }


   @Override
   public GVector2I scale(final GVector2I that) {
      return new GVector2I(_x * that.x(), _y * that.y());
   }


   @Override
   public GVector2I scale(final int scale) {
      return new GVector2I(_x * scale, _y * scale);
   }


   @Override
   public GVector2I add(final GVector2I that) {
      return new GVector2I(_x + that.x(), _y + that.y());
   }


   @Override
   public GVector2I add(final int delta) {
      return new GVector2I(_x + delta, _y + delta);
   }


   @Override
   public GVector2I sub(final GVector2I that) {
      return new GVector2I(_x - that.x(), _y - that.y());
   }


   @Override
   public GVector2I sub(final int delta) {
      return new GVector2I(_x - delta, _y - delta);
   }


   @Override
   public String asParseableString() {
      return Integer.toString(_x) + "," + Integer.toString(_y);
   }


   @Override
   public final String toString() {
      return "(" + _x + ", " + _y + ")";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _x;
      result = prime * result + _y;
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
      final GVector2I other = (GVector2I) obj;
      if (_x != other._x) {
         return false;
      }
      if (_y != other._y) {
         return false;
      }
      return true;
   }


   @Override
   public boolean between(final GVector2I min,
                          final GVector2I max) {
      return GMath.between(_x, min.x(), max.x()) && GMath.between(_y, min.y(), max.y());
   }


   @Override
   public boolean greaterOrEquals(final GVector2I that) {
      return GMath.greaterOrEquals(_x, that.x()) && GMath.greaterOrEquals(_y, that.y());
   }


   @Override
   public boolean lessOrEquals(final GVector2I that) {
      return GMath.lessOrEquals(_x, that.x()) && GMath.greaterOrEquals(_y, that.y());
   }


   @Override
   public byte dimensions() {
      return 2;
   }


   @Override
   public int get(final byte i) {
      switch (i) {
         case 0:
            return _x;
         case 1:
            return _y;
         default:
            throw new IndexOutOfBoundsException("" + i);
      }
   }


   @Override
   public int[] getCoordinates() {
      return new int[] { _x, _y };
   }


}
