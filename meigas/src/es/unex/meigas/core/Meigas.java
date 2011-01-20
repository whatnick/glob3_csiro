package es.unex.meigas.core;

import java.awt.Frame;
import java.io.File;

import javax.swing.JFrame;

import es.unex.meigas.core.gis.GISConnection;
import es.unex.meigas.equations.Equations;
import es.unex.meigas.extBase.IMeigasExtension;
import es.unex.meigas.extCalculateVolume.CalculateVolumeExtension;
import es.unex.meigas.extDelete.DeleteExtension;
import es.unex.meigas.extEquationEditor.EquationEditorExtension;
import es.unex.meigas.extFillData.FillDataExtension;
import es.unex.meigas.extIFN2.IFN2Extension;
import es.unex.meigas.extLoadSaveProject.CloseExtension;
import es.unex.meigas.extLoadSaveProject.NewProjectExtension;
import es.unex.meigas.extRandomUtils.RandomStandExtension;
import es.unex.meigas.extShowErrors.ShowErrorsExtension;
import es.unex.meigas.extSpeciesEditor.SpeciesEditorExtension;


public class Meigas {

   private static IMeigasExtension  m_Extensions[] = { new NewProjectExtension(), new RandomStandExtension(),
            new DeleteExtension(), new ShowErrorsExtension(), new IFN2Extension(), new CalculateVolumeExtension(),
            new SpeciesEditorExtension(), new CloseExtension(), new FillDataExtension(), new EquationEditorExtension() };
   /*"es.unex.meigas.extLoadSaveProject.LoadProjectExtension",
   "es.unex.meigas.extLoadSaveProject.SaveProjectExtension",
   //"es.unex.meigas.extLoadSaveProject.CloseExtension",
   "es.unex.meigas.extCalculateVolume.CalculateVolumeExtension", "es.unex.meigas.extDelete.DeleteExtension",
   "es.unex.meigas.extEquationEditor.EquationEditorExtension", "es.unex.meigas.extFillData.FillDataExtension",
   "es.unex.meigas.extIFN2.IFN2Extension", "es.unex.meigas.extIFN3.IFN3Extension",
   "es.unex.meigas.extImportFromLayer.ImportFromLayerExtension", "es.unex.meigas.extExportToShp.ExportToShpExtension",
   "es.unex.meigas.extShowErrors.ShowErrorsExtension", "es.unex.meigas.extRandomUtils.RandomStandExtension",
   "es.unex.meigas.extImportPlotFromLaserScanner.ImportPlotFromLaserScannerExtension",
   "es.unex.meigas.extGIS.GisConfigurationExtension", "es.unex.meigas.extGIS.ZoomExtension",
   "es.unex.meigas.extSpeciesEditor.SpeciesEditorExtension", "es.unex.meigas.extOperations.PerformOperationExtension"*/

   //private static IMeigasExtension[] m_Extensions;
   private static GISConnection     m_GISConnection;
   private static Frame             m_MainFrame;
   private static SpeciesCatalog    m_SpeciesCatalog;
   private static Equations         m_Equations;
   private static String            m_sDataFolder;
   private static DasocraticProject m_DasocraticProject;


   public static IMeigasExtension[] getExtensions() {

      return m_Extensions;

   }


   public static Equations getEquations() {

      return m_Equations;

   }


   public static SpeciesCatalog getSpeciesCatalog() {

      return m_SpeciesCatalog;

   }


   public static String getDataFolder() {

      return m_sDataFolder;

   }


   public static void setDataFolder(final String sFolder) {

      m_sDataFolder = sFolder;

   }


   public static void initialize() {

      m_SpeciesCatalog = new SpeciesCatalog();
      m_GISConnection = new GISConnection();
      m_Equations = new Equations();
      String sFile = Meigas.getDataFolder() + File.separator + "data" + File.separator + "eq.csv";
      m_Equations.open(sFile);
      sFile = Meigas.getDataFolder() + File.separator + "data" + File.separator + "species.txt";
      m_SpeciesCatalog.open(sFile);

   }


   public static Frame getMainFrame() {

      return m_MainFrame;

   }


   public static void setMainFrame(final JFrame frame) {

      m_MainFrame = frame;

   }


   public static DasocraticProject getDasocraticProject() {

      return m_DasocraticProject;

   }


   public static DasocraticProject startNewDasocraticProject() {

      m_DasocraticProject = new DasocraticProject();
      return m_DasocraticProject;
   }


   public static GISConnection getGIS() {

      return m_GISConnection;

   }

}
