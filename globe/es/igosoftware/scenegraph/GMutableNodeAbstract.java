package es.igosoftware.scenegraph;

import es.igosoftware.euclid.mutability.GMutableSupport;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector3;

public abstract class GMutableNodeAbstract<MutableT extends GMutableNodeAbstract<MutableT>>
         extends
            GNodeAbstract
         implements
            IMutableNode<MutableT> {

   private final GMutableSupport<MutableT> _mutableSupport;


   protected GMutableNodeAbstract(final String name,
                                  final GTransformationOrder order) {
      super(name, order);
      _mutableSupport = new GMutableSupport<MutableT>();
   }


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.addChangeListener(listener);
   }


   @Override
   public void changed() {
      calculateLocalTransformMatrix();
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


   @Override
   public void setHeading(final double heading) {
      checkMutable();

      _heading = heading;
      cleanCaches();
      changed();
   }


   @Override
   public void setPitch(final double pitch) {
      checkMutable();

      _pitch = pitch;
      cleanCaches();
      changed();
   }


   @Override
   public void setRoll(final double roll) {
      checkMutable();

      _roll = roll;
      cleanCaches();
      changed();
   }


   @Override
   public void setScale(final double scale) {
      checkMutable();

      _scale = scale;
      cleanCaches();
      changed();
   }


   @Override
   public void setTranslation(final IVector3<?> translation) {
      checkMutable();

      _translation = translation;
      cleanCaches();
      changed();
   }


   @SuppressWarnings("unchecked")
   @Override
   public void reparentTo(final GGroupNode parent) {
      final GGroupNode previousParent = getParent();
      if (previousParent != null) {
         previousParent.removeChild((MutableT) this);
      }

      if (parent != null) {
         parent.addChild((MutableT) this);
      }
   }

}
