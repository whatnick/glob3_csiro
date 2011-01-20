package es.unex.meigas.extFillData;

import java.awt.Cursor;
import java.util.ArrayList;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Tree;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;
import es.unex.sextante.libMath.regression.LeastSquaresFit;

public class FillDataExtensionPanel
         extends
            MainWizardWindow {


   public FillDataExtensionPanel(final MeigasPanel panel) {

      super(panel);

      setName("Rellenado de datos");

   }


   @Override
   protected void finish() {


      int iTree, iVariable;
      int iTreesCount;
      double dX, dY;
      XYVariables variable;
      final LeastSquaresFit lsf = new LeastSquaresFit();
      final Tree[] trees = m_MeigasPanel.getActiveElement().getTrees(null);
      final ArrayList variables = ((FillDataPanel) m_Panels[0]).getVariables();

      for (iVariable = 0; iVariable < variables.size(); iVariable++) {
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         variable = (XYVariables) variables.get(iVariable);
         iTreesCount = 0;
         for (iTree = 0; iTree < trees.length; iTree++) {
            dY = getValue(variable.y, trees[iTree]);
            if (dY != DasocraticElement.NO_DATA) {
               dX = getValue(variable.x, trees[iTree]);
               if (dX != DasocraticElement.NO_DATA) {
                  lsf.addValue(dX, dY);
                  iTreesCount++;
               }
            }
         }
         if (iTreesCount < 2) {
            continue;
         }

         lsf.calculate(1);

         this.setCursor(Cursor.getDefaultCursor());

         final FittingDataPanel dialog = new FittingDataPanel(lsf, trees, variable);
         dialog.setVisible(true);

      }

   }


   private double getValue(final int iParameter,
                           final Tree tree) {

      String sParameter;

      switch (iParameter) {
         case FillDataPanel.DBH:
            sParameter = Tree.DBH;
            break;
         case FillDataPanel.HEIGHT:
            sParameter = Tree.HEIGHT;
            break;
         case FillDataPanel.LOG_HEIGHT:
            sParameter = Tree.LOG_HEIGHT;
            break;
         case FillDataPanel.VOLUME_WITH_BARK:
            sParameter = Tree.VOLUME;
            break;
         case FillDataPanel.VOLUME_WITHOUT_BARK:
            sParameter = Tree.NO_BARK_VOLUME;
            break;
         case FillDataPanel.BARK:
            sParameter = Tree.BARK;
            break;
         case FillDataPanel.CROWN_DIAMETER:
            sParameter = Tree.CROWN_DIAMETER;
            break;
         default:
            return DasocraticElement.NO_DATA;
      }


      return ((Double) tree.getParameterValue(sParameter)).doubleValue();

   }


   @Override
   protected void setPanels() {

      m_Panels = new BaseWizardPanel[1];
      m_Panels[0] = new FillDataPanel(this);

   }
}
