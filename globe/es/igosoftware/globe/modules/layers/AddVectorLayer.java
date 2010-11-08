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
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.GVectorLayerType;
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
import es.igosoftware.globe.layers.GLinesRenderer;
import es.igosoftware.globe.layers.GPointsRenderer;
import es.igosoftware.globe.layers.GPolygonsRenderer;
import es.igosoftware.globe.layers.GVectorRenderer;
import es.igosoftware.globe.layers.RasterRenderer;
import es.igosoftware.globe.layers.ShapefileTools;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;


public class AddVectorLayer
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
            return coloringMethods[((IGlobeVectorLayer) layer).getRenderer().getColoringMethod()];
         }


         @Override
         public void set(final String value) {
            int iMethod = GVectorRenderer.COLORING_METHOD_COLOR_RAMP;
            if (value.equals("Unique color")) {
               iMethod = GVectorRenderer.COLORING_METHOD_UNIQUE;
            }
            else if (value.equals("Color ramp")) {
               iMethod = GVectorRenderer.COLORING_METHOD_COLOR_RAMP;
            }
            else if (value.equals("Lookup table")) {
               iMethod = RasterRenderer.COLORING_METHOD_LUT;
            }
            ((IGlobeVectorLayer) layer).getRenderer().setColoringMethod(iMethod);
            ((IGlobeVectorLayer) layer).redraw();
         }

      };

      final ILayerAttribute<?> color = new GColorLayerAttribute("Unique Color", "UniqueColor") {
         @Override
         public boolean isVisible() {
            return (layer instanceof IGlobeVectorLayer);
         }


         @Override
         public Color get() {
            return ((IGlobeVectorLayer) layer).getRenderer().getColor();
         }


         @Override
         public void set(final Color value) {
            ((IGlobeVectorLayer) layer).getRenderer().setColor(value);
            ((IGlobeVectorLayer) layer).redraw();
         }
      };

      final ILayerAttribute<?> borderColor = new GColorLayerAttribute("Border Color", "BorderColor") {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return iShapeType == GVectorLayerType.POLYGON;
            }
            return false;
         }


         @Override
         public Color get() {
            return ((IGlobeVectorLayer) layer).getRenderer().getColor();
         }


         @Override
         public void set(final Color value) {
            ((IGlobeVectorLayer) layer).getRenderer().setColor(value);
            ((IGlobeVectorLayer) layer).redraw();
         }
      };

      final ILayerAttribute<?> ramp = new GColorRampLayerAttribute("Color ramp", COLOR_RAMP) {
         @Override
         public boolean isVisible() {
            return layer instanceof IGlobeVectorLayer;
         }


         @Override
         public LinearGradientPaint get() {
            final IGlobeVectorLayer vectorLayer = (IGlobeVectorLayer) layer;
            return vectorLayer.getRenderer().getGradient();
         }


         @Override
         public void set(final LinearGradientPaint gradient) {
            ((IGlobeVectorLayer) layer).getRenderer().setGradient(gradient);
            ((IGlobeVectorLayer) layer).redraw();
         }
      };

      final ILayerAttribute<?> colorField = new GVectorFieldLayerAttribute("Color field", COLOR_FIELD) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return iShapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GField[] fields = ((IGlobeVectorLayer) layer).getFields();
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            return fields[rend.getFieldIndex()].getName();
         }


         @Override
         public void set(final String value) {

            int iField = 0;
            final GField[] fields = ((IGlobeVectorLayer) layer).getFields();

            for (int i = 0; i < fields.length; i++) {
               if (fields[i].getName().equals(value.toString())) {
                  iField = i;
               }
            }
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            rend.setFieldIndex(iField);
            ((IGlobeVectorLayer) layer).redraw();
         }

      };


      final ILayerAttribute<?> thickness = new GFloatLayerAttribute("Line thickness", LINE_THICKNESS, 1, 8,
               GFloatLayerAttribute.WidgetType.SPINNER, 1) {

         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return (iShapeType == GVectorLayerType.LINE) || (iShapeType == GVectorLayerType.POLYGON);
            }
            return false;
         }


         @Override
         public Float get() {
            final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
            if (iShapeType == GVectorLayerType.LINE) {
               return (float) ((GLinesRenderer) ((IGlobeVectorLayer) layer).getRenderer()).getLineThickness();
            }
            else if (iShapeType == GVectorLayerType.POLYGON) {
               return (float) ((GPolygonsRenderer) ((IGlobeVectorLayer) layer).getRenderer()).getBorderThickness();
            }
            else {
               return 1f;
            }
         }


         @Override
         public void set(final Float value) {
            final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
            if (iShapeType == GVectorLayerType.LINE) {
               ((GLinesRenderer) ((IGlobeVectorLayer) layer).getRenderer()).setLineThickness(value.intValue());
            }
            else if (iShapeType == GVectorLayerType.POLYGON) {
               ((GPolygonsRenderer) ((IGlobeVectorLayer) layer).getRenderer()).setBorderThickness(value.intValue());
            }
            ((IGlobeVectorLayer) layer).redraw();
         }
      };

      final String[] altitudeMethods = new String[] { "Clamped to the ground", "Relative", "Absolute" };
      final ILayerAttribute<?> altitudeMethod = new GSelectionLayerAttribute<String>("Altitude method", ALTITUDE_METHOD,
               altitudeMethods) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return iShapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            return altitudeMethods[rend.getAltitudeMethod()];
         }


         @Override
         public void set(final String value) {
            int iMethod = GPointsRenderer.ALTITUDE_METHOD_CLAMPED_TO_GROUND;
            if (value.equals("Clamped to the ground")) {
               iMethod = GPointsRenderer.ALTITUDE_METHOD_CLAMPED_TO_GROUND;
            }
            else if (value.equals("Relative")) {
               iMethod = GPointsRenderer.ALTITUDE_METHOD_RELATIVE_TO_GROUND;
            }
            else if (value.equals("Absolute")) {
               iMethod = GPointsRenderer.ALTITUDE_METHOD_ABSOLUTE;
            }
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            rend.setAltitudeMethod(iMethod);
            ((IGlobeVectorLayer) layer).redraw();
         }

      };

      final String[] altitudeSources = new String[] { "Fixed value", "Field" };
      final ILayerAttribute<?> altitudeSource = new GSelectionLayerAttribute<String>("Take altitude from", ALTITUDE_SOURCE,
               altitudeSources) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return iShapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            return altitudeSources[rend.getAltitudeOrigin()];
         }


         @Override
         public void set(final String value) {
            int iMethod = GPointsRenderer.TAKE_ALTITUDE_FROM_FIXED;
            if (value.equals("Fixed value")) {
               iMethod = GPointsRenderer.TAKE_ALTITUDE_FROM_FIXED;
            }
            else if (value.equals("Field")) {
               iMethod = GPointsRenderer.TAKE_ALTITUDE_FROM_FIELD;
            }
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            rend.setAltitudeOrigin(iMethod);
            ((IGlobeVectorLayer) layer).redraw();
         }

      };

      final ILayerAttribute<?> altitudeField = new GVectorFieldLayerAttribute("Altitude field", ALTITUDE_FIELD) {
         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return iShapeType == GVectorLayerType.POINT;
            }
            return false;
         }


         @Override
         public String get() {
            final GField[] fields = ((IGlobeVectorLayer) layer).getFields();
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            return fields[rend.getAltitudeField()].getName();
         }


         @Override
         public void set(final String value) {

            int iField = 0;
            final GField[] fields = ((IGlobeVectorLayer) layer).getFields();

            for (int i = 0; i < fields.length; i++) {
               if (fields[i].getName().equals(value.toString())) {
                  iField = i;
               }
            }
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            rend.setAltitudeField(iField);
            ((IGlobeVectorLayer) layer).redraw();
         }

      };

      final ILayerAttribute<?> fixedAltitude = new GFloatLayerAttribute("Fixed altitude", FIXED_ALTITUDE, 0, Float.MAX_VALUE,
               GFloatLayerAttribute.WidgetType.TEXTBOX, Float.MIN_VALUE) {

         @Override
         public boolean isVisible() {
            if (layer instanceof IGlobeVectorLayer) {
               final GVectorLayerType iShapeType = ((IGlobeVectorLayer) layer).getShapeType();
               return (iShapeType == GVectorLayerType.POINT);
            }
            return false;
         }


         @Override
         public Float get() {
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            return new Float(rend.getFixedAltitude());
         }


         @Override
         public void set(final Float value) {
            final GPointsRenderer rend = (GPointsRenderer) ((IGlobeVectorLayer) layer).getRenderer();
            rend.setFixedAltitude(value.doubleValue());
            ((IGlobeVectorLayer) layer).redraw();
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
            final IGlobeVectorLayer vl = ShapefileTools.readFile(new File(sFilename));
            if (vl != null) {
               vl.setProjection((GProjection) selectedValue);
               vl.redraw();
               application.getModel().getLayers().add(vl);
               vl.setPickEnabled(false);
               return vl;
            }
         }
         catch (final Exception e) {
            //TODO:
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
      // TODO Auto-generated method stub

   }


}
