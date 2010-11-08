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


package es.igosoftware.globe.layers;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;

import es.igosoftware.globe.GField;
import es.igosoftware.globe.IGlobeVectorLayer;


public class ShapefileTools {

   public static IGlobeVectorLayer readFile(final File file) {

      try {
         final HashMap<String, URL> connect = new HashMap<String, URL>();
         connect.put("url", file.toURI().toURL());

         final DataStore dataStore = DataStoreFinder.getDataStore(connect);
         final DefaultQuery query = new DefaultQuery(dataStore.getTypeNames()[0], Filter.INCLUDE);
         final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(query.getTypeName());
         final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = featureSource.getFeatures(query);
         final FeatureIterator<SimpleFeature> iterator = featureCollection.features();
         final Feature[] features = new Feature[featureCollection.size()];
         for (int i = 0; iterator.hasNext(); i++) {
            final SimpleFeature feature = iterator.next();
            features[i] = new Feature((Geometry) feature.getDefaultGeometry(), feature.getAttributes().toArray(new Object[0]));
         }

         final int iFields = featureSource.getSchema().getAttributeCount() - 1;
         //         final String[] sFields = new String[iFields];
         //         final Class<?>[] types = new Class[iFields];
         final GField[] fields = new GField[iFields];
         for (int i = 0; i < iFields; i++) {
            final String fieldName = featureSource.getSchema().getType(i + 1).getName().getLocalPart();
            final Class<?> fieldType = featureSource.getSchema().getType(i + 1).getBinding();

            fields[i] = new GField(fieldName, fieldType);
         }

         return new GGlobeVectorLayer(file.getName(), features, fields, null);

      }

      catch (final Exception e) {
         return null;
      }

      //      final Shapefile shp = new Shapefile(file);
      //
      //      final String shapeType = shp.getShapeType();
      //      if (shapeType.equals(Shapefile.SHAPE_POINT)) {
      //         return makePointLayer(shp);
      //      }
      //      else if (shapeType.equals(Shapefile.SHAPE_POLYLINE)) {
      //         return makePolylineLayer(shp);
      //      }
      //      else if (shapeType.equals(Shapefile.SHAPE_POLYGON)) {
      //         return makePolygonLayer(shp);
      //      }
      //      return null;

   }


   //   private static GGlobeVectorLayer makePolygonLayer(final Shapefile shp) {
   //
   //      final GeometryFactory gf = new GeometryFactory();
   //      final List<ShapefileRecord> records = shp.getRecords();
   //      if (records.size() != 0) {
   //         final Feature[] features = new Feature[records.size()];
   //         for (int i = 0; i < records.size(); i++) {
   //            final ShapefileRecordPolygon record = (ShapefileRecordPolygon) records.get(i);
   //            final Polygon[] polygons = new Polygon[record.getNumberOfParts()];
   //            for (int iPart = 0; iPart < record.getNumberOfParts(); iPart++) {
   //               final Iterable<double[]> points = record.getPoints(iPart);
   //               final Coordinate[] coords = new Coordinate[record.getNumberOfPoints(iPart)];
   //               int iPoint = 0;
   //               for (final double[] point : points) {
   //                  coords[iPoint] = new Coordinate(point[0], point[1]);
   //                  iPoint++;
   //               }
   //               polygons[iPart] = gf.createPolygon(gf.createLinearRing(coords), null);
   //            }
   //            final Collection<Object> values = record.getAttributes().getValues();
   //            final Object[] array = values.toArray(new Object[0]);
   //            final MultiPolygon geom = gf.createMultiPolygon(polygons);
   //            features[i] = new Feature(geom, array);
   //         }
   //
   //         final Set<Entry<String, Object>> entries = records.get(0).getAttributes().getEntries();
   //         final String[] sFields = new String[entries.size()];
   //         final Class<?>[] types = new Class<?>[entries.size()];
   //         int iField = 0;
   //         for (final Entry<String, Object> entry : entries) {
   //            sFields[iField] = entry.getKey();
   //            types[iField] = entry.getValue().getClass();
   //            iField++;
   //         }
   //
   //         final GGlobeVectorLayer layer = new GGlobeVectorLayer(shp.getFile().getName(), features, sFields, types, GProjection.EPSG_4326);
   //         return layer;
   //      }
   //      return null;
   //   }


