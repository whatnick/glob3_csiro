package es.unex.meigas.extGIS;

import info.clearthought.layout.TableLayout;

import java.util.ArrayList;

import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public abstract class LayersSelectionPanel extends JPanel{

	private ArrayList m_SelectedIndices = new ArrayList();
	private JTextField textField;
	private JButton button;

	public LayersSelectionPanel (){

		super();

		initGUI();

	}

	private void initGUI(){

		button = new JButton ("...");
		textField = new JTextField("0 elementos seleccionados");
		textField.setEditable(false);

		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnActionPerformed(evt);
            }
        });

		TableLayout thisLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL, 25.0 }, { TableLayout.FILL } });
		this.setLayout(thisLayout);
		this.add(textField, "0,  0");
		this.add(button, "1,  0");

		setText();
	}

	private void btnActionPerformed(ActionEvent e){

		StringBuffer sText = new StringBuffer();

		Frame window = new Frame();

		MultipleInputSelectionDialog dialog = new MultipleInputSelectionDialog(window, getLayers());

		dialog.pack();
		dialog.setVisible(true);

		LayerAndIDField[] layers = dialog.getSelectedLayerAndIDFields();
		if (layers != null){
			setLayers(layers);
			setText();
		}

	}

	private void setText() {

		StringBuffer sText = new StringBuffer();
		int iCount = getLayers().length;

		sText.append(Integer.toString(iCount));
		if (iCount == 1){
			sText.append(" elemento seleccionado");
		}
		else{
			sText.append(" elementos seleccionados");
		}
		textField.setText(sText.toString());

	}

	public void setEnabled(boolean b){

		textField.setEnabled(b);
		button.setEnabled(b);

	}

	protected abstract void setLayers(LayerAndIDField[] layers);

	protected abstract LayerAndIDField[] getLayers();


}
