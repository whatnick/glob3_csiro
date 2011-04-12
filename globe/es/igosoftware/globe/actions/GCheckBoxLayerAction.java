

package es.igosoftware.globe.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import es.igosoftware.globe.IGlobeApplication;


public abstract class GCheckBoxLayerAction
         extends
            GLayerAction {


   private boolean _value;
   private Action  _action;


   protected GCheckBoxLayerAction(final String label,
                                  final char mnemonic,
                                  final Icon icon,
                                  final boolean showOnToolBar,
                                  final boolean initialValue) {
      super(label, mnemonic, icon, showOnToolBar);
      _value = initialValue;

      //      _action = createAction(label, icon, initialValue);
   }


   protected GCheckBoxLayerAction(final String label,
                                  final Icon icon,
                                  final boolean showOnToolBar,
                                  final boolean initialValue) {
      super(label, icon, showOnToolBar);
      _value = initialValue;

      //      _action = createAction(label, icon, initialValue);
   }


   private Action createAction(final String label,
                               final Icon icon,
                               final boolean initialValue) {
      final Action action = new AbstractAction(label, icon) {
         private static final long serialVersionUID = 1L;


         @Override
         public void actionPerformed(final ActionEvent e) {
            _value = !_value;
            putValue(Action.SELECTED_KEY, _value);

            execute();
         }
      };

      final char mnemonic = getMnemonic();
      if (mnemonic != ' ') {
         action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonic));
      }

      action.setEnabled(isEnabled());
      action.putValue(Action.SELECTED_KEY, initialValue);
      return action;
   }


   private Action getAction(final IGlobeApplication application) {
      if (_action == null) {
         _action = createAction(application.getTranslation(getLabel()), getIcon(), _value);
      }
      return _action;
   }


   @Override
   public Component createToolbarWidget(final IGlobeApplication application) {
      return new JToggleButton(getAction(application));
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeApplication application) {
      return new JCheckBoxMenuItem(getAction(application));
   }


   public boolean isSelected() {
      return _value;
   }


}
