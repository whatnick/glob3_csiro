/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.modules.layers;

import java.awt.Color;
import java.awt.Component;
import java.awt.LinearGradientPaint;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.GVectorLayerType;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.GShapefileTools;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GColorLayerAttribute;
import es.igosoftware.globe.attributes.GColorRampLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GSelectionLayerAttribute;
import es.igosoftware.globe.attributes.GVectorFieldLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.GGlobeVector2Layer;
import es.igosoftware.globe.layers.GLines2RenderingTheme;
import es.igosoftware.globe.layers.GPoints2RenderingTheme;
import es.igosoftware.globe.layers.GPolygons2RenderingTheme;
import es.igosoftware.globe.layers.GVector2RenderingTheme;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;


public class GAddVectorLayerModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {

   private static final String COLORING_METHOD = "COLORING_METHOD";
   private static final String ALTITUDE_METHOD = "ALTITUDE_METHOD";
   private static final String COLOR_RAMP      = "COLOR_RAMP";
   private static final String ALTITUDE_SOURCE = "ALTITUDE_SOURCE";
   private static final String ALTITUDE_FIELD  = "ALTITUDE_FIELD";
   private static final String COLOR_FIELD     = "COLOR_FIELD";
   private static final String LINE_THICKNESS  = "LINE_THICKNESS";
   private static final String FIXED_ALTITUDE  = "FIXED_ALTITUDE";


   @Override
   public String getName() {
      return "Add vector layer";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to open vector layers";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application,
                                             final IGlobeLayer layer) {
      return null;
   }


   @SuppressWarnings("unchecked")
   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {

      final String[] coloringMethods = new String[] { "Unique color", "Color ramp", "Lookup table" };
      final ILayerAttribute<?> method = new GSelectionLayerAttribute<String>("Coloring method", COLORING_METHOD, coloringMethods) {
         @Override
         public boolean isVisible() {
            return layer instanceof IGlobeVectorLayer;
         }


         @Override
         public String get() {
            return coloringMethods[((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme().getColoringMethod().ordinal()];
         }


         @Override
         public void set(final String value) {


            GVector2RenderingTheme.ColoringMethod iMethod = GVector2RenderingTheme.ColoringMethod.COLOR_RAMP;
            if (value.equals("Unique color")) {
               iMethod = GVector2RenderingTheme.ColoringMethod.UNIQUE;
            }
            else if (value.equals("Color ramp")) {
               iMethod = GVector2RenderingTheme.ColoringMethod.COLOR_RAMP;
            }
            else if (value.equals("Lookup table")) {
               iMethod = GVector2RenderingTheme.ColoringMethod.COLOR_LUT;
            }
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            vectorLayer.getRenderingTheme().setColoringMethod(iMethod);
            vectorLayer.redraw();
         }

      };

      final ILayerAttribute<?> color = new GColorLayerAttribute("Unique Color", "UniqueColor") {
         @Override
         public boolean isVisible() {
            return (layer instanceof IGlobeVectorLayer);
         }


         @Override
         public Color get() {
            return ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme().getColor();
         }


         @Override
         public void set(final Color value) {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            vectorLayer.getRenderingTheme().setColor(value);
            vectorLayer.redraw();
         }
      };

      final ILayerAttribute<?> borderColor = new GColorLayerAttribute("Border Color", "BorderColor") {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return shapeType == GVectorLayerType.POLYGON;
            }
            return false;
         }


         @Override
         public Color get() {
            return ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme().getColor();
         }


         @Override
         public void set(final Color value) {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            vectorLayer.getRenderingTheme().setColor(value);
            vectorLayer.redraw();
         }
      };

      final ILayerAttribute<?> ramp = new GColorRampLayerAttribute("Color ramp", COLOR_RAMP) {
         @Override
         public boolean isVisible() {
            return layer instanceof IGlobeVectorLayer;
         }


         @Override
         public LinearGradientPaint get() {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            return vectorLayer.getRenderingTheme().getGradient();
         }


         @Override
         public void set(final LinearGradientPaint gradient) {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            vectorLayer.getRenderingTheme().setGradient(gradient);
            vectorLayer.redraw();
         }
      };

      final ILayerAttribute<?> colorField = new GVectorFieldLayerAttribute("Color field", COLOR_FIELD) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return shapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final List<GField> fields = vectorLayer.getFeaturesCollection().getFields();
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            return fields.get(rend.getFieldIndex()).getName();
         }


