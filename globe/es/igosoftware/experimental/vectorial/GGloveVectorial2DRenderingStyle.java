

package es.igosoftware.experimental.vectorial;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeMutableFeatureCollection;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRenderingStyle;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.GLengthLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;


public class GGloveVectorial2DRenderingStyle
         implements
            IGlobeRenderingStyle {


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                                final IGlobeLayer layer) {
      GAssert.isInstanceOf(layer, IGlobeVector2Layer.class, "layer");

      final int _______________Diego_at_work;

      final IGlobeVector2Layer vector2Layer = (IGlobeVector2Layer) layer;

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> featuresCollection = vector2Layer.getFeaturesCollection();

      if (featuresCollection instanceof IGlobeMutableFeatureCollection) {
         @SuppressWarnings("unchecked")
         final IGlobeMutableFeatureCollection<IVector2, ? extends IFiniteBounds<IVector2, ?>, ?> mutableFeaturesCollection = (IGlobeMutableFeatureCollection<IVector2, ? extends IFiniteBounds<IVector2, ?>, ?>) featuresCollection;

         mutableFeaturesCollection.addChangeListener(new IMutable.ChangeListener() {
            @Override
            public void mutableChanged() {
               final int _______________Diego_at_work_update_attributes;
            }
         });
      }

      final EnumSet<GGeometryType> geometriesTypes = featuresCollection.getGeometryType();

      final List<ILayerAttribute<?>> result = new ArrayList<ILayerAttribute<?>>();

      if (geometriesTypes.contains(GGeometryType.POINT)) {
         result.add(createPointsLayerAttributes(application));
      }

      if (geometriesTypes.contains(GGeometryType.CURVE)) {
         result.add(createCurveLayerAttributes(application));
      }

      if (geometriesTypes.contains(GGeometryType.SURFACE)) {
         result.add(createSurfaceLayerAttributes(application));
      }

      return result;
   }


   private ILayerAttribute<?> createPointsLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute pointSize = new GLengthLayerAttribute("Size", "Set the point size", "PointsSize", 0, 10, 1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            System.out.println(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return GLength.Meter.value(1);
         }
      };

      return new GGroupAttribute("Points Style", application.getSmallIcon(GFileName.relative("points-style.png")),
               "Points rendering settings", pointSize);
   }


   private ILayerAttribute<?> createCurveLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute thickness = new GLengthLayerAttribute("Width", "Set the curves thickness", "CurveWidth", 0, 10,
               1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            System.out.println(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return GLength.Meter.value(1);
         }
      };


      return new GGroupAttribute("Curves Style", application.getSmallIcon(GFileName.relative("curves-style.png")),
               "Set the curves style settings", thickness);
   }


   private ILayerAttribute<?> createSurfaceLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute thickness = new GLengthLayerAttribute("Border Width", "Set the border thickness",
               "SurfaceBorderWidth", 0, 10, 1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            System.out.println(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return GLength.Meter.value(1);
         }
      };

      return new GGroupAttribute("Surfaces Style", application.getSmallIcon(GFileName.relative("surfaces-style.png")),
               "Set the surfaces style settings", thickness);
   }


}
