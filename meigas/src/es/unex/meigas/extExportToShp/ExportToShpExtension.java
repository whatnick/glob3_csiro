package es.unex.meigas.extExportToShp;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.unex.meigas.core.ConcentricPlot;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.FixedRadiusPlot;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Tree;
import es.unex.meigas.dataObjects.FileOutputChannel;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.exceptions.UnsupportedOutputChannelException;
import es.unex.meigas.extBase.AbstractMeigasExtension;
import es.unex.meigas.extGIS.LayerAndIDField;
import es.unex.meigas.gui.MeigasPanel;

public class ExportToShpExtension extends AbstractMeigasExtension {

	private String[] treeFieldsNames;
	private Class[] treeFieldsTypes;
	private String[] plotFieldsNames;
	private Class[] plotFieldsTypes;

	private JCheckBox plotCheckBox = null;
	private JCheckBox treeCheckBox = null;
	private JTextField treesTextField = null;
	private JTextField plotsTextField = null;
	private JCheckBox loadGISLayerChBox = null;
	private JFileChooser fileChooser = new JFileChooser();
	private JDialog exportDialog = null;

	private MeigasPanel meigasPanel = null;

	// Generic params
	private int STAND = 0;
	private int CRUISE = 1;
	private int PLOT = 2;
	// Tree params
	private int NAME = 3;
	private int NORMAL_DIA = 4;
	private int CROWN_DIA = 5;
	private int HEIGHT_TOT = 6;
	private int HEIGHT_LOG = 7;
	private int VOL_BARK_M3 = 8;
	private int VOL_NO_BARK_M3 = 9;
	private int AGE = 10;
	private int GROW_RAD = 11;
	private int GROW_HEI = 12;
	private int ESP_CORT = 13;
	private int PAR_SHAPE = 14;
	private int SPECIE = 15;
	//Plot params
	private int PLOT_TYPE = 3;
	private int DATE = 4;
	private int CRUISER = 5;
	private int SLOPE = 6;
	private int ASPECT = 7;
	private int ELEVATION = 8;
	private int RADIUS_1 = 9;
	private int RADIUS_2 = 10;
	private int RADIUS_3 = 11;
	private int RADIUS_4 = 12;
	private int RADIUS_5 = 13;
	private int MIN_DIA_1 = 14;
	private int MIN_DIA_2 = 15;
	private int MIN_DIA_3 = 16;
	private int MIN_DIA_4 = 17;
	private int MIN_DIA_5 = 18;

	public String getName(){

		return "Exportar a SHP";

	}

	public String getMenuName() {

		return "Herramientas";

	}

	public Icon getIcon() {

		Icon icon = new ImageIcon(getClass().getClassLoader()
				.getResource("images/folder_go.png"));
		return icon;
	}


	public void setTreeFieldsTypes(){
		if (treeFieldsNames == null){
			setTreeFieldsNames();
		}
		treeFieldsTypes = new Class[treeFieldsNames.length];
		treeFieldsTypes[STAND] = String.class;
		treeFieldsTypes[CRUISE] = String.class;
		treeFieldsTypes[PLOT] = String.class;
		treeFieldsTypes[NAME] = String.class;
		treeFieldsTypes[NORMAL_DIA] = Double.class;
		treeFieldsTypes[CROWN_DIA] = Double.class;
		treeFieldsTypes[HEIGHT_TOT] = Double.class;
		treeFieldsTypes[HEIGHT_LOG] = Double.class;
		treeFieldsTypes[VOL_BARK_M3] = Double.class;
		treeFieldsTypes[VOL_NO_BARK_M3] = Double.class;
		treeFieldsTypes[AGE] = Double.class;
		treeFieldsTypes[GROW_RAD] = Double.class;
		treeFieldsTypes[GROW_HEI] = Double.class;
		treeFieldsTypes[ESP_CORT] = Double.class;
		treeFieldsTypes[PAR_SHAPE] = Integer.class;
		treeFieldsTypes[SPECIE] = String.class;

	}

