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


package es.igosoftware.experimental.pointscloud.rendering;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.GButtonLayerAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.GBooleanLayerAttribute;
import es.igosoftware.globe.attributes.GColorLayerAttribute;
import es.igosoftware.globe.attributes.GFloatLayerAttribute;
import es.igosoftware.globe.attributes.GGroupAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.pointscloud.IPointsCloudLoader;
import es.igosoftware.util.GPair;


public class GPointsCloudModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {

   private final IPointsCloudLoader _pointsCloudLoader;


   public GPointsCloudModule(final IPointsCloudLoader pointsCloudLoader) {
      _pointsCloudLoader = pointsCloudLoader;
   }


   @Override
   public String getName() {
      return "Points Clouds";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Points Clouds visualization, analysis and edition";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;
   }


   //   private abstract class PoinsCloudLayerAction
   //            extends
   //               GLayerAction {
   //
   //      private final IGlobeLayer _layer;
   //
   //
   //      private PoinsCloudLayerAction(final IGlobeLayer layer,
   //                                    final String label,
   //                                    final Icon icon,
   //                                    final boolean showOnToolBar) {
   //         super(label, icon, showOnToolBar);
   //         _layer = layer;
   //      }
   //
   //
   //      @Override
   //      public final boolean isVisible() {
   //         return (_layer instanceof GPointsCloudLayer);
   //      }
   //
   //
   //      @Override
   //      public final void execute() {
   //         execute((GPointsCloudLayer) _layer);
   //      }
   //
   //
   //      protected abstract void execute(final GPointsCloudLayer layer);
   //
   //   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {

      final ILayerAction reload = new GButtonLayerAction("Reload", application.getSmallIcon(GFileName.relative("reload.png")),
               true) {
         @Override
         public void execute() {
            ((GPointsCloudLayer) layer).reload();
         }


         @Override
         public boolean isVisible() {
            return layer instanceof GPointsCloudLayer;
         }
      };

