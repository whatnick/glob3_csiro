package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.EventListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GRange;

public abstract class GRangeLayerAttribute<T extends Number & Comparable<T>>
         extends
            GAbstractLayerAttribute<GRange<T>> {


   private final GRange<T> _minimumMaximum;
   private final T         _stepSize;


   public GRangeLayerAttribute(final String label,
                               final String propertyName,
                               final boolean readOnly,
                               final GRange<T> minimumMaximum,
                               final T stepSize) {
      super(label, propertyName, readOnly);

      _minimumMaximum = minimumMaximum;
      _stepSize = stepSize;
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {

      final GRange<T> value = get();

      final JSpinner fromWidget = new JSpinner(new SpinnerNumberModel(value._lower, _minimumMaximum._lower,
               _minimumMaximum._upper, _stepSize));
      final JSpinner toWidget = new JSpinner(new SpinnerNumberModel(value._upper, _minimumMaximum._lower, _minimumMaximum._upper,
               _stepSize));

      if (isReadOnly()) {
         fromWidget.setEnabled(false);
         toWidget.setEnabled(false);
      }
      else {
         fromWidget.addChangeListener(new ChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void stateChanged(final ChangeEvent e) {
               final T from = (T) fromWidget.getValue();
               T to = get()._upper;

               if (from.compareTo(to) > 0) {
                  to = from;
               }

               final GRange<T> newRange = new GRange<T>(from, to);
               set(newRange);
            }
         });

         toWidget.addChangeListener(new ChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void stateChanged(final ChangeEvent e) {
               T from = get()._lower;
               final T to = (T) toWidget.getValue();

               if (to.compareTo(from) < 0) {
                  from = to;
               }

               final GRange<T> newRange = new GRange<T>(from, to);
               set(newRange);
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            final GRange<T> range = get();
            fromWidget.setValue(range._lower);
            toWidget.setValue(range._upper);
         }
      });


      final EventListener listener = subscribeToEvents(layer);

      final JPanel row = new JPanel(new FlowLayout());
      row.add(fromWidget);
      row.add(new JLabel("-"));
      row.add(toWidget);

      return new GPair<Component, EventListener>(row, listener);
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }


}
