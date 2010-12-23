

package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.GeometryType;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GProgress;


public class GShapeLoader {


   private static final class GProgressListenerAdapter
            extends
               GProgress
            implements
               ProgressListener {


      private static final int FACTOR = 10000;

      private final String     _taskName;
      private int              _currentSteps;


      private GProgressListenerAdapter(final String taskName) {
         super(100 * FACTOR);
         _taskName = taskName;
      }


      @Override
      public InternationalString getTask() {
         return null;
      }


      @Override
      public String getDescription() {
         return null;
      }


      @Override
      public void setTask(final InternationalString task) {
      }


      @Override
      public void setDescription(final String description) {
      }


      @Override
      public void started() {
         //System.out.println("started");
         _currentSteps = 0;
      }


      @Override
      public void progress(final float percent) {
         //System.out.println("  percent: " + GMath.roundTo(Math.round(10000f * percent) / 100f, 2) + "%");
         final int currentTotalSteps = Math.round(percent * FACTOR);
         stepsDone(currentTotalSteps - _currentSteps);
         _currentSteps = currentTotalSteps;
      }


      @Override
      public float getProgress() {
         return 0;
      }


      @Override
      public void complete() {
         final int currentTotalSteps = (100 * FACTOR);
         stepsDone(currentTotalSteps - _currentSteps);
         _currentSteps = currentTotalSteps;
      }


      @Override
      public void dispose() {
         System.out.println("dispose");
      }


      @Override
      public boolean isCanceled() {
         return false;
      }


      @Override
      public void setCanceled(final boolean cancel) {
      }


      @Override
      public void warningOccurred(final String source,
                                  final String location,
                                  final String warning) {
         System.out.println("WARNING: source=" + source + ", location=" + location + ", warning=" + warning);
      }


      @Override
      public void exceptionOccurred(final Throwable exception) {
         exception.printStackTrace();
      }


      @Override
      public void informProgress(final double percent,
                                 final long elapsed,
                                 final long estimatedMsToFinish) {
         System.out.println(_taskName + ": " + progressString(percent, elapsed, estimatedMsToFinish));
      }
   }


   private static List<IVector2<?>> convert(final Coordinate[] coordinates,
                                            final GProjection projection) {
      final List<IVector2<?>> result = new ArrayList<IVector2<?>>(coordinates.length);

      for (final Coordinate coordinate : coordinates) {
         if (projection.isLatLong()) {
            result.add(new GVector2D(Math.toRadians(coordinate.x), Math.toRadians(coordinate.y)));
         }
         else {
            result.add(new GVector2D(coordinate.x, coordinate.y).reproject(projection, GProjection.EPSG_4326));
         }
      }

      return result;
   }


   private static List<IVector2<?>> removeLastIfRepeated(final List<IVector2<?>> points) {
      if (points.size() < 2) {
         return points;
      }

      final IVector2<?> first = points.get(0);
      final int lastIndex = points.size() - 1;
      final IVector2<?> last = points.get(lastIndex);
      if (first.closeTo(last)) {
         return points.subList(0, lastIndex - 1);
      }

      return points;
   }


   private static List<IVector2<?>> removeConsecutiveEqualsPoints(final List<IVector2<?>> points) {
      final int pointsCount = points.size();
      final ArrayList<IVector2<?>> result = new ArrayList<IVector2<?>>(pointsCount);

      for (int i = 0; i < pointsCount; i++) {
         final IVector2<?> current = points.get(i);
         final IVector2<?> next = points.get((i + 1) % pointsCount);
         if (!current.closeTo(next)) {
            result.add(current);
         }
      }

      result.trimToSize();
      return result;
   }


