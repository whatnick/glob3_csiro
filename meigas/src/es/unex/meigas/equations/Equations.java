package es.unex.meigas.equations;

import java.util.ArrayList;

public class Equations {

   private final static int DESCRIPTION = 0;
   private final static int ZONE        = 1;
   private final static int SPECIE      = 2;
   private final static int QUALITY     = 3;
   private final static int PARAMETER   = 4;
   private final static int EQUATION    = 5;
   private final static int SHAPEFACTOR = 6;

   private final ArrayList  m_Equations;
   private boolean          m_bHasChanged;
   private String           m_sFile;


   public Equations() {

      m_Equations = new ArrayList();
   }


   public ArrayList getEquations() {

      return m_Equations;

   }


   public void removeEquation(final Equation eq) {

      try {
         m_Equations.remove(m_Equations.indexOf(eq));
         m_bHasChanged = true;
      }
      catch (final Exception e) {}

   }


   public void addEquation(final Equation eq) {

      m_Equations.add(eq);
      m_bHasChanged = true;

   }


   public boolean hasChanged() {

      return m_bHasChanged;

   }


   public void open(final String sFile) {

      final int i;
      final int iQuality;
      final int iParameter;
      final double dShapeFactor;
      final String sEquation;
      final String sDescription;
      final String sSpecie;
      final String sZone;

      final boolean[] bShapeFactor = new boolean[6];

      m_sFile = sFile;

      try {
         /*ITable table = (ITable) Meigas.getInputFactory().openDataObjectFromFile(sFile);

         IRecordsetIterator iter = table.iterator();
         IRecord record;
         while(iter.hasNext()){
         	record = iter.next();
         	sDescription = record.getValue(DESCRIPTION).toString().trim();
         	sZone = record.getValue(ZONE).toString().trim();
         	sSpecie = record.getValue(SPECIE).toString().trim();
         	iQuality = (int) Double.parseDouble(record.getValue(QUALITY).toString().trim());
         	iParameter = (int) Double.parseDouble(record.getValue(PARAMETER).toString().trim());
         	sEquation = record.getValue(EQUATION).toString().trim();
         	for (i = 0; i < 6; i++){
         		dShapeFactor = Double.parseDouble(record.getValue(SHAPEFACTOR + i).toString().trim());
         		bShapeFactor[i] = (dShapeFactor != 0);
         	}
         	Equation eq = new Equation();
         	eq.setDescription(sDescription);
         	eq.setLocation(sZone);
         	eq.setSpecie(sSpecie);
         	eq.setSiteIndex(iQuality);
         	eq.setEquation(sEquation);
         	eq.setParameter(iParameter);
         	eq.setShapeFactor(bShapeFactor);
         	m_Equations.add(eq);
         }

         table.close();*/

      }
      catch (final Exception e) {
         e.printStackTrace();
      }


   }


   public void save() {

   /*FileOutputChannel channel = new FileOutputChannel(m_sFile);
   Class[] types = new Class[12];
   String[] sFields = new String[12];
   types[0] = String.class;
   types[1] = String.class;
   types[2] = String.class;
   types[3] = Integer.class;
   types[4] = Integer.class;
   types[5] = String.class;
   sFields[0] = "DESC";
   sFields[1] = "ZONE";
   sFields[2] = "SPECIE";
   sFields[3] = "QUAL";
   sFields[4] = "PARAM";
   sFields[5] = "EQ";
   for (int i = 0; i < 6; i++){
   	sFields[i+6] = "SF" + Integer.toString(i);
   	types[i+6] = Integer.class;
   }

   try {
   	ITable table = Meigas.getOutputFactory().getNewTable("", types, sFields, channel);
   	table.open();
   	Object[] values = new Object[12];
   	for (int i = 0; i < m_Equations.size(); i++){
   		Equation eq = (Equation) m_Equations.get(i);
   		values[0] = eq.getDescription();
   		values[1] = eq.getLocation();
   		values[2] = eq.getSpecie();
   		values[3] = new Integer(eq.getSiteIndex());
   		values[4] = new Integer(eq.getParameter());
   		values[5] = eq.getEquation();
   		boolean[] sf = eq.getShapeFactor();
   		for (i = 0; i < 6; i++){
   			if (sf[i]){
   				values[6 + i] = new Integer(1);
   			}
   			else{
   				values[6 + i] = new Integer(0);
   			}
   		}
   		table.addRecord(values);
   	}
   	table.close();

   } catch (UnsupportedOutputChannelException e) {
   	// TODO Auto-generated catch block
   	e.printStackTrace();
   }*/

   }

}