      return Collections.singletonList(reload);
   }


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                                final IGlobeLayer layer) {
      return Arrays.asList(createInfoGroup(layer), createColorizationGroup(layer), createPointsShapeGroup(layer),
               createQualityGroup(layer));
   }


   private GGroupAttribute createQualityGroup(final IGlobeLayer layer) {

      final GBooleanLayerAttribute autoQuality = new GBooleanLayerAttribute("Auto Quality",
               "Tune the quality automatically trying to find a good compromise between quality and frames per second", null) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).getAutoQuality();
         }


         @Override
         public void set(final Boolean value) {
            ((GPointsCloudLayer) layer).setAutoQuality(value);
         }
      };

      final GFloatLayerAttribute qualityFactor = new GFloatLayerAttribute("Quality Factor", "Set the quality factor",
               "QualityFactor", GPointsCloudLayer.MIN_QUALITYFACTOR, GPointsCloudLayer.MAX_QUALITYFACTOR,
               GFloatLayerAttribute.WidgetType.SLIDER, GPointsCloudLayer.STEP_QUALITYFACTOR) {

         @Override
         public boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Float get() {
            return ((GPointsCloudLayer) layer).getQualityFactor();
         }


         @Override
         public void set(final Float value) {
            ((GPointsCloudLayer) layer).setQualityFactor(value);
         }
      };


      return new GGroupAttribute("Quality", "", autoQuality, qualityFactor);
   }


   private GGroupAttribute createPointsShapeGroup(final IGlobeLayer layer) {

      final GBooleanLayerAttribute smooth = new GBooleanLayerAttribute("Smooth", "Render smooth points", null) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).getSmooth();
         }


         @Override
         public void set(final Boolean value) {
            ((GPointsCloudLayer) layer).setSmooth(value);
         }
      };


      final GFloatLayerAttribute pointSize = new GFloatLayerAttribute("Point Size", "Set the point size", "PointSize",
               GPointsCloudLayer.MIN_POINT_SIZE, GPointsCloudLayer.MAX_POINT_SIZE, GFloatLayerAttribute.WidgetType.SLIDER,
               GPointsCloudLayer.STEP_POINT_SIZE) {

         @Override
         public boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Float get() {
            return ((GPointsCloudLayer) layer).getPointSize();
         }


         @Override
         public void set(final Float value) {
            ((GPointsCloudLayer) layer).setPointSize(value);
         }
      };


      final GBooleanLayerAttribute dynamicPointSize = new GBooleanLayerAttribute("Dynamic point size",
               "Change the point size based dynamically based on camera position", null) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).getDynamicPointSize();
         }


         @Override
         public void set(final Boolean value) {
            ((GPointsCloudLayer) layer).setDynamicPointSize(value);
         }
      };


      return new GGroupAttribute("Points Shape", "", smooth, pointSize, dynamicPointSize);
   }


   private GGroupAttribute createColorizationGroup(final IGlobeLayer layer) {

      final GBooleanLayerAttribute colorFromElevation = new GBooleanLayerAttribute("Color from Elevation",
               "Set the points color from the elevation (Z)", null) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).getColorFromElevation();
         }


         @Override
         public void set(final Boolean value) {
            ((GPointsCloudLayer) layer).setColorFromElevation(value);
         }
      };


      final GBooleanLayerAttribute colorFromState = new GBooleanLayerAttribute("Color from State",
               "Set the points colors based on algorithm state", null) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).getColorFromState();
         }


         @Override
         public void set(final Boolean value) {
            ((GPointsCloudLayer) layer).setColorFromState(value);
         }
      };


      final GColorLayerAttribute pointsColor = new GColorLayerAttribute("Points Color", "PointsColor", "Set the points color") {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Color get() {
            return ((GPointsCloudLayer) layer).getPointsColor();
         }


         @Override
         public void set(final Color value) {
            ((GPointsCloudLayer) layer).setPointsColor(value);
         }
      };

      return new GGroupAttribute("Colorization", "", colorFromElevation, colorFromState, pointsColor);
   }


   private GGroupAttribute createInfoGroup(final IGlobeLayer layer) {

      final GBooleanLayerAttribute hasColors = new GBooleanLayerAttribute("Has Colors", null, null, true) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).hasColors();
         }


         @Override
         public void set(final Boolean value) {
         }
      };

      final GBooleanLayerAttribute hasNormals = new GBooleanLayerAttribute("Has Normals", null, null, true) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).hasNormals();
         }


         @Override
         public void set(final Boolean value) {
         }
      };


      final GBooleanLayerAttribute hasIntensities = new GBooleanLayerAttribute("Has Intensities", null, null, true) {
         @Override
         public final boolean isVisible() {
            return (layer instanceof GPointsCloudLayer);
         }


         @Override
         public Boolean get() {
            return ((GPointsCloudLayer) layer).hasIntensities();
         }


         @Override
         public void set(final Boolean value) {
         }
      };

      return new GGroupAttribute("Information", "", hasColors, hasNormals, hasIntensities);
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeApplication application) {
      try {
         final List<String> pointsCloudsNames = _pointsCloudLoader.getPointsCloudsNames();
         Collections.sort(pointsCloudsNames);
         return GLayerInfo.createFromNames(pointsCloudsNames);
      }
      catch (final IOException e) {
         application.logSevere(e);
         return Collections.emptyList();
      }
   }


   @Override
   public final IGlobeLayer addNewLayer(final IGlobeApplication application,
                                        final ILayerInfo layerInfo) {
      final GPointsCloudLayer layer = new GPointsCloudLayer(GFileName.relative(layerInfo.getName()), _pointsCloudLoader);
      //      _layer.setShowExtents(true);
      layer.setVerbose(true);
      //      _layer.setColorFromState(false);
      //      _layer.setDynamicPointSize(false);
      //      _layer.setColorFromElevation(true);

      application.addLayer(layer);

      return layer;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", "Reload", "Recargar");
      application.addTranslation("es", "Has Colors", "Colores");
      application.addTranslation("es", "Has Normals", "Normales");
      application.addTranslation("es", "Has Intensities", "Intensidades");
      application.addTranslation("es", "Color from Elevation", "Color desde Altura");
      application.addTranslation("es", "Color from State", "Color desde Estado");
      application.addTranslation("es", "Points Color", "Color de Puntos");
      application.addTranslation("es", "Smooth", "Punto Suave");
      application.addTranslation("es", "Point Size", "Tamaño de Punto");
      application.addTranslation("es", "Dynamic point size", "Tamaño Dinámico de Punto");
      application.addTranslation("es", "Auto Quality", "Auto-calidad");
      application.addTranslation("es", "Quality Factor", "Factor de Calidad");
   }

}