   public static GPair<String, List<IPolygon2D<?>>> readPolygons(final String fileName,
                                                                 final GProjection projection) throws IOException {
      final File file = new File(fileName);
      if (!file.exists()) {
         throw new IOException("File not found!");
      }


      final FileDataStore store = FileDataStoreFinder.getDataStore(file);

      // final FeatureSource featureSource = new CachingFeatureSource(store.getFeatureSource());
      final FeatureSource featureSource = store.getFeatureSource();

      final FeatureCollection features = featureSource.getFeatures();

      final GIntHolder validCounter = new GIntHolder(0);
      final GIntHolder polygonsWithHolesCounter = new GIntHolder(0);
      final GIntHolder invalidCounter = new GIntHolder(0);
      //      final GIntHolder validVerticesCounter = new GIntHolder(0);

      final ArrayList<IPolygon2D<?>> euclidPolygons = new ArrayList<IPolygon2D<?>>(features.size());

      features.accepts(new FeatureVisitor() {
         @Override
         public void visit(final Feature feature) {
            final GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();

            final GeometryType type = geometryAttribute.getType();

            if (type.getBinding() == com.vividsolutions.jts.geom.MultiPolygon.class) {

               final com.vividsolutions.jts.geom.MultiPolygon multipolygon = (com.vividsolutions.jts.geom.MultiPolygon) geometryAttribute.getValue();
               final int geometriesCount = multipolygon.getNumGeometries();

               for (int i = 0; i < geometriesCount; i++) {
                  final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) multipolygon.getGeometryN(i);

                  try {
                     final IPolygon2D<?> outerEuclidPolygon = createPolygon(jtsPolygon.getCoordinates(), projection);

                     final int holesCount = jtsPolygon.getNumInteriorRing();
                     if (holesCount == 0) {
                        euclidPolygons.add(outerEuclidPolygon);
                     }
                     else {

                        final List<IPolygon2D<?>> euclidHoles = new ArrayList<IPolygon2D<?>>(holesCount);
                        for (int j = 0; j < holesCount; j++) {
                           final LineString jtsHole = jtsPolygon.getInteriorRingN(j);

                           try {
                              final IPolygon2D<?> euclidHole = createPolygon(jtsHole.getCoordinates(), projection);
                              euclidHoles.add(euclidHole);
                           }
                           catch (final IllegalArgumentException e) {
                              //                              System.err.println(e.getMessage());
                           }
                        }

                        final IPolygon2D<?> euclidPolygon;
                        if (euclidHoles.isEmpty()) {
                           euclidPolygon = outerEuclidPolygon;
                        }
                        else {
                           euclidPolygon = new GComplexPolygon2D(outerEuclidPolygon, euclidHoles);
                        }
                        euclidPolygons.add(euclidPolygon);

                        polygonsWithHolesCounter.increment();
                        // System.out.println("Found polygon with " + holesCount + " holes");
                     }
                  }
                  catch (final IllegalArgumentException e) {
                     //                     System.err.println(e.getMessage());
                  }
               }

               validCounter.increment();
            }
            else if (type.getBinding() == com.vividsolutions.jts.geom.MultiLineString.class) {

               final com.vividsolutions.jts.geom.MultiLineString multiline = (com.vividsolutions.jts.geom.MultiLineString) geometryAttribute.getValue();
               final int geometriesCount = multiline.getNumGeometries();

               for (int i = 0; i < geometriesCount; i++) {
                  final com.vividsolutions.jts.geom.LineString jtsPolygon = (com.vividsolutions.jts.geom.LineString) multiline.getGeometryN(i);

                  try {
                     final IPolygon2D<?> euclidLines = createLine(jtsPolygon.getCoordinates(), projection);

                     euclidPolygons.add(euclidLines);
                  }
                  catch (final IllegalArgumentException e) {
                     //                     System.err.println(e.getMessage());
                  }
               }

               validCounter.increment();
            }
            else {
               invalidCounter.increment();
               System.out.println("invalid type: " + type);
            }
         }


      }, new GProgressListenerAdapter("Loading \"" + fileName + "\""));

      store.dispose();

      euclidPolygons.trimToSize();

      System.out.println();
      System.out.println("Features: " + features.size());


      System.out.println();
      System.out.println("Read " + validCounter.get() + " valid geometries");
      //      System.out.println("Valid Vertices: " + validVerticesCounter.get());
      System.out.println("Polygons with holes: " + polygonsWithHolesCounter.get());
      if (invalidCounter.get() > 0) {
         System.out.println("Ignored " + invalidCounter.get() + " invalid geometries");
      }

      System.out.println();


      final String uniqueName = file.getName() + Long.toHexString(file.lastModified()) + Long.toHexString(file.length());

