package es.unex.meigas.extRandomUtils;

import java.util.Date;
import java.util.Random;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.AdministrativeUnit;
import es.unex.meigas.core.Cruise;
import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.FixedRadiusPlot;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Stand;
import es.unex.meigas.core.Tree;
import es.unex.meigas.core.parameters.MeigasNumericalValue;

public class RandomUtils {

   /**
    * Generate new DasocraticElements on the project. First element on the 'elementsForLevel' array has the number of
    * AdministrationUnits, the second the number of Stands, and so on.
    * 
    * elementsForLevel[0] are number of levels of ADMINISTRATIVE_UNITs elementsForLevel[1] are number of STANDs
    * elementsForLevel[2] are number of CRUISEs elementsForLevel[3] are number of FIXED_RADIUS_PLOTs elementsForLevel[4] are
    * number of CONCENTRIC_PLOTs elementsForLevel[5] are number of TREEs
    * 
    * @param model
    * @param elementsForLevel
    *                Array of integers with the numbers of elements on each level.
    * @param randomFill
    */
   public static void createNewDasocraticElements(final DefaultTreeModel model,
                                                  final int[] elementsForLevel,
                                                  final double dbh_mean,
                                                  final double dbh_stdDev,
                                                  final double height_mean,
                                                  final double height_stdDev,
                                                  final double radius,
                                                  final boolean randomFill) {

      createNewDasocraticElements(model, elementsForLevel, elementsForLevel, dbh_mean, dbh_stdDev, height_mean, height_stdDev,
               radius, randomFill);

   }


   private static int getRandomInteger(final int min,
                                       final int max) {

      final int value = min;
      final Random r = new Random();
      return r.nextInt(max - min + 1) + min;
   }


