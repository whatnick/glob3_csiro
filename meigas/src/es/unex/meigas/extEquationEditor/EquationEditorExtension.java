/*******************************************************************************
EquationEditorExtension.java
Copyright (C) Victor Olaya

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *******************************************************************************/
package es.unex.meigas.extEquationEditor;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.equations.EquationSelectionDialog;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class EquationEditorExtension
         extends
            AbstractMeigasExtension {

   @Override
   public void initialize() {}


   @Override
   public String getName() {

      return "Editor de ecuaciones";

   }


   public String getMenuName() {

      return "Herramientas";

   }


   public Icon getIcon() {

      final Icon icon = new ImageIcon("images/edit_equation.png");
      return icon;

   }


   @Override
   public void execute(final MeigasPanel panel) {

      final EquationSelectionDialog dialog = new EquationSelectionDialog(panel, EquationSelectionDialog.ALL_EQUATIONS, null);
      dialog.setVisible(true);

   }


   public boolean isEnabled() {

      return true;

   }


   @Override
   public boolean showInContextMenu() {

      return false;

   }


   @Override
   public boolean showInMenuBar(final MeigasPanel panel) {

      return true;

   }


   public boolean isEnabled(final MeigasPanel window) {

      return true;

   }


}
