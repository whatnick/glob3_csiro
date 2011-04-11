

package es.igosoftware.util;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;


public class GSwingUtils {


   private GSwingUtils() {
      // private, only static methods
   }


   public static Font makeBold(final Font font) {
      return font.deriveFont(font.getStyle() ^ Font.BOLD);
   }


   public static JLabel makeBold(final JLabel label) {
      label.setFont(makeBold(label.getFont()));
      return label;
   }


   public static Font makeBigger(final Font font,
                                 final float delta) {
      return font.deriveFont(font.getSize() + delta);
   }


   public static void repaint(final Component component) {
      if (component == null) {
         return;
      }

      if (component instanceof JList) {
         repaint((JList) component);
         return;
      }

      component.invalidate();
      component.doLayout();
      component.repaint();
   }


   public static void repaint(final JList list) {
      if (list == null) {
         return;
      }

      list.updateUI();
   }


}
