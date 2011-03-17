

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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.IGlobeFeatureCollection;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.GGlobeFeature;
import es.igosoftware.globe.layers.GShapefileTools;
import es.igosoftware.globe.layers.IGlobeFeature;
import es.igosoftware.io.GFileName;
import es.igosoftware.utils.GJTSUtils;
import es.unex.sextante.dataObjects.AbstractVectorLayer;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.dataObjects.vectorFilters.IVectorLayerFilter;


public class WWVectorLayer
         extends
            AbstractVectorLayer {

   private GFileName                      _filename;
   private String                         _name;
   private GProjection                    _projection;

   private GField[]                       _fields;

   private WWFeatureIterator              _iterator;
   private final List<IVectorLayerFilter> _filters = new ArrayList<IVectorLayerFilter>();


   public WWVectorLayer(final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> layer) {
      initializeFromLayer(layer);
   }


   private void initializeFromLayer(final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> layer) {
      m_BaseDataObject = layer;
      _name = layer.getName();
      _projection = layer.getProjection();
      _fields = layer.getFields();
   }


   public WWVectorLayer(final String name,
                        final GField[] fields,
                        final GFileName filename,
                        final GProjection projection) {

      _filename = filename;
      _name = name;
      _fields = fields;

      _projection = projection;

      m_BaseDataObject = new ArrayList<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>>();
   }


   @Override
   public void open() {
   }


   @Override
   public void close() {
   }


   @Override
   @SuppressWarnings("unchecked")
   public void addFeature(final Geometry jtsGeometry,
                          final Object[] values) {

      if (m_BaseDataObject instanceof List) {
         final List<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> list = (List<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>>) m_BaseDataObject;
         for (final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> euclidGeometry : GJTSUtils.toEuclid(jtsGeometry)) {
            list.add(new GGlobeFeature<IVector2<?>, GAxisAlignedRectangle>(euclidGeometry, Arrays.asList(values)));
         }
      }
      else {
         throw new RuntimeException("m_BaseDataObject type not supported " + m_BaseDataObject.getClass());
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


   private static int getShapeType(final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geom) {
      if (geom instanceof IPolygon) {
         return IVectorLayer.SHAPE_TYPE_POLYGON;
      }
      else if ((geom instanceof GSegment2D) || (geom instanceof GLinesStrip2D)) {
         return IVectorLayer.SHAPE_TYPE_LINE;
      }
      else if (geom instanceof IVector2) {
         return IVectorLayer.SHAPE_TYPE_POINT;
      }
      else {
         throw new RuntimeException("Unsuported geometry type (" + geom + ")");
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
         final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> layer = GShapefileTools.readFile(_filename.asFile());
         if (layer != null) {
            layer.setName(_name);
            initializeFromLayer(layer);
            layer.redraw();
         }
      }
      catch (final IOException e) {
         e.printStackTrace();
      }

   }


   @SuppressWarnings("unchecked")
   public void saveShapefile() {

      if (m_BaseDataObject instanceof Iterable) {
         try {
            saveFeatures((Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>>) m_BaseDataObject);
         }
         catch (final IOException e) {
            e.printStackTrace();
         }
      }
      else {
         throw new RuntimeException("m_BaseDataObject has an unsupported type (" + m_BaseDataObject.getClass() + ")");
      }

   }


   @SuppressWarnings("unchecked")
   private void saveFeatures(final Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> features) throws IOException {
      final SimpleFeatureType featureType = buildFeatureType(_name, getShapeType(), _fields, DefaultGeographicCRS.WGS84);
      final DataStore dataStore = createDatastore(_filename, featureType);
      dataStore.createSchema(featureType);
      final Query query = new Query(_name, Filter.INCLUDE);
      final FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = dataStore.getFeatureSource(query.getTypeName());
      final SimpleFeatureType ft = featureSource.getSchema();
      final FeatureWriter<SimpleFeatureType, SimpleFeature> featWriter = dataStore.getFeatureWriterAppend(ft.getTypeName(),
               Transaction.AUTO_COMMIT);

      for (final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature : features) {
         final Geometry gtsGeometry = GJTSUtils.toJTS(feature.getGeometry());

         final List<Object> attributes = new ArrayList<Object>();
         attributes.addAll(Arrays.asList(feature.getAttributes()));
         final SimpleFeature sf = featWriter.next();
         sf.setAttributes(attributes);
         sf.setDefaultGeometry(gtsGeometry);
         featWriter.write();

      }
      featWriter.close();
   }


   private DataStore createDatastore(final GFileName filename,
                                     final SimpleFeatureType m_FeatureType) throws IOException {

      final File file = filename.asFile();
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
   public Rectangle2D getFullExtent() {

      if (m_BaseDataObject instanceof Iterable) {
         @SuppressWarnings("unchecked")
         final Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> features = (Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>>) m_BaseDataObject;
         return getRectangle3DBounds(features);
      }

      throw new RuntimeException("m_BaseDataObject has an unsupported type (" + m_BaseDataObject.getClass() + ")");

   }


   private Rectangle2D getRectangle3DBounds(final Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> features) {
      double xMin = Double.POSITIVE_INFINITY;
      double xMax = Double.NEGATIVE_INFINITY;
      double yMin = Double.POSITIVE_INFINITY;
      double yMax = Double.NEGATIVE_INFINITY;

      for (final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature : features) {
         final GAxisAlignedRectangle envelope = feature.getGeometry().getBounds();
         xMin = Math.min(xMin, envelope._lower.x());
         yMin = Math.min(yMin, envelope._lower.y());
         xMax = Math.max(xMax, envelope._upper.x());
         yMax = Math.max(yMax, envelope._upper.y());
      }

      return new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
   }


   @Override
   public String getFilename() {
      return _filename.buildPath();
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
         final List<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> features = (List<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>>) m_BaseDataObject;
         return getShapeType(features.get(0).getGeometry());
      }
      else if (m_BaseDataObject instanceof IGlobeVectorLayer) {
         final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) m_BaseDataObject).getFeaturesCollection();
         return getShapeType(features.get(0).getGeometry());
      }
      else {
         throw new RuntimeException("m_BaseDataObject has an unsupported type (" + m_BaseDataObject.getClass() + ")");
      }

   }


   @Override
   protected IFeatureIterator createIterator() {
      if (_iterator == null) {
         if (m_BaseDataObject instanceof IGlobeVectorLayer) {
            @SuppressWarnings("unchecked")
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> layer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) m_BaseDataObject;
            _iterator = new WWFeatureIterator(layer.getFeaturesCollection(), _filters);
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