   public static void createNewDasocraticElements(final DefaultTreeModel model,
                                                  final int[] minElementsForLevel,
                                                  final int[] maxElementsForLevel,
                                                  final double dbh_mean,
                                                  final double dbh_stdDev,
                                                  final double height_mean,
                                                  final double height_stdDev,
                                                  final double radius,
                                                  final boolean randomFill) {

      final int plotcount = 0;
      int treecount = 0;

      final int levels = minElementsForLevel.length;
      if (minElementsForLevel.length != maxElementsForLevel.length) {
         return;
      }

      int numElements = 0;
      DefaultMutableTreeNode parentTree = (DefaultMutableTreeNode) model.getRoot();
      DasocraticElement parentElement = (DasocraticElement) parentTree.getUserObject();

      final Stack treeLevelNodes = new Stack();
      final Stack auxStackNodes = new Stack();

      if (levels > 0) {
         // AdministrationUnit
         numElements = getRandomInteger(minElementsForLevel[0], maxElementsForLevel[0]);
         if (numElements == 0) {
            // At least one unit must be created
            numElements = 1;
         }

         for (int i = 0; i < numElements; i++) {
            final DasocraticElement element = new AdministrativeUnit();
            element.setName("unidad_" + i);
            element.setNotes("Notes of " + element.getName());
            parentElement.addElement(element);
            parentElement = element;
            final DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
            model.insertNodeInto(elementNode, parentTree, parentTree.getChildCount());
            parentTree = elementNode;
         }
      }

      if (levels > 1) {
         // Stand
         // Only add Stands on the lower AdministrativeUnit
         numElements = getRandomInteger(minElementsForLevel[1], maxElementsForLevel[1]);
         for (int j = 0; j < numElements; j++) {
            final DasocraticElement element = new Stand();
            element.setName("canton_" + j);
            element.setNotes("Notes of " + element.getName());
            //				element.setEasternLimit(sEasternLimit);
            //				element.setNorthernLimit(sNorthernLimit);
            //				element.setSouthernLimit(sSouthernLimit);
            //				element.setWesternLimit(sWesternLimit);
            //				element.setSiteIndex(iSiteIndex);
            //				element.setPolygon(poly);
            parentElement.addElement(element);
            final DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
            model.insertNodeInto(elementNode, parentTree, parentTree.getChildCount());
            treeLevelNodes.push(elementNode);
         }
      }

      if (levels > 2) {
         // Cruise
         for (; 0 < treeLevelNodes.size();) {
            parentTree = (DefaultMutableTreeNode) treeLevelNodes.pop();
            final DasocraticElement parent = (DasocraticElement) parentTree.getUserObject();
            numElements = getRandomInteger(minElementsForLevel[2], maxElementsForLevel[2]);
            for (int j = 0; j < numElements; j++) {
               final Cruise element = new Cruise();
               element.setName("muestreo_" + j);
               element.setNotes("Notes of " + element.getName());
               element.setParameterValue(Cruise.DATE, new Date());
               parent.addElement(element);
               final DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
               model.insertNodeInto(elementNode, parentTree, parentTree.getChildCount());
               auxStackNodes.push(elementNode);
            }
         }
      }
      treeLevelNodes.addAll(auxStackNodes);
      auxStackNodes.clear();

      if (levels > 4) {
         for (; 0 < treeLevelNodes.size();) {
            parentTree = (DefaultMutableTreeNode) treeLevelNodes.pop();
            final DasocraticElement parent = (DasocraticElement) parentTree.getUserObject();
            numElements = getRandomInteger(minElementsForLevel[3], maxElementsForLevel[3]);
            for (int j = 0; j < numElements; j++) {
               final FixedRadiusPlot frp = new FixedRadiusPlot();
               frp.setName("plot_" + j);
               frp.setParameterValue(FixedRadiusPlot.DATE, new Date());
               final double auxX = getRandomInteger(0, 1000);
               final double auxY = getRandomInteger(0, 1000);
               final Coordinate coord = new Coordinate(auxX, auxY);
               frp.setParameterValue(FixedRadiusPlot.COORD, coord);
               frp.setParameterValue(FixedRadiusPlot.RADIUS, new Double(radius));
               parent.addElement(frp);
               final DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(frp);
               model.insertNodeInto(elementNode, parentTree, parentTree.getChildCount());
               auxStackNodes.push(elementNode);
            }
         }

         treeLevelNodes.addAll(auxStackNodes);
         auxStackNodes.clear();

         if (levels > 5) {
            // Tree
            for (; 0 < treeLevelNodes.size();) {
               treecount = 0;
               parentTree = (DefaultMutableTreeNode) treeLevelNodes.pop();
               final DasocraticElement parent = (DasocraticElement) parentTree.getUserObject();
               numElements = getRandomInteger(minElementsForLevel[5], maxElementsForLevel[5]);
               final Plot plot = (Plot) parent;
               for (int j = 0; j < numElements; j++) {
                  final Tree element = new Tree();
                  element.setName(plot.getName() + "_arbol_" + treecount);
                  element.setNotes("Notes of " + element.getName());
                  final MeigasNumericalValue dbh = (MeigasNumericalValue) element.getParameter(Tree.DBH);
                  double dValue = getRandomNormal(dbh_mean, dbh_stdDev, dbh.getMin(), dbh.getMax());
                  element.setParameterValue(Tree.DBH, new Double(dValue));
                  final MeigasNumericalValue height = (MeigasNumericalValue) element.getParameter(Tree.HEIGHT);
                  dValue = getRandomNormal(height_mean, height_stdDev, height.getMin(), height.getMax());
                  element.setParameterValue(Tree.HEIGHT, dValue);
                  double dRadius = 0;
                  final FixedRadiusPlot frp = (FixedRadiusPlot) plot;
                  dRadius = ((Number) frp.getParameterValue(FixedRadiusPlot.RADIUS)).intValue();
                  dRadius = Math.random() * dRadius;
                  final double dAngle = Math.random() * 2. * Math.PI;
                  final double auxX = dRadius * Math.cos(dAngle);
                  final double auxY = dRadius * Math.sin(dAngle);
                  final Coordinate plotCoords = (Coordinate) plot.getParameterValue(FixedRadiusPlot.COORD);
                  final Coordinate coords = new Coordinate(plotCoords.x + auxX, plotCoords.y + auxY);
                  element.setParameterValue(Tree.COORD, coords);
                  parent.addElement(element);
                  final DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
                  model.insertNodeInto(elementNode, parentTree, parentTree.getChildCount());
                  treecount++;
               }
            }
         } // End TREEs
         model.reload();
      }
   }


   static double getRandomNormal(final double mean,
                                 final double stdDev) {

      double x1, x2, w, y1;
      do {
         x1 = 2.0 * Math.random() - 1.0;
         x2 = 2.0 * Math.random() - 1.0;

         w = x1 * x1 + x2 * x2;
      }
      while (w >= 1.0);

      w = Math.sqrt((-2.0 * Math.log(w)) / w);
      y1 = x1 * w;

      return (mean + stdDev * y1);

   }


   static double getRandomNormal(final double mean,
                                 final double stdDev,
                                 final double dMin,
                                 final double dMax) {

      double value = getRandomNormal(mean, stdDev);
      if ((value < dMin) || (value > dMax)) {
         value = getRandomNormal(mean, stdDev, dMin, dMax);
      }
      return value;

   }


}
