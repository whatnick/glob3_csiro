

package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.Feature;
import es.igosoftware.globe.layers.ShapefileTools;
import es.unex.s3xtante.utils.ProjectionUtils;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;


public class WWVectorLayer
         extends
            AbstractVectorLayer {

   private String      m_sFilename;
   private String      m_sName;
   private GProjection m_CRS;

   private GField[]    _fields;


   public void create(final IGlobeVectorLayer layer) {

      m_BaseDataObject = layer;
      m_sName = layer.getName();
      m_CRS = layer.getProjection();
      _fields = layer.getFields();
   }


   public void create(final String sName,
                      final GField[] fields,
                      final String filename,
                      final Object crs) {

      m_sFilename = filename;
      m_sName = sName;
      _fields = fields;

      if (!(crs instanceof GProjection)) {
         m_CRS = ProjectionUtils.getDefaultProjection();
      }
      else {
         m_CRS = (GProjection) crs;
      }

      m_BaseDataObject = new ArrayList<Feature>();


   }


   @Override
   public void open() {
   }


   @Override
   public void close() {
   }


   @Override
   @SuppressWarnings("unchecked")
   public void addFeature(final Geometry g,
                          final Object[] values) {

      if (m_BaseDataObject instanceof ArrayList) {
         final ArrayList<Feature> list = (ArrayList<Feature>) m_BaseDataObject;
         list.add(new Feature(g, values));
      }

   }


   @Override
   public IFeatureIterator iterator() {

      if (m_BaseDataObject != null) {
         if (m_BaseDataObject instanceof IGlobeVectorLayer) {
            return new WWFeatureIterator(((IGlobeVectorLayer) m_BaseDataObject).getFeatures());
         }
         return new WWFeatureIterator(m_BaseDataObject);
      }

      return null;


   }


   @Override
   public String getFieldName(final int i) {
      return _fields[i].getName();
   }


   @Override
   public Class<?> getFieldType(final int i) {
      return _fields[i].getType();
   }


   @Override
   public int getFieldCount() {
      return _fields.length;
   }


   @Override
   @SuppressWarnings("unchecked")
   public int getShapesCount() {

      if (m_BaseDataObject instanceof ArrayList) {
         final ArrayList<Feature> list = (ArrayList<Feature>) m_BaseDataObject;
         return list.size();
      }
      else if (m_BaseDataObject instanceof IGlobeVectorLayer) {
         final Feature[] features = ((IGlobeVectorLayer) m_BaseDataObject).getFeatures();
         return features.length;
      }
      else {
         return 0;
      }

   }


   public int getShapeType(final Geometry geom) {

      if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }
      else if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
         return IVectorLayer.SHAPE_TYPE_LINE;
      }
      else {
         return IVectorLayer.SHAPE_TYPE_POINT;
      }

   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public void postProcess() {

      saveShapefile();
      try {
         final IGlobeVectorLayer layer = ShapefileTools.readFile(new File(m_sFilename));
         if (layer != null) {
            layer.setName(m_sName);
            layer.setProjection(m_CRS);
            create(layer);
            layer.redraw();
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @SuppressWarnings("unchecked")
   public void saveShapefile() {

      if (m_BaseDataObject instanceof ArrayList) {
         final GeometryFactory gf = new GeometryFactory();
         final ArrayList<Feature> list = (ArrayList<Feature>) m_BaseDataObject;
         try {
            final SimpleFeatureType featureType = buildFeatureType(m_sName, getShapeType(), _fields, DefaultGeographicCRS.WGS84);
            final DataStore mds = createDatastore(m_sFilename, featureType);
            mds.createSchema(featureType);
            final DefaultQuery query = new DefaultQuery(m_sName, Filter.INCLUDE);
            final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = mds.getFeatureSource(query.getTypeName());
            final SimpleFeatureType ft = featureSource.getSchema();
            final FeatureWriter<SimpleFeatureType, SimpleFeature> featWriter = mds.getFeatureWriterAppend(ft.getTypeName(),
                     Transaction.AUTO_COMMIT);
            for (int i = 0; i < list.size(); i++) {
               final Feature feature = list.get(i);
               Geometry geom;
               if (feature._geometry instanceof Polygon) {
                  geom = gf.createMultiPolygon(new Polygon[] { (Polygon) feature._geometry });
               }
               else if (feature._geometry instanceof LineString) {
                  geom = gf.createMultiLineString(new LineString[] { (LineString) feature._geometry });
               }
               else {
                  geom = feature._geometry;
               }

               try {
                  final List<Object> attributes = new ArrayList<Object>();
                  attributes.add(geom);
                  attributes.addAll(Arrays.asList(feature._attributes));
                  final SimpleFeature sf = featWriter.next();
                  sf.setAttributes(attributes);
                  featWriter.write();
               }
               catch (final Exception e) {
                  e.printStackTrace();
               }
            }
            featWriter.close();
         }
         catch (final Exception e) {
            e.printStackTrace();
         }
      }

   }


   private DataStore createDatastore(final String sFilename,
                                     final SimpleFeatureType m_FeatureType) throws IOException {

      final File file = new File(sFilename);
      final Map<String, Serializable> params = new HashMap<String, Serializable>();
      params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
      params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, false);

      final FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
      final ShapefileDataStore dataStore = (ShapefileDataStore) factory.createNewDataStore(params);
      dataStore.createSchema(m_FeatureType);
      return dataStore;

   }


   private SimpleFeatureType buildFeatureType(final String sName,
                                              final int iShapeType,
                                              final GField[] fields,
                                              final CoordinateReferenceSystem crs) {
      final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName(sName);

      final AttributeTypeBuilder attBuilder = new AttributeTypeBuilder();
      builder.add(toGeometryAttribute(iShapeType, crs, attBuilder));
      builder.setDefaultGeometry("geom");
      for (final GField field : fields) {
         final AttributeType type = attBuilder.binding(field.getType()).buildType();
         final AttributeDescriptor descriptor = attBuilder.buildDescriptor(field.getName(), type);
         builder.add(descriptor);
      }
      return builder.buildFeatureType();

   }


   private GeometryDescriptor toGeometryAttribute(final int shapeType,
                                                  final CoordinateReferenceSystem crs,
                                                  final AttributeTypeBuilder builder) {

      final Class<?> s[] = { Point.class, MultiLineString.class, MultiPolygon.class };
      final GeometryType buildGeometryType = builder.crs(crs).binding(s[shapeType]).buildGeometryType();
      return builder.buildDescriptor("geom", buildGeometryType);

   }


   @Override
   @SuppressWarnings("unchecked")
   public Rectangle2D getFullExtent() {

      double dXMin = Double.MAX_VALUE;
      double dXMax = Double.NEGATIVE_INFINITY;
      double dYMin = Double.MAX_VALUE;
      double dYMax = Double.NEGATIVE_INFINITY;

      if (m_BaseDataObject instanceof ArrayList) {
         final ArrayList<Feature> list = (ArrayList<Feature>) m_BaseDataObject;
         for (int i = 0; i < list.size(); i++) {
            final Feature feature = list.get(i);
            final Envelope envelope = feature._geometry.getEnvelopeInternal();
            dXMin = Math.min(dXMin, envelope.getMinX());
            dYMin = Math.min(dYMin, envelope.getMinY());
            dXMax = Math.max(dXMax, envelope.getMaxX());
            dYMax = Math.max(dYMax, envelope.getMaxY());
         }
         return new Rectangle2D.Double(dXMin, dYMin, dXMax - dXMin, dYMax - dYMin);
      }
      else if (m_BaseDataObject instanceof Feature[]) {
         final Feature[] features = (Feature[]) m_BaseDataObject;
         for (final Feature element : features) {
            final Envelope envelope = element._geometry.getEnvelopeInternal();
            dXMin = Math.min(dXMin, envelope.getMinX());
            dYMin = Math.min(dYMin, envelope.getMinY());
            dXMax = Math.max(dXMax, envelope.getMaxX());
            dYMax = Math.max(dYMax, envelope.getMaxY());
         }
         return new Rectangle2D.Double(dXMin, dYMin, dXMax - dXMin, dYMax - dYMin);
      }
      else {
         return new Rectangle2D.Double();
      }

   }


   @Override
   public String getFilename() {

      return m_sFilename;

   }


   @Override
   public Object getCRS() {

      return m_CRS;

   }


   @Override
   public void setName(final String name) {

      m_sName = name;

   }


   @SuppressWarnings("unchecked")
   @Override
   public int getShapeType() {

      if (getShapesCount() == 0) {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }

      if (m_BaseDataObject instanceof ArrayList) {
         final ArrayList<Feature> list = (ArrayList<Feature>) m_BaseDataObject;
         return getShapeType(list.get(0)._geometry);
      }
      else if (m_BaseDataObject instanceof IGlobeVectorLayer) {
         final Feature[] features = ((IGlobeVectorLayer) m_BaseDataObject).getFeatures();
         return getShapeType(features[0]._geometry);
      }
      else {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }


   }


}
