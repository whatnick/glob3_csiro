/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.shape.ILineal2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;


public class GShapeLoader {


   private static List<IVector2> convert(final Coordinate[] coordinates,
                                         final GProjection projection) {
      final List<IVector2> result = new ArrayList<IVector2>(coordinates.length);

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


   private static List<IVector2> removeLastIfRepeated(final List<IVector2> points) {
      if (points.size() < 2) {
         return points;
      }

      final IVector2 first = points.get(0);
      final int lastIndex = points.size() - 1;
      final IVector2 last = points.get(lastIndex);
      if (first.closeTo(last)) {
         return points.subList(0, lastIndex - 1);
      }

      return points;
   }


   private static List<IVector2> removeConsecutiveEqualsPoints(final List<IVector2> points) {
      final int pointsCount = points.size();
      final ArrayList<IVector2> result = new ArrayList<IVector2>(pointsCount);

      for (int i = 0; i < pointsCount; i++) {
         final IVector2 current = points.get(i);
         final IVector2 next = points.get((i + 1) % pointsCount);
         if (!current.closeTo(next)) {
            result.add(current);
         }
      }

      result.trimToSize();
      return result;
   }


   public static IGlobeFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>, ?> readFeatures(final File file,
                                                                                                                                     final GProjection projection)
                                                                                                                                                                  throws IOException {
      return readFeatures(GFileName.fromFile(file), projection);
   }


   public static IGlobeFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>, ?> readFeatures(final GFileName fileName,
                                                                                                                                     final GProjection projection)
                                                                                                                                                                  throws IOException {
      final File file = fileName.asFile();
      if (!file.exists()) {
         throw new IOException("File not found!");
      }

      final FileDataStore store = FileDataStoreFinder.getDataStore(file);

      // final FeatureSource featureSource = new CachingFeatureSource(store.getFeatureSource());
      final SimpleFeatureSource featureSource = store.getFeatureSource();

      final SimpleFeatureCollection featuresCollection = featureSource.getFeatures();

      final GIntHolder validCounter = new GIntHolder(0);
      final GIntHolder polygonsWithHolesCounter = new GIntHolder(0);
      final GIntHolder invalidCounter = new GIntHolder(0);
      //      final GIntHolder validVerticesCounter = new GIntHolder(0);

      final int featuresCount = featuresCollection.size();
      final ArrayList<IGlobeFeature<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> euclidFeatures = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>(
               featuresCount);


      final GProgress progress = new GProgress(featuresCount) {
         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            System.out.println("Loading \"" + fileName.buildPath() + "\" "
                               + progressString(percent, elapsed, estimatedMsToFinish));
         }
      };

      final FeatureIterator<SimpleFeature> iterator = featuresCollection.features();

      while (iterator.hasNext()) {
         final SimpleFeature feature = iterator.next();

         final GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();

         final GeometryType type = geometryAttribute.getType();

         if (type.getBinding() == com.vividsolutions.jts.geom.MultiPolygon.class) {

            final com.vividsolutions.jts.geom.MultiPolygon multipolygon = (com.vividsolutions.jts.geom.MultiPolygon) geometryAttribute.getValue();
            final int geometriesCount = multipolygon.getNumGeometries();

            for (int i = 0; i < geometriesCount; i++) {
               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) multipolygon.getGeometryN(i);

               try {
                  final IPolygon2D outerEuclidPolygon = createPolygon(jtsPolygon.getCoordinates(), projection);

                  final int holesCount = jtsPolygon.getNumInteriorRing();
                  if (holesCount == 0) {
                     euclidFeatures.add(createFeature(outerEuclidPolygon, feature));
                  }
                  else {

                     final List<IPolygon2D> euclidHoles = new ArrayList<IPolygon2D>(holesCount);
                     for (int j = 0; j < holesCount; j++) {
                        final LineString jtsHole = jtsPolygon.getInteriorRingN(j);

                        try {
                           final IPolygon2D euclidHole = createPolygon(jtsHole.getCoordinates(), projection);
                           euclidHoles.add(euclidHole);
                        }
                        catch (final IllegalArgumentException e) {
                           //                              System.err.println(e.getMessage());
                        }
                     }

                     final IPolygon2D euclidPolygon;
                     if (euclidHoles.isEmpty()) {
                        euclidPolygon = outerEuclidPolygon;
                     }
                     else {
                        euclidPolygon = new GComplexPolygon2D(outerEuclidPolygon, euclidHoles);
                     }
                     euclidFeatures.add(createFeature(euclidPolygon, feature));

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
                  final ILineal2D euclidLines = createLine(jtsPolygon.getCoordinates(), projection);

                  euclidFeatures.add(createFeature(euclidLines, feature));
               }
               catch (final IllegalArgumentException e) {
                  //                     System.err.println(e.getMessage());
               }
            }

            validCounter.increment();
         }
         else if (type.getBinding() == com.vividsolutions.jts.geom.Point.class) {
            final com.vividsolutions.jts.geom.Point point = (com.vividsolutions.jts.geom.Point) geometryAttribute.getValue();

            final IVector2 euclidPoint = createPoint(point.getCoordinate(), projection);

            euclidFeatures.add(createFeature(euclidPoint, feature));

            validCounter.increment();
         }
         else {
            invalidCounter.increment();
            System.out.println("invalid type: " + type);
         }


         progress.stepDone();
      }


      store.dispose();

      euclidFeatures.trimToSize();

      System.out.println();
      System.out.println("Features: " + featuresCount);


      System.out.println();
      System.out.println("Read " + validCounter.get() + " valid geometries");
      //      System.out.println("Valid Vertices: " + validVerticesCounter.get());
      System.out.println("Polygons with holes: " + polygonsWithHolesCounter.get());
      if (invalidCounter.get() > 0) {
         System.out.println("Ignored " + invalidCounter.get() + " invalid geometries");
      }

      System.out.println();

      final SimpleFeatureType schema = featureSource.getSchema();
      final int fieldsCount = schema.getAttributeCount() - 1;
      final List<GField> fields = new ArrayList<GField>(fieldsCount);
      for (int i = 0; i < fieldsCount; i++) {
         final String fieldName = schema.getType(i + 1).getName().getLocalPart();
         final Class<?> fieldType = schema.getType(i + 1).getBinding();

         fields.add(new GField(fieldName, fieldType));
      }


      return new GListFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>(
               GProjection.EPSG_4326, fields, euclidFeatures, GIOUtils.getUniqueID(file));
   }


   private static IGlobeFeature<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> createFeature(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry,
                                                                                                                          final SimpleFeature feature) {
      return new GGlobeFeature<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>(geometry,
               feature.getAttributes());
   }


   private static IPolygon2D createPolygon(final Coordinate[] jtsCoordinates,
                                           final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createPolygon2(false, points);
   }


   private static ILineal2D createLine(final Coordinate[] jtsCoordinates,
                                       final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createLine2(false, points);
   }


   private static IVector2 createPoint(final Coordinate coordinate,
                                       final GProjection projection) {

      if (projection.isLatLong()) {
         return new GVector2D(Math.toRadians(coordinate.x), Math.toRadians(coordinate.y));
      }

      return new GVector2D(coordinate.x, coordinate.y).reproject(projection, GProjection.EPSG_4326);
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GShapeLoader 0.1");
      System.out.println("----------------\n");

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>, ?> features = GShapeLoader.readFeatures(
               GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "great_britain.shp", "roads.shp"),
               GProjection.EPSG_4326);

      System.out.println(features);
   }

}
