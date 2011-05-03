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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.xml.sax.SAXException;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;


public class GKmlLoader {


   private static List<IVector2> convert(final com.vividsolutions.jts.geom.Coordinate[] coordinates,
                                         final GProjection projection) {
      final List<IVector2> result = new ArrayList<IVector2>(coordinates.length);

      for (final com.vividsolutions.jts.geom.Coordinate coordinate : coordinates) {
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


   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readFeatures(final GFileName fileName,
                                                                                                                            final GProjection projection)
                                                                                                                                                         throws IOException,
                                                                                                                                                         SAXException,
                                                                                                                                                         ParserConfigurationException {
      return readFeatures(fileName.asFile(), projection);
   }


   //   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readFeatures(final File file,
   //                                                                                                                                            final GProjection projection)
   //                                                                                                                                                                         throws IOException,
   //                                                                                                                                                                         SAXException,
   //                                                                                                                                                                         ParserConfigurationException {
   //      if (!file.exists()) {
   //         throw new IOException("File not found!");
   //      }
   //
   //      //final FileDataStore store = FileDataStoreFinder.getDataStore(file);
   //      //final SimpleFeatureSource featureSource = store.getFeatureSource();
   //      //final SimpleFeatureCollection featuresCollection = featureSource.getFeatures();
   //
   //      //      org.geotools.xml.Parser parser = new org.geotools.xml.Parser( configuration );
   //      final KMLConfiguration configuration = new KMLConfiguration();
   //      //configuration.setupBindings();
   //
   //      final org.geotools.xml.Parser parser = new org.geotools.xml.Parser(configuration);
   //      //parser.getNamespaces().declarePrefix("prueba", "http://earth.google.com/kml/2.1");
   //
   //      //the xml instance document above
   //      System.out.println("File= " + file.getPath());
   //      final InputStream kmlStream = new FileInputStream(file);
   //
   //
   //      //      System.out.println("Validating.. ");
   //      //      parser.validate(kmlStream);
   //      //      final List pepe = parser.getValidationErrors();
   //      //      for (final Object p : pepe) {
   //      //         System.out.println("Error: " + p.toString());
   //      //      }
   //
   //      //SimpleFeature f = (SimpleFeature) parser.parse(getClass().getResourceAsStream("states.kml"));
   //      final SimpleFeature f = (SimpleFeature) parser.parse(kmlStream);
   //
   //
   //      final Collection placemarks = (Collection) f.getAttribute("Feature");
   //      //final Collection placemarks = (Collection) f.getAttribute("placemark");
   //      //final AbstractFeatureCollection placemarks = (AbstractFeatureCollection) f.getAttribute("Feature");
   //      //final FeatureCollection placemarks = (FeatureCollection) f.getAttribute("Feature");
   //      System.out.println("Por aqui vamos..");
   //
   //
   //      //final SimpleFeatureCollection fc = FeatureCollections.newCollection();
   //      final SimpleFeatureCollection featuresCollection = FeatureCollections.newCollection();
   //
   //      System.out.println("placemarks size= " + placemarks.size());
   //      for (final Iterator iterator = placemarks.iterator(); iterator.hasNext();) {
   //         //final Object object = iterator.next();
   //         final SimpleFeatureImpl object = (SimpleFeatureImpl) iterator.next();
   //         //System.out.println("Object_id: " + object.getID());
   //         //System.out.println("Object_atr_count: " + object.getAttributeCount());
   //         System.out.println("Object_num_atr: " + object.getNumberOfAttributes());
   //         System.out.println("Object_f_type: " + object.getFeatureType());
   //         //System.out.println("Object_name: " + object.getName());
   //
   //         //**System.out.println("Object_atr_list: " + object.getAttributes().toString());
   //
   //         //if (object.getAttribute(0).equals("Features")) {
   //         System.out.println("Por aqui vamos..");
   //         //fc = (SimpleFeatureCollection) object.getAttribute("Features");
   //         //final Collection features = (Collection) object.getAttribute("Features");
   //         final Collection features = (Collection) object.getAttribute("Feature");
   //
   //         if (features != null) {
   //
   //            //            for (final Iterator iterator2 = features.iterator(); iterator2.hasNext();) {
   //            //               final SimpleFeature simpleFeature = (SimpleFeature) iterator2.next();
   //            //
   //            //               //simpleFeature.setDefaultGeometryProperty((GeometryAttribute) simpleFeature.getDefaultGeometry());
   //            //               //final SimpleFeature simpleFeature = element;
   //            //               //System.out.println("     => Atributos de simpleFeature" + simpleFeature.getAttributes().toString());
   //            //               //System.out.println("=> Description: " + simpleFeature.getAttribute("description").toString());
   //            //               //System.out.println("=> Geometry: " + simpleFeature.getAttribute("Geometry").toString());
   //            //               //System.out.println("=> Default Geometry: " + simpleFeature.getDefaultGeometry().toString());
   //            //               System.out.println("=> Default Geometry property: " + simpleFeature.getDefaultGeometryProperty().toString());
   //            //
   //            //               //featuresCollection.add(simpleFeature);
   //            //            }
   //
   //            featuresCollection.addAll(features);
   //         }
   //
   //         System.out.println("Avanzamos.. Size= " + featuresCollection.size());
   //         //}
   //      }
   //
   //
   //      //parse
   //      //final SimpleFeatureImpl sfi = (SimpleFeatureImpl) parser.parse(kmlStream);
   //      /////final SimpleFeatureCollection featuresCollection = (SimpleFeatureCollection) parser.parse(kmlStream);
   //      //final FeatureCollection featuresCollection = (FeatureCollection) parser.parse(xml);
   //      //final SimpleFeatureCollection featuresCollection = fc;
   //
   //
   //      final SimpleFeatureType schema = featuresCollection.getSchema();
   //      //final SimpleFeatureType schema = (SimpleFeatureType) featuresCollection.getSchema();
   //
   //
   //      final GIntHolder validCounter = new GIntHolder(0);
   //      final GIntHolder invalidCounter = new GIntHolder(0);
   //
   //      final int featuresCount = featuresCollection.size();
   //      final ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> euclidFeatures = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
   //               featuresCount);
   //
   //
   //      final GProgress progress = new GProgress(featuresCount) {
   //         @Override
   //         public void informProgress(final double percent,
   //                                    final long elapsed,
   //                                    final long estimatedMsToFinish) {
   //            System.out.println("Loading \"" + file.getName() + "\" " + progressString(percent, elapsed, estimatedMsToFinish));
   //         }
   //      };
   //
   //      final FeatureIterator<SimpleFeature> iterator = featuresCollection.features();
   //
   //      while (iterator.hasNext()) {
   //
   //         final SimpleFeature feature = iterator.next();
   //
   //         final GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();
   //         System.out.println("GeometryAttribute= " + geometryAttribute);
   //         System.out.println("GeometryAttribute.name= " + geometryAttribute.getName());
   //         System.out.println("GeometryAttribute.type= " + geometryAttribute.getType());
   //         System.out.println("GeometryAttribute.value= " + geometryAttribute.getValue());
   //         //final GeometryAttribute geometryAttribute = (GeometryAttribute) feature.getDefaultGeometry();
   //
   //
   //         final GeometryType type = geometryAttribute.getType();
   //         System.out.println("type class: " + type.getClass());
   //
   //         //System.out.println("GeometryType= " + type.toString());
   //
   //         //if (type.getBinding() == com.vividsolutions.jts.geom.MultiPolygon.class) {
   //         //if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {
   //         if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {
   //
   //            final com.vividsolutions.jts.geom.MultiPolygon multipolygon = (com.vividsolutions.jts.geom.MultiPolygon) geometryAttribute.getValue();
   //            final int geometriesCount = multipolygon.getNumGeometries();
   //
   //            final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(geometriesCount);
   //            for (int i = 0; i < geometriesCount; i++) {
   //               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) multipolygon.getGeometryN(i);
   //
   //               try {
   //                  final IPolygon2D euclidPolygon = createEuclidPolygon(projection, jtsPolygon);
   //
   //                  if (euclidPolygon != null) {
   //                     //                     euclidFeatures.add(createFeature(euclidPolygon, feature));
   //                     polygons.add(euclidPolygon);
   //                     validCounter.increment();
   //                  }
   //               }
   //               catch (final IllegalArgumentException e) {
   //                  //                     System.err.println(e.getMessage());
   //               }
   //            }
   //
   //            if (!polygons.isEmpty()) {
   //               if (polygons.size() == 1) {
   //                  euclidFeatures.add(createFeature(polygons.get(0), feature));
   //               }
   //               else {
   //                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygon2D>(polygons), feature));
   //               }
   //            }
   //
   //         }
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Polygon.class) {
   //
   //            final com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) geometryAttribute.getValue();
   //            final int geometriesCount = polygon.getNumGeometries();
   //
   //            final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(geometriesCount);
   //            for (int i = 0; i < geometriesCount; i++) {
   //               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) polygon.getGeometryN(i);
   //
   //               try {
   //                  final IPolygon2D euclidPolygon = createEuclidPolygon(projection, jtsPolygon);
   //
   //                  if (euclidPolygon != null) {
   //                     //                     euclidFeatures.add(createFeature(euclidPolygon, feature));
   //                     polygons.add(euclidPolygon);
   //                     validCounter.increment();
   //                  }
   //               }
   //               catch (final IllegalArgumentException e) {
   //                  //                     System.err.println(e.getMessage());
   //               }
   //            }
   //
   //            if (!polygons.isEmpty()) {
   //               if (polygons.size() == 1) {
   //                  euclidFeatures.add(createFeature(polygons.get(0), feature));
   //               }
   //               else {
   //                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygon2D>(polygons), feature));
   //               }
   //            }
   //
   //         }
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.LineString.class) {
   //
   //            final com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geometryAttribute.getValue();
   //            final int geometriesCount = line.getNumGeometries();
   //
   //            final List<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>(geometriesCount);
   //            for (int i = 0; i < geometriesCount; i++) {
   //               final com.vividsolutions.jts.geom.LineString jtsLine = (com.vividsolutions.jts.geom.LineString) line.getGeometryN(i);
   //
   //               try {
   //                  final IPolygonalChain2D euclidLine = createLine(jtsLine.getCoordinates(), projection);
   //
   //                  //euclidFeatures.add(createFeature(euclidLines, feature));
   //                  lines.add(euclidLine);
   //               }
   //               catch (final IllegalArgumentException e) {
   //                  //                     System.err.println(e.getMessage());
   //               }
   //            }
   //
   //            if (!lines.isEmpty()) {
   //               if (lines.size() == 1) {
   //                  euclidFeatures.add(createFeature(lines.get(0), feature));
   //               }
   //               else {
   //                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygonalChain2D>(lines), feature));
   //               }
   //            }
   //
   //            validCounter.increment();
   //         }
   //         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiLineString.class) {
   //         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {
   //
   //
   //            final com.vividsolutions.jts.geom.MultiLineString multiline = (com.vividsolutions.jts.geom.MultiLineString) geometryAttribute.getValue();
   //            final int geometriesCount = multiline.getNumGeometries();
   //
   //            final List<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>(geometriesCount);
   //            for (int i = 0; i < geometriesCount; i++) {
   //               final com.vividsolutions.jts.geom.LineString jtsLine = (com.vividsolutions.jts.geom.LineString) multiline.getGeometryN(i);
   //
   //               try {
   //                  final IPolygonalChain2D euclidLine = createLine(jtsLine.getCoordinates(), projection);
   //
   //                  //euclidFeatures.add(createFeature(euclidLines, feature));
   //                  lines.add(euclidLine);
   //               }
   //               catch (final IllegalArgumentException e) {
   //                  //                     System.err.println(e.getMessage());
   //               }
   //            }
   //
   //            if (!lines.isEmpty()) {
   //               if (lines.size() == 1) {
   //                  euclidFeatures.add(createFeature(lines.get(0), feature));
   //               }
   //               else {
   //                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygonalChain2D>(lines), feature));
   //               }
   //            }
   //
   //            validCounter.increment();
   //         }
   //         //else if (type.getBinding() == com.vividsolutions.jts.geom.Point.class) {
   //         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.Point.class) {
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Point.class) {
   //            final IVector2 euclidPoint = createPoint(
   //                     ((com.vividsolutions.jts.geom.Point) geometryAttribute.getValue()).getCoordinate(), projection);
   //            euclidFeatures.add(createFeature(euclidPoint, feature));
   //
   //            validCounter.increment();
   //         }
   //         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiPoint.class) {
   //         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
   //            final IBoundedGeometry2D<? extends IFinite2DBounds<?>> euclidMultipoint = createEuclidMultiPoint(
   //                     geometryAttribute, projection);
   //            euclidFeatures.add(createFeature(euclidMultipoint, feature));
   //
   //            validCounter.increment();
   //         }
   //         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.GeometryCollection.class) {
   //
   //            final com.vividsolutions.jts.geom.GeometryCollection geometryCollection = (com.vividsolutions.jts.geom.GeometryCollection) geometryAttribute.getValue();
   //            final int geometriesCount = geometryCollection.getNumGeometries();
   //
   //            System.out.println("GEOMETRY COLLECTION SIZE: " + geometriesCount);
   //            System.out.println("GEOMETRY COLLECTION TYPE: " + geometryCollection.getGeometryType());
   //
   //            final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(geometriesCount);
   //            for (int i = 0; i < geometriesCount; i++) {
   //               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) geometryCollection.getGeometryN(i);
   //
   //               try {
   //                  final IPolygon2D euclidPolygon = createEuclidPolygon(projection, jtsPolygon);
   //
   //                  if (euclidPolygon != null) {
   //                     //                     euclidFeatures.add(createFeature(euclidPolygon, feature));
   //                     polygons.add(euclidPolygon);
   //                     validCounter.increment();
   //                  }
   //               }
   //               catch (final IllegalArgumentException e) {
   //                  //                     System.err.println(e.getMessage());
   //               }
   //            }
   //
   //            if (!polygons.isEmpty()) {
   //               if (polygons.size() == 1) {
   //                  euclidFeatures.add(createFeature(polygons.get(0), feature));
   //               }
   //               else {
   //                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygon2D>(polygons), feature));
   //               }
   //            }
   //
   //         }
   //         else {
   //            invalidCounter.increment();
   //            System.out.println("invalid type: " + geometryAttribute.getValue());
   //         }
   //
   //
   //         progress.stepDone();
   //      }
   //
   //
   //      //store.dispose();
   //
   //      euclidFeatures.trimToSize();
   //
   //      System.out.println();
   //      System.out.println("Features: " + featuresCount);
   //
   //
   //      System.out.println();
   //      System.out.println("Read " + validCounter.get() + " valid geometries");
   //
   //      if (invalidCounter.get() > 0) {
   //         System.out.println("Ignored " + invalidCounter.get() + " invalid geometries");
   //      }
   //
   //      System.out.println();
   //
   //      //final SimpleFeatureType schema = featureSource.getSchema();
   //      final int fieldsCount = schema.getAttributeCount() - 1;
   //      final List<GField> fields = new ArrayList<GField>(fieldsCount);
   //      for (int i = 0; i < fieldsCount; i++) {
   //         final String fieldName = schema.getType(i + 1).getName().getLocalPart();
   //         final Class<?> fieldType = schema.getType(i + 1).getBinding();
   //         System.out.println("Fieldname: " + fieldName);
   //
   //         fields.add(new GField(fieldName, fieldType));
   //      }
   //
   //
   //      return new GListFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
   //               GProjection.EPSG_4326, fields, euclidFeatures, GIOUtils.getUniqueID(file));
   //   }


   public static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> readFeatures(final File file,
                                                                                                                            final GProjection projection)
                                                                                                                                                         throws IOException,
                                                                                                                                                         SAXException,
                                                                                                                                                         ParserConfigurationException {
      if (!file.exists()) {
         throw new IOException("File not found!");
      }

      final KMLConfiguration configuration = new KMLConfiguration();

      //the xml instance document above
      System.out.println("File= " + file.getPath());
      final InputStream kmlStream = new FileInputStream(file);

      final org.geotools.xml.StreamingParser parser = new org.geotools.xml.StreamingParser(configuration, kmlStream,
               KML.Placemark);
      //               SimpleFeature.class);

      SimpleFeature f = null;

      final SimpleFeatureCollection featuresCollection = FeatureCollections.newCollection();

      int counter = 0;
      System.out.print("Loading \"" + file.getName() + "\" [");
      while ((f = (SimpleFeature) parser.parse()) != null) {
         counter++;
         //System.out.println("Geometry= " + f.getDefaultGeometry().toString());
         if (counter % 1000 == 0) {
            System.out.print("#");
         }
         if (counter % 100000 == 0) {
            System.out.println();
         }
         featuresCollection.add(f);

      }
      System.out.println("]");
      System.out.println("Size= " + featuresCollection.size());

      final SimpleFeatureType schema = featuresCollection.getSchema();


      final GIntHolder validCounter = new GIntHolder(0);
      final GIntHolder invalidCounter = new GIntHolder(0);

      final int featuresCount = featuresCollection.size();
      final ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> euclidFeatures = new ArrayList<IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               featuresCount);


      final GProgress progress = new GProgress(featuresCount) {
         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            System.out.println("Loading \"" + file.getName() + "\" " + progressString(percent, elapsed, estimatedMsToFinish));
         }
      };

      final FeatureIterator<SimpleFeature> iterator = featuresCollection.features();

      while (iterator.hasNext()) {

         final SimpleFeature feature = iterator.next();

         final GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();

         //final GeometryType type = geometryAttribute.getType();
         //System.out.println("type class: " + type.getClass());
         //System.out.println("GeometryType= " + type.toString());

         if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Polygon.class) {

            final com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon) geometryAttribute.getValue();

            try {
               final IPolygon2D euclidPolygon = createEuclidPolygon(projection, polygon);

               if (euclidPolygon != null) {
                  euclidFeatures.add(createFeature(euclidPolygon, feature));
                  validCounter.increment();
               }
            }
            catch (final IllegalArgumentException e) {
               //                     System.err.println(e.getMessage());
            }

         }
         //if (type.getBinding() == com.vividsolutions.jts.geom.MultiPolygon.class) {
         //if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPolygon.class) {

