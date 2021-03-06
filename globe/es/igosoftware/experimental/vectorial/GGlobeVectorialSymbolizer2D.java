

package es.igosoftware.experimental.vectorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GExpressionsSymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCreateOval2DExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GCurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GLengthToFloatExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GOval2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygon2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GPolygonalChain2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GSurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeSymbolizer;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GAreaLayerAttribute;
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


public class GGlobeVectorialSymbolizer2D
         extends
            GExpressionsSymbolizer2D
         implements
            IGlobeSymbolizer {


   // curves attributes
   private static final IMeasure<GLength> DEFAULT_CURVE_BORDER_WIDTH         = GLength.Meter.value(10);
   private static final IColor            DEFAULT_CURVE_COLOR                = GColorF.YELLOW;
   private static final float             DEFAULT_CURVE_OPACITY              = 1;

   // surface attributes
   private static final IColor            DEFAULT_SURFACE_COLOR              = GColorF.BLUE;
   private static final float             DEFAULT_SURFACE_OPACITY            = 1;
   private static final IMeasure<GLength> DEFAULT_SURFACE_CURVE_BORDER_WIDTH = GLength.Meter.value(10);
   private static final IColor            DEFAULT_SURFACE_CURVE_COLOR        = DEFAULT_SURFACE_COLOR.muchDarker();
   private static final float             DEFAULT_SURFACE_CURVE_OPACITY      = DEFAULT_SURFACE_OPACITY;


   // points attributes
   private static final IMeasure<GArea>   DEFAULT_POINT_AREA                 = GArea.SquareKilometer.value(1);
   private static final IColor            DEFAULT_POINT_SURFACE_COLOR        = GColorF.MAGENTA;
   private static final float             DEFAULT_POINT_SURFACE_OPACITY      = 1;
   private static final IMeasure<GLength> DEFAULT_POINT_CURVE_BORDER_WIDTH   = GLength.Meter.value(10);
   private static final IColor            DEFAULT_POINT_CURVE_COLOR          = DEFAULT_POINT_SURFACE_COLOR.muchDarker();
   private static final float             DEFAULT_POINT_CURVE_OPACITY        = DEFAULT_POINT_SURFACE_OPACITY;


   private final IGlobeVector2Layer       _layer;

   // curves attributes
   private IMeasure<GLength>              _curveBorderWidth                  = DEFAULT_CURVE_BORDER_WIDTH;
   private IColor                         _curveColor                        = DEFAULT_CURVE_COLOR;
   private float                          _curveOpacity                      = DEFAULT_CURVE_OPACITY;

   // surface attributes
   private IMeasure<GLength>              _surfaceCurveBorderWidth           = DEFAULT_SURFACE_CURVE_BORDER_WIDTH;
   private IColor                         _surfaceCurveColor                 = DEFAULT_SURFACE_CURVE_COLOR;
   private float                          _surfaceCurveOpacity               = DEFAULT_SURFACE_CURVE_OPACITY;
   private IColor                         _surfaceColor                      = DEFAULT_SURFACE_COLOR;
   private float                          _surfaceOpacity                    = DEFAULT_SURFACE_OPACITY;

   // points attributes
   private IMeasure<GArea>                _pointArea                         = DEFAULT_POINT_AREA;
   private IMeasure<GLength>              _pointCurveBorderWidth             = DEFAULT_POINT_CURVE_BORDER_WIDTH;
   private IColor                         _pointCurveColor                   = DEFAULT_POINT_CURVE_COLOR;
   private float                          _pointCurveOpacity                 = DEFAULT_POINT_CURVE_OPACITY;
   private IColor                         _pointSurfaceColor                 = DEFAULT_POINT_SURFACE_COLOR;
   private float                          _pointSurfaceOpacity               = DEFAULT_POINT_SURFACE_OPACITY;


   public GGlobeVectorialSymbolizer2D(final IGlobeVector2Layer layer) {
      super(false, 2, true, false, //

            createPointExpression(//
                     DEFAULT_POINT_AREA, //
                     DEFAULT_POINT_CURVE_BORDER_WIDTH, DEFAULT_POINT_CURVE_COLOR, DEFAULT_POINT_CURVE_OPACITY, //
                     DEFAULT_POINT_SURFACE_COLOR, DEFAULT_POINT_SURFACE_OPACITY), //

            createCurveExpression(DEFAULT_CURVE_BORDER_WIDTH, DEFAULT_CURVE_COLOR, DEFAULT_CURVE_OPACITY), //

            createSurfaceExpression(//
                     DEFAULT_SURFACE_CURVE_BORDER_WIDTH, DEFAULT_SURFACE_CURVE_COLOR, DEFAULT_SURFACE_CURVE_OPACITY, //
                     DEFAULT_SURFACE_COLOR, DEFAULT_SURFACE_OPACITY));

      GAssert.notNull(layer, "layer");

      _layer = layer;
   }


   private static IExpression<IVector2, GSymbol2DList> createPointExpression(final IMeasure<GArea> pointArea,
                                                                             final IMeasure<GLength> curveBorderWidth,
                                                                             final IColor curveColor,
                                                                             final float curveOpacity,
                                                                             final IColor surfaceColor,
                                                                             final float surfaceOpacity) {

      final IExpression<IVector2, GAxisAlignedOval2D> toOvalExpression = new GCreateOval2DExpression(pointArea);

      final IExpression<GAxisAlignedOval2D, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<GAxisAlignedOval2D>( //
               new GLengthToFloatExpression<GAxisAlignedOval2D>(curveBorderWidth), //
               new GConstantExpression<GAxisAlignedOval2D, IColor>(curveColor), //
               new GConstantExpression<GAxisAlignedOval2D, Float>(curveOpacity));

      final IExpression<GAxisAlignedOval2D, ISurface2DStyle> surfaceStyleExpression = new GSurface2DStyleExpression<GAxisAlignedOval2D>( //
               new GConstantExpression<GAxisAlignedOval2D, IColor>(surfaceColor), //
               new GConstantExpression<GAxisAlignedOval2D, Float>(surfaceOpacity));

      return new GOval2DSymbolizerExpression<IVector2>(toOvalExpression, curveStyleExpression, surfaceStyleExpression);
   }


   private static IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> createCurveExpression(final IMeasure<GLength> borderWidth,
                                                                                                                     final IColor color,
                                                                                                                     final float opacity) {
      final IExpression<IPolygonalChain2D, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<IPolygonalChain2D>( //
               new GLengthToFloatExpression<IPolygonalChain2D>(borderWidth), //
               new GConstantExpression<IPolygonalChain2D, IColor>(color), //
               new GConstantExpression<IPolygonalChain2D, Float>(opacity));

      return new GPolygonalChain2DSymbolizerExpression(curveStyleExpression);
   }


   private static IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> createSurfaceExpression(final IMeasure<GLength> curveBorderWidth,
                                                                                                                         final IColor curveColor,
                                                                                                                         final float curveOpacity,
                                                                                                                         final IColor surfaceColor,
                                                                                                                         final float surfaceOpacity) {
      final IExpression<IPolygon2D, ICurve2DStyle> curveStyleExpression = new GCurve2DStyleExpression<IPolygon2D>( //
               new GLengthToFloatExpression<IPolygon2D>(curveBorderWidth), //
               new GConstantExpression<IPolygon2D, IColor>(curveColor), //
               new GConstantExpression<IPolygon2D, Float>(curveOpacity));

      final IExpression<IPolygon2D, ISurface2DStyle> surfaceStyleExpression = new GSurface2DStyleExpression<IPolygon2D>( //
               new GConstantExpression<IPolygon2D, IColor>(surfaceColor), //
               new GConstantExpression<IPolygon2D, Float>(surfaceOpacity));

      return new GPolygon2DSymbolizerExpression(curveStyleExpression, surfaceStyleExpression);
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                                final IGlobeLayer unusedlayer) {
      if (unusedlayer != _layer) {
         throw new RuntimeException("Invalid layer");
      }

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> featuresCollection = _layer.getFeaturesCollection();


      final EnumSet<GGeometryType> geometriesTypes = featuresCollection.getGeometryType();

      final List<ILayerAttribute<?>> result = new ArrayList<ILayerAttribute<?>>();

      if (geometriesTypes.contains(GGeometryType.POINT)) {
         result.add(createPointsLayerAttributes(application));
      }

      if (geometriesTypes.contains(GGeometryType.CURVE)) {
         result.add(createCurvesLayerAttributes(application));
      }

      if (geometriesTypes.contains(GGeometryType.SURFACE)) {
         result.add(createSurfacesLayerAttributes(application));
      }

      result.add(createAdvancedLayerAttributes(application));

      return result;
   }


   private ILayerAttribute<?> createAdvancedLayerAttributes(@SuppressWarnings("unused") final IGlobeApplication application) {

      final GBooleanLayerAttribute clusterSymbols = new GBooleanLayerAttribute("Cluster Symbols",
               "Set the Cluster-Symbols option", "ClusterSymbols") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void set(final Boolean value) {
            setClusterSymbols(value);
         }


         @Override
         public Boolean get() {
            return isClusterSymbols();
         }
      };


      final GFloatLayerAttribute lodMinSize = new GFloatLayerAttribute("LOD Min Size", "", "LODMinSize", 0, 10,
               GFloatLayerAttribute.WidgetType.SPINNER, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return (float) getLODMinSize();
         }


         @Override
         public void set(final Float value) {
            setLODMinSize(value);
         }
      };

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


      return new GGroupAttribute("Advanced", "Advanced settings", clusterSymbols, lodMinSize, renderLODIgnores, debugRendering);
   }


   private ILayerAttribute<?> createPointsLayerAttributes(final IGlobeApplication application) {

      final GAreaLayerAttribute pointArea = new GAreaLayerAttribute("Size", "Set the point size", "PointArea", 0, 1000, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void set(final IMeasure<GArea> value) {
            setPointArea(value);
         }


         @Override
         public IMeasure<GArea> get() {
            return getPointArea();
         }
      };

      final GLengthLayerAttribute pointCurveBorderWidth = new GLengthLayerAttribute("Border Width", "", "PointCurveBorderWidth",
               0, 1000, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getPointCurveBorderWidth();
         }


         @Override
         public void set(final IMeasure<GLength> value) {
            setPointCurveBorderWidth(value);
         }
      };

      final GColorLayerAttribute pointCurveColor = new GColorLayerAttribute("Border Color", "", "PointCurveColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getPointCurveColor().asAWTColor();
         }


         @Override
         public void set(final Color value) {
            setPointCurveColor(GColorF.fromAWTColor(value));
         }
      };

      final GFloatLayerAttribute pointCurveOpacity = new GFloatLayerAttribute("Border Opacity", "", "PointCurveOpacity", 0, 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getPointCurveOpacity();
         }


         @Override
         public void set(final Float value) {
            setPointCurveOpacity(value);
         }
      };

      final GColorLayerAttribute pointSurfaceColor = new GColorLayerAttribute("Surface Color", "", "PointSurfaceColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getPointSurfaceColor().asAWTColor();
         }


         @Override
         public void set(final Color value) {
            setPointSurfaceColor(GColorF.fromAWTColor(value));
         }
      };

      final GFloatLayerAttribute pointSurfaceOpacity = new GFloatLayerAttribute("Surface Opacity", "", "PointSurfaceOpacity", 0,
               1, GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getPointSurfaceOpacity();
         }


         @Override
         public void set(final Float value) {
            setPointSurfaceOpacity(value);
         }
      };

      return new GGroupAttribute("Points Style", application.getSmallIcon(GFileName.relative("points-style.png")),
               "Points rendering settings", pointArea, pointCurveBorderWidth, pointCurveColor, pointCurveOpacity,
               pointSurfaceColor, pointSurfaceOpacity);
   }


   private ILayerAttribute<?> createCurvesLayerAttributes(final IGlobeApplication application) {

      final GLengthLayerAttribute curveBorderWidth = new GLengthLayerAttribute("Width", "", "CurveBorderWidth", 0, 1000, 1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getCurveBorderWidth();
         }


         @Override
         public void set(final IMeasure<GLength> value) {
            setCurveBorderWidth(value);
         }
      };

      final GColorLayerAttribute curveColor = new GColorLayerAttribute("Color", "", "CurveColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getCurveColor().asAWTColor();
         }


         @Override
         public void set(final Color value) {
            setCurveColor(GColorF.fromAWTColor(value));
         }
      };

      final GFloatLayerAttribute curveOpacity = new GFloatLayerAttribute("Opacity", "", "CurveOpacity", 0, 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getCurveOpacity();
         }


         @Override
         public void set(final Float value) {
            setCurveOpacity(value);
         }
      };

      return new GGroupAttribute("Curves Style", application.getSmallIcon(GFileName.relative("curves-style.png")),
               "Curves rendering settings", curveBorderWidth, curveColor, curveOpacity);
   }


   private ILayerAttribute<?> createSurfacesLayerAttributes(final IGlobeApplication application) {
      final GLengthLayerAttribute borderWidth = new GLengthLayerAttribute("Border Width", "", "SurfaceCurveBorderWidth", 0, 1000,
               1) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public IMeasure<GLength> get() {
            return getSurfaceCurveBorderWidth();
         }


         @Override
         public void set(final IMeasure<GLength> value) {
            setSurfaceCurveBorderWidth(value);
         }
      };

      final GColorLayerAttribute borderColor = new GColorLayerAttribute("Border Color", "", "SurfaceCurveColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getSurfaceCurveColor().asAWTColor();
         }


         @Override
         public void set(final Color value) {
            setSurfaceCurveColor(GColorF.fromAWTColor(value));
         }
      };

      final GFloatLayerAttribute borderOpacity = new GFloatLayerAttribute("Border Opacity", "", "SurfaceCurveOpacity", 0, 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getSurfaceCurveOpacity();
         }


         @Override
         public void set(final Float value) {
            setSurfaceCurveOpacity(value);
         }
      };


      final GColorLayerAttribute surfaceColor = new GColorLayerAttribute("Surface Color", "", "SurfaceColor") {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Color get() {
            return getSurfaceColor().asAWTColor();
         }


         @Override
         public void set(final Color value) {
            setSurfaceColor(GColorF.fromAWTColor(value));
         }
      };

      final GFloatLayerAttribute surfaceOpacity = new GFloatLayerAttribute("Surface Opacity", "", "SurfaceOpacity", 0, 1,
               GFloatLayerAttribute.WidgetType.SLIDER, 0.1f) {
         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public Float get() {
            return getSurfaceOpacity();
         }


         @Override
         public void set(final Float value) {
            setSurfaceOpacity(value);
         }
      };

      return new GGroupAttribute("Surfaces Style", application.getSmallIcon(GFileName.relative("surfaces-style.png")),
               "Surfaces rendering settings", borderWidth, borderColor, borderOpacity, surfaceColor, surfaceOpacity);
   }


   private void styleChanged() {
      _layer.clearCache();
      _layer.redraw();
   }


   @Override
   public void setDebugRendering(final boolean debugRendering) {
      if (debugRendering == isDebugRendering()) {
         return;
      }

      super.setDebugRendering(debugRendering);

      _layer.firePropertyChange("DebugRendering", !debugRendering, debugRendering);

      styleChanged();
   }


   @Override
   public void setLODMinSize(final double lodMinSize) {
      final double oldLodMinSize = getLODMinSize();

      if (GMath.closeTo(lodMinSize, oldLodMinSize)) {
         return;
      }

      super.setLODMinSize(lodMinSize);

      _layer.firePropertyChange("LODMinSize", oldLodMinSize, lodMinSize);

      styleChanged();
   }


   @Override
   public void setRenderLODIgnores(final boolean renderLODIgnores) {
      if (renderLODIgnores == isRenderLODIgnores()) {
         return;
      }

      super.setRenderLODIgnores(renderLODIgnores);

      _layer.firePropertyChange("RenderLODIgnores", !renderLODIgnores, renderLODIgnores);

      styleChanged();
   }


   @Override
   public void setClusterSymbols(final boolean clusterSymbols) {
      if (clusterSymbols == isClusterSymbols()) {
         return;
      }

      super.setClusterSymbols(clusterSymbols);

      _layer.firePropertyChange("ClusterSymbols", !clusterSymbols, clusterSymbols);

      styleChanged();
   }


   @Override
   public void setSurfaceExpression(final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> surfaceExpression) {
      final IExpression<ISurface2D<? extends IFinite2DBounds<?>>, GSymbol2DList> oldSurfaceExpression = getSurfaceExpression();

      if (GUtils.equals(surfaceExpression, oldSurfaceExpression)) {
         return;
      }

      super.setSurfaceExpression(surfaceExpression);

      _layer.firePropertyChange("SurfaceExpression", oldSurfaceExpression, surfaceExpression);

      styleChanged();
   }


   @Override
   public void setCurveExpression(final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> curveExpression) {
      final IExpression<ICurve2D<? extends IFinite2DBounds<?>>, GSymbol2DList> oldCurveExpression = getCurveExpression();

      if (GUtils.equals(curveExpression, oldCurveExpression)) {
         return;
      }

      super.setCurveExpression(curveExpression);

      _layer.firePropertyChange("CurveExpression", oldCurveExpression, curveExpression);

      styleChanged();
   }


   @Override
   public void setPointExpression(final IExpression<IVector2, GSymbol2DList> pointExpression) {
      final IExpression<IVector2, GSymbol2DList> oldPointExpression = getPointExpression();

      if (GUtils.equals(pointExpression, oldPointExpression)) {
         return;
      }

      super.setPointExpression(pointExpression);

      _layer.firePropertyChange("PointExpression", oldPointExpression, pointExpression);

      styleChanged();
   }


   public IMeasure<GLength> getCurveBorderWidth() {
      return _curveBorderWidth;
   }


   public IColor getCurveColor() {
      return _curveColor;
   }


   public float getCurveOpacity() {
      return _curveOpacity;
   }


   private void updateCurveExpression() {
      setCurveExpression(createCurveExpression(_curveBorderWidth, _curveColor, _curveOpacity));
   }


   public void setCurveBorderWidth(final IMeasure<GLength> curverBorderWidth) {
      final IMeasure<GLength> oldCurveBorderWidth = _curveBorderWidth;
      if (GUtils.equals(curverBorderWidth, oldCurveBorderWidth)) {
         return;
      }

      _curveBorderWidth = curverBorderWidth;
      _layer.firePropertyChange("CurveBorderWidth", oldCurveBorderWidth, curverBorderWidth);

      updateCurveExpression();
   }


   public void setCurveColor(final GColorF curveColor) {
      final IColor oldCurveColor = _curveColor;
      if (GUtils.equals(curveColor, oldCurveColor)) {
         return;
      }

      _curveColor = curveColor;
      _layer.firePropertyChange("CurveColor", oldCurveColor, curveColor);

      updateCurveExpression();
   }


   public void setCurveOpacity(final float curveOpacity) {
      final float oldCurveOpacity = _curveOpacity;
      if (GMath.closeTo(curveOpacity, oldCurveOpacity)) {
         return;
      }

      _curveOpacity = curveOpacity;
      _layer.firePropertyChange("CurveOpacity", oldCurveOpacity, curveOpacity);

      updateCurveExpression();
   }


   public IMeasure<GLength> getSurfaceCurveBorderWidth() {
      return _surfaceCurveBorderWidth;
   }


   public IColor getSurfaceCurveColor() {
      return _surfaceCurveColor;
   }


   public float getSurfaceCurveOpacity() {
      return _surfaceCurveOpacity;
   }


   public IColor getSurfaceColor() {
      return _surfaceColor;
   }


   public float getSurfaceOpacity() {
      return _surfaceOpacity;
   }


   private void updateSurfaceExpression() {
      setSurfaceExpression(createSurfaceExpression(_surfaceCurveBorderWidth, _surfaceCurveColor, _surfaceCurveOpacity,
               _surfaceColor, _surfaceOpacity));
   }


   public void setSurfaceCurveBorderWidth(final IMeasure<GLength> surfaceCurveBorderWidth) {
      //      _surfaceCurveBorderWidth = surfaceCurveBorderWidth;

      final IMeasure<GLength> oldSurfaceCurveBorderWidth = _surfaceCurveBorderWidth;
      if (GUtils.equals(surfaceCurveBorderWidth, oldSurfaceCurveBorderWidth)) {
         return;
      }

      _surfaceCurveBorderWidth = surfaceCurveBorderWidth;
      _layer.firePropertyChange("SurfaceCurveBorderWidth", oldSurfaceCurveBorderWidth, surfaceCurveBorderWidth);

      updateSurfaceExpression();
   }


   public void setSurfaceCurveColor(final IColor surfaceCurveColor) {
      final IColor oldSurfaceCurveColor = _surfaceCurveColor;
      if (GUtils.equals(surfaceCurveColor, oldSurfaceCurveColor)) {
         return;
      }

      _surfaceCurveColor = surfaceCurveColor;
      _layer.firePropertyChange("SurfaceCurveColor", oldSurfaceCurveColor, surfaceCurveColor);

      updateSurfaceExpression();
   }


   public void setSurfaceCurveOpacity(final float surfaceCurveOpacity) {
      final float oldSurfaceCurveOpacity = _surfaceCurveOpacity;
      if (GMath.closeTo(surfaceCurveOpacity, oldSurfaceCurveOpacity)) {
         return;
      }

      _surfaceCurveOpacity = surfaceCurveOpacity;
      _layer.firePropertyChange("SurfaceCurveOpacity", oldSurfaceCurveOpacity, surfaceCurveOpacity);

      updateSurfaceExpression();
   }


   public void setSurfaceColor(final IColor surfaceColor) {
      final IColor oldSurfaceColor = _surfaceColor;
      if (GUtils.equals(surfaceColor, oldSurfaceColor)) {
         return;
      }

      _surfaceColor = surfaceColor;
      _layer.firePropertyChange("SurfaceColor", oldSurfaceColor, surfaceColor);

      updateSurfaceExpression();
   }


   public void setSurfaceOpacity(final float surfaceOpacity) {
      final float oldSurfaceOpacity = _surfaceOpacity;
      if (GMath.closeTo(surfaceOpacity, oldSurfaceOpacity)) {
         return;
      }

      _surfaceOpacity = surfaceOpacity;
      _layer.firePropertyChange("SurfaceOpacity", oldSurfaceOpacity, surfaceOpacity);

      updateSurfaceExpression();
   }


   public IMeasure<GArea> getPointArea() {
      return _pointArea;
   }


   public IMeasure<GLength> getPointCurveBorderWidth() {
      return _pointCurveBorderWidth;
   }


   public IColor getPointCurveColor() {
      return _pointCurveColor;
   }


   public float getPointCurveOpacity() {
      return _pointCurveOpacity;
   }


   public IColor getPointSurfaceColor() {
      return _pointSurfaceColor;
   }


   public float getPointSurfaceOpacity() {
      return _pointSurfaceOpacity;
   }


   private void updatePointExpression() {
      setPointExpression(createPointExpression(_pointArea, _pointCurveBorderWidth, _pointCurveColor, _pointCurveOpacity,
               _pointSurfaceColor, _pointSurfaceOpacity));
   }


   public void setPointArea(final IMeasure<GArea> pointArea) {
      final IMeasure<GArea> oldPointArea = getPointArea();
      if (GUtils.equals(pointArea, oldPointArea)) {
         return;
      }

      _pointArea = pointArea;

      _layer.firePropertyChange("PointArea", oldPointArea, pointArea);

      updatePointExpression();
   }


   public void setPointCurveBorderWidth(final IMeasure<GLength> pointCurveBorderWidth) {
      final IMeasure<GLength> oldPointCurveBorderWidth = _pointCurveBorderWidth;
      if (GUtils.equals(pointCurveBorderWidth, oldPointCurveBorderWidth)) {
         return;
      }

      _pointCurveBorderWidth = pointCurveBorderWidth;
      _layer.firePropertyChange("PointCurveBorderWidth", oldPointCurveBorderWidth, pointCurveBorderWidth);

      updatePointExpression();
   }


   public void setPointCurveColor(final IColor pointCurveColor) {
      final IColor oldPointCurveColor = _pointCurveColor;
      if (GUtils.equals(pointCurveColor, oldPointCurveColor)) {
         return;
      }

      _pointCurveColor = pointCurveColor;
      _layer.firePropertyChange("PointCurveColor", oldPointCurveColor, pointCurveColor);

      updatePointExpression();
   }


   public void setPointCurveOpacity(final float pointCurveOpacity) {
      final float oldPointCurveOpacity = _pointCurveOpacity;
      if (GMath.closeTo(pointCurveOpacity, oldPointCurveOpacity)) {
         return;
      }

      _pointCurveOpacity = pointCurveOpacity;
      _layer.firePropertyChange("PointCurveOpacity", oldPointCurveOpacity, pointCurveOpacity);

      updatePointExpression();
   }


   public void setPointSurfaceColor(final IColor pointSurfaceColor) {
      final IColor oldPointSurfaceColor = _pointSurfaceColor;
      if (GUtils.equals(pointSurfaceColor, oldPointSurfaceColor)) {
         return;
      }

      _pointSurfaceColor = pointSurfaceColor;
      _layer.firePropertyChange("PointSurfaceColor", oldPointSurfaceColor, pointSurfaceColor);

      updatePointExpression();
   }


   public void setPointSurfaceOpacity(final float pointSurfaceOpacity) {
      final float oldPointCurveOpacity = _pointSurfaceOpacity;
      if (GMath.closeTo(pointSurfaceOpacity, oldPointCurveOpacity)) {
         return;
      }

      _pointSurfaceOpacity = pointSurfaceOpacity;
      _layer.firePropertyChange("PointSurfaceOpacity", oldPointCurveOpacity, pointSurfaceOpacity);

      updatePointExpression();
   }


}
