package es.igosoftware.globe.actions;

import java.awt.Component;

import javax.swing.JMenuItem;

import es.igosoftware.globe.IGlobeApplication;


public interface IGenericAction
         extends
            IAction {


   public static enum MenuArea {
      FILE,
      NAVIGATION,
      VIEW,
      ANALYSIS,
      HELP;
   }


   public boolean isVisible();


   public IGenericAction.MenuArea getMenuBarArea();


   public void execute();


   public JMenuItem createMenuWidget(final IGlobeApplication application);


   public Component createToolbarWidget(final IGlobeApplication application);


}
