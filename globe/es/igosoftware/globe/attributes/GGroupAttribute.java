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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GComponentTitledBorder;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.GSwingUtils;
import es.igosoftware.util.GTriplet;


public class GGroupAttribute
         implements
            ILayerAttribute<Object> {


   private final String                   _label;
   private final Icon                     _icon;
   private final String                   _description;
   private final List<ILayerAttribute<?>> _children;


   public GGroupAttribute(final String label,
                          final String description,
                          final ILayerAttribute<?>... children) {
      this(label, null, description, children);
   }


   public GGroupAttribute(final String label,
                          final Icon icon,
                          final String description,
                          final ILayerAttribute<?>... children) {
      _label = label;
      _icon = icon;
      _description = description;
      _children = Arrays.asList(children);
   }


   public GGroupAttribute(final String label,
                          final String description,
                          final List<? extends ILayerAttribute<?>> children) {
      this(label, null, description, children);
   }


   public GGroupAttribute(final String label,
                          final Icon icon,
                          final String description,
                          final List<? extends ILayerAttribute<?>> children) {
      _label = label;
      _icon = icon;
      _description = description;
      _children = new ArrayList<ILayerAttribute<?>>(children);
   }


   @Override
   public boolean isVisible() {
      return GCollections.allSatisfy(_children, new GPredicate<ILayerAttribute<?>>() {
         @Override
         public boolean evaluate(final ILayerAttribute<?> element) {
            return element.isVisible();
         }
      });
   }


   @Override
   public final String getLabel() {
      return null;
   }


   @Override
   public final String getDescription() {
      return _description;
   }


   private final List<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>> _widgetsInLayerPropertiesPanel = new ArrayList<GTriplet<IGlobeLayer, ILayerAttribute<?>, GPair<Component, EventListener>>>();


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                             final IGlobeLayer layer) {
      final JPanel panel = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 1"));
      panel.setBackground(Color.WHITE);
      panel.setBorder(createTitledBorder(application, panel));

      //      panel.add(makeBold(new JLabel(application.getTranslation(_label))), "growx, wrap, span 2");


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
            panel.add(new JLabel(application.getTranslation(label)), "gap 3");
            panel.add(widget._first, "left, wrap");
         }
      }


      return new GPair<Component, EventListener>(panel, null);
   }


   private Border createTitledBorder(final IGlobeApplication application,
                                     final JComponent container) {
      final JLabel l;
      if (_icon == null) {
         l = new JLabel(" " + application.getTranslation(_label) + " ");
      }
      else {
         l = new JLabel(application.getTranslation(_label) + " ");
         l.setIcon(_icon);
      }
      final JLabel label = GSwingUtils.makeBold(l);
      label.setOpaque(true);
      label.setBackground(Color.WHITE);

      if (_description != null) {
         label.setToolTipText(application.getTranslation(_description));
      }

      final Border border = BorderFactory.createCompoundBorder( //
               BorderFactory.createLineBorder(Color.GRAY), //
               BorderFactory.createEmptyBorder(3, 3, 3, 3));

      return new GComponentTitledBorder(label, container, border);
   }


   @Override
   public final void cleanupWidget(final IGlobeLayer layer2,
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
   public final Object get() {
      return null;
   }


   @Override
   public final void set(final Object value) {

   }


   @Override
   public final boolean isReadOnly() {
      return true;
   }


   @Override
   public final void setListener(final ILayerAttribute.IChangeListener listener) {

   }


   @Override
   public final void changed() {

   }


}
