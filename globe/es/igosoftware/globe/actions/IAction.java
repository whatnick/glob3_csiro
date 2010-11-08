package es.igosoftware.globe.actions;

import javax.swing.Icon;


public interface IAction {

   public String getLabel();


   public Icon getIcon();


   public char getMnemonic();


   public boolean isShowOnToolBar();

}
