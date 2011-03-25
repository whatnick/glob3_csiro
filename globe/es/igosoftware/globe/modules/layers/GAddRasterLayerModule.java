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

import java.awt.Component;
import java.awt.LinearGradientPaint;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GColorRampLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GSelectionLayerAttribute;
import es.igosoftware.globe.attributes.GStringLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.ESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.globe.layers.RasterRenderer;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;


public class GAddRasterLayerModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {

   private static final String COLOR_RAMP      = "COLOR_RAMP";
   private static final String COLORING_METHOD = "COLORING_METHOD";


   @Override
   public String getName() {
      return "Add raster layer";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Module to open raster layers";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      return null;
   }


   @SuppressWarnings("unchecked")
   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {

      final ILayerAttribute<?> rows = new GStringLayerAttribute("Rows", true) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return Integer.toString(((GGlobeRasterLayer) layer).getRasterGeodata()._rows);
         }


         @Override
         public void set(final String value) {
         }

      };

      final ILayerAttribute<?> cols = new GStringLayerAttribute("Cols", true) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return Integer.toString(((GGlobeRasterLayer) layer).getRasterGeodata()._cols);
         }


         @Override
         public void set(final String value) {
         }

      };

      final ILayerAttribute<?> nodata = new GFloatLayerAttribute("No-data value", false, Float.NEGATIVE_INFINITY,
               Float.POSITIVE_INFINITY, GFloatLayerAttribute.WidgetType.TEXTBOX, Float.MIN_VALUE) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public Float get() {
            return new Float(((GGlobeRasterLayer) layer).getNoDataValue());
         }


         @Override
         public void set(final Float value) {
            ((GGlobeRasterLayer) layer).setNoDataValue(value.doubleValue());
            ((GGlobeRasterLayer) layer).redraw();
         }

      };

      final String[] coloringMethods = new String[] { "RGB", "Color ramp", "Lookup table" };
      final ILayerAttribute<?> method = new GSelectionLayerAttribute<String>("Coloring method", COLORING_METHOD, coloringMethods) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public String get() {
            return coloringMethods[(((GGlobeRasterLayer) layer).getRenderer().getColoringMethod())];
         }


         @Override
         public void set(final String value) {
            int iMethod = RasterRenderer.COLORING_METHOD_COLOR_RAMP;
            if (value.equals("RGB")) {
               iMethod = RasterRenderer.COLORING_METHOD_RGB;
            }
            else if (value.equals("Color ramp")) {
               iMethod = RasterRenderer.COLORING_METHOD_COLOR_RAMP;
            }
            else if (value.equals("Lookup table")) {
               iMethod = RasterRenderer.COLORING_METHOD_LUT;
            }
            ((GGlobeRasterLayer) layer).getRenderer().setColoringMethod(iMethod);
            ((GGlobeRasterLayer) layer).redraw();
         }

      };

      final ILayerAttribute<?> ramp = new GColorRampLayerAttribute("Color ramp", COLOR_RAMP) {
         @Override
         public boolean isVisible() {
            return layer instanceof GGlobeRasterLayer;
         }


         @Override
         public LinearGradientPaint get() {
            final GGlobeRasterLayer gRasterLayer = (GGlobeRasterLayer) layer;
            return gRasterLayer.getRenderer().getGradient();
         }


         @Override
         public void set(final LinearGradientPaint gradient) {
            ((GGlobeRasterLayer) layer).getRenderer().setGradient(gradient);
            ((GGlobeRasterLayer) layer).redraw();
         }

      };

      //      return new ILayerAttribute<?>[] { rows, cols, nodata, method, ramp };

      return GCollections.createList(rows, cols, nodata, method, ramp);
   }


   @Override
   public IGlobeLayer addNewLayer(final IGlobeApplication application,
                                  final ILayerInfo layerInfo) {

      final JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new GGenericFileFilter(new String[] { "asc" }, "ESRI ArcInfo ASCII (*.asc)"));
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
            final GGlobeRasterLayer rl = ESRIAsciiFileTools.readFile(new File(sFilename), (GProjection) selectedValue);
            if (rl != null) {
               application.getModel().getLayers().add(rl);
               rl.setPickEnabled(false);
               return rl;
            }
         }
         catch (final Exception e) {
            e.printStackTrace();
         }
      }

      return null;
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeApplication application) {
      return GCollections.createList(new GLayerInfo("ESRI ArcInfo ASCII"));
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {

   }


}
