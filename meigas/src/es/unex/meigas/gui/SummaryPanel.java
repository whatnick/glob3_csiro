package es.unex.meigas.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Tree;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasParameter;
import es.unex.meigas.core.parameters.ParametersSet;
import es.unex.sextante.docEngines.html.HTMLDoc;
import es.unex.sextante.libMath.simpleStats.SimpleStats;

public class SummaryPanel
         extends
            DasocraticInfoPanel {

   private JScrollPane jScrollPane;
   private JTextPane   jTextPane;


   public SummaryPanel(final DasocraticElement element,
                       final MeigasPanel meigasPanel) {

      super(element, meigasPanel);
      setName("Resumen");

   }


   protected void initializeContent() {}


   @Override
   protected boolean checkDataAndUpdate() {

      return true;

   }


   @Override
   protected void initGUI() {

      final BorderLayout thisLayout = new BorderLayout();
      this.setLayout(thisLayout);
      this.setPreferredSize(new java.awt.Dimension(511, 310));
      {
         jScrollPane = new JScrollPane();
         this.add(jScrollPane, BorderLayout.CENTER);
         jScrollPane.setBorder(BorderFactory.createTitledBorder("Resumen de valores totales"));
         {
            jTextPane = new JTextPane();
            jTextPane.setEditable(false);
            jTextPane.setContentType("text/html");
            final String sText = getHTML();
            jTextPane.setText(sText);
            jScrollPane.setViewportView(jTextPane);
            jTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
         }
      }

   }


   private String getHTML() {

      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      final ParametersSet params = Tree.getParametersDefinition();
      final String[] names = params.getParameterNames().toArray(new String[0]);
      final HashMap<String, SimpleStats> stats = m_Element.getTreeStats();


      final HTMLDoc html = new HTMLDoc();
      html.open("Resumen");
      html.addHeader("Resumen de datos para el elemento " + m_Element.getName(), 1);
      html.addHeader("Datos generales", 2);
      html.startUnorderedList();
      html.addListElement("Superficie (ha): " + getString(m_Element.getArea()));
      final ArrayList plots = new ArrayList();
      m_Element.getElementsOfClassRecursive(plots, Plot.class);
      html.addListElement("Número de parcelas: " + Integer.toString(plots.size()));
      html.closeUnorderedList();
      html.addHorizontalSeparator();

      for (final String sName : names) {
         final MeigasParameter param = params.getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            final SimpleStats parameterStats = stats.get(sName);
            if (parameterStats != null) {
               final String sDescription = param.getDescription();
               html.addHeader(sDescription, 2);
               html.startUnorderedList();
               html.addListElement("Media: " + parameterStats.getMean());
               html.addListElement("Máximo: " + parameterStats.getMax());
               html.addListElement("Máximo: " + parameterStats.getMin());
               html.closeUnorderedList();
               html.addHorizontalSeparator();
            }
         }
      }

      html.close();

      this.setCursor(Cursor.getDefaultCursor());

      return html.getHTMLCode();

   }


   private String getString(final double dValue) {

      final DecimalFormat df = new DecimalFormat("###.####");

      if (dValue == DasocraticElement.NO_DATA) {
         return "No disponible";
      }
      else {
         return df.format(dValue);
      }

   }


   @Override
   protected void updateContent() {

      final String sText = getHTML();
      jTextPane.setText(sText);

   }

}
