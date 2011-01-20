	package es.unex.meigas.extImportFromLayer;

import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.vividsolutions.jts.geom.Geometry;

import es.unex.meigas.core.ConcentricPlot;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.FixedRadiusPlot;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Tree;
import es.unex.meigas.dataObjects.AbstractInputFactory;
import es.unex.meigas.dataObjects.IFeature;
import es.unex.meigas.dataObjects.IFeatureIterator;
import es.unex.meigas.dataObjects.IRecord;
import es.unex.meigas.dataObjects.IVectorLayer;
import es.unex.meigas.exceptions.IteratorException;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;

public class ImportFromLayerWizard extends MainWizardWindow {

	private MeigasPanel meigasPanel = null;
	private IVectorLayer layer = null;

	public ImportFromLayerWizard(MeigasPanel panel) {

		super(panel);
		meigasPanel = panel;
		setName("Importar datos de capa vectorial");

	}

	@Override
	protected void finish() {

		DefaultTreeModel model = (DefaultTreeModel) meigasPanel.getTree().getModel();

		SelectPlotsForTreesPanel plotsForTreesPanel = ((SelectPlotsForTreesPanel)m_Panels[4]);
		DefaultMutableTreeNode elementNode = (DefaultMutableTreeNode) meigasPanel.getActiveTreePath().getLastPathComponent();
		SelectElementTypePanel elementTypePanel = (SelectElementTypePanel) m_Panels[0];
		if (elementTypePanel.getElementType() == SelectElementTypePanel.TREE){
			if (plotsForTreesPanel.usePlotsAttribute()) {
				importTreesUsingPlotAttribute(model, elementNode);
			} else if (plotsForTreesPanel.useCoordinates()){
				importTreesUsingCoordinates(model, elementNode);
			} else {
				// Ignore Plots
				importTreesIgnoringPlots(model, elementNode);
			}
		}
		else{
			importPlots(model, elementNode);
		}

		//TODO JDialog that shows a msg of success
		model.reload(elementNode);
		meigasPanel.getTree().repaint();

	}

	private void importPlots(DefaultTreeModel model,
			DefaultMutableTreeNode elementNode) {

		Plot[] plots = getPlots();

		for (int i = 0; i < plots.length; i++){

			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode();

			Plot plot = plots[i];
			newChild.setUserObject(plot);

			((DasocraticElement) elementNode.getUserObject()).addElement(plot);
			elementNode.add(newChild);

		}

	}

	private void importTreesUsingCoordinates(DefaultTreeModel model,
			DefaultMutableTreeNode elementNode) {

		Tree[] trees = getTrees();

		HashMap<Plot, DefaultMutableTreeNode> plotNodes = new HashMap<Plot, DefaultMutableTreeNode>();
		Enumeration plots = elementNode.children();
		while (plots.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) plots.nextElement();
			Plot plot = (Plot) node.getUserObject();
			plotNodes.put(plot, node);
		}


