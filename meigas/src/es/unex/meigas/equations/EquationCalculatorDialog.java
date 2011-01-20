
/*******************************************************************************
EquationCalculatorPanel.java
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
package es.unex.meigas.equations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import es.unex.meigas.core.Meigas;

public class EquationCalculatorDialog extends JDialog{

	private JTextField m_TextFormula = null;
	private JButton jButtonMinus;
	private JButton jButtonDivide;
	private JButton jButton2;
	private JTextArea jTextAreaFormula;
	private JPanel jPanelFormula;
	private JPanel jPanelOkCancel;
	private JButton jButtonVolume;
	private JButton jButtonLogHeight;
	private JButton jButtonDBH;
	private JButton jButtonHeight;
	private JButton jButtonDot;
	private JButton jButtonBark;
	private JButton jButtonPow;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JButton jButtonBrackets;
	private JButton jButton0;
	private JButton jButton9;
	private JButton jButton8;
	private JButton jButton7;
	private JButton jButton6;
	private JButton jButton5;
	private JButton jButton4;
	private JButton jButton3;
	private JButton jButton1;
	private JButton jButtonMultiply;
	private JButton jButtonPlus;

	public EquationCalculatorDialog(JTextField textFormula){

		super(Meigas.getMainFrame(), true);

		m_TextFormula = textFormula;

		initialize();

	}

	private void initialize(){

		this.setPreferredSize(new java.awt.Dimension(350, 300));
		this.setSize(new java.awt.Dimension(350, 300));

		ActionListener listener =new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addText(evt.getSource());
			}
		};

		ActionListener listenerBrackets =new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextAreaFormula.insert(" ()", jTextAreaFormula.getCaretPosition());
				jTextAreaFormula.setCaretPosition(jTextAreaFormula.getCaretPosition() - 1);
			}
		};

		ActionListener listenerVariables =new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jTextAreaFormula.insert(" " + ((JButton)evt.getSource()).getText() + "",
										jTextAreaFormula.getCaretPosition());
			}
		};

		TableLayout thisLayout = new TableLayout(new double[][] {
				{ 5.0, TableLayout.FILL, 10.0, TableLayout.FILL,
						TableLayout.FILL, TableLayout.FILL, 10.0,
						TableLayout.FILL, 5.0 },
				{ 50.0, TableLayout.FILL, TableLayout.FILL, TableLayout.FILL,
						TableLayout.FILL, TableLayout.FILL, 10.0,
						TableLayout.FILL, 5.0 } });
		thisLayout.setHGap(5);
		thisLayout.setVGap(5);
		this.setLayout(thisLayout);
		{
			jButtonPlus = new JButton();
			this.add(jButtonPlus, "1, 1");
			jButtonPlus.setText("+");
			jButtonPlus.addActionListener(listener);
		}
		{
			jButtonMinus = new JButton();
			this.add(jButtonMinus, "1, 2");
			jButtonMinus.setText("-");
			jButtonMinus.addActionListener(listener);
		}
		{
			jButtonMultiply = new JButton();
			this.add(jButtonMultiply, "1, 3");
			jButtonMultiply.setText("*");
			jButtonMultiply.addActionListener(listener);
		}
		{
			jButtonDivide = new JButton();
			this.add(jButtonDivide, "1, 4");
			jButtonDivide.setText("/");
			jButtonDivide.addActionListener(listener);
		}
		{
			jButton1 = new JButton();
			this.add(jButton1, "3, 3");
			jButton1.setText("1");
			jButton1.addActionListener(listener);
		}
		{
			jButton2 = new JButton();
			this.add(jButton2, "4, 3");
			jButton2.setText("2");
			jButton2.addActionListener(listener);
		}
		{
			jButton3 = new JButton();
			this.add(jButton3, "5, 3");
			jButton3.setText("3");
			jButton3.addActionListener(listener);
		}
		{
			jButton4 = new JButton();
			this.add(jButton4, "3, 2");
			jButton4.setText("4");
			jButton4.addActionListener(listener);
		}
		{
			jButton5 = new JButton();
			this.add(jButton5, "4, 2");
			jButton5.setText("5");
			jButton5.addActionListener(listener);
		}
		{
			jButton6 = new JButton();
			this.add(jButton6, "5, 2");
			jButton6.setText("6");
			jButton6.addActionListener(listener);
		}
		{
			jButton7 = new JButton();
			this.add(jButton7, "3, 1");
			jButton7.setText("7");
			jButton7.addActionListener(listener);
		}
		{
			jButton8 = new JButton();
			this.add(jButton8, "4, 1");
			jButton8.setText("8");
			jButton8.addActionListener(listener);
		}
		{
			jButton9 = new JButton();
			this.add(jButton9, "5, 1");
			jButton9.setText("9");
			jButton9.addActionListener(listener);
		}
		{
			jButton0 = new JButton();
			this.add(jButton0, "3, 4");
			jButton0.setText("0");
			jButton0.addActionListener(listener);
		}
		{
			jButtonDot = new JButton();
			this.add(jButtonDot, "5, 4");
			jButtonDot.setText(".");
			jButtonDot.addActionListener(listener);
		}
		{
			jButtonBrackets = new JButton();
			this.add(jButtonBrackets, "4, 4");
			jButtonBrackets.setText("( )");
			jButtonBrackets.addActionListener(listenerBrackets);
		}
		{
			jButtonHeight = new JButton();
			this.add(jButtonHeight, "7, 2");
			jButtonHeight.setText("Ht");
			jButtonHeight.addActionListener(listenerVariables);
		}
		{
			jButtonDBH = new JButton();
			this.add(jButtonDBH, "7, 1");
			jButtonDBH.setText("Dn");
			jButtonDBH.addActionListener(listenerVariables);
		}
		{
			jButtonLogHeight = new JButton();
			this.add(jButtonLogHeight, "7, 3");
			jButtonLogHeight.setText("Hf");
			jButtonLogHeight.addActionListener(listenerVariables);
		}
		{
			jButtonVolume = new JButton();
			this.add(jButtonVolume, "7, 4");
			jButtonVolume.setText("Vcc");
			jButtonVolume.addActionListener(listenerVariables);
		}
		{
			jPanelOkCancel = new JPanel();
			FlowLayout jPanelOkCancelLayout = new FlowLayout();
			jPanelOkCancelLayout.setAlignment(FlowLayout.RIGHT);
			jPanelOkCancel.setLayout(jPanelOkCancelLayout);
			this.add(jPanelOkCancel, "3, 7, 7, 7");
			{
				jButtonOk = new JButton();
				jPanelOkCancel.add(jButtonOk);
				jButtonOk.setText("Aceptar");
				jButtonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						m_TextFormula.setText(jTextAreaFormula.getText());
						cancel();
					}
				});
			}
			{
				jButtonCancel = new JButton();
				jPanelOkCancel.add(jButtonCancel);
				jButtonCancel.setText("Cancelar");
				jButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						cancel();
					}
				});
			}
		}
		{
			jButtonPow = new JButton();
			this.add(jButtonPow, "1, 5");
			jButtonPow.setText("^");
			jButtonPow.addActionListener(listener);
		}
		{
			jPanelFormula = new JPanel();
			BorderLayout jPanelFormulaLayout = new BorderLayout();
			jPanelFormula.setLayout(jPanelFormulaLayout);
			this.add(jPanelFormula, "1, 0, 7, 0");
			jPanelFormula.setBorder(BorderFactory.createTitledBorder(null, "Expresión", TitledBorder.LEADING, TitledBorder.TOP));
			{
				jTextAreaFormula = new JTextArea();
				jTextAreaFormula.setText(m_TextFormula.getText());
				jPanelFormula.add(jTextAreaFormula, BorderLayout.CENTER);
				jTextAreaFormula.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			}
		}
		{
			jButtonBark = new JButton();
			this.add(jButtonBark, "7, 5");
			jButtonBark.setText("Cz");
			jButtonBark.addActionListener(listenerVariables);
		}
	}

	private void addText(Object source){

		if (source instanceof JButton){
			String s = ((JButton) source).getText();
			try {
				int i = Integer.parseInt(s);
			} catch (NumberFormatException e) {
				s = " " + s + " ";
			}
			jTextAreaFormula.insert(s, jTextAreaFormula.getCaretPosition());
		}

	}

	protected void cancel() {

		this.dispose();
		this.setVisible(false);

	}



}
