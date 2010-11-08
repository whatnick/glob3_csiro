package es.igosoftware.globe.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.utils.GSwingUtils;

public abstract class GCheckBoxGenericAction
         extends
            GGenericAction {
   protected final boolean _initState;


   public GCheckBoxGenericAction(final String label,
                                 final Icon icon,
                                 final IGenericAction.MenuArea menuBarArea,
                                 final boolean showOnToolBar,
                                 final boolean initState) {
      this(label, ' ', icon, menuBarArea, showOnToolBar, initState);
   }


   public GCheckBoxGenericAction(final String label,
                                 final char mnemonic,
                                 final Icon icon,
                                 final MenuArea menuBarArea,
                                 final boolean showOnToolBar,
                                 final boolean initState) {
      super(label, mnemonic, icon, menuBarArea, showOnToolBar);

      _initState = initState;
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeApplication application) {
      final JMenuItem result = new JCheckBoxMenuItem(application.getTranslation(getLabel()), getIcon(), _initState);

      final char mnemonic = getMnemonic();
      if (mnemonic != ' ') {
         result.setMnemonic(mnemonic);
      }

      result.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            execute();
         }
      });

      return result;
   }


   @Override
   public Component createToolbarWidget(final IGlobeApplication application) {
      return GSwingUtils.createToolbarCheckBox(getIcon(), application.getTranslation(getLabel()), _initState,
               new ActionListener() {
                  @Override
                  public void actionPerformed(final ActionEvent e) {
                     execute();
                  }
               });
   }
}
