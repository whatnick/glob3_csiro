package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JButton;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;

public abstract class GColorRampLayerAttribute
         extends
            GAbstractLayerAttribute<LinearGradientPaint> {


   public GColorRampLayerAttribute(final String label,
                                   final String propertyName) {
      super(label, propertyName);
   }


   public GColorRampLayerAttribute(final String label,
                                   final String propertyName,
                                   final boolean readOnly) {
      super(label, propertyName, readOnly);
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JButton widget = new JButton(" ");
      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final MultipleGradientPaint gradient = GradientChooserDialog.showDialog(application.getFrame(), "Color ramp",
                        get());
               if (gradient != null) {
                  set((LinearGradientPaint) gradient);
                  //widget.setBackground(newColor);
               }
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            //widget.setBackground(get(layer));
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
