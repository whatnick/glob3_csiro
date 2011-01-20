package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.freixas.jcalendar.JCalendarCombo;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Meigas;
import es.unex.meigas.core.parameters.DerivedParameter;
import es.unex.meigas.core.parameters.MeigasCoordinateValue;
import es.unex.meigas.core.parameters.MeigasDateValue;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasParameter;
import es.unex.meigas.core.parameters.MeigasPolygonValue;
import es.unex.meigas.core.parameters.MeigasSelectionValue;
import es.unex.meigas.core.parameters.MeigasSpecieValue;
import es.unex.meigas.core.parameters.MeigasStringValue;
import es.unex.meigas.core.parameters.ParametersSet;

public class DasocraticElementParametersPanel
         extends
            DasocraticInfoPanel {

   private HashMap<Component, MeigasParameter> m_ComponentsMap;


   public DasocraticElementParametersPanel(final DasocraticElement element,
                                           final MeigasPanel panel) {

      super(element, panel);
      setName("Parámetros");

   }


   @Override
   protected boolean checkDataAndUpdate() {

      final Set<Component> components = m_ComponentsMap.keySet();
      for (final Iterator iterator = components.iterator(); iterator.hasNext();) {
         final Component component = (Component) iterator.next();
         final MeigasParameter param = m_ComponentsMap.get(component);
         if (param instanceof MeigasNumericalValue) {
            final boolean bReturn = ((MeigasValueTextField) component).checkDataAndUpdate(false);
            if (!bReturn) {
               return false;
            }
         }
         else if (param instanceof MeigasCoordinateValue) {
            final Coordinate coord = ((CoordinatesPanel) component).getCoordinates();
            if (coord == null) {
               return false;
            }
            ((MeigasCoordinateValue) param).setValue(coord);
         }
         else if (param instanceof MeigasPolygonValue) {
            //************
         }
         else if (param instanceof MeigasDateValue) {
            final Date date = ((JCalendarCombo) component).getDate();
            param.setValue(date);
         }
         else if (param instanceof MeigasSelectionValue) {
            final Object obj = ((JComboBox) component).getSelectedItem();
            boolean bReturn = param.setValue(obj);
            if (!bReturn) {
               return false;
            }
         }
         else if (param instanceof MeigasSpecieValue) {
            final Object obj = ((JComboBox) component).getSelectedItem();
            //final Specie specie = Meigas.getSpeciesCatalog().getSpecieFromName(obj.toString());
            param.setValue(obj);
         }
         else if (param instanceof MeigasStringValue) {
            final String s = ((JTextField) component).getText();
            param.setValue(s);
         }

      }

      return m_Element.hasValidData();
   }


   @Override
   protected void initGUI() {

      m_ComponentsMap = new HashMap<Component, MeigasParameter>();

      final ParametersSet params = m_Element.getParameters();
      final Set<String> names = params.getParameterNames();
      final TableLayout layout = new TableLayout(getLayoutMatrix(params.getParameterCount()));
      this.setLayout(layout);


      int i = 1;
      for (final Iterator iterator = names.iterator(); iterator.hasNext();) {
         final String sName = (String) iterator.next();
         final MeigasParameter param = params.getParameter(sName);
         if (!(param instanceof DerivedParameter)) {
            addParameter(param, i);
            i++;
         }
      }

   }


   private double[][] getLayoutMatrix(final int iParams) {

      final double[] cols = new double[] { 6.0, TableLayout.FILL, 6.0, TableLayout.FILL, 6.0 };
      final double[] rows = new double[iParams + 2];
      rows[0] = 6;
      rows[rows.length - 1] = TableLayout.FILL;
      for (int i = 1; i < rows.length; i++) {
         rows[i] = TableLayout.MINIMUM;
      }
      return new double[][] { cols, rows };

   }


   private void addParameter(final MeigasParameter param,
                             final int iRow) {

      final JLabel nameField = new JLabel(param.getDescription());
      this.add(nameField, "1," + Integer.toString(iRow));

      Component component = null;

      if (param instanceof MeigasNumericalValue) {
         component = new MeigasValueTextField((MeigasNumericalValue) param);
      }
      else if (param instanceof MeigasCoordinateValue) {
         component = new CoordinatesPanel((MeigasCoordinateValue) param);

      }
      else if (param instanceof MeigasPolygonValue) {
         component = new PolygonPanel();
         //TODO: set polygon
      }
      else if (param instanceof MeigasDateValue) {
         final JCalendarCombo calendar = new JCalendarCombo();
         calendar.setDate((Date) param.getValue());
         component = calendar;
      }
      else if (param instanceof MeigasSelectionValue) {
         final ComboBoxModel model = new DefaultComboBoxModel(((MeigasSelectionValue) param).getOptions());
         final JComboBox comboBox = new JComboBox();
         comboBox.setModel(model);
         component = comboBox;
      }
      else if (param instanceof MeigasSpecieValue) {
         final JComboBox combo = new JComboBox(Meigas.getSpeciesCatalog().getSpecies());
         combo.setSelectedItem(param.getValue());
         component = combo;
      }
      else if (param instanceof MeigasStringValue) {
         component = new JTextField(param.getValue().toString());
      }

      if (component != null) {
         this.add(component, "3," + Integer.toString(iRow));
         m_ComponentsMap.put(component, param);
      }

   }
}
