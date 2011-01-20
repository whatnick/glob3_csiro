/*******************************************************************************
IFN2Extension.java
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
package es.unex.meigas.extIFN2;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import es.unex.meigas.core.AdministrativeUnit;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Stand;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.extBase.IMeigasExtension;
import es.unex.meigas.gui.MeigasPanel;

public class IFN2Extension extends AbstractMeigasExtension{

	public String getName(){

		return "Rellenar con datos del IFN2";

	}

	public String getMenuName() {

		return "Herramientas";
		
	}
	
	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/importIFN2.gif"));
		return icon;
	}

	public void execute(MeigasPanel meigasPanel) {

		IFN2Panel dialog = new IFN2Panel(meigasPanel);
		dialog.setVisible(true);

	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public boolean showInContextMenu() {

		return true;

	}

	public boolean showInMenuBar(MeigasPanel panel) {

		return false;

	}

	public boolean isEnabled(MeigasPanel window) {

		DasocraticElement element = window.getActiveElement();

		if (element instanceof AdministrativeUnit || element instanceof Stand ){
			return true;
		}
		else{
			return false;
		}

	}

}