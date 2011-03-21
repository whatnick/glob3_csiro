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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComboBox;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.util.GPair;


public abstract class GVectorFieldLayerAttribute
         extends
            GAbstractLayerAttribute<String> {


   public GVectorFieldLayerAttribute(final String label,
                                     final String propertyName) {
      super(label, propertyName);
   }


   public GVectorFieldLayerAttribute(final String label,
                                     final String propertyName,
                                     final boolean readOnly) {
      super(label, propertyName, readOnly);
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final String options[];
      if (layer instanceof IGlobeVectorLayer) {
         @SuppressWarnings("unchecked")
         final IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> vectorLayer = (IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle>) layer;

         final List<GField> fields = vectorLayer.getFeaturesCollection().getFields();
         options = new String[fields.size()];
         for (int i = 0; i < fields.size(); i++) {
            options[i] = fields.get(i).getName();
         }
      }
      else {
         options = new String[0];
      }

      final JComboBox widget = new JComboBox(options);
      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               set((String) widget.getSelectedItem());
            }
         });
      }

      final EventListener listener = subscribeToEvents(layer);

      return new GPair<Component, EventListener>(widget, listener);
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget) {
      unsubscribeFromEvents(layer, widget._second);
   }
}
