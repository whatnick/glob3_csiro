package es.igosoftware.globe.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.utils.GSwingUtils;

public abstract class GButtonGenericAction
         extends
            GGenericAction {

   public GButtonGenericAction(final String label,
                               final char mnemonic,
                               final Icon icon,
                               final IGenericAction.MenuArea menuBarArea,
                               final boolean showOnToolBar) {
      super(label, mnemonic, icon, menuBarArea, showOnToolBar);
   }


   public GButtonGenericAction(final String label,
                               final Icon icon,
                               final IGenericAction.MenuArea menuBarArea,
                               final boolean showOnToolBar) {
      super(label, icon, menuBarArea, showOnToolBar);
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeApplication application) {
      final JMenuItem item = new JMenuItem(application.getTranslation(getLabel()), getIcon());

      final char mnemonic = getMnemonic();
      if (mnemonic != ' ') {
         item.setMnemonic(mnemonic);
      }

      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            execute();
         }
      });

      return item;
   }


   @Override
   public Component createToolbarWidget(final IGlobeApplication application) {
      return GSwingUtils.createToolbarButton(getIcon(), application.getTranslation(getLabel()), new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            execute();
         }
      });
   }

}
