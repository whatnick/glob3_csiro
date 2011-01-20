package es.unex.meigas.extCalculateVolume;

import java.util.ArrayList;
import java.util.HashMap;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.equations.Equation;
import es.unex.meigas.extBase.BaseWizardPanel;
import es.unex.meigas.extBase.MainWizardWindow;
import es.unex.meigas.gui.MeigasPanel;

public class CalculateVolumeDialog
         extends
            MainWizardWindow {

   public CalculateVolumeDialog(final MeigasPanel panel) {

      super(panel);

      setName("Cálculo de volúmenes");

   }


   @Override
   protected void setPanels() {

      m_Panels = new BaseWizardPanel[4];
      m_Panels[0] = new SingleOrMultipleEquationsPanel(this);
      m_Panels[1] = new EquationSelectionForSpeciesPanel(this, SingleOrMultipleEquationsPanel.SINGLE);
      m_Panels[2] = new EquationSelectionForSpeciesPanel(this, SingleOrMultipleEquationsPanel.MULTIPLE);
      m_Panels[3] = new OptionsPanel(this);

   }


   @Override
   protected void previousPanel() {

      if ((m_iCurrentPanel == 1) || (m_iCurrentPanel == 2)) {
         m_iCurrentPanel = 0;
      }
      else {
         final int iSelection = ((SingleOrMultipleEquationsPanel) m_Panels[0]).getSelection();
         if (iSelection == SingleOrMultipleEquationsPanel.SINGLE) {
            m_iCurrentPanel = 1;
         }
         else {
            m_iCurrentPanel = 2;
         }
      }
      jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
      updateButtons();
   }


   @Override
   protected void nextPanel() {

      if (m_iCurrentPanel == m_Panels.length - 1) {
         finish();
         cancel();
      }
      else if (m_iCurrentPanel == 0) {
         final int iSelection = ((SingleOrMultipleEquationsPanel) m_Panels[0]).getSelection();
         if (iSelection == SingleOrMultipleEquationsPanel.SINGLE) {
            m_iCurrentPanel = 1;
         }
         else {
            m_iCurrentPanel = 2;
         }
         jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
         updateButtons();
      }
      else {
         m_iCurrentPanel = 3;
         jPanelMainLayout.show(jPanelMain, Integer.toString(m_iCurrentPanel));
         updateButtons();
      }
   }


   @Override
   public void updateButtons() {

      if (m_iCurrentPanel < 1) {
         jButtonPrev.setEnabled(false);
      }
      else {
         jButtonPrev.setEnabled(true);
      }
      if (m_iCurrentPanel == m_Panels.length - 1) {
         jButtonNext.setText("Finalizar");
      }
      else {
         jButtonNext.setText("Siguiente >");
      }

      if (m_Panels[m_iCurrentPanel].hasEnoughInformation()) {
         jButtonNext.setEnabled(true);
      }
      else {
         jButtonNext.setEnabled(false);
      }


   }


   @Override
   protected void finish() {

      int i;
      int iTree, iStand;
      double dDBH;
      double dHeight;
      double dLogHeight;
      double dBark;
      double dVolume;
      boolean bCheckShapeFactor;
      boolean bCheckSiteIndex;
      Stand stand;
      final ArrayList stands = new ArrayList();//m_MeigasPanel.getActiveElement().getStands();
      //if (stands.size() == 0) {
      stands.add(m_MeigasPanel.getActiveElement().getParentOfType(Stand.class));
      //}

      final HashMap map = new HashMap();
      Equation[] equations;
      final int iConditions = ((OptionsPanel) m_Panels[3]).getSelection();
      final int iSelection = ((SingleOrMultipleEquationsPanel) m_Panels[0]).getSelection();

      if (iSelection == SingleOrMultipleEquationsPanel.SINGLE) {
         equations = ((EquationSelectionForSpeciesPanel) m_Panels[1]).getEquations();
      }
      else {
         equations = ((EquationSelectionForSpeciesPanel) m_Panels[2]).getEquations();
      }

      bCheckShapeFactor = ((iConditions & OptionsPanel.SHAPE_FACTOR) != 0);
      bCheckSiteIndex = ((iConditions & OptionsPanel.SITE_INDEX) != 0);

      Equation eq = equations[0];

      for (i = 0; i < equations.length; i++) {
         equations[i].parseEquation();
         map.put(equations[i].getSpecie(), equations[i]);
      }
      for (iStand = 0; iStand < stands.size(); iStand++) {
         stand = (Stand) stands.get(iStand);
         final Tree[] trees = stand.getTrees(null);
         for (iTree = 0; iTree < trees.length; iTree++) {
            dDBH = ((Double) trees[i].getParameter(Tree.DBH).getValue()).doubleValue();
            dHeight = ((Double) trees[i].getParameter(Tree.HEIGHT).getValue()).doubleValue();;
            dLogHeight = ((Double) trees[i].getParameter(Tree.LOG_HEIGHT).getValue()).doubleValue();
            dBark = ((Double) trees[i].getParameter(Tree.BARK).getValue()).doubleValue();;
            if (dDBH != DasocraticElement.NO_DATA) {
               dDBH /= 100.;
            }
            if (dBark != DasocraticElement.NO_DATA) {
               dBark /= 100.;
            }
            if (equations.length > 1) {
               eq = (Equation) map.get(trees[i].getParameterValue(Tree.SPECIE));
            }
            if (bCheckShapeFactor) {
               if (!checkShapeFactor(trees[i], eq)) {
                  continue;
               }
            }
            if (bCheckSiteIndex) {
               if (!checkSiteIndex(trees[i], eq, stand)) {
                  continue;
               }
            }
            if (eq != null) {
               dVolume = eq.Calculate(0, dHeight, dLogHeight, dDBH, dBark);
               if (dVolume != DasocraticElement.NO_DATA) {
                  if (eq.getParameter() == Equation.VOLUME) {
                     final double dPreviousValue = ((Double) trees[i].getParameter(Tree.VOLUME).getValue()).doubleValue();
                     if (dPreviousValue == DasocraticElement.NO_DATA) {
                        trees[i].getParameter(Tree.VOLUME).setValue(new Double(dVolume));
                        ((MeigasNumericalValue) trees[i].getParameter(Tree.VOLUME)).setIsEstimated(true);
                     }
                  }
                  else {
                     final double dPreviousValue = ((Double) trees[i].getParameter(Tree.NO_BARK_VOLUME).getValue()).doubleValue();
                     if (dPreviousValue == DasocraticElement.NO_DATA) {
                        trees[i].getParameter(Tree.NO_BARK_VOLUME).setValue(new Double(dVolume));
                        ((MeigasNumericalValue) trees[i].getParameter(Tree.NO_BARK_VOLUME)).setIsEstimated(true);
                     }
                  }
               }
            }
         }
      }

   }


   private boolean checkSiteIndex(final Tree tree,
                                  final Equation eq,
                                  final Stand stand) {

      if (eq.getSiteIndex() == Equation.ALL_SITE_INDICES) {
         return true;
      }

      final int iSiteIndex = Integer.parseInt(stand.getParameterValue(Stand.SITE_INDEX).toString());
      if (eq.getSiteIndex() == iSiteIndex) {
         return true;
      }
      else {
         return false;
      }

   }


   private boolean checkShapeFactor(final Tree tree,
                                    final Equation eq) {

      final int iSF = Integer.parseInt(tree.getParameterValue(Tree.SHAPE_FACTOR).toString());
      return eq.getShapeFactor()[iSF];

   }

}