            final com.vividsolutions.jts.geom.MultiPolygon multipolygon = (com.vividsolutions.jts.geom.MultiPolygon) geometryAttribute.getValue();
            final int geometriesCount = multipolygon.getNumGeometries();

            final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(geometriesCount);
            for (int i = 0; i < geometriesCount; i++) {
               final com.vividsolutions.jts.geom.Polygon jtsPolygon = (com.vividsolutions.jts.geom.Polygon) multipolygon.getGeometryN(i);

               try {
                  final IPolygon2D euclidPolygon = createEuclidPolygon(projection, jtsPolygon);

                  if (euclidPolygon != null) {
                     //                     euclidFeatures.add(createFeature(euclidPolygon, feature));
                     polygons.add(euclidPolygon);
                     validCounter.increment();
                  }
               }
               catch (final IllegalArgumentException e) {
                  //                     System.err.println(e.getMessage());
               }
            }

            if (!polygons.isEmpty()) {
               if (polygons.size() == 1) {
                  euclidFeatures.add(createFeature(polygons.get(0), feature));
               }
               else {
                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygon2D>(polygons), feature));
               }
            }

         }
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.LineString.class) {

            final com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geometryAttribute.getValue();

            try {
               final IPolygonalChain2D euclidLine = createLine(line.getCoordinates(), projection);

               euclidFeatures.add(createFeature(euclidLine, feature));
               validCounter.increment();

            }
            catch (final IllegalArgumentException e) {
               //                     System.err.println(e.getMessage());
            }

         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiLineString.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiLineString.class) {


            final com.vividsolutions.jts.geom.MultiLineString multiline = (com.vividsolutions.jts.geom.MultiLineString) geometryAttribute.getValue();
            final int geometriesCount = multiline.getNumGeometries();

            final List<IPolygonalChain2D> lines = new ArrayList<IPolygonalChain2D>(geometriesCount);
            for (int i = 0; i < geometriesCount; i++) {
               final com.vividsolutions.jts.geom.LineString jtsLine = (com.vividsolutions.jts.geom.LineString) multiline.getGeometryN(i);

               try {
                  final IPolygonalChain2D euclidLine = createLine(jtsLine.getCoordinates(), projection);

                  //euclidFeatures.add(createFeature(euclidLines, feature));
                  lines.add(euclidLine);
               }
               catch (final IllegalArgumentException e) {
                  //                     System.err.println(e.getMessage());
               }
            }

            if (!lines.isEmpty()) {
               if (lines.size() == 1) {
                  euclidFeatures.add(createFeature(lines.get(0), feature));
               }
               else {
                  euclidFeatures.add(createFeature(new GMultiGeometry2D<IPolygonalChain2D>(lines), feature));
               }
            }

            validCounter.increment();
         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.Point.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.Point.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.Point.class) {
            final IVector2 euclidPoint = createPoint(
                     ((com.vividsolutions.jts.geom.Point) geometryAttribute.getValue()).getCoordinate(), projection);
            euclidFeatures.add(createFeature(euclidPoint, feature));

            validCounter.increment();
         }
         //else if (type.getBinding() == com.vividsolutions.jts.geom.MultiPoint.class) {
         //else if (feature.getDefaultGeometry().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.MultiPoint.class) {
            final IBoundedGeometry2D<? extends IFinite2DBounds<?>> euclidMultipoint = createEuclidMultiPoint(geometryAttribute,
                     projection);
            euclidFeatures.add(createFeature(euclidMultipoint, feature));

            validCounter.increment();
         }
         else if (geometryAttribute.getValue().getClass() == com.vividsolutions.jts.geom.GeometryCollection.class) {

            final int TODO_handling_GeometryCollection;
            final com.vividsolutions.jts.geom.GeometryCollection geometryCollection = (com.vividsolutions.jts.geom.GeometryCollection) geometryAttribute.getValue();
            final int geometriesCount = geometryCollection.getNumGeometries();

            System.out.println("GEOMETRY COLLECTION size: " + geometriesCount);
            System.out.println("GEOMETRY COLLECTION type: " + geometryCollection.getGeometryType());


            for (int i = 0; i < geometriesCount; i++) {
               System.out.println("Geometry: " + geometryCollection.getGeometryN(i).getGeometryType());
               // proccess depending on the geometry type
            }

         }
         else {
            invalidCounter.increment();
            System.out.println("invalid type: " + geometryAttribute.getValue());
         }


         progress.stepDone();
      }

      //store.dispose();

      euclidFeatures.trimToSize();

      System.out.println();
      System.out.println("Features: " + featuresCount);

      System.out.println();
      System.out.println("Read " + validCounter.get() + " valid geometries");

      if (invalidCounter.get() > 0) {
         System.out.println("Ignored " + invalidCounter.get() + " invalid geometries");
      }

      System.out.println();

      //final SimpleFeatureType schema = featureSource.getSchema();
      final int fieldsCount = schema.getAttributeCount() - 1;
      System.out.println("Fields count: " + fieldsCount);
      final List<GField> fields = new ArrayList<GField>(fieldsCount);
      for (int i = 0; i < fieldsCount; i++) {
         final String fieldName = schema.getType(i + 1).getName().getLocalPart();
         final Class<?> fieldType = schema.getType(i + 1).getBinding();
         System.out.println("Fieldname: " + fieldName);

         fields.add(new GField(fieldName, fieldType));
      }


      return new GListFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(GProjection.EPSG_4326,
               fields, euclidFeatures, GIOUtils.getUniqueID(file));
   }


   private static IPolygon2D createEuclidPolygon(final GProjection projection,
                                                 final com.vividsolutions.jts.geom.Polygon jtsPolygon) {
      final ISimplePolygon2D outerEuclidPolygon = createPolygon(jtsPolygon.getCoordinates(), projection);

      final int holesCount = jtsPolygon.getNumInteriorRing();
      if (holesCount == 0) {
         return outerEuclidPolygon;
      }


      final List<ISimplePolygon2D> euclidHoles = new ArrayList<ISimplePolygon2D>(holesCount);
      for (int j = 0; j < holesCount; j++) {
         final com.vividsolutions.jts.geom.LineString jtsHole = jtsPolygon.getInteriorRingN(j);

         try {
            final ISimplePolygon2D euclidHole = createPolygon(jtsHole.getCoordinates(), projection);
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
      // System.out.println("Found polygon with " + holesCount + " holes");

      return euclidPolygon;

   }


   private static IBoundedGeometry2D<? extends IFinite2DBounds<?>> createEuclidMultiPoint(final GeometryAttribute geometryAttribute,
                                                                                          final GProjection projection) {
      final com.vividsolutions.jts.geom.MultiPoint multipoint = (com.vividsolutions.jts.geom.MultiPoint) geometryAttribute.getValue();

      if (multipoint.getNumGeometries() == 1) {
         final com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) multipoint.getGeometryN(0);
         return createPoint(jtsPoint.getCoordinate(), projection);
      }

      final IVector2[] euclidPoints = new IVector2[multipoint.getNumGeometries()];

      for (int i = 0; i < euclidPoints.length; i++) {
         final com.vividsolutions.jts.geom.Point jtsPoint = (com.vividsolutions.jts.geom.Point) multipoint.getGeometryN(i);
         euclidPoints[i] = createPoint(jtsPoint.getCoordinate(), projection);
      }

      return new GMultiGeometry2D<IVector2>(euclidPoints);
   }


   private static IGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> createFeature(final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry,
                                                                                                          final SimpleFeature feature) {
      return new GGlobeFeature<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(geometry, feature.getAttributes());
   }


   private static ISimplePolygon2D createPolygon(final com.vividsolutions.jts.geom.Coordinate[] jtsCoordinates,
                                                 final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createPolygon2(false, points);
   }


   private static IPolygonalChain2D createLine(final com.vividsolutions.jts.geom.Coordinate[] jtsCoordinates,
                                               final GProjection projection) {
      final List<IVector2> points = removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeConsecutiveEqualsPoints(removeLastIfRepeated(convert(
               jtsCoordinates, projection))))));

      return GShape.createLine2(false, points);
   }


   private static IVector2 createPoint(final com.vividsolutions.jts.geom.Coordinate coordinate,
                                       final GProjection projection) {

      if (projection.isLatLong()) {
         return new GVector2D(Math.toRadians(coordinate.x), Math.toRadians(coordinate.y));
      }

      return new GVector2D(coordinate.x, coordinate.y).reproject(projection, GProjection.EPSG_4326);
   }


   //   public static void main(final String[] args) throws IOException {
   //      System.out.println("GShapeLoader 0.1");
   //      System.out.println("----------------\n");
   //
   //      final GFileName samplesDirectory = GFileName.absolute("home", "dgd", "Desktop", "sample-shp");
   //
   //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "shp", "great_britain.shp", "roads.shp");
   //      //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "cartobrutal", "world-modified", "world.shp");
   //      //      final GFileName fileName = GFileName.fromParentAndParts(samplesDirectory, "shp", "argentina.shp", "places.shp");
   //
   //      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GKmlLoader.readFeatures(
   //               fileName, GProjection.EPSG_4326);
   //
   //
   //      System.out.println(features);
   //   }


}
