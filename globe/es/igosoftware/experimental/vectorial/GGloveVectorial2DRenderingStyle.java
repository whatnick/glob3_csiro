

package es.igosoftware.experimental.vectorial;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeMutableFeatureCollection;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRenderingStyle;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
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
         result.add(createPointsLayerAttributes());
      }

      if (geometriesTypes.contains(GGeometryType.CURVE)) {
         result.add(createCurveLayerAttributes());
      }

      if (geometriesTypes.contains(GGeometryType.SURFACE)) {
         result.add(createSurfaceLayerAttributes());
      }

      return result;
   }


   private ILayerAttribute<?> createPointsLayerAttributes() {

      final GFloatLayerAttribute pointSize = new GFloatLayerAttribute("Size", "Set the point size", "PointsSize", 0, 10,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public void set(final Float value) {
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return 1.5f;
         }
      };

      return new GGroupAttribute("Points Style", "Points rendering settings", pointSize);
   }


   private ILayerAttribute<?> createCurveLayerAttributes() {

      final GFloatLayerAttribute thickness = new GFloatLayerAttribute("Thickness", "Set the curves thickness", "CurveThickness",
               0, 10, GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public void set(final Float value) {
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return 1.5f;
         }
      };


      return new GGroupAttribute("Curves Style", "Set the curves style settings", thickness);
   }


   private ILayerAttribute<?> createSurfaceLayerAttributes() {

      final GFloatLayerAttribute thickness = new GFloatLayerAttribute("Border Thickness", "Set the border thickness",
               "SurfaceBorderThickness", 0, 10, GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public void set(final Float value) {
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return 1.5f;
         }
      };

      return new GGroupAttribute("Surfaces Style", "Set the surfaces style settings", thickness);
   }


}