	public void setTreeFieldsNames(){

		treeFieldsNames = new String[16];
		treeFieldsNames[STAND] = "Canton";
		treeFieldsNames[CRUISE] = "Muestreo";
		treeFieldsNames[PLOT] = "Parcela";
		treeFieldsNames[NAME] = "Nombre";
		treeFieldsNames[NORMAL_DIA] = "Dia_normal";
		treeFieldsNames[CROWN_DIA] = "Dia_copa";
		treeFieldsNames[HEIGHT_TOT] = "Altura_Tot";
		treeFieldsNames[HEIGHT_LOG] = "Altura_Fus";
		treeFieldsNames[VOL_BARK_M3] = "Vol_cc_m3";
		treeFieldsNames[VOL_NO_BARK_M3] = "Vol_sc_m3";
		treeFieldsNames[AGE] = "Edad";
		treeFieldsNames[GROW_RAD] = "Crec_rad";
		treeFieldsNames[GROW_HEI] = "Crec_alt";
		treeFieldsNames[ESP_CORT] = "Esp_cort";
		treeFieldsNames[PAR_SHAPE] = "Par_forma";
		treeFieldsNames[SPECIE] = "Especie";
		//fieldNames[13] = "";

	}

	public void setPlotFieldsTypes(){
		if (plotFieldsNames == null){
			setPlotFieldsNames();
		}
		plotFieldsTypes = new Class[plotFieldsNames.length];
		plotFieldsTypes[STAND] = String.class;
		plotFieldsTypes[CRUISE] = String.class;
		plotFieldsTypes[PLOT] = String.class;
		plotFieldsTypes[PLOT_TYPE] = String.class;
		plotFieldsTypes[DATE] = String.class;
		plotFieldsTypes[SLOPE] = Double.class;
		plotFieldsTypes[ASPECT] = Double.class;
		plotFieldsTypes[ELEVATION] = Double.class;
		plotFieldsTypes[CRUISER] = String.class;
		plotFieldsTypes[RADIUS_1] = Double.class;
		plotFieldsTypes[RADIUS_2] = Double.class;
		plotFieldsTypes[RADIUS_3] = Double.class;
		plotFieldsTypes[RADIUS_4] = Double.class;
		plotFieldsTypes[RADIUS_5] = Double.class;
		plotFieldsTypes[MIN_DIA_1] = Double.class;
		plotFieldsTypes[MIN_DIA_2] = Double.class;
		plotFieldsTypes[MIN_DIA_3] = Double.class;
		plotFieldsTypes[MIN_DIA_4] = Double.class;
		plotFieldsTypes[MIN_DIA_5] = Double.class;

	}

	public void setPlotFieldsNames(){

		plotFieldsNames = new String[19];
		plotFieldsNames[STAND] = "Canton";
		plotFieldsNames[CRUISE] = "Muestreo";
		plotFieldsNames[PLOT] = "Parcela";
		plotFieldsNames[PLOT_TYPE] = "Tipo";
		plotFieldsNames[DATE] = "Fecha";
		plotFieldsNames[SLOPE] = "Pendiente";
		plotFieldsNames[ASPECT] = "Rumbo";
		plotFieldsNames[ELEVATION] = "Altura";
		plotFieldsNames[CRUISER] = "Operario";
		plotFieldsNames[RADIUS_1] = "Radio_1";
		plotFieldsNames[RADIUS_2] = "Radio_2";
		plotFieldsNames[RADIUS_3] = "Radio_3";
		plotFieldsNames[RADIUS_4] = "Radio_4";
		plotFieldsNames[RADIUS_5] = "Radio_5";
		plotFieldsNames[MIN_DIA_1] = "MinDia_1";
		plotFieldsNames[MIN_DIA_2] = "MinDia_2";
		plotFieldsNames[MIN_DIA_3] = "MinDia_3";
		plotFieldsNames[MIN_DIA_4] = "MinDia_4";
		plotFieldsNames[MIN_DIA_5] = "MinDia_5";

	}

	private void refreshGUI(){

		treesTextField.setEnabled(treeCheckBox.isSelected());
		plotsTextField.setEnabled(plotCheckBox.isSelected());

	}

