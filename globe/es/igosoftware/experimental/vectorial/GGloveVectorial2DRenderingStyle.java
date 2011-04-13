

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
import es.igosoftware.util.GUtils;


public class GGloveVectorial2DRenderingStyle
         implements
            IGlobeRenderingStyle {


   private final IGlobeVector2Layer _layer;

   private IMeasure<GLength>        _pointSize          = GLength.Meter.value(1);
   private IMeasure<GLength>        _curveWidth         = GLength.Meter.value(1);
   private IMeasure<GLength>        _surfaceBorderWidth = GLength.Meter.value(1);


   public GGloveVectorial2DRenderingStyle(final IGlobeVector2Layer layer) {
      _layer = layer;
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                                final IGlobeLayer unusedlayer) {
      if (unusedlayer != _layer) {
         throw new RuntimeException("Invalid layer");
      }

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> featuresCollection = _layer.getFeaturesCollection();

      if (featuresCollection instanceof IGlobeMutableFeatureCollection) {
         @SuppressWarnings("unchecked")
         final IGlobeMutableFeatureCollection<IVector2, ? extends IFiniteBounds<IVector2, ?>, ?> mutableFeaturesCollection = (IGlobeMutableFeatureCollection<IVector2, ? extends IFiniteBounds<IVector2, ?>, ?>) featuresCollection;

         mutableFeaturesCollection.addChangeListener(new IMutable.ChangeListener() {
            @Override
            public void mutableChanged() {
               final int __________Diego_at_work____Update_attributes;
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

      final GLengthLayerAttribute pointSize = new GLengthLayerAttribute("Size", "Set the point size", "PointSize", 0, 10, 1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            setPointSize(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getPointSize();
         }
      };


      return new GGroupAttribute("Points Style", application.getSmallIcon(GFileName.relative("points-style.png")),
               "Points rendering settings", pointSize);
   }


   private ILayerAttribute<?> createCurveLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute width = new GLengthLayerAttribute("Width", "Set the curves width", "CurveWidth", 0, 10, 1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            setCurveWidth(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getCurveWidth();
         }
      };


      return new GGroupAttribute("Curves Style", application.getSmallIcon(GFileName.relative("curves-style.png")),
               "Set the curves style settings", width);
   }


   private ILayerAttribute<?> createSurfaceLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute borderWidth = new GLengthLayerAttribute("Border Width", "Set the border width",
               "SurfaceBorderWidth", 0, 10, 1) {
         @Override
         public void set(final IMeasure<GLength> value) {
            setSurfaceBorderWidth(value);
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getSurfaceBorderWidth();
         }
      };


      return new GGroupAttribute("Surfaces Style", application.getSmallIcon(GFileName.relative("surfaces-style.png")),
               "Set the surfaces style settings", borderWidth);
   }


   private void styleChanged() {
      final int __________Diego_at_work____Inform_the_layer_the_style_has_changed;
   }


   public IMeasure<GLength> getPointSize() {
      return _pointSize;
   }


   public void setPointSize(final IMeasure<GLength> newPointSize) {
      if (GUtils.equals(newPointSize, _pointSize)) {
         return;
      }

      final IMeasure<GLength> oldPointSize = _pointSize;
      _pointSize = newPointSize;
      _layer.firePropertyChange("PointSize", oldPointSize, newPointSize);

      styleChanged();
   }


   public IMeasure<GLength> getCurveWidth() {
      return _curveWidth;
   }


   public void setCurveWidth(final IMeasure<GLength> newCurveWidth) {
      if (GUtils.equals(newCurveWidth, _curveWidth)) {
         return;
      }

      final IMeasure<GLength> oldCurveWidth = _curveWidth;
      _curveWidth = newCurveWidth;
      _layer.firePropertyChange("CurveWidth", oldCurveWidth, newCurveWidth);

      styleChanged();
   }


   public IMeasure<GLength> getSurfaceBorderWidth() {
      return _surfaceBorderWidth;
   }


   public void setSurfaceBorderWidth(final IMeasure<GLength> newSurfaceBorderWidth) {
      if (GUtils.equals(newSurfaceBorderWidth, _surfaceBorderWidth)) {
         return;
      }

      final IMeasure<GLength> oldSurfaceBorderWidth = _surfaceBorderWidth;
      _surfaceBorderWidth = newSurfaceBorderWidth;
      _layer.firePropertyChange("SurfaceBorderWidth", oldSurfaceBorderWidth, newSurfaceBorderWidth);

      styleChanged();
   }


}
