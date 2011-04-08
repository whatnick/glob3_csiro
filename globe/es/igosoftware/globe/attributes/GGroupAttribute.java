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


package es.igosoftware.globe.attributes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GTriplet;
import es.igosoftware.util.IPredicate;


public class GGroupAttribute
         implements
            ILayerAttribute<Object> {


   private final String                   _label;
   private final List<ILayerAttribute<?>> _children;


   public GGroupAttribute(final String label,
                          final ILayerAttribute<?>... children) {
      _label = label;
      _children = Arrays.asList(children);
   }


   public GGroupAttribute(final String label,
                          final List<? extends ILayerAttribute<?>> children) {
      _label = label;
      _children = new ArrayList<ILayerAttribute<?>>(children);
   }


   @Override
   public boolean isVisible() {
      return GCollections.allSatisfy(_children, new IPredicate<ILayerAttribute<?>>() {
         @Override
         public boolean evaluate(final ILayerAttribute<?> element) {
            return element.isVisible();
         }
      });
   }


   @Override
   public String getLabel() {
      return null;
   }

   private final List<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>>();


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JPanel panel = new JPanel(new MigLayout("fillx, insets 0 0 0 0"));
      panel.setBackground(Color.WHITE);

      panel.add(makeBold(new JLabel(_label)), "growx, wrap, span 2");


      for (final ILayerAttribute<?> attribute : _children) {
         if (!attribute.isVisible()) {
            continue;
         }

         final GPair<Component, EventListener> widget = attribute.createWidget(application, layer);
         if (widget == null) {
            continue;
         }

         _widgetsInLayerPropertiesPanel.add(new GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>(layer,
                  attribute, widget));

         final String label = attribute.getLabel();
         if (label == null) {
            panel.add(widget._first, "growx, wrap, span 2");
         }
         else {
            panel.add(new JLabel("   " + application.getTranslation(label)), "gap 3");
            panel.add(widget._first, "left, wrap");
         }
      }


      return new GPair<Component, EventListener>(panel, null);
   }


   private JLabel makeBold(final JLabel label) {
      final Font font = label.getFont();

      label.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));

      return label;
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer2,
                             final GPair<Component, EventListener> widget2) {
      for (final GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>> layerAttributeAndWidget : _widgetsInLayerPropertiesPanel) {
         final IGlobeLayer layer = layerAttributeAndWidget._first;
         final ILayerAttribute<?> attribute = layerAttributeAndWidget._second;
         final GPair<Component, EventListener> widget = layerAttributeAndWidget._third;

         attribute.cleanupWidget(layer, widget);
      }
      _widgetsInLayerPropertiesPanel.clear();
   }


   @Override
   public Object get() {
      return null;
   }


   @Override
   public void set(final Object value) {

   }


   @Override
   public boolean isReadOnly() {
      return true;
   }


   @Override
   public void setListener(final ILayerAttribute.IChangeListener listener) {

   }


   @Override
   public void changed() {

   }


}
