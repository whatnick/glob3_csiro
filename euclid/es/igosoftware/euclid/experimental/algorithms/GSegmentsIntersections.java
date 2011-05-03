

package es.igosoftware.euclid.experimental.algorithms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.utils.GGeometry2DRenderer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GStringUtils;


public class GSegmentsIntersections {

   private GSegmentsIntersections() {

   }


   private static IVector2 getSegmentSegmentIntersection(final GSegment2D segment1,
                                                         final GSegment2D segment2) {

      final IVector2 begin = segment1._from;
      final IVector2 end = segment1._to;
      final IVector2 anotherBegin = segment2._from;
      final IVector2 anotherEnd = segment2._to;

      final double denominator = ((anotherEnd.y() - anotherBegin.y()) * (end.x() - begin.x()))
                                 - ((anotherEnd.x() - anotherBegin.x()) * (end.y() - begin.y()));

      final double numeratorA = ((anotherEnd.x() - anotherBegin.x()) * (begin.y() - anotherBegin.y()))
                                - ((anotherEnd.y() - anotherBegin.y()) * (begin.x() - anotherBegin.x()));

      final double numeratorB = ((end.x() - begin.x()) * (begin.y() - anotherBegin.y()))
                                - ((end.y() - begin.y()) * (begin.x() - anotherBegin.x()));

      if (denominator == 0.0) {
         if ((numeratorA == 0.0) && (numeratorB == 0.0)) {
            return null;
         }

         return null;
      }

      final double ua = numeratorA / denominator;
      final double ub = numeratorB / denominator;

      if ((ua >= 0.0) && (ua <= 1.0) && (ub >= 0.0) && (ub <= 1.0)) {
         // Get the intersection point.
         final double x = begin.x() + ua * (end.x() - begin.x());
         final double y = begin.y() + ua * (end.y() - begin.y());

         return new GVector2D(x, y);
      }

      return null;
   }


   public static Map<IVector2, Set<GSegment2D>> getIntersections(final List<GSegment2D> segments) {
      System.out.println("- finding intersections...");
      final long start = System.currentTimeMillis();

      final Map<IVector2, Set<GSegment2D>> result = getIntersectionsBruteForce(segments);

      System.out.println("- found " + result.size() + " intersections, in " + segments.size() + " segments, in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      return result;
   }


   private static Map<IVector2, Set<GSegment2D>> getIntersectionsBruteForce(final List<GSegment2D> segments) {
      final Map<IVector2, Set<GSegment2D>> result = new HashMap<IVector2, Set<GSegment2D>>();

      final int segmentsCount = segments.size();

      for (int i = 0; i < segmentsCount; i++) {
         final GSegment2D segmentI = segments.get(i);

         for (int j = i + 1; j < segmentsCount; j++) {
            final GSegment2D segmentJ = segments.get(j);

            final IVector2 intersectionPoint = getSegmentSegmentIntersection(segmentI, segmentJ);
            if (intersectionPoint != null) {
               Set<GSegment2D> intersections = result.get(intersectionPoint);
               if (intersections == null) {
                  intersections = new HashSet<GSegment2D>();
                  result.put(intersectionPoint, intersections);
               }
               intersections.add(segmentI);
               intersections.add(segmentJ);
            }
         }
      }

      return result;
   }


   private static void drawResults(final IVectorI2 extent,
                                   final List<GSegment2D> segments,
                                   final Map<IVector2, Set<GSegment2D>> intersections) throws IOException {
      final Collection<IGeometry2D> allGeometries = new ArrayList<IGeometry2D>(segments.size() + intersections.size());
      allGeometries.addAll(segments);
      allGeometries.addAll(intersections.keySet());

      // release some memory
      segments.clear();
      intersections.clear();

      System.out.println("- drawing...");
      final BufferedImage image = GGeometry2DRenderer.render(allGeometries, true, new GAxisAlignedRectangle(GVector2D.ZERO,
               new GVector2D(extent.x(), extent.y())), extent);

      ImageIO.write(image, "png", new File("/home/dgd/Desktop/GSegmentsIntersections.png"));
   }


   private static List<GSegment2D> createRandomSegments(final Random random,
                                                        final IVectorI2 extent,
                                                        final int segmentsCount) {
      System.out.println("- creating " + segmentsCount + " random segments...");

      final List<GSegment2D> segments = new ArrayList<GSegment2D>();

      for (int i = 0; i < segmentsCount; i++) {
         final GVector2D randomFrom = new GVector2D(random.nextDouble() * extent.x(), random.nextDouble() * extent.y());
         final GVector2D randomTo = new GVector2D(random.nextDouble() * extent.x(), random.nextDouble() * extent.y());

         final GSegment2D randomSegment = new GSegment2D(randomFrom, randomTo);
         segments.add(randomSegment);
      }

      return segments;
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GSegmentsIntersections 0.1");
      System.out.println("--------------------------\n");


      final boolean drawResults = false;
      final int segmentsCount = 5000;
      final IVectorI2 extent = new GVector2I(1024 * 2, 768 * 2);


      final Random random = new Random(0);
      final List<GSegment2D> segments = createRandomSegments(random, extent, segmentsCount);

      final Map<IVector2, Set<GSegment2D>> intersections = GSegmentsIntersections.getIntersections(segments);

      if (drawResults) {
         drawResults(extent, segments, intersections);
      }

      System.out.println("- done!");
   }
}
