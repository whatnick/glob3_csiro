

package es.igosoftware.euclid.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.shape.IPolygonalChain;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.ITransformer;


public class GListFeatureCollection<

VectorT extends IVector<VectorT, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>

         implements
            IGlobeFeatureCollection<VectorT, FeatureGeometryT> {


   public static <

   VectorT extends IVector<VectorT, ?>,

   FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > GListFeatureCollection<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> fromGeometryList(final GProjection projection,
                                                                                                                      final List<FeatureGeometryT> geometries,
                                                                                                                      final String uniqueID) {

      final List<GField> fields = Collections.emptyList();

      final List<IGlobeFeature<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>>> features = GCollections.collect(
               geometries,
               new ITransformer<FeatureGeometryT, IGlobeFeature<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>>>() {

                  @Override
                  public IGlobeFeature<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transform(final FeatureGeometryT geometry) {
                     return new GGlobeFeature<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>>(geometry,
                              Collections.emptyList());
                  }
               });


      return new GListFeatureCollection<VectorT, IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>>(projection,
               fields, features, uniqueID);
   }

   private final GProjection                                    _projection;
   private final List<GField>                                   _fields;
   private final List<IGlobeFeature<VectorT, FeatureGeometryT>> _features;
   private final String                                         _uniqueID;

   private GAxisAlignedOrthotope<VectorT, ?>                    _bounds;

   private EnumSet<GGeometryType>                               _geometriesTypes;


   public GListFeatureCollection(final GProjection projection,
                                 final List<GField> fields,
                                 final List<IGlobeFeature<VectorT, FeatureGeometryT>> features,
                                 final String uniqueID) {
      GAssert.notNull(projection, "projection");
      GAssert.notNull(fields, "fields");
      GAssert.notEmpty(features, "features");

      _projection = projection;

      // creates copies of the lists to protect the modifications from outside
      _fields = new ArrayList<GField>(fields);
      _features = new ArrayList<IGlobeFeature<VectorT, FeatureGeometryT>>(features);

      _uniqueID = uniqueID; // can be null, it means no disk-cache is possible
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureGeometryT> visitor) {
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
   public Iterator<IGlobeFeature<VectorT, FeatureGeometryT>> iterator() {
      return Collections.unmodifiableList(_features).iterator();
   }


   @Override
   public IGlobeFeature<VectorT, FeatureGeometryT> get(final long index) {
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
   public EnumSet<GGeometryType> getGeometriesTypes() {
      // lazy initialized to avoid an iteration on _features if GeometriesTypes is not needed

      if (_geometriesTypes == null) {
         _geometriesTypes = calculateGeometriesTypes();
      }

      return _geometriesTypes;
   }


   private EnumSet<GGeometryType> calculateGeometriesTypes() {
      final EnumSet<GGeometryType> result = EnumSet.noneOf(GGeometryType.class);

      for (final IGlobeFeature<VectorT, FeatureGeometryT> feature : _features) {
         result.add(getShapeType(feature.getDefaultGeometry()));
         if (result.containsAll(GGeometryType.ALL)) {
            return GGeometryType.ALL;
         }
      }

      return result;
   }


   private static <VectorT extends IVector<VectorT, ?>> GGeometryType getShapeType(final IGeometry<VectorT> geometry) {
      if (geometry instanceof IVector) {
         return GGeometryType.POINT;
      }
      else if (geometry instanceof IPolygonalChain) {
         return GGeometryType.CURVE;
      }
      else if (geometry instanceof IPolygon) {
         return GGeometryType.SURFACE;
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

      if (_features.isEmpty()) {
         return null;
      }

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


   @Override
   public boolean isEditable() {
      return false;
   }


}