	private void initGUI(){

		exportDialog = new JDialog(Meigas.getMainFrame(), "Exportar a SHP");
		JPanel panel = new JPanel();

		// border
		double b = 10;
		// vertical space between label and element
		double vs = 5;
		// vertical gap
		double vg = 10;
		// horizontal gap
		double hg = 10;
		// prerrefered
		double p = TableLayout.PREFERRED;
		// fill
		double f = TableLayout.FILL;

		double[][] tableSize = {
				{b, p, vs, p, vg, p, vg, p, b},
				{b, p, hg, p, hg, p, hg, p, b}
		};
		panel.setLayout(new TableLayout(tableSize));
		treeCheckBox = new JCheckBox("Árboles");
		treeCheckBox.setSelected(true);
		treeCheckBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				refreshGUI();

			}
		});
		panel.add(treeCheckBox, "1, 1");
		treesTextField = new JTextField("/tmp/trees.shp");
		panel.add(treesTextField, "3, 1, 5, 1");
		JButton treeFileButton = new JButton("...");
		treeFileButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					java.io.File file = fileChooser.getSelectedFile();
					System.out.println(file.getName());
					treesTextField.setText(file.getAbsolutePath());

				}
			}

		});

		panel.add(treeFileButton, "7, 1");

		plotCheckBox = new JCheckBox("Parcelas");
		plotCheckBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				refreshGUI();

			}
		});
		panel.add(plotCheckBox, "1, 3");
		plotsTextField = new JTextField("/tmp/plots.shp");
		panel.add(plotsTextField, "3, 3, 5, 3");
		JButton plotFileButton = new JButton("...");
		plotFileButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					java.io.File file = fileChooser.getSelectedFile();
					System.out.println(file.getName());
					plotsTextField.setText(file.getAbsolutePath());

				}
			}
		});

		panel.add(plotFileButton, "7, 3");

		loadGISLayerChBox = new JCheckBox("Cargar en SIG");
		loadGISLayerChBox.setSelected(false);

		panel.add(loadGISLayerChBox, "1, 5, 5, 5");


		JButton okButton = new JButton("Exportar");
		okButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				export(plotCheckBox.isSelected(),
						plotsTextField.getText(),
						treeCheckBox.isSelected(),
						treesTextField.getText());

				if (loadGISLayerChBox.isSelected()) {
					loadLayers(plotCheckBox.isSelected(),
						plotsTextField.getText(),
						treeCheckBox.isSelected(),
						treesTextField.getText());
				}
			}


		});
		JButton cancelButton = new JButton("Cancelar");
		cancelButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				exportDialog.setVisible(false);
			}

		});

		panel.add(okButton, "3, 7");
		panel.add(cancelButton, "5, 7");
		exportDialog.add(panel);

		refreshGUI();
		exportDialog.pack();
		exportDialog.setResizable(false);
		exportDialog.setVisible(true);
		exportDialog.setLocationRelativeTo(null);

	}


	public void execute(MeigasPanel panel) {

		meigasPanel = panel;
		initGUI();

	}

	private boolean overwriteFile(File file){

		if (file.exists()){
			final JOptionPane optionPane = new JOptionPane(
					"El fichero \"" + file.getAbsolutePath() + "\" ya existe. \n"
					+ "¿Desea sobreescribirlo?",
					JOptionPane.QUESTION_MESSAGE,
					JOptionPane.YES_NO_OPTION);
			final JDialog dialog = new JDialog(Meigas.getMainFrame(),
					"",
					true);
			dialog.setContentPane(optionPane);
			dialog.setLocationRelativeTo(Meigas.getMainFrame());
			dialog.setDefaultCloseOperation(
					JDialog.DO_NOTHING_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					//setLabel("Thwarted user attempt to close window.");
				}
			});
			optionPane.addPropertyChangeListener(
					new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent e) {
							String prop = e.getPropertyName();

							if (dialog.isVisible()
									&& (e.getSource() == optionPane)
									&& (prop.equals(JOptionPane.VALUE_PROPERTY) ||
											prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
								//If you were going to check something
								//before closing the window, you'd do
									//it here.
									dialog.setVisible(false);
							}
						}
					});

			dialog.pack();
			dialog.setVisible(true);

			int value = ((Integer)optionPane.getValue()).intValue();
			dialog.setVisible(false);
			if (value == JOptionPane.NO_OPTION) {
				return false;
			}
		}
		return true;
	}

	private void export(boolean exportPlots, String plotsFilename,
			boolean exportTrees, String treesFilename){

		setTreeFieldsNames();
		setTreeFieldsTypes();
		setPlotFieldsNames();
		setPlotFieldsTypes();

		IVectorLayer treeVectorLayer;
		IVectorLayer plotVectorLayer;
		try {
			if (exportTrees){
				if (!overwriteFile(new File(treesFilename))) {
					return;
				}
				treeVectorLayer = Meigas.getOutputFactory()
												.getNewVectorLayer("Trees",
														IVectorLayer.SHAPE_TYPE_POINT,
														treeFieldsTypes,
														treeFieldsNames,
														new FileOutputChannel(treesFilename),
														null);
				DasocraticElement activeElement = meigasPanel.getActiveElement();
				ArrayList treeArray = activeElement.getTrees();
				for (int i = 0; i < treeArray.size(); i++){
					Tree tree = (Tree)treeArray.get(i);
					// Get Coords
					GeometryFactory gf = new GeometryFactory();
					Coordinate coord = new Coordinate(tree.getCoords().getX(), tree.getCoords().getY());
					Point geom = gf.createPoint(coord);
					//get Values
					Object[] values = getValues(tree);
					treeVectorLayer.addFeature(geom, values);
				}
				treeVectorLayer.postProcess();
			}

			if (exportPlots){
				if (!overwriteFile(new File(plotsFilename))) {
					return;
				}
				plotVectorLayer = Meigas.getOutputFactory()
													.getNewVectorLayer("Plots",
															IVectorLayer.SHAPE_TYPE_POLYGON,
															plotFieldsTypes,
															plotFieldsNames,
															new FileOutputChannel(plotsFilename),
															null);
				DasocraticElement activeElement = meigasPanel.getActiveElement();
				ArrayList plotArray = activeElement.getPlots();
				for (int i = 0; i < plotArray.size(); i++){
					Plot plot = (Plot)plotArray.get(i);
					//get Values
					Object[] values = getValues(plot);
					// Get Coords
					GeometryFactory gf = new GeometryFactory();
					Coordinate coord = new Coordinate(plot.getCoords().getX(), plot.getCoords().getY());
					Point center = gf.createPoint(coord);
					if (plot instanceof ConcentricPlot){
						//TODO Check which is the greatest radius
						double radius = getMaxRadius((ConcentricPlot)plot);
						Geometry geom = center.buffer(radius);
						plotVectorLayer.addFeature(geom, values);
					} else {
						// FixedRadioPlot
						double radius = (Double)values[RADIUS_1];
						Geometry geom = center.buffer(radius);
						plotVectorLayer.addFeature(geom, values);
					}
				}
				plotVectorLayer.postProcess();
				//Set gisLayer params
			}

		} catch (UnsupportedOutputChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		exportDialog.setVisible(false);
	}

	/**
	 * Load the exported layers of Meigas into gvSIG ToC
	 *
	 * @param exportPlots true to load Plots layers, false otherwise
	 * @param plotsFilename path to the Plots layer
	 * @param exportTrees true to load Trees layers, false otherwise
	 * @param treesFilename path to the Trees layer
	 */
	private void loadLayers(boolean exportPlots, String plotsFilename,
			boolean exportTrees, String treesFilename){

		if (exportPlots){
			IVectorLayer layer = (IVectorLayer) Meigas.getInputFactory().openDataObjectFromFile(plotsFilename);
			layer.setName("Parcelas");
			LayerAndIDField layerAndIDField = new LayerAndIDField(layer, plotFieldsNames[NAME]);
			Meigas.getGISConnection().addPlotLayerAndIDField(layerAndIDField);
			Meigas.getInputFactory().addDataObjectToGIS(layer);
			Meigas.getInputFactory().addDataObject(layer);
		}

		if (exportTrees){
			IVectorLayer layer = (IVectorLayer) Meigas.getInputFactory().openDataObjectFromFile(treesFilename);
			LayerAndIDField layerAndIDField = new LayerAndIDField(layer, treeFieldsNames[NAME]);
			Meigas.getGISConnection().addTreeLayerAndIDField(layerAndIDField);
			layer.setName("Arboles");
			Meigas.getInputFactory().addDataObjectToGIS(layer);
			Meigas.getInputFactory().addDataObject(layer);
		}
	}


	private double getMaxRadius(ConcentricPlot plot){

		Double[] radius = plot.getRadius();
		double max_radius = Double.MIN_VALUE;
		for (int i = 0; i < radius.length; i++){
			if (max_radius < radius[i]){
				max_radius = radius[i];
			}
		}
		return max_radius;

	}

	private Object[] getValues(DasocraticElement element) {

		Object values[] = null;
		if (element instanceof Tree){
			Tree tree = (Tree)element;
			values = new Object[treeFieldsNames.length];

			DasocraticElement plot = tree.getParent();
			DasocraticElement cruise = plot.getParent();
			DasocraticElement stand = cruise.getParent();

			values[STAND] = stand.getName();
			values[CRUISE] = cruise.getName();
			values[PLOT] = plot.getName();
			values[NAME] = tree.getName();
			values[NORMAL_DIA] = new Double(tree.getDBH().getValue());
			values[CROWN_DIA] = new Double(tree.getCrownDiameter().getValue());
			values[HEIGHT_TOT] = new Double(tree.getHeight().getValue());
			values[HEIGHT_LOG] = new Double(tree.getLogHeight().getValue());
			values[VOL_BARK_M3] = new Double(tree.getVolumeWithBark().getValue());
			values[VOL_NO_BARK_M3] = new Double(tree.getVolumeWithoutBark().getValue());
			values[AGE] = new Double(tree.getAge().getValue());
			values[GROW_RAD] = new Double(tree.getRadialGrowth().getValue());
			values[GROW_HEI] = new Double(tree.getHeightGrowth().getValue());
			values[ESP_CORT] = new Double(tree.getTotalVolumeWithBark());
			values[PAR_SHAPE] = new Integer(tree.getShapeFactor());
			values[SPECIE] = tree.getSpecie();
		}

		if (element instanceof Plot){
			Plot plot = (Plot)element;
			values = new Object[plotFieldsNames.length];

			DasocraticElement cruise = plot.getParent();
			DasocraticElement stand = cruise.getParent();

			values[STAND] = stand.getName();
			values[CRUISE] = cruise.getName();
			values[PLOT] = plot.getName();
			values[SLOPE] = new Double(plot.getSlope().getValue());
			values[ASPECT] = new Double(plot.getAspect().getValue());
			values[ELEVATION] = new Double(plot.getElevation().getValue());
			values[DATE] = plot.getDate().toString();
			values[CRUISER] = plot.getCruiser();

			if (plot instanceof ConcentricPlot) {
				ConcentricPlot cPlot = (ConcentricPlot)plot;
				values[PLOT_TYPE] = "CONCENTRIC";

				Double[] radius = cPlot.getRadius();
				if (radius.length > 0) {
					values[RADIUS_1] = new Double(radius[0]);
				}
				if (radius.length > 1) {
					values[RADIUS_2] = new Double(radius[1]);
				}
				if (radius.length > 2) {
					values[RADIUS_3] = new Double(radius[2]);
				}
				if (radius.length > 3) {
					values[RADIUS_4] = new Double(radius[3]);
				}
				if (radius.length > 4) {
					values[RADIUS_5] = new Double(radius[4]);
				}

				Double[] diam = cPlot.getMinAcceptableDiameters();
				if (diam.length > 0) {
					values[MIN_DIA_1] = new Double(diam[0]);
				}
				if (diam.length > 1) {
					values[MIN_DIA_2] = new Double(diam[1]);
				}
				if (diam.length > 2) {
					values[MIN_DIA_3] = new Double(diam[2]);
				}
				if (diam.length > 3) {
					values[MIN_DIA_4] = new Double(diam[3]);
				}
				if (diam.length > 4) {
					values[MIN_DIA_5] = new Double(diam[4]);
				}
			} else {
				values[PLOT_TYPE] = "FIXED_RADIUS";
				FixedRadiusPlot fPlot = (FixedRadiusPlot) plot;
				values[RADIUS_1] = new Double(fPlot.getRadius().getValue());
			}
		}
		return values;
	}

	public void initialize() {}

	public boolean showInContextMenu() {

		return true;

	}

	public boolean showInMenuBar(MeigasPanel panel) {

		return false;

	}

	public boolean isEnabled(MeigasPanel window) {

		return true;

	}
}