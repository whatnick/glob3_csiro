package es.unex.meigas.gui;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import es.unex.meigas.core.parameters.MeigasNumericalValue;


public class MeigasValueTextField
         extends
            JTextField {

   private final MeigasNumericalValue m_Value;


   public MeigasValueTextField(final MeigasNumericalValue value) {

      super();

      m_Value = value;

      addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(final FocusEvent e) {
            checkDataAndUpdate(true);
         }
      });

      setTextFromValue();

   }


   private void setTextFromValue() {

      final DecimalFormat df = new DecimalFormat("##.####");
      final double dValue = (Double) m_Value.getValue();

      if (dValue != MeigasNumericalValue.NO_DATA) {
         setText(df.format(dValue));
      }
      else {
         setText("");
      }

      if (m_Value.isEstimated()) {
         setForeground(Color.blue);
      }
      else {
         if (m_Value.isBetweenLimits()) {
            setForeground(Color.black);
         }
         else {
            setForeground(Color.red);
         }
      }

   }


   protected boolean checkDataAndUpdate(final boolean bShowMessages) {

      String content = getText();
      if (content.length() != 0) {
         try {
            content = content.replace(',', '.');
            final double d = Double.parseDouble(content);
            final int iRet = m_Value.setValue(d);
            switch (iRet) {
               case MeigasNumericalValue.VALUE_OUTSIDE_LIMITS:
                  if (bShowMessages) {
                     JOptionPane.showMessageDialog(null, "El parámetro está fuera del intervalo lógico \n ("
                                                         + Double.toString(m_Value.getMin()) + " , "
                                                         + Double.toString(m_Value.getMax()) + ") ", "Aviso",
                              JOptionPane.WARNING_MESSAGE);
                  }
                  m_Value.setIsEstimated(false);
                  setTextFromValue();
                  return false;
               case MeigasNumericalValue.CHANGED:
                  m_Value.setIsEstimated(false);
                  break;
            }
         }
         catch (final NumberFormatException nfe) {
            setTextFromValue();
            return false;
         }
      }
      else {
         m_Value.setValue(MeigasNumericalValue.NO_DATA);
      }
      setTextFromValue();
      return true;

   }

}
