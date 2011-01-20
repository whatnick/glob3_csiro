package es.unex.meigas.extGIS;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.unex.meigas.core.Meigas;

public class GisPanel extends JDialog{

	private JPanel jPanel1;
	private LayersSelectionPanel jPanelTreesLayers;
	private LayersSelectionPanel jPanelPlotsLayers;
	private JLabel descriptionLabel;
	private JCheckBox configureChB;
	private JCheckBox zoomChB;
	private JButton syncB;
	private JButton confAllB;
	private JTextField minScaleTF;
	private JCheckBox minScaleChB;
	private JLabel treeLayerLabel;
	private JLabel plotLayerLabel;
	private LayerAndIDField[] m_TreesLayersOld;
	private LayerAndIDField[] m_PlotsLayersOld;

	public GisPanel(){

		super(Meigas.getMainFrame(), "Sincronizacion con SIG", true);

		m_TreesLayersOld = Meigas.getGISConnection().getTreeLayersAndIDFields();
		m_PlotsLayersOld = Meigas.getGISConnection().getPlotLayersAndIDFields();

		setLocationRelativeTo(null);

	}

	protected void initGUI() {

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancel();
			}
		});

		BorderLayout thisLayout = new BorderLayout();
		this.setLayout(thisLayout);
		{
			jPanel1 = new JPanel();
			this.add(jPanel1, BorderLayout.NORTH);
			TableLayout jPanel1Layout = new TableLayout(new double[][]
			        {{14.0, 10.0, TableLayout.FILL, TableLayout.FILL, 10.0, TableLayout.FILL, 10.0, TableLayout.FILL, 6.0},
					{14.0, TableLayout.PREFERRED, TableLayout.FILL, TableLayout.MINIMUM, TableLayout.MINIMUM, 22.0, 14.0, TableLayout.MINIMUM, 16.0, TableLayout.MINIMUM, 6.0}});
			jPanel1Layout.setHGap(5);
			jPanel1Layout.setVGap(5);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(622, 214));
			{
				descriptionLabel = new JLabel();
				jPanel1.add(descriptionLabel, "1, 1, 7, 1");
				descriptionLabel.setText("Meigas permite la sincronización de los elementos dasocráticos con elementos geográficos mostrados en el SIG.");
			}
			{
				configureChB = new JCheckBox();
				jPanel1.add(configureChB, "1, 3, 2, 3");
				configureChB.setText("Configurar");
				configureChB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							refreshGUI();
					}
				});
			}
			{
				treeLayerLabel = new JLabel();
				jPanel1.add(treeLayerLabel, "2, 4");
				treeLayerLabel.setText("Capa Arboles:");
			}

			{
				plotLayerLabel = new JLabel();
				jPanel1.add(plotLayerLabel, "5, 4");
				plotLayerLabel.setText("Capa Parcelas: ");
			}
			{
				zoomChB = new JCheckBox();
				jPanel1.add(zoomChB, "2, 7");
				zoomChB.setText("Zoom al seleccionar");
				zoomChB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							refreshGUI();
					}
				});
				minScaleChB = new JCheckBox();
				jPanel1.add(minScaleChB, "5, 7");
				minScaleChB.setText("Mínima escala");
				minScaleChB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							refreshGUI();
					}
				});

				minScaleTF = new JTextField();
				jPanel1.add(minScaleTF, "7, 7");
				minScaleTF.setText("1000");

				confAllB = new JButton();
				jPanel1.add(confAllB, "5, 9");
				confAllB.setText("Aceptar");
				confAllB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						saveAndClose();
					}
				});
			}
			{
				syncB = new JButton();
				jPanel1.add(syncB, "7, 9");
				jPanel1.add(getJPanelTreesLayers(), "2, 5, 3, 5");
				jPanel1.add(getJPanelPlotsLayers(), "5, 5, 7, 5");
				syncB.setText("Cancelar");
				syncB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						cancel();
					}
				});
			}
		}

		IGISConnection conn = Meigas.getGISConnection();
		configureChB.setSelected(conn.isSync());
		zoomChB.setSelected(conn.isZoom());
		minScaleChB.setSelected(conn.isMinScale());
		minScaleTF.setText(String.valueOf(conn.getMinScale()));

		refreshGUI();

	}


	protected void saveAndClose() {

		try{
			IGISConnection conn = Meigas.getGISConnection();
			conn.setSync(configureChB.isSelected());
			conn.setZoom(zoomChB.isSelected());
			conn.setMinScale(minScaleChB.isSelected());
			if (minScaleChB.isSelected()){
				conn.setMinScale(Integer.parseInt(minScaleTF.getText()));
			}
			this.dispose();
			this.setVisible(false);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this, "Los parámetros introducidos son invalidos o insuficientes",
										"Parámetros inválidos", JOptionPane.WARNING_MESSAGE);
		}

	}

	protected void cancel() {

		Meigas.getGISConnection().setTreeLayersAndIDFields(m_TreesLayersOld);
		Meigas.getGISConnection().setPlotLayersAndIDFields(m_PlotsLayersOld);

		this.dispose();
		this.setVisible(false);

	}

	public void refreshGUI(){

		treeLayerLabel.setEnabled(configureChB.isSelected());
		plotLayerLabel.setEnabled(configureChB.isSelected());
		jPanelPlotsLayers.setEnabled(configureChB.isSelected());
		jPanelTreesLayers.setEnabled(configureChB.isSelected());
		zoomChB.setEnabled(configureChB.isSelected());
		minScaleChB.setEnabled(configureChB.isSelected()&&zoomChB.isSelected());
		minScaleTF.setEnabled(zoomChB.isSelected()&&minScaleChB.isSelected());
		syncB.setEnabled(configureChB.isSelected());
		confAllB.setEnabled(configureChB.isSelected());

	}

	private JPanel getJPanelTreesLayers() {

		if(jPanelTreesLayers == null) {
			jPanelTreesLayers = new TressLayersSelectionPanel();
		}
		return jPanelTreesLayers;

	}

	private JPanel getJPanelPlotsLayers() {

		if(jPanelPlotsLayers == null) {
			jPanelPlotsLayers = new PlotsLayersSelectionPanel();
		}
		return jPanelPlotsLayers;

	}

}
