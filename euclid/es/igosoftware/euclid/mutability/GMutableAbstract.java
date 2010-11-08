package es.igosoftware.euclid.mutability;


public class GMutableAbstract<MutableT extends GMutableAbstract<MutableT>>
         implements
            IMutable<MutableT> {

   //   private final boolean                                      _isMutable = true;
   //   private final List<WeakReference<IMutable.ChangeListener>> _listeners = null;
   private final GMutableSupport<MutableT> _mutableSupport;


   protected GMutableAbstract() {
      _mutableSupport = new GMutableSupport<MutableT>();
   }


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.addChangeListener(listener);
   }


   @Override
   public void changed() {
      _mutableSupport.changed();
   }


   @Override
   public boolean isMutable() {
      return _mutableSupport.isMutable();
   }


   @Override
   public void checkMutable() {
      _mutableSupport.checkMutable();
   }


   @Override
   public void makeImmutable() {
      _mutableSupport.makeImmutable();
   }


   @Override
   public void removeAllChangeListener() {
      _mutableSupport.removeAllChangeListener();
   }


   @Override
   public void removeChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.removeChangeListener(listener);
   }

   //   public static void main(final String[] args) {
   //      class Mutable
   //               extends
   //                  GMutableAbstract<Mutable> {
   //
   //      }
   //
   //      final Mutable mutable = new Mutable();
   //      System.out.println(mutable.isMutable());
   //
   //      mutable.addChangeListener(new IMutable.ChangeListener<Mutable>() {
   //         @Override
   //         public void changed(final Mutable mutable1) {
   //            System.out.println(mutable1 + " has changed (listener 1)");
   //         }
   //      });
   //
   //      mutable.addChangeListener(new IMutable.ChangeListener<Mutable>() {
   //         @Override
   //         public void changed(final Mutable mutable1) {
   //            System.out.println(mutable1 + " has changed (listener 2)");
   //         }
   //      });
   //
   //      mutable.changed();
   //      System.out.println(mutable.isMutable());
   //
   //      mutable.makeImmutable();
   //      System.out.println(mutable.isMutable());
   //      //      mutable.changed();
   //
   //      // System.out.println(mutable._listeners);
   //   }

}
