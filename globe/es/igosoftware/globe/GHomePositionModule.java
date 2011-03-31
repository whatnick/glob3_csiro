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


package es.igosoftware.globe;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.utils.GOnFirstRenderLayer;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;


public class GHomePositionModule
         extends
            GAbstractGlobeModule {


   private static final GFileName DEFAULT_ICON_NAME = GFileName.relative("home.png");
   private static final String    DEFAULT_LABEL     = "Go to home";

   private boolean                _gotoToHomeOnStartup;
   private final Position         _position;
   private final double           _elevation;
   private final Angle            _heading;
   private final Angle            _pitch;
   private final GFileName        _iconName;
   private final String           _label;


   public GHomePositionModule(final Position position,
                              final Angle heading,
                              final Angle pitch,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName,
                              final String label) {
      _position = position;
      _heading = heading;
      _pitch = pitch;
      _elevation = elevation;
      _gotoToHomeOnStartup = gotoToHomeOnStartup;
      _iconName = iconName;
      _label = label;
   }


   public GHomePositionModule(final Position position,
                              final Angle heading,
                              final Angle pitch,
                              final double elevation,
                              final boolean gotoToHomeOnStartup) {
      this(position, heading, pitch, elevation, gotoToHomeOnStartup, DEFAULT_ICON_NAME, DEFAULT_LABEL);
   }


   public GHomePositionModule(final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName) {
      this(position, Angle.ZERO, Angle.ZERO, elevation, gotoToHomeOnStartup, iconName, DEFAULT_LABEL);
   }


   public GHomePositionModule(final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup,
                              final GFileName iconName,
                              final String label) {
      this(position, Angle.ZERO, Angle.ZERO, elevation, gotoToHomeOnStartup, iconName, label);
   }


   public GHomePositionModule(final Position position,
                              final double elevation,
                              final boolean gotoToHomeOnStartup) {
      this(position, elevation, gotoToHomeOnStartup, DEFAULT_ICON_NAME);
   }


   public boolean isGotoToHomeOnStartup() {
      return _gotoToHomeOnStartup;
   }


   public void setGotoToHomeOnStartup(final boolean gotoToHomeOnStartup) {
      _gotoToHomeOnStartup = gotoToHomeOnStartup;
   }


   @Override
   public String getDescription() {
      return "Handler for home-position";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {
      final IGenericAction gotoHome = new GButtonGenericAction(_label, application.getIcon(_iconName),
               IGenericAction.MenuArea.NAVIGATION, true) {

         @Override
         public void execute() {
            doIt(application);
         }
      };

      return Collections.singletonList(gotoHome);
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      super.initialize(application);

      if (_gotoToHomeOnStartup) {
         application.addLayer(new GOnFirstRenderLayer() {
            @Override
            protected void execute(final DrawContext dc) {
               doIt(application);
            }
         });
      }

   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public String getName() {
      return "Home Position Module";
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   private void doIt(final IGlobeApplication application) {
      if ((_heading == null) || (_pitch == null)) {
         application.goTo(_position, _elevation);
      }
      else {
         application.goTo(_position, _heading, _pitch, _elevation);
      }
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", "Go to home", "Ir a casa");
      application.addTranslation("de", "Go to home", "Nach Hause gehen");
      application.addTranslation("pt", "Go to home", "Home");
   }

}
