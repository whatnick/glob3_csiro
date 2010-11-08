package es.igosoftware.globe.actions;


public interface ILayerAction
         extends
            IAction {

   public boolean isVisible();


   public boolean isEnabled();


   public void execute();

}
