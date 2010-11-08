package es.igosoftware.utils;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class GSwingUtils {
   private GSwingUtils() {}


   public static JButton createToolbarButton(final Icon icon,
                                             final String label,
                                             final ActionListener actionListener) {

      final JButton button;
      if (icon == null) {
         button = new JButton(label);
      }
      else {
         button = new JButton(icon);
         button.setToolTipText(label);
      }

      button.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      if (actionListener != null) {
         button.addActionListener(actionListener);
      }

      return button;
   }


   public static Component createToolbarCheckBox(final Icon icon,
                                                 final String label,
                                                 final boolean initState,
                                                 final ActionListener actionListener) {

      final JToggleButton button;
      if (icon == null) {
         button = new JToggleButton(label);
      }
      else {
         button = new JToggleButton(icon);
         button.setToolTipText(label);
      }

      button.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      button.addActionListener(actionListener);

      return button;

      //
      //      final JCheckBoxMenuItem result = new JCheckBoxMenuItem(label, icon, initState);
      //
      //      result.addActionListener(actionListener);
      //
      //      return result;
   }


   public static JMenuItem createMenuItem(final String label,
                                          final Icon icon,
                                          final char mnemonic,
                                          final ActionListener actionListener) {
      final JMenuItem item = new JMenuItem(label, icon);
      if (mnemonic != ' ') {
         item.setMnemonic(mnemonic);
      }
      item.addActionListener(actionListener);
      return item;
   }

}
