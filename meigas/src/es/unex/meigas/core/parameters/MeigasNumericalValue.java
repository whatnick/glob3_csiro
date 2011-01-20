package es.unex.meigas.core.parameters;

import java.io.Serializable;
import java.text.DecimalFormat;

public class MeigasNumericalValue
         extends
            MeigasParameter
         implements
            Serializable {

   public static final double NO_DATA              = -999999999;

   public static final int    CHANGED              = 0;
   public static final int    NOT_CHANGED          = 1;
   public static final int    VALUE_OUTSIDE_LIMITS = 2;

   private double             m_dMin, m_dMax;
   private boolean            m_bIsEstimated;
   private boolean            m_bShowAsInteger;
   private final boolean      m_bIsAccumulated;


   public MeigasNumericalValue(final String sName,
                               final String sDescription,
                               final boolean bIsAccumulated) {

      this(sName, sDescription, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false, bIsAccumulated);


   }


   public MeigasNumericalValue(final String sName,
                               final String sDescription,
                               final double dMin,
                               final double dMax) {

      this(sName, sDescription, dMin, dMax, false, false);

   }


   public MeigasNumericalValue(final String sName,
                               final String sDescription,
                               final double dMin,
                               final double dMax,
                               final boolean bShowAsInteger,
                               final boolean bIsAccumulated) {

      super(sName, sDescription);

      m_dMin = dMin;
      m_dMax = dMax;
      m_Value = new Double(NO_DATA);
      m_bIsEstimated = false;
      m_bShowAsInteger = bShowAsInteger;
      m_bIsAccumulated = bIsAccumulated;

   }


   public boolean isEstimated() {

      return m_bIsEstimated;

   }


   public boolean isAccumulated() {

      return m_bIsAccumulated;

   }


   public void setIsEstimated(final boolean isEstimated) {

      m_bIsEstimated = isEstimated;

   }


   public double getMax() {

      return m_dMax;

   }


   public void setMax(final double max) {

      m_dMax = max;

   }


   public double getMin() {

      return m_dMin;

   }


   public void setMin(final double min) {

      m_dMin = min;

   }


   public int setValue(final double dValue) {


      if (Math.abs(dValue - ((Double) m_Value).doubleValue()) < 0.001) {
         return NOT_CHANGED;
      }
      else {
         m_Value = new Double(dValue);
         if (isBetweenLimits()) {
            return CHANGED;
         }
         else {
            return VALUE_OUTSIDE_LIMITS;
         }
      }

   }


   public boolean isBetweenLimits() {

      final double dValue = ((Double) m_Value).doubleValue();
      if (dValue != NO_DATA) {
         return ((dValue < m_dMax) && (dValue > m_dMin));
      }
      else {
         return true;
      }

   }


   public boolean isNoData() {

      final double dValue = ((Double) m_Value).doubleValue();
      return (dValue == NO_DATA);

   }


   public boolean getShowAsInteger() {

      return m_bShowAsInteger;

   }


   public void setShowAsInteger(final boolean showAsInteger) {

      m_bShowAsInteger = showAsInteger;

   }


   @Override
   public String getErrorMessage() {

      if (hasValidData()) {
         return null;
      }

      final double dValue = ((Double) m_Value).doubleValue();
      final DecimalFormat df = new DecimalFormat("##.###");
      final String s = "El valor del parametro " + m_sName + "(" + df.format(dValue) + ")"
                       + "no está entre los límites lógicos (" + df.format(m_dMin) + ", " + df.format(m_dMax) + ")";
      return s;
   }


   @Override
   public boolean hasValidData() {

      return isBetweenLimits();

   }


   @Override
   public boolean setValue(final Object value) {

      if (value instanceof Double) {
         m_Value = value;
         return true;
      }
      else {
         return false;
      }
   }


   @Override
   protected Object getDefaultValue() {

      return Double.valueOf(NO_DATA);

   }

}
