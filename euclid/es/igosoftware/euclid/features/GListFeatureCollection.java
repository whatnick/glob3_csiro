

package es.igosoftware.euclid.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GLinesStrip;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public class GListFeatureCollection<

VectorT extends IVector<VectorT, ?, ?>,

FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>

>

         implements
            IGlobeFeatureCollection<VectorT, FeatureBoundsT, GListFeatureCollection<VectorT, FeatureBoundsT>> {


   public static <

   VectorT extends IVector<VectorT, ?, ?>,

   FeatureBoundsT extends IFiniteBounds<VectorT, FeatureBoundsT>,

   GeometryT extends IBoundedGeometry<VectorT, ?, FeatureBoundsT>

   > GListFeatureCollection<VectorT, FeatureBoundsT> fromGeometryList(final GProjection projection,
                                                                      final List<GeometryT> geometries,
                                                                      final String uniqueID) {

      final List<GField> fields = Collections.emptyList();

      final List<IGlobeFeature<VectorT, FeatureBoundsT>> features = GCollections.collect(geometries,
               new ITransformer<GeometryT, IGlobeFeature<VectorT, FeatureBoundsT>>() {
                  @Override
                  public IGlobeFeature<VectorT, FeatureBoundsT> transform(final GeometryT geometry) {
                     return new GGlobeFeature<VectorT, FeatureBoundsT>(geometry, Collections.emptyList());
                  }
               });


      return new GListFeatureCollection<VectorT, FeatureBoundsT>(projection, fields, features, uniqueID);
   }


   private final GProjection                                  _projection;
   private final List<GField>                                 _fields;
   private final List<IGlobeFeature<VectorT, FeatureBoundsT>> _features;
   private final String                                       _uniqueID;

   private GAxisAlignedOrthotope<VectorT, ?>                  _bounds;


   public GListFeatureCollection(final GProjection projection,
                                 final List<GField> fields,
                                 final List<IGlobeFeature<VectorT, FeatureBoundsT>> features,
                                 final String uniqueID) {
      GAssert.notNull(projection, "projection");
      GAssert.notNull(fields, "fields");
      GAssert.notEmpty(features, "features");

      _projection = projection;

      // creates copies of the lists to protect the modifications from outside
      _fields = new ArrayList<GField>(fields);
      _features = new ArrayList<IGlobeFeature<VectorT, FeatureBoundsT>>(features);

      _uniqueID = uniqueID; // can be null, it means no disk-cache is possible
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureBoundsT> visitor) {
      try {
         for (int i = 0; i < _features.size(); i++) {
            visitor.visit(_features.get(i), i);
         }
      }
      catch (final IGlobeFeatureCollection.AbortVisiting e) {
         // ignore exception, just exit the for loop
      }
   }


   @Override
   public String toString() {
      return "GListFeatureCollection [projection=" + _projection + ", fields=" + _fields.size() + ", features="
             + _features.size() + ", uniqueID=" + _uniqueID + "]";
   }


   @Override
   public Iterator<IGlobeFeature<VectorT, FeatureBoundsT>> iterator() {
      return Collections.unmodifiableList(_features).iterator();
   }


   @Override
   public IGlobeFeature<VectorT, FeatureBoundsT> get(final long index) {
      return _features.get(toInt(index));
   }


   private int toInt(final long index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("index #" + index + " is negative");
      }
      if (index > Integer.MAX_VALUE) {
         throw new IndexOutOfBoundsException("index #" + index + " is bigger that Integer.MAX_VALUE");
      }

      return (int) index; // safe to cast here as the bounds was just checked
   }


   @Override
   public boolean isEmpty() {
      return _features.isEmpty();
   }


   @Override
   public long size() {
      return _features.size();
   }


   @Override
   public GVectorLayerType getShapeType() {
      if (_features.isEmpty()) {
         return GVectorLayerType.POLYGON;
      }

      return getShapeType(_features.get(0).getDefaultGeometry());
   }


   private static <VectorT extends IVector<VectorT, ?, ?>> GVectorLayerType getShapeType(final IGeometry<VectorT, ?> geometry) {
      if (geometry instanceof IVector) {
         return GVectorLayerType.POINT;
      }
      else if ((geometry instanceof GSegment) || (geometry instanceof GLinesStrip)) {
         return GVectorLayerType.LINE;
      }
      else if (geometry instanceof IPolygon) {
         return GVectorLayerType.POLYGON;
      }
      else {
         throw new RuntimeException("Unsupported geometry type: " + geometry.getClass());
      }
   }


   @Override
   public List<GField> getFields() {
      return Collections.unmodifiableList(_fields);
   }


   @Override
   public String getUniqueID() {
      return _uniqueID;
   }


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      if (_bounds == null) {
         _bounds = calculateBounds();
      }

      return _bounds;
   }


   private GAxisAlignedOrthotope<VectorT, ?> calculateBounds() {
      final GAxisAlignedOrthotope<VectorT, ?> firstBounds = _features.get(0).getDefaultGeometry().getBounds().asAxisAlignedOrthotope();
      VectorT minLower = firstBounds._lower.asDouble();
      VectorT maxUpper = firstBounds._upper.asDouble();

      for (int i = 1; i < _features.size(); i++) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = _features.get(i).getDefaultGeometry().getBounds().asAxisAlignedOrthotope();

         minLower = minLower.min(bounds._lower);
         maxUpper = maxUpper.max(bounds._upper);
      }

      return GAxisAlignedOrthotope.create(minLower, maxUpper);
   }


}
