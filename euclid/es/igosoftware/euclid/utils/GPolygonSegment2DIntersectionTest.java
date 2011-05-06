

package es.igosoftware.euclid.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVectorI2;


public class GPolygonSegment2DIntersectionTest {


   public static void main(final String[] args) throws IOException {
      System.out.println("Polygon Segment Intersections Test");
      System.out.println("----------------------------------\n");

      final ISimplePolygon2D hull = new GSimplePolygon2D(true, new GVector2D(10, 10), new GVector2D(90, 10),
               new GVector2D(90, 90), new GVector2D(10, 90));

      final ISimplePolygon2D hole = new GSimplePolygon2D(true, new GVector2D(30, 30), new GVector2D(70, 30),
               new GVector2D(70, 70), new GVector2D(30, 70));

      final IPolygon2D polygon = new GComplexPolygon2D(hull, Collections.singletonList(hole));
      //      final IPolygon2D polygon = hull;

      final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(GVector2D.ZERO, new GVector2D(100, 100));
      final IVectorI2 imageSize = new GVector2I(1024, 768);
      final GGeometry2DRenderer renderer = new GGeometry2DRenderer(bounds, imageSize);

      renderer.drawGeometry(polygon, new Color(0, 255, 0, 64), false);

      processSegment(new GSegment2D(new GVector2D(5, 15), new GVector2D(75, 95)), polygon, renderer);

      processSegment(new GSegment2D(new GVector2D(25, 55), new GVector2D(50, 95)), polygon, renderer);

      processSegment(new GSegment2D(new GVector2D(35, 65), new GVector2D(55, 75)), polygon, renderer);

      processSegment(new GSegment2D(new GVector2D(85, 65), new GVector2D(75, 80)), polygon, renderer);

      processSegment(bounds.getHorizontalBisector(), polygon, renderer);
      processSegment(bounds.getVerticalBisector(), polygon, renderer);

      processSegment(new GSegment2D(bounds._lower, bounds._upper), polygon, renderer);

      ImageIO.write(renderer.getImage(), "png", new File("/home/dgd/Desktop/PolygonSegmentIntersections.png"));

      System.out.println("- done!");
   }


   private static void processSegment(final GSegment2D segment,
                                      final IPolygon2D polygon,
                                      final GGeometry2DRenderer renderer) {
      renderer.drawGeometry(segment, Color.RED, false);
      //      labelSegmentVertices(renderer, segment, Color.RED);

      final List<GSegment2D> intersections = polygon.getIntersections(segment);
      renderer.drawGeometries(intersections, true);

      //      for (final GSegment2D intersection : intersections) {
      //         labelSegmentVertices(renderer, intersection, Color.WHITE);
      //      }
   }


   //   private static void labelSegmentVertices(final GGeometry2DRenderer renderer,
   //                                            final GSegment2D segment,
   //                                            final Color color) {
   //      renderer.drawString(segment._from.toString(), segment._from, color);
   //      renderer.drawString(segment._to.toString(), segment._to, color);
   //   }


}
