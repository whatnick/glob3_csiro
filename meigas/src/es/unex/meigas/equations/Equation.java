package es.unex.meigas.equations;

import org.nfunk.jep.JEP;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Specie;

public class Equation {

   public static final int HT                      = 0;
   public static final int HF                      = 1;
   public static final int DN                      = 2;
   public static final int CZ                      = 3;
   public static final int VCC                     = 4;

   public static final int VOLUME                  = 0;
   public static final int VOLUME_WITHOUT_BARK     = 1;
   public static final int VOLUME_INCREMENT        = 2;

   public static final int ALL_SITE_INDICES        = 0;

   private Integer         m_ID                    = null;
   private String          m_sDescription          = null;
   private int             m_iParameter            = 0;
   private String          m_sEquation             = null;
   private boolean[]       m_bShapeFactor          = new boolean[6];
   private int             m_iSiteIndex            = 0;
   private Specie          m_sSpecie               = null;
   private String          m_sLocation             = null;
   private final boolean   m_bRequiredParameters[] = new boolean[5];
   private JEP             m_Parser;


   public boolean[] getShapeFactor() {

      return m_bShapeFactor;

   }


   public void setShapeFactor(final boolean[] shapeFactor) {

      m_bShapeFactor = shapeFactor;

   }


   public int getSiteIndex() {

      return m_iSiteIndex;

   }


   public void setSiteIndex(final int siteIndex) {

      m_iSiteIndex = siteIndex;

   }


   public String getEquation() {

      return m_sEquation;

   }


   public void setEquation(final String equation) {

      m_sEquation = equation;

      m_Parser = null;

   }


   public void parseEquation() {

      if (m_Parser == null) {

         final String eq = m_sEquation.toLowerCase();

         m_bRequiredParameters[HT] = (eq.indexOf("ht") != -1);
         m_bRequiredParameters[HF] = (eq.indexOf("hf") != -1);
         m_bRequiredParameters[DN] = (eq.indexOf("dn") != -1);
         m_bRequiredParameters[CZ] = (eq.indexOf("cz") != -1);
         m_bRequiredParameters[VCC] = (eq.indexOf("vcc") != -1);

         m_Parser = new JEP();
         m_Parser.addStandardConstants();
         m_Parser.addStandardFunctions();
         if (m_bRequiredParameters[DN]) {
            m_Parser.addVariable("dn", 0.0);
         }
         if (m_bRequiredParameters[HT]) {
            m_Parser.addVariable("ht", 0.0);
         }
         if (m_bRequiredParameters[HF]) {
            m_Parser.addVariable("hf", 0.0);
         }
         if (m_bRequiredParameters[CZ]) {
            m_Parser.addVariable("cz", 0.0);
         }
         if (m_bRequiredParameters[VCC]) {
            m_Parser.addVariable("vcc", 0.0);
         }
         m_Parser.parseExpression(eq);
         if (m_Parser.hasError()) {
            m_Parser = null;
         }

      }

   }


   public String getLocation() {

      return m_sLocation;

   }


   public void setLocation(final String location) {

      m_sLocation = location;

   }


   public Specie getSpecie() {

      return m_sSpecie;

   }


   public void setSpecie(final Specie specie) {

      m_sSpecie = specie;

   }


   public Integer getID() {

      return m_ID;

   }


   public void setID(final Integer id) {

      m_ID = id;

   }


   public int getParameter() {

      return m_iParameter;

   }


   public void setParameter(final int parameter) {

      m_iParameter = parameter;

   }


   public String getDescription() {

      return m_sDescription;

   }


   public void setDescription(final String description) {

      m_sDescription = description;

   }


   @Override
   public String toString() {

      return m_sDescription;

   }


   public double Calculate(final double dVCC,
                           final double dHeight,
                           final double dLogHeight,
                           final double dDBH,
                           final double dBark) {

      if (m_Parser == null) {
         return DasocraticElement.NO_DATA;
      }

      if (m_bRequiredParameters[DN]) {
         if (dDBH != DasocraticElement.NO_DATA) {
            m_Parser.addVariable("dn", dDBH);
         }
         else {
            return DasocraticElement.NO_DATA;
         }
      }
      if (m_bRequiredParameters[HT]) {
         if (dHeight != DasocraticElement.NO_DATA) {
            m_Parser.addVariable("ht", dHeight);
         }
         else {
            return DasocraticElement.NO_DATA;
         }
      }
      if (m_bRequiredParameters[HF]) {
         if (dLogHeight != DasocraticElement.NO_DATA) {
            m_Parser.addVariable("hf", dLogHeight);
         }
         else {
            return DasocraticElement.NO_DATA;
         }
      }
      if (m_bRequiredParameters[CZ]) {
         if (dBark != DasocraticElement.NO_DATA) {
            m_Parser.addVariable("cz", dBark);
         }
         else {
            return DasocraticElement.NO_DATA;
         }
      }
      if (m_bRequiredParameters[VCC]) {
         if (dVCC != DasocraticElement.NO_DATA) {
            m_Parser.addVariable("vcc", dVCC);
         }
         else {
            return DasocraticElement.NO_DATA;
         }
      }

      return m_Parser.getValue();


   }

}
