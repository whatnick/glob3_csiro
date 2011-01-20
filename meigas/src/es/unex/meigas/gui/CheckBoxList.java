/*******************************************************************************
    CheckBoxList
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

package es.unex.meigas.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CheckBoxList extends JList
{
   protected static Border noFocusBorder =
                                 new EmptyBorder(1, 1, 1, 1);

   public CheckBoxList(){

	   setCellRenderer(new CellRenderer());

	   addMouseListener(new MouseAdapter()
	   {
		   public void mousePressed(MouseEvent e)
		   {
			   int index = locationToIndex(e.getPoint());

			   if (index != -1) {
				   JCheckBox checkbox = (JCheckBox)
				   getModel().getElementAt(index);
				   checkbox.setSelected(
						   !checkbox.isSelected());
				   repaint();
			   }
		   }
	   }
	   );

      //setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

   }

   public int[] getSelectedIndices(){

	   int i;
	   int iSize = getModel().getSize();
	   JCheckBox check;
	   ArrayList list = new ArrayList();

	   for (i = 0; i < iSize; i++){
		   check = (JCheckBox)getModel().getElementAt(i);
		   if (check.isSelected()){
			   list.add(new Integer(i));
		   }
	   }

	   int ret[] = new int[list.size()];
	   for (i = 0; i < list.size(); i++){
		   ret[i] = ((Integer)list.get(i)).intValue();
	   }

	   return ret;

   }

   protected class CellRenderer implements ListCellRenderer
   {
      public Component getListCellRendererComponent(
                    JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus)
      {
         JCheckBox checkbox = (JCheckBox) value;
         checkbox.setBackground(getBackground());
         checkbox.setForeground(getForeground());
         checkbox.setEnabled(isEnabled());
         checkbox.setFont(getFont());
         checkbox.setFocusPainted(false);
         checkbox.setBorderPainted(true);
         checkbox.setBorder(noFocusBorder);
         return checkbox;
      }
   }
}