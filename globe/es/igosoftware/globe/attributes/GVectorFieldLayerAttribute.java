package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JComboBox;

import es.igosoftware.globe.GField;
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
         final IGlobeVectorLayer vlayer = (IGlobeVectorLayer) layer;

         final GField[] fields = vlayer.getFields();
         options = new String[fields.length];
         for (int i = 0; i < fields.length; i++) {
            options[i] = fields[i].getName();
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

      setListener(new IChangeListener() {
         @Override
         public void changed() {}
      });

      final EventListener listener = subscribeToEvents(layer);

      return new GPair<Component, EventListener>(widget, listener);
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }
}
