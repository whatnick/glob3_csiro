package es.igosoftware.euclid.mutability;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GMutableSupport<MutableT extends IMutable<MutableT>>
         implements
            IMutable<MutableT> {


   private boolean                                      _isMutable = true;
   private List<WeakReference<IMutable.ChangeListener>> _listeners = null;


   public GMutableSupport() {}


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      if (_listeners == null) {
         _listeners = new ArrayList<WeakReference<IMutable.ChangeListener>>(1);
      }
      _listeners.add(new WeakReference<IMutable.ChangeListener>(listener));
   }


   @Override
   public void changed() {
      checkMutable();

      notifyListeners();
   }


   @Override
   public void checkMutable() {
      if (!isMutable()) {
         throw new RuntimeException("The receiver is immutable");
      }
   }


   @Override
   public boolean isMutable() {
      return _isMutable;
   }


   @Override
   public void makeImmutable() {
      checkMutable();

      _isMutable = false;
      notifyListeners();
      removeAllChangeListener();
   }


   private void notifyListeners() {
      if (_listeners == null) {
         return;
      }

      final List<WeakReference<IMutable.ChangeListener>> listenersCopy = new ArrayList<WeakReference<IMutable.ChangeListener>>(
               _listeners);
      for (final WeakReference<IMutable.ChangeListener> listenerWR : listenersCopy) {
         final IMutable.ChangeListener listener = listenerWR.get();
         if (listener != null) {
            listener.mutableChanged();
         }
      }
   }


   @Override
   public void removeAllChangeListener() {
      _listeners = null;
   }


   @Override
   public void removeChangeListener(final IMutable.ChangeListener listener) {
      if (_listeners == null) {
         return;
      }

      final List<WeakReference<IMutable.ChangeListener>> toRemove = new ArrayList<WeakReference<IMutable.ChangeListener>>(1);

      for (final WeakReference<IMutable.ChangeListener> currentListenerWR : _listeners) {
         final IMutable.ChangeListener currentListener = currentListenerWR.get();
         if ((currentListener == null) || currentListener.equals(listener)) {
            toRemove.add(currentListenerWR);
         }
      }

      if (!toRemove.isEmpty()) {
         _listeners.removeAll(toRemove);
      }

   }

}
