package es.unex.meigas.extIFN2;

import java.awt.Cursor;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import es.unex.meigas.core.NamedGeometry;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;
import gov.nasa.worldwind.formats.shapefile.DBaseField;
import gov.nasa.worldwind.formats.shapefile.DBaseFile;
import gov.nasa.worldwind.formats.shapefile.DBaseRecord;

public class IFN2Panel
         extends
            MainWizardWindow {

   private Geometry  m_Limits[];
   private Cruise    m_Cruises[];
   private ArrayList m_Plots;


   public IFN2Panel(final MeigasPanel panel) {

      super(panel);

      setName("Extracción de datos del IFN2");

   }


   @Override
   protected void finish() {

      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      m_Plots = new ArrayList();

      try {
         createCruises();
         fillCruises();
         filter();
      }
      catch (final Exception e) {
         //TODO:Show error
      }
      finally {
         m_MeigasPanel.fillTree();
         m_MeigasPanel.updateSelection(null);
         m_MeigasPanel.setEnabledButtons();

         this.setCursor(Cursor.getDefaultCursor());
      }

   }


   private void createCruises() {

      Stand stand;
      int i;
      final DasocraticElement activeElement = m_MeigasPanel.getActiveElement();

      final int iLimits = ((ExtentDefinitionPanel) m_Panels[1]).getSelectedType();
      if (iLimits == ExtentDefinitionPanel.USE_GEOMETRY) {
         ArrayList stands;
         if ((activeElement instanceof AdministrativeUnit) || (activeElement instanceof Stand)) {
            stands = activeElement.getStandsWithLimits();
         }
         else {
            stands = activeElement.getParentOfType(Stand.class).getStandsWithLimits();
         }
         m_Cruises = new Cruise[stands.size()];
         m_Limits = new Polygon[stands.size()];
         for (i = 0; i < stands.size(); i++) {
            m_Cruises[i] = new Cruise();
            stand = (Stand) stands.get(i);
            stand.addElement(m_Cruises[i]);
            m_Limits[i] = ((NamedGeometry) stand.getParameterValue(Stand.POLYGON)).geom;
         }
      }
      else {
         if (activeElement instanceof AdministrativeUnit) {
            stand = new Stand();
            final DasocraticElement parent = activeElement.addElement(stand);
            if (parent == null) {
               final AdministrativeUnit unit = new AdministrativeUnit();
               activeElement.addElement(unit);
               unit.addElement(stand);
            }
         }
         else if (activeElement instanceof Stand) {
            stand = (Stand) activeElement;
         }
         else {
            stand = (Stand) activeElement.getParentOfType(Stand.class);
         }
         m_Cruises = new Cruise[1];
         m_Cruises[0] = new Cruise();
         stand.addElement(m_Cruises[0]);
         m_Limits = new Polygon[1];
         if (iLimits == ExtentDefinitionPanel.USE_RECT) {
            final GeometryFactory gf = new GeometryFactory();
            final Rectangle2D rect = ((ExtentDefinitionPanel) m_Panels[1]).getRect();
            final Coordinate[] coords = new Coordinate[5];
            coords[0] = new Coordinate(rect.getMinX(), rect.getMinY());
            coords[1] = new Coordinate(rect.getMinX(), rect.getMaxY());
            coords[2] = new Coordinate(rect.getMaxX(), rect.getMaxY());
            coords[3] = new Coordinate(rect.getMaxX(), rect.getMinY());
            coords[4] = new Coordinate(rect.getMinX(), rect.getMinY());
            m_Limits[0] = gf.createPolygon(gf.createLinearRing(coords), null);
         }
         else {
            m_Limits[0] = null;
         }
      }

   }


   private void filter() {

      int i;

      for (i = 0; i < m_Plots.size(); i++) {
         final Plot plot = (Plot) m_Plots.get(i);
         if (plot.getElementsCount() == 0) {
            plot.getParent().removeElement(plot);
         }
      }

      for (i = 0; i < m_Cruises.length; i++) {
         if (m_Cruises[i].getElementsCount() == 0) {
            m_Cruises[i].getParent().removeElement(m_Cruises[i]);
         }
      }

   }


   private void fillPlots(final HashMap plots,
                          final File file) throws FileNotFoundException {

      final int ID = 1;
      final int COMPLETE_TREE_ID = 3;
      final int TREE_ID = 4;
      final int ANGLE = 5;
      final int DISTANCE = 6;
      final int SPECIE = 7;
      final int DBH1 = 8;
      final int DBH2 = 9;
      final int SHAPE_FACTOR = 11;
      final int TREE_HEIGHT = 12;

      final int COMPLETE_ID = 1;
      final int COMPLETE_ID2 = 3;
      final int CROWN_DIAMETER1 = 4;
      final int CROWN_DIAMETER2 = 5;
      final int BARK1 = 6;
      final int BARK2 = 7;
      final int GROWTH1 = 8;
      final int GROWTH2 = 9;
      final int LOG_HEIGHT = 12;

      int iID;
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
      final HashMap completeTrees = new HashMap();
      String sFilename = file.getAbsolutePath();
      final String sCode = sFilename.substring(sFilename.length() - 6, sFilename.length() - 4);
      sFilename = file.getParent() + File.separator + "PIESMA" + sCode + ".dbf";

      BufferedInputStream dbfStream = new BufferedInputStream(new FileInputStream(new File(sFilename)));
      DBaseFile dbfFile = new DBaseFile(dbfStream);
      while (dbfFile.hasNext()) {

         final DBaseRecord record = dbfFile.nextRecord();
         iID = Integer.parseInt(getRecordValueAsString(dbfFile, record, ID));
         plot = (Plot) plots.get(new Integer(iID));
         if (plot != null) {
            sTreeID = getRecordValueAsString(dbfFile, record, TREE_ID);
            dDBH1 = Integer.parseInt(getRecordValueAsString(dbfFile, record, DBH1));
            dDBH2 = Integer.parseInt(getRecordValueAsString(dbfFile, record, DBH2));
            dDBH = (dDBH1 + dDBH2) / 20;
            dHeight = Double.parseDouble(getRecordValueAsString(dbfFile, record, TREE_HEIGHT));
            iShapeFactor = Integer.parseInt(getRecordValueAsString(dbfFile, record, SHAPE_FACTOR));
            sSpecie = getRecordValueAsString(dbfFile, record, SPECIE);
            dDistance = Double.parseDouble(getRecordValueAsString(dbfFile, record, DISTANCE));
            dAngle = Double.parseDouble(getRecordValueAsString(dbfFile, record, ANGLE));
            final Coordinate coords = (Coordinate) plot.getParameterValue(Plot.COORD);
            dX = coords.x + dDistance * Math.sin(dAngle * Math.PI / 180.);
            dY = coords.y + dDistance * Math.cos(dAngle * Math.PI / 180.);
            tree = new Tree();
            tree.setName(Integer.toString(iID) + "-" + sTreeID);
            tree.setParameterValue(Tree.DBH, new Double(dDBH));
            tree.setParameterValue(Tree.SHAPE_FACTOR, Integer.toString(iShapeFactor));
            tree.setParameterValue(Tree.HEIGHT, new Double(dHeight));
            tree.setParameterValue(Tree.COORD, new Coordinate(dX, dY));
            tree.setParameterValue(Tree.SPECIE, Meigas.getSpeciesCatalog().getSpecieFromName(sSpecie));
            plot.addElement(tree);
            s = getRecordValueAsString(dbfFile, record, COMPLETE_TREE_ID);
            if (s != "") {
               sCompleteTreeID = Integer.toString(iID) + "_" + s;
               completeTrees.put(sCompleteTreeID, tree);
            }
         }

      }


      dbfFile.close();

      sFilename = file.getParent() + File.separator + "TIPOSX" + sCode + ".dbf";
      dbfStream = new BufferedInputStream(new FileInputStream(new File(sFilename)));
      dbfFile = new DBaseFile(dbfStream);
      while (dbfFile.hasNext()) {

         final DBaseRecord record = dbfFile.nextRecord();
         iID = Integer.parseInt(getRecordValueAsString(dbfFile, record, COMPLETE_ID));

         s = getRecordValueAsString(dbfFile, record, COMPLETE_ID2);
         sCompleteTreeID = Integer.toString(iID) + "_" + s;
         tree = (Tree) completeTrees.get(sCompleteTreeID);
         if (tree != null) {
            dCrownDiameter1 = Double.parseDouble(getRecordValueAsString(dbfFile, record, CROWN_DIAMETER1));
            dCrownDiameter2 = Double.parseDouble(getRecordValueAsString(dbfFile, record, CROWN_DIAMETER2));
            dCrownDiameter = (dCrownDiameter1 + dCrownDiameter2) / 2.;
            tree.setParameterValue(Tree.CROWN_DIAMETER, new Double(dCrownDiameter));
            dBark1 = Double.parseDouble(getRecordValueAsString(dbfFile, record, BARK1));
            dBark2 = Double.parseDouble(getRecordValueAsString(dbfFile, record, BARK2));
            dBark = (dBark1 + dBark2) / 2. / 10.;
            tree.setParameterValue(Tree.BARK, new Double(dBark));
            s = getRecordValueAsString(dbfFile, record, GROWTH1);
            if (s != "") {
               dGrowth1 = Double.parseDouble(s);
               dGrowth2 = Double.parseDouble(getRecordValueAsString(dbfFile, record, GROWTH2));
               dGrowth = (dGrowth1 + dGrowth2) / 2. / 100.;
               tree.setParameterValue(Tree.RADIAL_GROWTH, new Double(dGrowth));
            }
            dHeight = Double.parseDouble(getRecordValueAsString(dbfFile, record, LOG_HEIGHT));
            tree.setParameterValue(Tree.LOG_HEIGHT, new Double(dHeight));
         }


      }
      dbfFile.close();

   }


   private String getRecordValueAsString(final DBaseFile dbfFile,
                                         final DBaseRecord record,
                                         final int iField) {

      final DBaseField[] fields = dbfFile.getFields();
      return record.getStringValue(fields[iField].getName()).trim();

   }


   private void fillCruises() throws FileNotFoundException {

      final int ID = 1;
      final int COORDX = 15;
      final int COORDY = 16;
      final int DATE = 3;
      final int ASPECT = 86;
      final int ELEVATION = 23;
      final int SLOPE = 24;

      int iFile;
      int iID;
      int iCruise;
      int x, y;
      int iSlope;
      int iAspect;
      int iElevation;
      int iDate;
      final File[] files = ((DatabaseFilesSelectionPanel) m_Panels[0]).getFiles();

      final HashMap plots = new HashMap();

      for (iFile = 0; iFile < files.length; iFile++) {
         final BufferedInputStream dbfStream = new BufferedInputStream(new FileInputStream(files[iFile]));
         final DBaseFile dbfFile = new DBaseFile(dbfStream);
         while (dbfFile.hasNext()) {

            final DBaseRecord record = dbfFile.nextRecord();
            iID = Integer.parseInt(getRecordValueAsString(dbfFile, record, ID));
            x = Integer.parseInt(getRecordValueAsString(dbfFile, record, COORDX)) * 1000;
            y = Integer.parseInt(getRecordValueAsString(dbfFile, record, COORDY)) * 1000;
            iSlope = Integer.parseInt(getRecordValueAsString(dbfFile, record, SLOPE));
            iAspect = Integer.parseInt(getRecordValueAsString(dbfFile, record, ASPECT));
            iElevation = Integer.parseInt(getRecordValueAsString(dbfFile, record, ELEVATION));
            iDate = Integer.parseInt(getRecordValueAsString(dbfFile, record, DATE));
            for (iCruise = 0; iCruise < m_Cruises.length; iCruise++) {
               if (m_Limits[iCruise] == null) {
                  m_Cruises[iCruise].addElement(getPlot(iID, x, y, iElevation, iSlope, iAspect, iDate, plots));
                  break;
               }
               else {
                  if (m_Limits[iCruise].contains(new GeometryFactory().createPoint(new Coordinate(x, y)))) {
                     m_Cruises[iCruise].addElement(getPlot(iID, x, y, iElevation, iSlope, iAspect, iDate, plots));
                     break;
                  }
               }
            }


         }
         dbfFile.close();

         fillPlots(plots, files[iFile]);
      }

   }


   private DasocraticElement getPlot(final int iID,
                                     final int x,
                                     final int y,
                                     final int iElevation,
                                     final int iSlope,
                                     final int iAspect,
                                     final int iDate,
                                     final HashMap map) {

      int iDay, iMonth, iYear;
      final double slope[] = { 1.5, 7.5, 16, 27.5, 35 };
      final Double minDiameter[] = { new Double(7.5), new Double(12.5), new Double(22.5), new Double(42.5) };
      final Double radius[] = { new Double(5), new Double(10), new Double(15), new Double(25) };

      final ConcentricPlot plot = new ConcentricPlot();

      plot.setParameterValue(Plot.COORD, new Coordinate(x, y));
      for (int i = 0; i < radius.length; i++) {
         plot.setParameterValue(ConcentricPlot.RADIUS[i], radius[i]);
         plot.setParameterValue(ConcentricPlot.MIN_ACCEPTABLE_DIAMETER[i], minDiameter[i]);
      }
      plot.setParameterValue(Plot.ELEVATION, new Double(iElevation * 100));
      plot.setParameterValue(Plot.ASPECT, new Double(iAspect * 100));
      plot.setParameterValue(Plot.SLOPE, new Double(slope[Math.min(iSlope - 1, 4)]));
      iYear = (int) Math.floor(iDate / 10000);
      iMonth = iDate - iYear * 10000;
      iMonth = (int) Math.floor(iMonth / 100);
      iDay = iDate - iYear * 10000 - iMonth * 100;
      final GregorianCalendar cal = new GregorianCalendar(iYear + 1900, iMonth - 1, iDay);
      plot.setParameterValue(Plot.DATE, cal.getTime());
      plot.setName("Estadillo " + Integer.toString(iID));
      map.put(new Integer(iID), plot);
      m_Plots.add(plot);

      return plot;

   }


   /*private boolean checkIsIFN2PlotFile(ITable table) {

   	//TODO:

   	return true;
   }

   private boolean checkIsIFN2TreeFile(ITable table) {

   	//TODO:

   	return true;
   }

   private boolean checkIsIFN2CompleteTreeFile(ITable table) {

   //		TODO:

   	return true;

   }*/


   @Override
   protected void setPanels() {

      m_Panels = new BaseWizardPanel[2];
      m_Panels[0] = new DatabaseFilesSelectionPanel(this);
      m_Panels[1] = new ExtentDefinitionPanel(this);

   }

}
