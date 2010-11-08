package es.igosoftware.euclid.vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;

public class GVector2S
         implements
            IVectorI2<GVector2S> {

   private static final long     serialVersionUID   = 1L;


   public static final GVector2S UNIT               = new GVector2S(1, 1);
   public static final GVector2S ZERO               = new GVector2S(0, 0);

   public static final GVector2S NEGATIVE_MIN_VALUE = new GVector2S(Short.MIN_VALUE, Short.MIN_VALUE);
   public static final GVector2S POSITIVE_MAX_VALUE = new GVector2S(Short.MAX_VALUE, Short.MAX_VALUE);


   public static final GVector2S X_UP               = new GVector2S(1, 0);
   public static final GVector2S Y_UP               = new GVector2S(0, 1);
   public static final GVector2S X_DOWN             = new GVector2S(-1, 0);
   public static final GVector2S Y_DOWN             = new GVector2S(0, -1);


   public final IVectorI2<?> load(final DataInputStream input) throws IOException {
      final short x = input.readShort();
      final short y = input.readShort();
      return new GVector2S(x, y);
   }


   public final void save(final DataOutputStream output) throws IOException {
      output.writeShort(_x);
      output.writeShort(_y);
   }

   public final short _x;
   public final short _y;


   public GVector2S(final short x,
                    final short y) {
      GAssert.notNan(x, "x");
      GAssert.notNan(y, "y");
      _x = x;
      _y = y;
   }


   public GVector2S(final int x,
                    final int y) {
      this((short) x, (short) y);
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
   public GVector2S scale(final GVector2S that) {
      return new GVector2S(_x * that.x(), _y * that.y());
   }


   @Override
   public GVector2S scale(final int scale) {
      return new GVector2S(_x * scale, _y * scale);
   }


   @Override
   public GVector2S add(final GVector2S that) {
      return new GVector2S(_x + that.x(), _y + that.y());
   }


   @Override
   public GVector2S add(final int delta) {
      return new GVector2S(_x + delta, _y + delta);
   }


   @Override
   public GVector2S sub(final GVector2S that) {
      return new GVector2S(_x - that.x(), _y - that.y());
   }


   @Override
   public GVector2S sub(final int delta) {
      return new GVector2S(_x - delta, _y - delta);
   }


   @Override
   public String asParseableString() {
      return Short.toString(_x) + "," + Short.toString(_y);
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
      final GVector2S other = (GVector2S) obj;
      if (_x != other._x) {
         return false;
      }
      if (_y != other._y) {
         return false;
      }
      return true;
   }


   @Override
   public boolean between(final GVector2S min,
                          final GVector2S max) {
      return GMath.between(_x, min.x(), max.x()) && GMath.between(_y, min.y(), max.y());
   }


   @Override
   public boolean greaterOrEquals(final GVector2S that) {
      return GMath.greaterOrEquals(_x, that.x()) && GMath.greaterOrEquals(_y, that.y());
   }


   @Override
   public boolean lessOrEquals(final GVector2S that) {
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