         @Override
         public void set(final String value) {
            int iField = 0;
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final List<GField> fields = vectorLayer.getFeaturesCollection().getFields();

            for (int i = 0; i < fields.size(); i++) {
               if (fields.get(i).getName().equals(value.toString())) {
                  iField = i;
               }
            }
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            rend.setFieldIndex(iField);
            vectorLayer.redraw();
         }

      };


      final ILayerAttribute<?> thickness = new GFloatLayerAttribute("Line thickness", LINE_THICKNESS, 1, 8,
               GFloatLayerAttribute.WidgetType.SPINNER, 1) {

         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return (shapeType == GVectorLayerType.LINE) || (shapeType == GVectorLayerType.POLYGON);
            }
            return false;
         }


         @Override
         public Float get() {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final GVectorLayerType shapeType = vectorLayer.getFeaturesCollection().getShapeType();
            if (shapeType == GVectorLayerType.LINE) {
               return (float) ((GLines2RenderingTheme) vectorLayer.getRenderingTheme()).getLineThickness();
            }
            else if (shapeType == GVectorLayerType.POLYGON) {
               return (float) ((GPolygons2RenderingTheme) vectorLayer.getRenderingTheme()).getBorderThickness();
            }
            else {
               return 1f;
            }
         }


         @Override
         public void set(final Float value) {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final GVectorLayerType shapeType = vectorLayer.getFeaturesCollection().getShapeType();
            if (shapeType == GVectorLayerType.LINE) {
               ((GLines2RenderingTheme) vectorLayer.getRenderingTheme()).setLineThickness(value.intValue());
            }
            else if (shapeType == GVectorLayerType.POLYGON) {
               ((GPolygons2RenderingTheme) vectorLayer.getRenderingTheme()).setBorderThickness(value.intValue());
            }
            vectorLayer.redraw();
         }
      };

      final String[] altitudeMethods = new String[] { "Clamped to the ground", "Relative", "Absolute" };
      final ILayerAttribute<?> altitudeMethod = new GSelectionLayerAttribute<String>("Altitude method", ALTITUDE_METHOD,
               altitudeMethods) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return shapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme();
            return altitudeMethods[rend.getAltitudeMethod().ordinal()];
         }


         @Override
         public void set(final String value) {
            GPoints2RenderingTheme.AltitudeMethod iMethod = GPoints2RenderingTheme.AltitudeMethod.CLAMPED_TO_GROUND;
            if (value.equals("Clamped to the ground")) {
               iMethod = GPoints2RenderingTheme.AltitudeMethod.CLAMPED_TO_GROUND;
            }
            else if (value.equals("Relative")) {
               iMethod = GPoints2RenderingTheme.AltitudeMethod.RELATIVE_TO_GROUND;
            }
            else if (value.equals("Absolute")) {
               iMethod = GPoints2RenderingTheme.AltitudeMethod.ABSOLUTE;
            }
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            rend.setAltitudeMethod(iMethod);
            vectorLayer.redraw();
         }

      };

      final String[] altitudeSources = new String[] { "Fixed value", "Field" };
      final ILayerAttribute<?> altitudeSource = new GSelectionLayerAttribute<String>("Take altitude from", ALTITUDE_SOURCE,
               altitudeSources) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return shapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme();
            return altitudeSources[rend.getAltitudeOrigin().ordinal()];
         }


         @Override
         public void set(final String value) {
            GPoints2RenderingTheme.TakeAltitude iMethod = GPoints2RenderingTheme.TakeAltitude.FROM_FIXED;
            if (value.equals("Fixed value")) {
               iMethod = GPoints2RenderingTheme.TakeAltitude.FROM_FIXED;
            }
            else if (value.equals("Field")) {
               iMethod = GPoints2RenderingTheme.TakeAltitude.FROM_FIELD;
            }
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            rend.setAltitudeOrigin(iMethod);
            vectorLayer.redraw();
         }

      };

      final ILayerAttribute<?> altitudeField = new GVectorFieldLayerAttribute("Altitude field", ALTITUDE_FIELD) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return shapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final List<GField> fields = vectorLayer.getFeaturesCollection().getFields();
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            return fields.get(rend.getAltitudeField()).getName();
         }


         @Override
         public void set(final String value) {

            int iField = 0;
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final List<GField> fields = vectorLayer.getFeaturesCollection().getFields();

            for (int i = 0; i < fields.size(); i++) {
               if (fields.get(i).getName().equals(value.toString())) {
                  iField = i;
               }
            }
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            rend.setAltitudeField(iField);
            vectorLayer.redraw();
         }

      };

      final ILayerAttribute<?> fixedAltitude = new GFloatLayerAttribute("Fixed altitude", FIXED_ALTITUDE, 0, Float.MAX_VALUE,
               GFloatLayerAttribute.WidgetType.TEXTBOX, Float.MIN_VALUE) {

         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType shapeType = ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getFeaturesCollection().getShapeType();
               return (shapeType == GVectorLayerType.POINT);
            }
            return false;
         }


         @Override
         public Float get() {
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) ((IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer).getRenderingTheme();
            return new Float(rend.getFixedAltitude());
         }


         @Override
         public void set(final Float value) {
            final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;
            final GPoints2RenderingTheme rend = (GPoints2RenderingTheme) vectorLayer.getRenderingTheme();
            rend.setFixedAltitude(value.doubleValue());
            vectorLayer.redraw();
         }
      };


      return GCollections.createList(method, color, ramp, colorField, thickness, borderColor, /*shape,*/altitudeMethod,
               altitudeSource, altitudeField, fixedAltitude);
   }


   @Override
   public IGlobeLayer addNewLayer(final IGlobeApplication application,
                                  final ILayerInfo layerInfo) {

      final JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new GGenericFileFilter(new String[] { "shp" }, "ESRI Shapefiles (*.shp)"));
      final int returnVal = fc.showOpenDialog(application.getFrame());

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final Object[] possibleValues = GProjection.getEPSGProjections();
         final Object selectedValue = JOptionPane.showInputDialog(null, "Choose layer projection", "Projection",
                  JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
         if (selectedValue == null) {
            return null;
         }
         final String sFilename = fc.getSelectedFile().getAbsolutePath();

         try {
            final File file = new File(sFilename);
            final GListFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features = GShapefileTools.readFile(file);
            if (features != null) {

               final GGlobeVector2Layer layer = new GGlobeVector2Layer(file.getName(), features);

               layer.redraw();
               application.getLayerList().add(layer);
               layer.setPickEnabled(false);
               return layer;
            }
         }
         catch (final IOException e) {
            application.logSevere(e);
         }

      }

      return null;
   }


   @Override
   public List<ILayerInfo> getAvailableLayers(final IGlobeApplication application) {
      return GCollections.createList(new GLayerInfo("Shape File"));
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {

   }


}
