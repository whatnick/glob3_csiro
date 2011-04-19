

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Arrays;


public class GAWTPoints {
   final int[] _xPoints;
   final int[] _yPoints;


   GAWTPoints(final int[] xPoints,
              final int[] yPoints) {
      _xPoints = xPoints;
      _yPoints = yPoints;
   }


   @Override
   public String toString() {
      return "Points [_xPoints=" + Arrays.toString(_xPoints) + ", _yPoints=" + Arrays.toString(_yPoints) + "]";
   }


   Shape asShape() {
      return new Polygon(_xPoints, _yPoints, _xPoints.length);
   }


   Area asArea() {
      return new Area(asShape());
   }
}
