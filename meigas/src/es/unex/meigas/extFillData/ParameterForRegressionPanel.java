package es.unex.meigas.extFillData;
import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.JPanel;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ParameterForRegressionPanel extends JPanel {
	
	String m_sParameterName;
	String[] m_sParameters;
	private JComboBox jComboBox;
	private JLabel jLabel;
	private JCheckBox jCheckBox;

	public ParameterForRegressionPanel (String sParameterName, String[] sParameters){
		
		super();
		
		m_sParameterName = sParameterName;
		m_sParameters = sParameters;
		
		initGUI();
		
	}
	
	private void initGUI() {
		
		try {
			{
				TableLayout thisLayout = new TableLayout(new double[][] {
						{ TableLayout.MINIMUM, TableLayout.FILL,
								TableLayout.MINIMUM, TableLayout.MINIMUM },
						{ TableLayout.FILL } });
				thisLayout.setHGap(5);
				thisLayout.setVGap(5);
				this.setLayout(thisLayout);
				this.setPreferredSize(new java.awt.Dimension(400, 36));
				{
					jCheckBox = new JCheckBox();
					this.add(jCheckBox, "0,  0");
					jCheckBox.setText(m_sParameterName);
					jCheckBox.setFont(new java.awt.Font("Tahoma",1,11));
					jCheckBox.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent evt) {
							if (evt.getStateChange() == ItemEvent.DESELECTED){
								jComboBox.setEnabled(false);
							}
							else{
								jComboBox.setEnabled(true);
							}
							((FillDataPanel)getParent().getParent().getParent().
											getParent()).updateParentButtons();
						}
					});
				}
				{
					jLabel = new JLabel();
					this.add(jLabel, "2, 0");
					jLabel.setText("Parámetro X para regresión:");
				}
				{
					ComboBoxModel jComboBoxModel = new DefaultComboBoxModel(m_sParameters);
					jComboBox = new JComboBox();
					this.add(jComboBox, "3, 0");
					jComboBox.setModel(jComboBoxModel);
					jComboBox.setMinimumSize(new java.awt.Dimension(125, 20));
					jComboBox.setEnabled(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSelected(){
		
		return jCheckBox.isSelected();
		
	}
	
	public int getXParameter(){
		
		return jComboBox.getSelectedIndex(); 
		
	}

}
