package es.igosoftware.euclid.mutability;

/**
 * 
 * The Mutable objects supports change notification, and support to switch to immutable state.
 * 
 * When the receiver switch to immutable state, all the listeners will be automatically removed.
 * 
 * @author dgd
 * 
 */
public interface IMutable<MutableT extends IMutable<MutableT>> {

   public static interface ChangeListener {
      public void mutableChanged();
   }


   /**
    * Answer if the receiver is in mutable state
    * 
    * @return boolean
    */
   public boolean isMutable();


   /**
    * Throws an RunTimeException if the receiver is not in mutable state
    */
   public void checkMutable();


   /**
    * Makes the receiver to switch to immutable state.<br/>
    * <br/>
    * All the change listeners will be notified, and then removed (as it make no sense to keep change listener for an object that
    * will not change anymore)
    */
   public void makeImmutable();


   /**
    * The object (that is still in mutable state) was changed, notify all the change-listeners.<br/>
    * <br/>
    * Fires an {@link RuntimeException} if called in immutable state.
    */
   public void changed();


   /**
    * Add a new change-listener to the receiver.
    * 
    * @param listener
    *           The callback interface
    */
   public void addChangeListener(final IMutable.ChangeListener listener);


   /**
    * Removes the given listener (if present) from the notification list.
    * 
    * @param listener
    */
   public void removeChangeListener(final IMutable.ChangeListener listener);


   /**
    * Removes all the change listeners in a shot.
    */
   public void removeAllChangeListener();

}
