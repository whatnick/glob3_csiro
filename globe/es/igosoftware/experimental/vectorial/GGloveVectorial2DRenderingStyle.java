

package es.igosoftware.experimental.vectorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.IRenderingStyle;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeMutableFeatureCollection;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRenderingStyle;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GColorLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.GLengthLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GUtils;


public class GGloveVectorial2DRenderingStyle
         implements
            IGlobeRenderingStyle,
            IRenderingStyle {


   private final IGlobeVector2Layer _layer;


   // general
   private boolean                  _debugRendering   = false;
   private boolean                  _renderLODIgnores = true;

   // point style
   private IMeasure<GLength>        _pointSize        = GLength.Meter.value(1);
   private IColor                   _pointColor       = GColorF.WHITE;
   private float                    _pointOpacity     = 1;


   //   // curve style
   //   private final IMeasure<GLength>  _curveWidth         = GLength.Meter.value(1);
   //
   //   // surface style
   //   private final IMeasure<GLength>  _surfaceBorderWidth = GLength.Meter.value(1);


   public GGloveVectorial2DRenderingStyle(final IGlobeVector2Layer layer) {
      GAssert.notNull(layer, "layer");

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

      //      if (geometriesTypes.contains(GGeometryType.CURVE)) {
      //         result.add(createCurveLayerAttributes(application));
      //      }
      //
      //      if (geometriesTypes.contains(GGeometryType.SURFACE)) {
      //         result.add(createSurfaceLayerAttributes(application));
      //      }

      result.add(createAdvancedLayerAttributes(application));

      return result;
   }


   private ILayerAttribute<?> createAdvancedLayerAttributes(@SuppressWarnings("unused") final IGlobeApplication application) {

      final GBooleanLayerAttribute renderLODIgnores = new GBooleanLayerAttribute("Render LOD Ignores",
               "Set the RenderLODIgnores option", "RenderLODIgnores") {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void set(final Boolean value) {
            setRenderLODIgnores(value);
         }


         @Override
         public Boolean get() {
            return isRenderLODIgnores();
         }
      };


      final GBooleanLayerAttribute debugRendering = new GBooleanLayerAttribute("Debug Rendering", "Set the debug rendering mode",
               "DebugRendering") {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void set(final Boolean value) {
            setDebugRendering(value);
         }


         @Override
         public Boolean get() {
            return isDebugRendering();
         }
      };


      return new GGroupAttribute("Advanced", "Advanced settings", renderLODIgnores, debugRendering);
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


      final GColorLayerAttribute pointColor = new GColorLayerAttribute("Color", "Set the point color", "PointColor") {

         @Override
         public void set(final Color value) {
            setPointColor(GColorF.fromAWTColor(value));
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getPointColor().asAWTColor();
         }
      };


      final ILayerAttribute<?> pointOpacity = new GFloatLayerAttribute("Opacity", "Set the point color opacity", "PointOpacity",
               0, 1, GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getPointOpacity();
         }


         @Override
         public void set(final Float value) {
            setPointOpacity(value);
         }
      };

      return new GGroupAttribute("Points Style", application.getSmallIcon(GFileName.relative("points-style.png")),
               "Points rendering settings", pointSize, pointColor, pointOpacity);
   }


   //   private ILayerAttribute<?> createCurveLayerAttributes(final IGlobeApplication application) {
   //
   //      final GLengthLayerAttribute width = new GLengthLayerAttribute("Width", "Set the curves width", "CurveWidth", 0, 10, 1) {
   //         @Override
   //         public void set(final IMeasure<GLength> value) {
   //            setCurveWidth(value);
   //         }
   //
   //
   //         @Override
   //         public boolean isVisible() {
   //            return true;
   //         }
   //
   //
   //         @Override
   //         public IMeasure<GLength> get() {
   //            return getCurveWidth();
   //         }
   //      };
   //
   //
   //      return new GGroupAttribute("Curves Style", application.getSmallIcon(GFileName.relative("curves-style.png")),
   //               "Set the curves style settings", width);
   //   }


   //   private ILayerAttribute<?> createSurfaceLayerAttributes(final IGlobeApplication application) {
   //
   //      final GLengthLayerAttribute borderWidth = new GLengthLayerAttribute("Border Width", "Set the border width",
   //               "SurfaceBorderWidth", 0, 10, 1) {
   //         @Override
   //         public void set(final IMeasure<GLength> value) {
   //            setSurfaceBorderWidth(value);
   //         }
   //
   //
   //         @Override
   //         public boolean isVisible() {
   //            return true;
   //         }
   //
   //
   //         @Override
   //         public IMeasure<GLength> get() {
   //            return getSurfaceBorderWidth();
   //         }
   //      };
   //
   //
   //      return new GGroupAttribute("Surfaces Style", application.getSmallIcon(GFileName.relative("surfaces-style.png")),
   //               "Set the surfaces style settings", borderWidth);
   //   }


   private void styleChanged() {
      _layer.clearCache();
   }


   @Override
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


   @Override
   public IColor getPointColor() {
      return _pointColor;
   }


   public void setPointColor(final IColor newPointColor) {
      if (GUtils.equals(newPointColor, _pointColor)) {
         return;
      }

      final IColor oldPointColor = _pointColor;
      _pointColor = newPointColor;
      _layer.firePropertyChange("PointColor", oldPointColor, newPointColor);

      styleChanged();
   }


   @Override
   public float getPointOpacity() {
      return _pointOpacity;
   }


   public void setPointOpacity(final float newPointOpacity) {
      if (GMath.closeTo(newPointOpacity, _pointOpacity)) {
         return;
      }

      final float oldPointOpacity = _pointOpacity;
      _pointOpacity = newPointOpacity;
      _layer.firePropertyChange("PointOpacity", oldPointOpacity, newPointOpacity);

      styleChanged();
   }


   @Override
   public boolean isDebugRendering() {
      return _debugRendering;
   }


   public void setDebugRendering(final boolean debugRendering) {
      if (debugRendering == _debugRendering) {
         return;
      }

      _debugRendering = debugRendering;
      _layer.firePropertyChange("DebugRendering", !debugRendering, debugRendering);

      styleChanged();
   }


   @Override
   public String uniqueName() {
      final int TODO_PUT_ALL_DATA_ON_UNIQUE_NAME;
      return (_debugRendering ? "t" : "f") + //
             _pointColor.toHexString() + //
             Float.toHexString(_pointOpacity) + //
             _pointSize;
   }


   @Override
   public boolean isRenderLODIgnores() {
      return _renderLODIgnores;
   }


   public void setRenderLODIgnores(final boolean renderLODIgnores) {
      if (renderLODIgnores == _renderLODIgnores) {
         return;
      }

      _renderLODIgnores = renderLODIgnores;
      _layer.firePropertyChange("RenderLODIgnores", !renderLODIgnores, renderLODIgnores);

      styleChanged();
   }


   @Override
   public IColor getLODColor() {
      return _pointColor;
   }


   @Override
   public double getLODMinSize() {
      return 5;
   }

   //   @Override
   //   public IMeasure<GLength> getCurveWidth() {
   //      return _curveWidth;
   //   }
   //
   //
   //   public void setCurveWidth(final IMeasure<GLength> newCurveWidth) {
   //      if (GUtils.equals(newCurveWidth, _curveWidth)) {
   //         return;
   //      }
   //
   //      final IMeasure<GLength> oldCurveWidth = _curveWidth;
   //      _curveWidth = newCurveWidth;
   //      _layer.firePropertyChange("CurveWidth", oldCurveWidth, newCurveWidth);
   //
   //      styleChanged();
   //   }


   //   @Override
   //   public IMeasure<GLength> getSurfaceBorderWidth() {
   //      return _surfaceBorderWidth;
   //   }
   //
   //
   //   public void setSurfaceBorderWidth(final IMeasure<GLength> newSurfaceBorderWidth) {
   //      if (GUtils.equals(newSurfaceBorderWidth, _surfaceBorderWidth)) {
   //         return;
   //      }
   //
   //      final IMeasure<GLength> oldSurfaceBorderWidth = _surfaceBorderWidth;
   //      _surfaceBorderWidth = newSurfaceBorderWidth;
   //      _layer.firePropertyChange("SurfaceBorderWidth", oldSurfaceBorderWidth, newSurfaceBorderWidth);
   //
   //      styleChanged();
   //   }


}
