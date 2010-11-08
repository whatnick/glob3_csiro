package es.igosoftware.globe.attributes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

import es.igosoftware.globe.GGlobeComponent;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GAssert;

public abstract class GAbstractLayerAttribute<T>
         extends
            GGlobeComponent
         implements
            ILayerAttribute<T> {


   private final String    _label;
   private final boolean   _readOnly;
   private IChangeListener _listener;
   private String          _propertyName;


   public GAbstractLayerAttribute(final String label) {
      this(label, null, false);
   }


   public GAbstractLayerAttribute(final String label,
                                  final boolean readOnly) {
      this(label, null, readOnly);
   }


   public GAbstractLayerAttribute(final String label,
                                  final String propertyName) {
      this(label, propertyName, false);
   }


   public GAbstractLayerAttribute(final String label,
                                  final String propertyName,
                                  final boolean readOnly) {
      GAssert.notNull(label, "label");

      _label = label;
      _propertyName = propertyName;
      _readOnly = readOnly;
   }


   @Override
   public String getLabel() {
      return _label;
   }


   @Override
   public boolean isReadOnly() {
      return _readOnly;
   }


   @Override
   public void setListener(final IChangeListener listener) {
      if ((_listener != null) && (listener != null)) {
         throw new IllegalArgumentException("Listener already set");
      }

      _listener = listener;
   }


   @Override
   public void changed() {
      if (_listener != null) {
         _listener.changed();
      }
   }


   protected final String getPropertyName() {
      return _propertyName;
   }


   protected final EventListener subscribeToEvents(final IGlobeLayer layer) {
      final String propertyName = getPropertyName();
      if (propertyName == null) {
         return null;
      }

      final PropertyChangeListener listener = new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            changed();
         }
      };
      layer.addPropertyChangeListener(propertyName, listener);
      return listener;
   }


   protected final void unsubscribeFromEvents(final IGlobeLayer layer,
                                              final EventListener listener) {
      final String propertyName = getPropertyName();
      if ((listener == null) || (propertyName == null)) {
         return;
      }

      layer.removePropertyChangeListener(propertyName, (PropertyChangeListener) listener);
   }
}
