package es.unex.meigas.gui;

import info.clearthought.layout.TableLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vividsolutions.jts.geom.Coordinate;

import es.unex.meigas.core.parameters.MeigasCoordinateValue;

public class CoordinatesPanel
         extends
            JPanel {

   private JTextField jTextFieldX;
   private JTextField jTextFieldY;


   public CoordinatesPanel(final MeigasCoordinateValue param) {

      super();

      initGUI();

      final Coordinate coord = (Coordinate) param.getValue();
      if (coord != null) {
         jTextFieldX.setText(Double.toString(coord.x));
         jTextFieldY.setText(Double.toString(coord.y));
      }

   }


   public Coordinate getCoordinates() {


      try {
         final double x = Double.parseDouble(jTextFieldX.getText());
         final double y = Double.parseDouble(jTextFieldY.getText());
         return new Coordinate(x, y);
      }
      catch (final Exception e) {
         return null;
      }

   }


   private void initGUI() {
      try {
         final TableLayout thisLayout = new TableLayout(new double[][] { { TableLayout.FILL, 10.0, TableLayout.FILL },
                  { TableLayout.FILL } });
         thisLayout.setHGap(5);
         thisLayout.setVGap(5);
         this.setLayout(thisLayout);
         {
            jTextFieldX = new JTextField();
            this.add(jTextFieldX, "0, 0");
         }
         {
            jTextFieldY = new JTextField();
            this.add(jTextFieldY, "2, 0");
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }
   }

}
