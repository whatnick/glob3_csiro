package es.igosoftware.globe;


public abstract class GGlobeComponent
         implements
            IGlobeComponent {


   @Override
   public void initialize(final IGlobeApplication application) {
      // do nothing, overload on subclasses for proper initialization
   }


   @Override
   public void finalize(final IGlobeApplication application) {
      // do nothing, overload on subclasses for proper finalization
   }


}