   //   private static GGlobeVectorLayer makePolylineLayer(final Shapefile shp) {
   //
   //      final GeometryFactory gf = new GeometryFactory();
   //      final List<ShapefileRecord> records = shp.getRecords();
   //      if (records.size() != 0) {
   //         final Feature[] features = new Feature[records.size()];
   //         for (int i = 0; i < records.size(); i++) {
   //            final ShapefileRecordPolyline record = (ShapefileRecordPolyline) records.get(i);
   //            final LineString[] lines = new LineString[record.getNumberOfParts()];
   //            for (int iPart = 0; iPart < record.getNumberOfParts(); iPart++) {
   //               final Iterable<double[]> points = record.getPoints(iPart);
   //               final Coordinate[] coords = new Coordinate[record.getNumberOfPoints(iPart)];
   //               int iPoint = 0;
   //               for (final double[] point : points) {
   //                  coords[iPoint] = new Coordinate(point[0], point[1]);
   //                  iPoint++;
   //               }
   //               lines[iPart] = gf.createLineString(coords);
   //            }
   //            final Collection<Object> values = record.getAttributes().getValues();
   //            final Object[] array = values.toArray(new Object[0]);
   //            final MultiLineString geom = gf.createMultiLineString(lines);
   //            features[i] = new Feature(geom, array);
   //         }
   //
   //         final Set<Entry<String, Object>> entries = records.get(0).getAttributes().getEntries();
   //         final String[] sFields = new String[entries.size()];
   //         final Class<?>[] types = new Class<?>[entries.size()];
   //         int iField = 0;
   //         for (final Entry<String, Object> entry : entries) {
   //            sFields[iField] = entry.getKey();
   //            types[iField] = Object.class;
   //            iField++;
   //         }
   //
   //         final GGlobeVectorLayer layer = new GGlobeVectorLayer(shp.getFile().getName(), features, sFields, types, GProjection.EPSG_4326);
   //         return layer;
   //      }
   //
   //      return null;
   //   }


   //   private static GGlobeVectorLayer makePointLayer(final Shapefile shp) {
   //
   //      final GeometryFactory gf = new GeometryFactory();
   //      final List<ShapefileRecord> records = shp.getRecords();
   //      if (records.size() != 0) {
   //         final Feature[] features = new Feature[records.size()];
   //         for (int i = 0; i < records.size(); i++) {
   //            final ShapefileRecordPoint point = (ShapefileRecordPoint) records.get(i);
   //            final double[] pointCoords = point.getPoint();
   //            final Coordinate coord = new Coordinate(pointCoords[0], pointCoords[1]);
   //            final Point pt = gf.createPoint(coord);
   //            final Collection<Object> values = point.getAttributes().getValues();
   //            final Object[] array = values.toArray(new Object[0]);
   //            features[i] = new Feature(pt, array);
   //         }
   //
   //         final Set<Entry<String, Object>> entries = records.get(0).getAttributes().getEntries();
   //         final String[] sFields = new String[entries.size()];
   //         final Class<?>[] types = new Class<?>[entries.size()];
   //         int iField = 0;
   //         for (final Entry<String, Object> entry : entries) {
   //            sFields[iField] = entry.getKey();
   //            types[iField] = Object.class;
   //            iField++;
   //         }
   //
   //         final GGlobeVectorLayer layer = new GGlobeVectorLayer(shp.getFile().getName(), features, sFields, types, GProjection.EPSG_4326);
   //         return layer;
   //      }
   //
   //      return null;
   //   }


}
