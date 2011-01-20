/*******************************************************************************
IFN2Panel.java
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
package es.unex.meigas.extIFN3;

import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import es.unex.meigas.core.AdministrativeUnit;
import es.unex.meigas.core.ConcentricPlot;
import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;
import es.unex.meigas.dataObjects.IRecord;
import es.unex.meigas.dataObjects.IRecordsetIterator;
import es.unex.meigas.dataObjects.ITable;
import es.unex.meigas.exceptions.IteratorException;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;

public class IFN3Panel extends MainWizardWindow{

	private Geometry m_Limits[];
	private Cruise m_Cruises[];
	private ArrayList m_Plots;

	private int TREE_ID = -1;
	private int TREE_PLOT_ID = -1;
	private int TREE_ANGLE = -1; // Rumbo
	private int TREE_DISTANCE = -1; //
	private int TREE_SPECIE = -1; //
	private int TREE_DBH1 = -1; //
	private int TREE_DBH2 = -1; //
	private int TREE_SHAPE_FACTOR = -1; //
	private int TREE_HEIGHT = -1; //
	//
	private int TREE_COMPLETE_ID = -1;
	private int TREE_COMPLETE_ID2 = -1;
	private int TREE_CROWN_DIAMETER1 = -1;
	private int TREE_CROWN_DIAMETER2 = -1;
	private int TREE_BARK1 = -1;
	private int TREE_BARK2 = -1;
	private int TREE_GROWTH1 = -1;
	private int TREE_GROWTH2 = -1;
	private int TREE_LOG_HEIGHT = -1;

	private int PLOT_ID = -1;
	private int PLOT_COORDX = -1;
	private int PLOT_COORDY = -1;
	private int PLOT_DATE = -1;
	private int PLOT_ASPECT = -1;
	private int PLOT_ELEVATION = -1;
	private int PLOT_SLOPE = -1;

	private String treeFile00 = "PCMayores.dbf";
	// private String treeFile01 = "PCMayores2.dbf"; // IFN2
	private String plotFile00 = "PCParcelas.dbf";
	private String plotFile01 = "Listado Definitivo.dbf";

	private void getIndexes(){

		///////////////////////////////
		// Parcelas: PCParcelas.dbf
		ITable dbfFile = (ITable) Meigas.getInputFactory().openDataObjectFromFile(plotFile00);
		//TODO WarningDialog
		if (dbfFile == null){
			System.out.println("No existe el fichero: "+ plotFile00);
		}

		PLOT_ID = dbfFile.getFieldIndexByName("ESTADILLO");
		// There are another Aspects: Orienta2 and for the photos
		PLOT_ASPECT = dbfFile.getFieldIndexByName("ORIENTA1");

		// TODO
		//PLOT_ELEVATION = dbfFile.getFieldIndexByName("");

		// There are another Slopes
		PLOT_SLOPE = dbfFile.getFieldIndexByName("MAXPEND1");

		// There is another date: FechaFin and HoraIni - HoraFin
		PLOT_DATE = dbfFile.getFieldIndexByName("FECHAINI");

		/////////////////////////////
		// Parcelas: Listado Definitivo.dbf
		dbfFile = (ITable) Meigas.getInputFactory().openDataObjectFromFile(plotFile01);
		//TODO WarningDialog
		if (dbfFile == null){
			System.out.println("No existe el fichero: "+ plotFile01);
		}

		PLOT_COORDX = dbfFile.getFieldIndexByName("COORX");
		PLOT_COORDY = dbfFile.getFieldIndexByName("COORY");

		/////////////////////////////
		// Parcelas: PCMayores.dbf
		dbfFile = (ITable) Meigas.getInputFactory().openDataObjectFromFile(treeFile00);
		//TODO WarningDialog
		if (dbfFile == null){
			System.out.println("No existe el fichero: "+ treeFile00);
		}

		TREE_PLOT_ID =  dbfFile.getFieldIndexByName("ESTADILLO");
		TREE_ID =  dbfFile.getFieldIndexByName("ORDENIF3");

		TREE_ANGLE = dbfFile.getFieldIndexByName("RUMBO");

		//TREE_CROWN_DIAMETER1 = dbfFile.getFieldIndexByName("Rumbo");
		TREE_DISTANCE = dbfFile.getFieldIndexByName("DISTANCI");

		TREE_HEIGHT = dbfFile.getFieldIndexByName("HT");

		TREE_DBH1 = dbfFile.getFieldIndexByName("DN1");

		TREE_DBH2 = dbfFile.getFieldIndexByName("DN2");

		TREE_SPECIE = dbfFile.getFieldIndexByName("ESPECIE");

		TREE_SHAPE_FACTOR = dbfFile.getFieldIndexByName("FORMA");

		//TREE_BARK1 = dbfFile.getFieldIndexByName("");


	}

	public IFN3Panel(MeigasPanel panel) {

		super(panel);

		setName("Extracción de datos del IFN3");

	}

	protected void finish() {

		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		m_Plots = new ArrayList();

		createCruises();
		fillCruises();
		filter();

		m_MeigasPanel.fillTree();
		m_MeigasPanel.updateSelection(null);
		m_MeigasPanel.setEnabledButtons();

		this.setCursor(Cursor.getDefaultCursor());

	}

	private void createCruises(){

		Stand stand;
		int i;
		DasocraticElement activeElement = m_MeigasPanel.getActiveElement();

		int iLimits = ((ExtentDefinitionPanel)m_Panels[1]).getSelectedType();
		if (iLimits == ExtentDefinitionPanel.USE_GEOMETRY){
			ArrayList stands;
			if (activeElement instanceof AdministrativeUnit || activeElement instanceof Stand){
				stands = activeElement.getStandsWithLimits();
			}
			else{
				stands = activeElement.getParentOfType(DasocraticElement.STAND).getStandsWithLimits();
			}
			m_Cruises = new Cruise[stands.size()];
			m_Limits = new Polygon[stands.size()];
			for (i = 0; i < stands.size(); i++){
				m_Cruises[i] = new Cruise();
				stand = (Stand) stands.get(i);
				stand.addElement(m_Cruises[i]);
				m_Limits[i] = stand.getPolygon().geom;
			}
		}
		else{
			if (activeElement instanceof AdministrativeUnit){
				stand = new Stand();
				DasocraticElement parent = activeElement.addElement(stand);
				if (parent == null){
					AdministrativeUnit unit = new AdministrativeUnit();
					activeElement.addElement(unit);
					unit.addElement(stand);
				}
			}
			else if (activeElement instanceof Stand){
				stand = (Stand) activeElement;
			}
			else{
				stand = (Stand) activeElement.getParentOfType(DasocraticElement.STAND);
			}
			m_Cruises = new Cruise[1];
			m_Cruises[0] = new Cruise();
			stand.addElement(m_Cruises[0]);
			m_Limits = new Polygon[1];
			if (iLimits == ExtentDefinitionPanel.USE_RECT){
				GeometryFactory gf = new GeometryFactory();
				Rectangle2D rect = ((ExtentDefinitionPanel)m_Panels[1]).getRect();
				Coordinate[] coords = new Coordinate[5];
				coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
				coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
				coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
				coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
				coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
				m_Limits[0] = gf.createPolygon(gf.createLinearRing(coords), null);
			}
			else{
				m_Limits[0] = null;
			}
		}

	}

	private void filter() {

		int i;

		for (i = 0; i < m_Plots.size(); i++){
			Plot plot = (Plot)m_Plots.get(i);
			if (plot.getElementsCount() == 0){
				plot.getParent().removeElement(plot);
			}
		}

		for (i = 0; i < m_Cruises.length; i++){
			if (m_Cruises[i].getElementsCount() == 0){
				m_Cruises[i].getParent().removeElement(m_Cruises[i]);
			}
		}

	}

	private boolean fillPlots(HashMap plots) {

		int plot_iID;
		int iShapeFactor;
		double dDBH, dDBH1, dDBH2;
		double dCrownDiameter, dCrownDiameter1, dCrownDiameter2;
		double dBark, dBark1, dBark2;
		double dGrowth, dGrowth1, dGrowth2;
		double dHeight;
		double dAngle, dDistance;
		double dX, dY;
		String sTreeID;
		String s;
		String sSpecie;
		String sCompleteTreeID;
		Plot plot;
		Tree tree;
		HashMap completeTrees = new HashMap();

		ITable dbFile00 = (ITable) Meigas.getInputFactory().openDataObjectFromFile(treeFile00);

		IRecordsetIterator iter = dbFile00.iterator();

		while (iter.hasNext()){
			try {
				IRecord record = iter.next();
				plot_iID = Integer.parseInt(record.getValue(TREE_PLOT_ID).toString().trim());
				plot = (Plot) plots.get(new Integer(plot_iID));
				if (plot != null){
					sTreeID = record.getValue(TREE_ID).toString().trim();
					dDBH1 = Integer.parseInt(record.getValue(TREE_DBH1).toString().trim());
					dDBH2 = Integer.parseInt(record.getValue(TREE_DBH2).toString().trim());
					dDBH = (dDBH1 + dDBH2) / 20;
					dHeight = Double.parseDouble(record.getValue(TREE_HEIGHT).toString().trim());
					iShapeFactor = Integer.parseInt(record.getValue(TREE_SHAPE_FACTOR).toString().trim());
					sSpecie = record.getValue(TREE_SPECIE).toString().trim();
					dDistance = Double.parseDouble(record.getValue(TREE_DISTANCE).toString().trim());
					dAngle = Double.parseDouble(record.getValue(TREE_ANGLE).toString().trim());
					dX = plot.getCoords().getX() + dDistance * Math.sin(dAngle * Math.PI / 180.);
					dY = plot.getCoords().getY() + dDistance * Math.cos(dAngle * Math.PI / 180.);
					tree = new Tree();
					tree.setName(Integer.toString(plot_iID) + "-" + sTreeID);
					tree.getDBH().setValue(dDBH);
					tree.setShapeFactor(iShapeFactor);
					tree.getHeight().setValue(dHeight);
					tree.setCoords(new Point2D.Double(dX,dY));
					tree.setSpecie(sSpecie);
					plot.addElement(tree);
					completeTrees.put(tree.getName(), tree);
				}
			}
			catch (IteratorException e) {
				//TODO
			}
		}
		iter.close();

		dbFile00.close();

		return true;

	}


	private void initDBFFiles(File file){

		treeFile00 = file.getParent() + File.separator + treeFile00;
		plotFile00 = file.getParent() + File.separator + plotFile00;
		plotFile01 = file.getParent() + File.separator + plotFile01;

	}

	private void fillCruises() {

		int iFile;
		int iID;
		int iRecord;
		int iCruise;
		int x, y;
		double iSlope;
		int iAspect;
		int iElevation;
		String iDate;
		ITable dbFile, dbFile2;
		File[] files = ((DatabaseFilesSelectionPanel)m_Panels[0]).getFiles();
		HashMap plots = new HashMap();

		File file = files[0];
		initDBFFiles(file);
		getIndexes();

		//for(iFile = 0; iFile < files.length; iFile++){
		dbFile = (ITable) Meigas.getInputFactory().openDataObjectFromFile(plotFile00);
		dbFile2 = (ITable) Meigas.getInputFactory().openDataObjectFromFile(plotFile01);
		IRecordsetIterator iter = dbFile.iterator();
		IRecordsetIterator iter2 = dbFile2.iterator();
		while (iter.hasNext() && iter2.hasNext()){
			try {
				IRecord record = iter.next();
				IRecord record2 = iter2.next();
				iID = Integer.parseInt(record.getValue(PLOT_ID).toString().trim());
				String coordx = (record2.getValue(PLOT_COORDX).toString().trim());
				String coordy = (record2.getValue(PLOT_COORDY).toString().trim());
				x = Integer.parseInt(coordx); // * 1000;
				y = Integer.parseInt(coordy); // * 1000;
				String slope = record.getValue(PLOT_SLOPE).toString().trim();
				iSlope = Double.parseDouble(record.getValue(PLOT_SLOPE).toString().trim());
				String aspect = record.getValue(PLOT_ASPECT).toString().trim();
				iAspect = Integer.parseInt(record.getValue(PLOT_ASPECT).toString().trim());
				//iElevation = Integer.parseInt(record.getValue(PLOT_ELEVATION).toString().trim()) * 100;
				iElevation = 0;
				String date = record.getValue(PLOT_DATE).toString().trim();
				iDate = record.getValue(PLOT_DATE).toString().trim();
				for (iCruise = 0; iCruise < m_Cruises.length; iCruise++){
					if (m_Limits[iCruise] == null){

						m_Cruises[iCruise].addElement(getPlot(iID, x, y, iElevation,
								iSlope, iAspect, iDate,
								plots));
						break;
					}
					else{
						if (m_Limits[iCruise].contains(new GeometryFactory().createPoint(new Coordinate(x, y)))){
							m_Cruises[iCruise].addElement(getPlot(iID, x, y, iElevation,
									iSlope, iAspect, iDate,
									plots));
							break;
						}
					}
				}
			} catch (NumberFormatException e) {
				//TODO
			} catch (IteratorException e) {
				//TODO
			}
			iter.close();
		}
		dbFile.close();
		fillPlots(plots);
	}

	private DasocraticElement getPlot(int iID, int x, int y,
			int iElevation, double iSlope, int iAspect,
			String iDate, HashMap map) {

		int iDay, iMonth, iYear;
		double slope [] = { 1.5, 7.5, 16, 27.5, 35};
		Double minDiameter[] = { new Double(7.5), new Double(12.5),
				new Double(22.5), new Double(42.5) };
		Double radius[] = { new Double(5), new Double(10),
				new Double(15), new Double(25) };

		ConcentricPlot plot = new ConcentricPlot();

		plot.setCoords(new Point2D.Double(x, y));
		plot.setRadius(radius);
		plot.setMinAcceptableDiameters(minDiameter);
		plot.getElevation().setValue(iElevation * 100);
		plot.getAspect().setValue(iAspect);
		//TODO
//		plot.getSlope().setValue(slope[Math.min(iSlope - 1, 4)]);
		//TODO Date
//		iYear = (int) Math.floor(iDate / 10000);
//		iMonth = iDate - iYear * 10000;
//		iMonth = (int) Math.floor(iMonth / 100);
//		iDay = iDate - iYear * 10000 - iMonth * 100;
//		GregorianCalendar cal = new GregorianCalendar(iYear + 1900, iMonth - 1, iDay);
//		plot.setDate(cal.getTime());
		plot.setName("Estadillo " + Integer.toString(iID));
		map.put(new Integer(iID), plot);
		m_Plots.add(plot);

		return plot;

	}

	private boolean checkIsIFN3PlotFile(ITable table) {

		boolean isPlotTable = true;

		if (table.getFieldName(1) == "Estadillo") isPlotTable = false;
		if (table.getFieldName(4) == "Tipo") isPlotTable = false;
		if (table.getFieldName(5) == "Vuelo1") isPlotTable = false;

		return isPlotTable;
	}

	private boolean checkIsIFN3TreeFile(ITable table) {

		boolean isTreeTable = true;

		if (table.getFieldName(0) == "Estadillo") isTreeTable = false;
		if (table.getFieldName(3) == "nArbol") isTreeTable = false;
		if (table.getFieldName(4) == "OrdenIf3") isTreeTable = false;

		return isTreeTable;

	}

	protected void setPanels() {

		m_Panels = new BaseWizardPanel[2];
		m_Panels[0] = new DatabaseFilesSelectionPanel(this);
		m_Panels[1] = new ExtentDefinitionPanel(this);

	}

}