		for (int i = 0; i < trees.length; i++){
			Set<Plot> set = plotNodes.keySet();
			Iterator<Plot> iter = set.iterator();
			while(iter.hasNext()){
				Plot plot = iter.next();
				if (trees[i].insidePlot(plot)){
					DefaultMutableTreeNode newChild = new DefaultMutableTreeNode();
					DasocraticElement tree = trees[i];
					((DasocraticElement) elementNode.getUserObject()).addElement(tree);
					elementNode.add(newChild);
					break;
				}
			}
		}

	}

	private void importTreesUsingPlotAttribute(DefaultTreeModel model,
										  	   DefaultMutableTreeNode elementNode){

		Tree[] trees = getTrees();

		HashMap<String, DefaultMutableTreeNode> plotNodes = new HashMap<String, DefaultMutableTreeNode>();
		Enumeration plots = elementNode.children();
		while (plots.hasMoreElements()){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) plots.nextElement();
			Plot plot = (Plot) node.getUserObject();
			plotNodes.put(plot.getName(), node);
		}

		SelectPlotsForTreesPanel plotsForTreesPanel = ((SelectPlotsForTreesPanel)m_Panels[4]);
		String[] plotsOfTheTrees = plotsForTreesPanel.getParentTreeNodes();

		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) elementNode;
		DasocraticElement parentElement = (DasocraticElement)parentNode.getUserObject();

		for (int i = 0; i < plotsOfTheTrees.length; i++){
			String plotName = plotsOfTheTrees[i];
			if (!plotNodes.containsKey(plotName)){
				DefaultMutableTreeNode plotNode = null;
				//if (plotsOfTheTrees[i].toLowerCase().indexOf("fixed") != -1) {
					plotNode = addNewFixedRadiusPlot(plotName,
							parentElement,
							parentNode,
							model);
				/*} else {
					plotNode = addNewConcentricPlot(plotName,
							parentElement,
							parentNode,
							model);
				}*/
				plotNodes.put(plotName, plotNode);
			}
		}

		for (int i = 0; i < trees.length; i++){

			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode();

			DasocraticElement tree = trees[i];
			String plotName = plotsOfTheTrees[i];
			newChild.setUserObject(tree);

			DefaultMutableTreeNode plotElement = ((DefaultMutableTreeNode)plotNodes.get(plotName));
			((DasocraticElement) plotElement.getUserObject()).addElement(tree);
			plotElement.add(newChild);

		}

	}


	private Plot[] getPlots() {

		Plot elements[] = new Plot[getLayer().getShapesCount()];
		SelectPlotParametersFieldsPanel plotParamsPanel = (SelectPlotParametersFieldsPanel) m_Panels[5];

		int nameIdx = plotParamsPanel.getNameFieldIndex()-1;
		int dateIdx = plotParamsPanel.getDateFieldIndex()-1;
		int cruiserIdx = plotParamsPanel.getCruiserFieldIndex()-1;
		int elevationIdx = plotParamsPanel.getElevationFieldIndex()-1;
		int slopeIdx = plotParamsPanel.getSlopeFieldIndex()-1;
		int aspectIdx = plotParamsPanel.getAspectFieldIndex()-1;
		int plotTypeIdx = plotParamsPanel.getPlotTypeFieldIndex()-1;

		int plotRadiusIdx = plotParamsPanel.getRadiusIndex() - 1;

		Integer[] concentricPlotRadiusIdx = plotParamsPanel.getConcentricPlotRadiusFieldIndices();
		Integer[] concentricPlotMinDiametersIdx = plotParamsPanel.getMinimumDiameterFieldIndices();

		int counter = 0;
		for (IFeatureIterator it = layer.iterator(); it.hasNext(); counter++){
			Plot plot = null;
			String name = "plot_" + Integer.toString(counter);
			Date date = new Date();
			String cruiser = "";
			double elevation = 0.0;
			double slope = 0.0;
			double aspect = 0.0;
			String plotType = "";
			try {
				IFeature feat = it.next();
				IRecord record = feat.getRecord();
				if (nameIdx != -1){
					name = (String) record.getValue(nameIdx).toString();
				}
				if (dateIdx != -1){
					Object obj = record.getValue(dateIdx);
					if (obj instanceof Date){
						date = (Date) obj;
					}
				}
				if (cruiserIdx != -1){
					cruiser = (String) record.getValue(cruiserIdx);
				}
				if (elevationIdx != -1){
					elevation = (Double) record.getValue(elevationIdx);
				}
				if (slopeIdx != -1){
					slope = (Double) record.getValue(slopeIdx);
				}
				if (aspectIdx != -1){
					aspect = (Double) record.getValue(aspectIdx);
				}
				if (plotTypeIdx != -1){
					plotType = (String) record.getValue(plotTypeIdx);
				}
				if (plotType.toUpperCase().compareTo("CONCENTRIC") == 0){
					plot = new ConcentricPlot();
					Double dRadius[] = new Double[concentricPlotRadiusIdx.length];
					for (int i = 0; i < concentricPlotRadiusIdx.length; i++) {
						dRadius[i] = (Double) record.getValue(concentricPlotRadiusIdx[i].intValue());
					}
					((ConcentricPlot)plot).setRadius(dRadius);
					Double dMindiameter[] = new Double[concentricPlotRadiusIdx.length];
					for (int i = 0; i < concentricPlotMinDiametersIdx.length; i++) {
						dMindiameter[i] = (Double) record.getValue(concentricPlotMinDiametersIdx[i].intValue());
					}
					((ConcentricPlot)plot).setMinAcceptableDiameters(dMindiameter);
				} else {
					plot = new FixedRadiusPlot();
					double radius = 0;
					if (plotRadiusIdx != -1){
						radius = (Double) record.getValue(plotRadiusIdx);
					}
					((FixedRadiusPlot)plot).getRadius().setValue(radius);
				}

				plot.setCruiser(cruiser);
				plot.setDate(date);
				plot.setName(name);
				plot.getSlope().setValue(slope);
				plot.getAspect().setValue(aspect);
				plot.getElevation().setValue(elevation);
				Geometry geometry = feat.getGeometry();
				double coordx = geometry.getCoordinate().x;
				double coordy = geometry.getCoordinate().y;

				Point2D coords = new Point2D.Double(coordx, coordy);
				plot.setCoords(coords);
				elements[counter] = plot;

			} catch (Exception e) {
				// ignore plot
			}
		}
		return elements;

	}



	private Tree[] getTrees(){

		Tree[] elements = new Tree[getLayer().getShapesCount()];

		SelectTreeParametersFieldsPanel treeParamsPanel = (SelectTreeParametersFieldsPanel) m_Panels[2];

		int nameIdx = treeParamsPanel.getPlotNameFieldIndex()-1;
		int dbhIdx = treeParamsPanel.getDBHFieldIndex()-1;
		int heightIdx = treeParamsPanel.getHeightFieldIndex()-1;
		int volumeIdx = treeParamsPanel.getVolumeFieldIndex()-1;
		int noBarkVolumeIdx = treeParamsPanel.getNoBarkVolumeFieldIndex()-1;
		int ageIdx = treeParamsPanel.getAgeFieldIndex()-1;
		int radialGrowthIdx = treeParamsPanel.getRadialGrowthFieldIndex()-1;
		int heightGrowthIdx = treeParamsPanel.getHeightGrowthFieldIndex()-1;
		int logHeightIdx = treeParamsPanel.getLogHeightFieldIndexIndex()-1;
		int crownDiameterIdx = treeParamsPanel.getCrownDiameterFieldIndex()-1;
		int barkIdx = treeParamsPanel.getBarkFieldIndex()-1;
		int positionIdx = treeParamsPanel.getPositionFieldIndex()-1;
		int specieIdx = treeParamsPanel.getSpecieFieldIndex()-1;
		int shapeFactorIdx = treeParamsPanel.getShapeFactorFieldIndex()-1;

		int counter = 0;
		for (IFeatureIterator it = getLayer().iterator(); it.hasNext(); counter++){
			Tree tree = new Tree();
			try {
				String name = "default";
				double dbh = 0.0;
				double height = 0.0;
				double volume = 0.0;
				double noBarkVolume = 0.0;
				double age = 0.0;
				double radialGrowth = 0.0;
				double heightGrowth = 0.0;
				double logHeight = 0.0;
				double crownDiameter = 0.0;
				double bark = 0.0;
				int position = 1;
				String specie = "";
				int shapeFactor = 0;
				double coordx = 0.0;
				double coordy = 0.0;

				IFeature feat = it.next();
				IRecord record = feat.getRecord();
				if (nameIdx != -1){
					name = (String) record.getValue(nameIdx).toString();
				}
				if (dbhIdx != -1){
					dbh = (Double) record.getValue(dbhIdx);
				}
				if (heightIdx != -1){
					height = (Double) record.getValue(heightIdx);
				}
				if (volumeIdx != -1){
					volume = (Double) record.getValue(volumeIdx);
				}
				if (noBarkVolumeIdx != -1){
					noBarkVolume = (Double) record.getValue(noBarkVolumeIdx);
				}
				if (ageIdx != -1){
					age = (Double) record.getValue(ageIdx);
				}
				if (radialGrowthIdx != -1){
					radialGrowth = (Double) record.getValue(radialGrowthIdx);
				}
				if (heightGrowthIdx != -1){
					heightGrowth = (Double) record.getValue(heightGrowthIdx);
				}
				if (logHeightIdx != -1){
					logHeight = (Double) record.getValue(logHeightIdx);
				}
				if (crownDiameterIdx != -1){
					crownDiameter = (Double) record.getValue(crownDiameterIdx);
				}
				if (barkIdx != -1){
					bark = (Double) record.getValue(barkIdx);
				}
				if (positionIdx != -1){
					position = (Integer) record.getValue(positionIdx);
				}
				if (specieIdx != -1){
					specie = (String) record.getValue(specieIdx);
				}
				if (shapeFactorIdx != -1){
					shapeFactor = (Integer) record.getValue(shapeFactorIdx);
				}

				SelectTreeCoordinatesPanel treeCoordsPanel = (SelectTreeCoordinatesPanel) m_Panels[3];

				if (treeCoordsPanel.useGeometry()){
					Geometry geometry = feat.getGeometry();
					coordx = geometry.getCoordinate().x;
					coordy = geometry.getCoordinate().y;
				} else {
					int coordXIdx = treeCoordsPanel.getTreeCoordXIdx();
					int coordYIdx = treeCoordsPanel.getTreeCoordXIdx();
					if (coordXIdx != -1){
						coordx = (Double) record.getValue(coordXIdx);
					}
					if (coordYIdx != -1){
						coordy = (Double) record.getValue(coordYIdx);
					}
				}

				tree.setValues(name, coordx, coordy,
							   dbh, height, volume,
							   noBarkVolume, age, radialGrowth,
							   heightGrowth, logHeight,
							   crownDiameter, bark,
							   position, specie,
							   shapeFactor);
				Point2D coords = new Point2D.Double(coordx, coordy);
				tree.setCoords(coords);
				elements[counter] = tree;

			} catch (IteratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return elements;

	}


	private void importTreesIgnoringPlots(DefaultTreeModel model,
										  DefaultMutableTreeNode elementNode){

		DasocraticElement[] trees = getTrees();

		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) elementNode;
		DasocraticElement parent = (DasocraticElement)parentNode.getUserObject();

		DefaultMutableTreeNode plotNode = addNewFixedRadiusPlot("Fixed Radius Plot",
				parent, parentNode, model);

		for (int i = 0; i < trees.length; i++){
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode();
			DasocraticElement tree = trees[i];
			newChild.setUserObject(tree);

			((DasocraticElement) plotNode.getUserObject()).addElement(tree);
			plotNode.add(newChild);
		}

	}

	/**
	 * Create a new Concentric Plot, added to the model and return a reference to
	 * this object
	 *
	 * @return
	 */
//	private DefaultMutableTreeNode addNewConcentricPlot(String plotName,
//			DasocraticElement parentElement,
//			DefaultMutableTreeNode parentNode,
//			DefaultTreeModel model) {
//
//				//ConcentricPlot
//		ConcentricPlot plot = new ConcentricPlot();
//		plot.setName(plotName);
//		plot.setNotes("Notes of "+ plot.getName());
//		plot.setDate(new Date());
//		//plot.setCruiser(parentElement.getName());
//		//TODO Coords of trees must be inside plot area
//		double auxX = 0.0;
//		double auxY = 0.0;
//		Point2D coords = new Point2D.Double(auxX, auxY);
//		plot.setCoords(coords);
////		element2.setRegenerationNotes(sRegenerationNotes);
//		//TODO
//		Double[] radiusArray = {new Double(5.0), new Double(10.0),
//								new Double(15.0), new Double(25.0)};
//		plot.setRadius(radiusArray);
//		Double[] mDiamenters = {new Double(7.5), new Double(12.5),
//								new Double(22.5), new Double(42.5)};
//		plot.setMinAcceptableDiameters(mDiamenters);
//
//		parentElement.addElement(plot);
//		DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(plot);
//		model.insertNodeInto(elementNode, parentNode, parentNode.getChildCount());
//
//		return elementNode;
//	}

	/**
	 * Create a new Fixed Radius Plot, added to the model and return a reference to
	 * this object
	 *
	 * @return
	 */
	private DefaultMutableTreeNode addNewFixedRadiusPlot(String plotName,
			DasocraticElement parentElement,
			DefaultMutableTreeNode parentNode,
			DefaultTreeModel model) {

		//FixedRadiusPlot
		FixedRadiusPlot plot = new FixedRadiusPlot();
		plot.setName(plotName);
		plot.setNotes("Notes of "+plot.getName());
		plot.setDate(new Date());
		//TODO Coords of trees must be inside plot area
		double auxX = 0.0;
		double auxY = 0.0;
		Point2D coords = new Point2D.Double(auxX, auxY);
		plot.setCoords(coords);
//		element1.setRegenerationNotes(sRegenerationNotes);
		plot.getRadius().setValue(25);
		//plot.setCruiser(parentElement.getName());
		parentElement.addElement(plot);
		DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(plot);
		model.insertNodeInto(elementNode, parentNode, parentNode.getChildCount());

		return elementNode;
	}

	protected void setPanels() {

		m_Panels = new BaseWizardPanel[6];
		m_Panels[0] = new SelectElementTypePanel(this);
		m_Panels[1] = new LayerSelectionPanel(this);
		m_Panels[2] = new SelectTreeParametersFieldsPanel(this);
		m_Panels[3] = new SelectTreeCoordinatesPanel(this);
		m_Panels[4] = new SelectPlotsForTreesPanel(this);
		m_Panels[5] = new SelectPlotParametersFieldsPanel(this);

	}

	protected void previousPanel() {

		if (m_iCurrentPanel == 5){
			m_iCurrentPanel = 1;
		}
		else{
			m_iCurrentPanel--;
		}
		jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
		updateButtons();

	}

	protected void nextPanel() {

		if (m_iCurrentPanel == 1){
			SelectElementTypePanel panel = (SelectElementTypePanel) m_Panels[0];
			if (panel.getElementType() == SelectElementTypePanel.TREE){
				m_iCurrentPanel = 2;
			}
			else{
				m_iCurrentPanel = 5;
			}
			jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
			updateButtons();
		}
		if (m_iCurrentPanel == m_Panels.length - 1 ||
				m_Panels[m_iCurrentPanel].isFinish()){
			finish();
			cancel();
		}
		else{
			m_iCurrentPanel++;
			jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
			updateButtons();
		}

	}

	protected void setLayer(IVectorLayer layer){

		this.layer = layer;

		for (int i = 2; i < this.m_Panels.length; i++) {
			m_Panels[i].initGUI();
		}

	}

	protected IVectorLayer getLayer(){

		if (layer == null){
			IVectorLayer[] layers = Meigas.getInputFactory().getVectorLayers(AbstractInputFactory.SHAPE_TYPE_ANY);
			setLayer(layers[0]);
		}

		return layer;

	}

	protected String[] getAttributes(){

		String[] atts = new String[getLayer().getFieldCount()+1];
		// First element a empty string
		System.arraycopy(getLayer().getFieldNames(), 0, atts, 1, getLayer().getFieldCount());
		atts[0] = "";
		return atts;

	}

	protected void selectOneOfThis(JComboBox combo, String[] strings){

		if (combo == null || combo.getItemCount() == 0){
			combo = new JComboBox(getAttributes());
		}

		boolean found = false;
		for (int i = 0; !found && i < strings.length; i++){
			String str = strings[i].toUpperCase();
			for (int j = 0; j < combo.getItemCount(); j++){
				String item = combo.getItemAt(j).toString().toUpperCase();
				if (item.indexOf(str)>-1){
					found = true;
					combo.setSelectedIndex(j);
					break;
				}
			}
		}

	}

	protected int getTreeCoordXIdx(){
		SelectTreeCoordinatesPanel panel3 = ((SelectTreeCoordinatesPanel)m_Panels[2]);
		return (panel3.coordXComboBox.getSelectedIndex()-1);
	}

	protected int getTreeCoordYIdx(){
		SelectTreeCoordinatesPanel panel3 = ((SelectTreeCoordinatesPanel)m_Panels[2]);
		return (panel3.coordYComboBox.getSelectedIndex()-1);
	}

}
