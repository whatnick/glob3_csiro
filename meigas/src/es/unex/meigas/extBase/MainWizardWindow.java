/*******************************************************************************
MainWizardWindow.java
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
package es.unex.meigas.extBase;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.gui.MeigasPanel;
import info.clearthought.layout.TableLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;

import javax.swing.JButton;


public abstract class MainWizardWindow extends JDialog {

	protected JButton jButtonPrev;
	protected JButton jButtonNext;
	private JPanel jPanelSidebar;
	protected JPanel jPanelMain;
	protected CardLayout jPanelMainLayout;
	protected JButton jButtonCancel;
	protected JPanel jPanelButtons;
	protected BaseWizardPanel[] m_Panels;
	protected MeigasPanel m_MeigasPanel;
	protected int m_iCurrentPanel;

    public MainWizardWindow(MeigasPanel panel) {

    	super(Meigas.getMainFrame(), true);

    	m_MeigasPanel = panel;
    	m_iCurrentPanel = 0;

    	initGUI();

    	this.setLocationRelativeTo(null);

	}

    public MeigasPanel getMeigasPanel(){

    	return m_MeigasPanel;

    }

	protected abstract void setPanels();

	private void initGUI() {

		int i;

		setPanels();

		this.setPreferredSize(new java.awt.Dimension(700, 350));
		this.setSize(new java.awt.Dimension(700, 350));
		try {
			{
				TableLayout thisLayout = new TableLayout(new double[][] {
						{ 5.0, 150.0, TableLayout.FILL, 5.0 },
						{ 5.0, TableLayout.FILL, 35.0, 5.0 } });
				thisLayout.setHGap(5);
				thisLayout.setVGap(5);
				this.setLayout(thisLayout);
				{
					jPanelButtons = new JPanel();
					TableLayout jPanelButtonsLayout = new TableLayout(
						new double[][] {
								{ TableLayout.FILL, 110.0, 110.0, 110.0 },
								{ 5.0, TableLayout.FILL } });
					jPanelButtonsLayout.setHGap(5);
					jPanelButtonsLayout.setVGap(5);
					jPanelButtons.setLayout(jPanelButtonsLayout);
					this.add(jPanelButtons, "2, 2");
					{
						jButtonPrev = new JButton();
						jPanelButtons.add(jButtonPrev, "1, 1");
						jButtonPrev.setText("< Anterior");
						jButtonPrev.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								previousPanel();
							}
						});
					}
					{
						jButtonNext = new JButton();
						jPanelButtons.add(jButtonNext, "2, 1");
						jButtonNext.setText("Siguiente >");
						jButtonNext.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								nextPanel();
							}
						});
					}
					{
						jButtonCancel = new JButton();
						jPanelButtons.add(jButtonCancel, "3, 1");
						jButtonCancel.setText("Cancelar");
						jButtonCancel.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								cancel();
							}
						});
					}
				}
				{
					jPanelMain = new JPanel();
					jPanelMainLayout = new CardLayout();
					jPanelMain.setLayout(jPanelMainLayout);
					for (i = 0; i < m_Panels.length; i++){
						jPanelMain.add(m_Panels[i], Integer.toString(i));
					}

					this.add(jPanelMain, "2, 1");
					jPanelMain.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
				}
				{
					jPanelSidebar = new SidebarPanel();
					this.add(jPanelSidebar, "1, 1, 1, 2");
				}
			}
			updateButtons();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void cancel() {

		this.dispose();
		this.setVisible(false);

	}

	public void updateButtons(){

		if (m_iCurrentPanel < 1){
			jButtonPrev.setEnabled(false);
		}
		else{
			jButtonPrev.setEnabled(true);
		}
		if (m_iCurrentPanel == m_Panels.length - 1 ||
				m_Panels[m_iCurrentPanel].isFinish()){
			jButtonNext.setText("Finalizar");
		}
		else{
			jButtonNext.setText("Siguiente >");
		}

		if (m_Panels[m_iCurrentPanel].hasEnoughInformation()){
			jButtonNext.setEnabled(true);
		}
		else{
			jButtonNext.setEnabled(false);
		}

	}

	protected void nextPanel() {

		if (m_iCurrentPanel == m_Panels.length - 1 ||
				m_Panels[m_iCurrentPanel].isFinish()){
			finish();
			cancel();
		}
		else{
			m_iCurrentPanel++;
			m_Panels[m_iCurrentPanel].initGUI();
			jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
			updateButtons();
		}
	}

	protected void previousPanel() {

		m_iCurrentPanel--;
		jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
		updateButtons();

	}

	protected abstract void finish();

}
