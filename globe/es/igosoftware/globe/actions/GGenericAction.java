package es.igosoftware.globe.actions;

import javax.swing.Icon;

import es.igosoftware.globe.GGlobeComponent;

public abstract class GGenericAction
         extends
            GGlobeComponent
         implements
            IGenericAction {


   private final String                  _label;
   private final char                    _mnemonic;
   private final Icon                    _icon;
   private final IGenericAction.MenuArea _menuBarArea;
   private final boolean                 _showOnToolBar;


   public GGenericAction(final String label,
                         final Icon icon,
                         final IGenericAction.MenuArea menuBarArea,
                         final boolean showOnToolBar) {
      this(label, ' ', icon, menuBarArea, showOnToolBar);
   }


   public GGenericAction(final String label,
                         final char mnemonic,
                         final Icon icon,
                         final IGenericAction.MenuArea menuBarArea,
                         final boolean showOnToolBar) {
      super();
      if ((label == null) && (icon == null)) {
         throw new IllegalArgumentException("Label and/or icon are needed");
      }

      _label = label;
      _mnemonic = mnemonic;
      _icon = icon;
      _menuBarArea = menuBarArea;
      _showOnToolBar = showOnToolBar;
   }


   @Override
   public MenuArea getMenuBarArea() {
      return _menuBarArea;
   }


   @Override
   public boolean isShowOnToolBar() {
      return _showOnToolBar;
   }


   @Override
   public Icon getIcon() {
      return _icon;
   }


   @Override
   public String getLabel() {
      return _label;
   }


   @Override
   public char getMnemonic() {
      return _mnemonic;
   }


   @Override
   public boolean isVisible() {
      return true;
   }


}