      return new GPair<String, List<IPolygon2D<?>>>(uniqueName, euclidPolygons);
   }


   private static IPolygon2D<?> createPolygon(final Coordinate[] jtsCoordinates,
                                              final GProjection projection) {
      final List<IVector2<?>> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createPolygon2(false, points);
   }


   private static IPolygon2D<?> createLine(final Coordinate[] jtsCoordinates,
                                           final GProjection projection) {
      final List<IVector2<?>> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createLine2(false, points);

   }


   //   public static void main(final String[] args) throws IOException {
   //      System.out.println("Shape Loader 0.1");
   //      System.out.println("----------------\n");
   //
   //      System.out.println("GeoTools version: " + GeoTools.getVersion() + "\n");
   //
   //
   //      //      final String fileName = "data/parcelasEdificadas.shp";
   //      //      final boolean convertToRadians = false;
   //
   //      //      final String fileName = "data/S_Naturales_forestales.shp";
   //      //      final boolean convertToRadians = false;
   //
   //
   //      final String fileName = "/home/dgd/Escritorio/trastero/cartobrutal/world-modified/world.shp";
   //      final boolean convertToRadians = true;
   //
   //      final List<IPolygon2D<?>> polygons = readPolygons(fileName, convertToRadians);
   //
   //      //      System.out.println(">>>>>>>>>> CONNECT PROFILER");
   //      //      GUtils.delay(20 * 1000);
   //
   //
   //      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(polygons);
   //
   //
   //      final GPolygon2DRenderer renderer = new GPolygon2DRenderer(polygons);
   //
   //
   //      final GAxisAlignedRectangle region = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(polygonsBounds),
   //               polygonsBounds._center));
   //      final String directoryName = "render";
   //      final boolean renderLODIgnores = true;
   //      final float borderWidth = 0.0001f;
   //      final Color fillColor = new Color(borderWidth, borderWidth, 1, 0.75f);
   //      final Color borderColor = Color.BLACK;
   //      final double lodMinSize = 5;
   //      final boolean debugLODRendering = true;
   //      final int textureDimension = 256;
   //      final boolean renderBounds = false;
   //
   //      final IVector2<?> extent = region.getExtent();
   //
   //      final int textureWidth;
   //      final int textureHeight;
   //
   //      if (extent.x() > extent.y()) {
   //         textureHeight = textureDimension;
   //         textureWidth = (int) Math.round(extent.x() / extent.y() * textureDimension);
   //      }
   //      else {
   //         textureWidth = textureDimension;
   //         textureHeight = (int) Math.round(extent.y() / extent.x() * textureDimension);
   //      }
   //
   //      final GRenderingAttributes attributes = new GRenderingAttributes(renderLODIgnores, borderWidth, fillColor, borderColor,
   //               lodMinSize, debugLODRendering, textureWidth, textureHeight, renderBounds);
   //
   //
   //      GIOUtils.assureEmptyDirectory(directoryName, false);
   //
   //
   //      final int maxDepth = 2;
   //      render(renderer, region, directoryName, attributes, maxDepth);
   //
   //   }
   //
   //
   //   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> centerBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds,
   //                                                                                                       final VectorT center) {
   //      final VectorT delta = bounds.getCenter().sub(center);
   //      return bounds.translatedBy(delta.negated());
   //   }
   //
   //
   //   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> multipleOfSmallestDimention(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
   //      final VectorT extent = bounds._extent;
   //
   //      double smallestExtension = Double.POSITIVE_INFINITY;
   //      for (byte i = 0; i < bounds.dimensions(); i++) {
   //         final double ext = extent.get(i);
   //         if (ext < smallestExtension) {
   //            smallestExtension = ext;
   //         }
   //      }
   //
   //      final VectorT newExtent = smallestBiggerMultipleOf(extent, smallestExtension);
   //      final VectorT newUpper = bounds._lower.add(newExtent);
   //      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   //   }
   //
   //
   //   @SuppressWarnings("unchecked")
   //   private static <VectorT extends IVector<VectorT, ?>> VectorT smallestBiggerMultipleOf(final VectorT lower,
   //                                                                                         final double smallestExtension) {
   //
   //      final byte dimensionsCount = lower.dimensions();
   //
   //      final double[] dimensionsValues = new double[dimensionsCount];
   //      for (byte i = 0; i < dimensionsCount; i++) {
   //         dimensionsValues[i] = smallestBiggerMultipleOf(lower.get(i), smallestExtension);
   //      }
   //
   //      return (VectorT) GVectorUtils.createD(dimensionsValues);
   //   }
   //
   //
   //   private static double smallestBiggerMultipleOf(final double value,
   //                                                  final double multiple) {
   //      if (GMath.closeTo(value, multiple)) {
   //         return multiple;
   //      }
   //
   //      final int times = (int) (value / multiple);
   //
   //      double result = times * multiple;
   //      if (value < 0) {
   //         if (result > value) {
   //            result -= multiple;
   //         }
   //      }
   //      else {
   //         if (result < value) {
   //            result += multiple;
   //         }
   //      }
   //
   //      return result;
   //   }
   //
   //
   //   private static void render(final GPolygon2DRenderer renderer,
   //                              final GAxisAlignedRectangle region,
   //                              final String directoryName,
   //                              final GRenderingAttributes attributes,
   //                              final int maxDepth) throws IOException {
   //      render(renderer, region, directoryName, attributes, 0, maxDepth);
   //   }
   //
   //
   //   private static void render(final GPolygon2DRenderer renderer,
   //                              final GAxisAlignedRectangle region,
   //                              final String directoryName,
   //                              final GRenderingAttributes attributes,
   //                              final int depth,
   //                              final int maxDepth) throws IOException {
   //
   //      final long start = System.currentTimeMillis();
   //      final BufferedImage renderedImage = renderer.render(region, attributes);
   //
   //      final String imageName = depth + "_" + region.asParseableString();
   //      final File file = new File(directoryName, imageName + ".png");
   //      ImageIO.write(renderedImage, "png", file);
   //
   //      System.out.println(StringUtils.spaces(depth * 2) + "Rendered " + imageName + " in "
   //                         + GUtils.getTimeMessage(System.currentTimeMillis() - start));
   //
   //      if (depth < maxDepth) {
   //         final GAxisAlignedRectangle[] subRegions = region.subdivideAtCenter();
   //         for (final GAxisAlignedRectangle subRegion : subRegions) {
   //            render(renderer, subRegion, directoryName, attributes, depth + 1, maxDepth);
   //         }
   //      }
   //   }

}
