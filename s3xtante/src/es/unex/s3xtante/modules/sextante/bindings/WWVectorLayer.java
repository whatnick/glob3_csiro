

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
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Query;
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
import es.igosoftware.globe.layers.GGlobeFeature;
import es.igosoftware.globe.layers.IGlobeFeature;
import es.igosoftware.globe.layers.GShapefileTools;
import es.unex.s3xtante.utils.ProjectionUtils;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;


public class WWVectorLayer
         extends
            AbstractVectorLayer {

   private String                         _filename;
   private String                         _name;
   private GProjection                    _projection;

   private GField[]                       _fields;

   private WWFeatureIterator              _iterator;
   private final List<IVectorLayerFilter> _filters = new ArrayList<IVectorLayerFilter>();


   public void create(final IGlobeVectorLayer layer) {

      m_BaseDataObject = layer;
      _name = layer.getName();
      _projection = layer.getProjection();
      _fields = layer.getFields();
   }


   public void create(final String sName,
                      final GField[] fields,
                      final String filename,
                      final Object crs) {

      _filename = filename;
      _name = sName;
      _fields = fields;

      if (!(crs instanceof GProjection)) {
         _projection = ProjectionUtils.getDefaultProjection();
      }
      else {
         _projection = (GProjection) crs;
      }

      m_BaseDataObject = new ArrayList<IGlobeFeature>();
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

      if (m_BaseDataObject instanceof List) {
         final List<IGlobeFeature> list = (List<IGlobeFeature>) m_BaseDataObject;
         list.add(new GGlobeFeature(g, Arrays.asList(values)));
      }

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


   //   @Override
   //   @SuppressWarnings("unchecked")
   //   public int getShapesCount() {
   //
   //      if (m_BaseDataObject instanceof List) {
   //         final List<Feature> list = (List<Feature>) m_BaseDataObject;
   //         return list.size();
   //      }
   //      else if (m_BaseDataObject instanceof IGlobeVectorLayer) {
   //         final Feature[] features = ((IGlobeVectorLayer) m_BaseDataObject).getFeatures();
   //         return features.length;
   //      }
   //      else {
   //         return 0;
   //      }
   //
   //   }


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

      return _name;

   }


   @Override
   public void postProcess() {

      saveShapefile();
      try {
         final IGlobeVectorLayer layer = GShapefileTools.readFile(new File(_filename));
         if (layer != null) {
            layer.setName(_name);
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

      if (m_BaseDataObject instanceof List) {
         final GeometryFactory gf = new GeometryFactory();
         final List<IGlobeFeature> list = (List<IGlobeFeature>) m_BaseDataObject;
         try {
            final SimpleFeatureType featureType = buildFeatureType(_name, getShapeType(), _fields, DefaultGeographicCRS.WGS84);
            final DataStore mds = createDatastore(_filename, featureType);
            mds.createSchema(featureType);
            final Query query = new Query(_name, Filter.INCLUDE);
            final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = mds.getFeatureSource(query.getTypeName());
            final SimpleFeatureType ft = featureSource.getSchema();
            final FeatureWriter<SimpleFeatureType, SimpleFeature> featWriter = mds.getFeatureWriterAppend(ft.getTypeName(),
                     Transaction.AUTO_COMMIT);
            for (int i = 0; i < list.size(); i++) {
               final IGlobeFeature feature = list.get(i);
               Geometry geom;
               if (feature.getGeometry() instanceof Polygon) {
                  geom = gf.createMultiPolygon(new Polygon[] { (Polygon) feature.getGeometry() });
               }
               else if (feature.getGeometry() instanceof LineString) {
                  geom = gf.createMultiLineString(new LineString[] { (LineString) feature.getGeometry() });
               }
               else {
                  geom = feature.getGeometry();
               }

               try {
                  final List<Object> attributes = new ArrayList<Object>();
                  attributes.addAll(Arrays.asList(feature.getAttributes()));
                  final SimpleFeature sf = featWriter.next();
                  sf.setAttributes(attributes);
                  sf.setDefaultGeometry(geom);
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

      if (m_BaseDataObject instanceof List) {
         final List<IGlobeFeature> list = (List<IGlobeFeature>) m_BaseDataObject;
         for (int i = 0; i < list.size(); i++) {
            final IGlobeFeature feature = list.get(i);
            final Envelope envelope = feature.getGeometry().getEnvelopeInternal();
            dXMin = Math.min(dXMin, envelope.getMinX());
            dYMin = Math.min(dYMin, envelope.getMinY());
            dXMax = Math.max(dXMax, envelope.getMaxX());
            dYMax = Math.max(dYMax, envelope.getMaxY());
         }
         return new Rectangle2D.Double(dXMin, dYMin, dXMax - dXMin, dYMax - dYMin);
      }
      else if (m_BaseDataObject instanceof IGlobeFeature[]) {
         final IGlobeFeature[] features = (IGlobeFeature[]) m_BaseDataObject;
         for (final IGlobeFeature element : features) {
            final Envelope envelope = element.getGeometry().getEnvelopeInternal();
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

      return _filename;

   }


   @Override
   public Object getCRS() {
      return _projection;
   }


   @Override
   public void setName(final String name) {
      _name = name;
   }


   @SuppressWarnings("unchecked")
   @Override
   public int getShapeType() {

      if (getShapesCount() == 0) {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }

      if (m_BaseDataObject instanceof List) {
         final List<IGlobeFeature> list = (List<IGlobeFeature>) m_BaseDataObject;
         return getShapeType(list.get(0).getGeometry());
      }
      else if (m_BaseDataObject instanceof IGlobeVectorLayer) {
         final List<IGlobeFeature> features = ((IGlobeVectorLayer) m_BaseDataObject).getFeaturesCollection().getFeatures();
         return getShapeType(features.get(0).getGeometry());
      }
      else {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }


   }


   @Override
   protected IFeatureIterator createIterator() {

      if (_iterator == null) {
         if (m_BaseDataObject instanceof IGlobeVectorLayer) {
            final IGlobeVectorLayer layer = (IGlobeVectorLayer) m_BaseDataObject;
            _iterator = new WWFeatureIterator(layer.getFeaturesCollection().getFeatures(), _filters);
         }
         else {
            _iterator = new WWFeatureIterator();
         }
      }

      return _iterator.getNewInstance();


   }


   @Override
   public void addFilter(final IVectorLayerFilter filter) {

      _filters.add(filter);
      _iterator = null;

   }


   @Override
   public void removeFilters() {

      _filters.clear();
      _iterator = null;

   }

}
