package es.igosoftware.globe.attributes;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;

public abstract class GColorLayerAttribute
         extends
            GAbstractLayerAttribute<Color> {


   public GColorLayerAttribute(final String label,
                               final String propertyName) {
      super(label, propertyName);
   }


   public GColorLayerAttribute(final String label,
                               final String propertyName,
                               final boolean readOnly) {
      super(label, propertyName, readOnly);
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JButton widget = new JButton(" ");
      //      widget.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      widget.setBackground(get());

      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final Color newColor = JColorChooser.showDialog(application.getFrame(), "Choose Points Color", get());

               if (newColor != null) {
                  set(newColor);
                  widget.setBackground(newColor);
               }
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            widget.setBackground(get());
         }
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
