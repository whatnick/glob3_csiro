package es.unex.meigas.core;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import es.unex.meigas.core.parameters.DistributionStats;
import es.unex.meigas.core.parameters.MeigasNumericalValue;
import es.unex.meigas.core.parameters.MeigasParameter;
import es.unex.meigas.core.parameters.ParametersSet;
import es.unex.sextante.libMath.simpleStats.SimpleStats;

public abstract class DasocraticElement
         implements
            Serializable {

   public static final double                     NO_DATA  = MeigasNumericalValue.NO_DATA;

   protected ParametersSet                        m_Parameters;


   protected HashMap<String, SimpleStats>         m_TreeStats;
   protected HashMap<String, Double>              m_AccumulatedTreeParameters;
   protected HashMap<String, DistributionStats[]> m_Distributions;

   protected ArrayList<DasocraticElement>         m_Elements;
   protected String                               m_sName  = "";
   protected String                               m_sNotes = "";
   protected ArrayList<Picture>                   m_Pictures;
   protected DasocraticElement                    m_Parent = null;


   public ParametersSet getParameters() {

      return m_Parameters;

   }

   //TODO add errors

   protected boolean m_bHasChanged = false;
   protected boolean m_bCalculated = false;
   protected Specie  m_CurrentSpecieForParameterCalculation;


   //protected Color m_Color;

   public abstract Rectangle2D getBoundingBox();


   public abstract double getArea(); // in ha


   public abstract String[] getReport();


   public DasocraticElement() {

      m_Elements = new ArrayList<DasocraticElement>();
      m_Pictures = new ArrayList<Picture>();
      m_TreeStats = new HashMap<String, SimpleStats>();
      m_AccumulatedTreeParameters = new HashMap<String, Double>();
      m_Distributions = new HashMap<String, DistributionStats[]>();
      m_Parameters = new ParametersSet();

   }


   public HashMap<String, DistributionStats[]> getDistributions(final Specie specie) {

      if (!m_bCalculated) {
         if (specie == null) {
            if (m_CurrentSpecieForParameterCalculation == null) {
               calculateParameters(specie);
            }
         }
         else if (specie.equals(m_CurrentSpecieForParameterCalculation)) {
            calculateParameters(specie);
         }
      }
      return m_Distributions;

   }


   public HashMap<String, Double> getAccumulatedTreeParameters() {

      return m_AccumulatedTreeParameters;

   }


   public HashMap<String, SimpleStats> getTreeStats() {

      return m_TreeStats;

   }


   public DasocraticElement getElement(final int iIndex) {

      return m_Elements.get(iIndex);

   }


   public int getElementsCount() {

      return m_Elements.size();

   }


   protected void setParameters(final ParametersSet params) {

      m_Parameters = params;
      setHasChanged(true);

   }


   public boolean setParameterValue(final String sParamName,
                                    final Object value) {

      final MeigasParameter param = m_Parameters.getParameter(sParamName);

      if (param != null) {
         return param.setValue(value);
      }
      else {
         return false;
      }

   }


   public Object getParameterValue(final String sParamName) {

      final MeigasParameter param = m_Parameters.getParameter(sParamName);

      if (param != null) {
         return param.getValue();
      }
      else {
         return null;
      }

   }


   public MeigasParameter getParameter(final String sParamName) {

      final MeigasParameter param = m_Parameters.getParameter(sParamName);
      return param;

   }


   public void getElementsRecursive(final ArrayList elements) {

      int i;

      for (i = 0; i < m_Elements.size(); i++) {
         elements.add(m_Elements.get(i));
      }

      for (i = 0; i < m_Elements.size(); i++) {
         (m_Elements.get(i)).getElementsRecursive(elements);
      }

   }


   public void getElementsOfClassRecursive(final ArrayList elements,
                                           final Class clazz) {

      int i;

      for (i = 0; i < m_Elements.size(); i++) {
         if (m_Elements.get(i).getClass().equals(clazz)) {
            elements.add(m_Elements.get(i));
         }
      }

      for (i = 0; i < m_Elements.size(); i++) {
         (m_Elements.get(i)).getElementsOfClassRecursive(elements, clazz);
      }

   }


   public Tree[] getTrees(final Specie specie) {

      final ArrayList<Tree> list = new ArrayList<Tree>();

      getElementsOfClassRecursive(list, Tree.class);

      final ArrayList<Tree> selectedTrees = new ArrayList<Tree>();

      if (specie != null) {
         for (int i = 0; i < list.size(); i++) {
            final Tree tree = list.get(i);
            final Specie treeSpecie = (Specie) tree.getParameter(Tree.SPECIE).getValue();
            if (specie.equals(treeSpecie)) {
               selectedTrees.add(tree);
            }
         }
         return selectedTrees.toArray(new Tree[0]);
      }
      else {
         return list.toArray(new Tree[0]);
      }

   }


   public DasocraticElement addElement(final DasocraticElement element) {

      final DasocraticElement parent = _addElement(element);

      if (parent != null) {
         setHasChanged(true);
         return parent;
      }
      else {
         return null;
      }

   }


   protected abstract DasocraticElement _addElement(DasocraticElement element);


   public String getName() {

      return m_sName;

   }


   public void setName(final String sName) {

      if (!sName.equals(m_sName)) {
         m_sName = sName;
         setHasChanged(true);
      }

   }


   @Override
   public String toString() {

      return m_sName;

   }


   public DasocraticElement getParent() {

      return m_Parent;

   }


   public void setParent(final DasocraticElement parent) {

      m_Parent = parent;

   }


   public String getNotes() {

      return m_sNotes;

   }


   public void setNotes(final String sNotes) {

      if (!sNotes.equals(m_sNotes)) {
         m_sNotes = sNotes;
         setHasChanged(true);
      }

   }


   public DasocraticElement getParentOfType(final Class type) {

      /*if (type.isInstance(this)) {
         return null;
      }*/

      DasocraticElement parent = this;

      while (!type.isInstance(parent) && (parent != null)) {
         parent = parent.getParent();
      }

      return parent;

   }


   public void setHasChanged(final boolean bHasChanged) {


      m_bHasChanged = bHasChanged;
      if (bHasChanged) {
         final DasocraticElement project = getParentOfType(DasocraticProject.class);
         if (project != null) {
            project.setHasChanged(bHasChanged);
         }
         m_bCalculated = true;
      }


   }


   public boolean getHasChanged() {

      return m_bHasChanged;

   }


   public ArrayList getPictures() {

      return m_Pictures;

   }


   public void addPicture(final Picture pic) {

      m_Pictures.add(pic);

   }


   public boolean removePicture(final Picture pic) {

      return m_Pictures.remove(pic);

   }


   public String[] getSpeciesNames() {

      int i, j;
      final ArrayList list = new ArrayList();
      String[] species;
      String[] speciesArray;

      for (i = 0; i < m_Elements.size(); i++) {
         species = (m_Elements.get(i)).getSpeciesNames();
         for (j = 0; j < species.length; j++) {
            if (!list.contains(species[j])) {
               list.add(new String(species[j]));
            }
         }
      }

      speciesArray = new String[list.size()];
      speciesArray = (String[]) list.toArray(speciesArray);

      return speciesArray;

   }


   public void removeElement(final DasocraticElement element) {

      m_Elements.remove(element);
      setHasChanged(true);

   }


   public boolean hasTrees() {

      int i;
      boolean bHasTrees;

      for (i = 0; i < m_Elements.size(); i++) {
         bHasTrees = (m_Elements.get(i)).hasTrees();
         if (bHasTrees) {
            return true;
         }
      }

      return false;

   }


   /*   public double getPlotsArea(IFilter[] filters) {//in ha

         int i;
         double dArea = 0;
         double dPlotArea;
         final ArrayList plots = getPlots(filters);

         for (i = 0; i < plots.size(); i++) {
            dPlotArea = ((Plot) plots.get(i)).getArea();
            if (dPlotArea != NO_DATA) {
               dArea += dPlotArea;
            }
         }

         if (dArea == 0) {
            dArea = NO_DATA;
         }

         return dArea;

      }


      public ArrayList getPlots() {

         return getPlots(new IFilter[0]);

      }


      public ArrayList getPlots(IFilter[] filters) {

         int i, j;
         ArrayList plots;
         final ArrayList allPlots = new ArrayList();

         for (i = 0; i < m_Elements.size(); i++) {
            plots = ((DasocraticElement) m_Elements.get(i)).getPlots(filters);
            for (j = 0; j < plots.size(); j++) {
               allPlots.add(plots.get(j));
            }
         }

         return allPlots;

      }


      public ArrayList getStands() {

         return getStands(new IFilter[0]);

      }


      public ArrayList getStands(IFilter[] filters) {

         int i, j;
         ArrayList stands;
         final ArrayList allStands = new ArrayList();

         for (i = 0; i < m_Elements.size(); i++) {
            stands = ((DasocraticElement) m_Elements.get(i)).getStands(filters);
            for (j = 0; j < stands.size(); j++) {
               allStands.add(stands.get(j));
            }
         }

         return allStands;

      }


      public ArrayList getStandsWithLimits() {

         int i, j;
         ArrayList stands;
         final ArrayList allStands = new ArrayList();

         for (i = 0; i < m_Elements.size(); i++) {
            stands = ((DasocraticElement) m_Elements.get(i)).getStandsWithLimits();
            for (j = 0; j < stands.size(); j++) {
               allStands.add(stands.get(j));
            }
         }

         return allStands;

      }*/


   public boolean hasValidData() {

      int i;

      for (i = 0; i < m_Elements.size(); i++) {
         if (!(m_Elements.get(i)).hasValidData()) {
            return false;
         }
      }

      return m_Parameters.checkData();

   }


   public DasocraticElement getNewInstance() {

      DasocraticElement element = null;
      try {
         element = this.getClass().newInstance();
         element.setName(m_sName);
         element.setNotes(m_sNotes);
         for (int i = 0; i < m_Pictures.size(); i++) {
            element.addPicture(m_Pictures.get(i).getNewInstance());
         }
         for (int i = 0; i < m_Elements.size(); i++) {
            element.addElement(m_Elements.get(i).getNewInstance());
         }
         element.setParameters(m_Parameters);
      }
      catch (final Exception e) {}

      return element;

   }


   public void calculateParameters(final Specie specie) {

      m_CurrentSpecieForParameterCalculation = specie;

      m_bCalculated = true;

      final double dArea;
      final Tree[] trees = getTrees(specie);

      if (trees.length == 0) {
         return;
      }

      m_TreeStats.clear();

      final ParametersSet params = Tree.getParametersDefinition();
      final Set<String> names = params.getParameterNames();

      for (final String sName : names) {
         final MeigasParameter param = params.getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            m_TreeStats.put(sName, new SimpleStats());
         }
      }

      for (final Tree tree : trees) {
         for (final String sName : names) {
            final MeigasParameter param = tree.getParameters().getParameter(sName);
            if (param instanceof MeigasNumericalValue) {
               final SimpleStats stats = m_TreeStats.get(sName);
               final double dValue = ((Double) param.getValue()).doubleValue();
               if (dValue != DasocraticElement.NO_DATA) {
                  stats.addValue(dValue);
               }
            }
         }
      }

      int iMaxDBH = (int) m_TreeStats.get(Tree.DBH).getMax();
      //TODO: Correct stats when there are no valid trees.

      if (iMaxDBH == NO_DATA) {
         iMaxDBH = 0;
      }


      //distributions
      for (final String sName : names) {
         final MeigasParameter param = params.getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            final DistributionStats[] distributionStats = new DistributionStats[iMaxDBH + 1];
            for (int i = 0; i < distributionStats.length; i++) {
               distributionStats[i] = new DistributionStats();
            }
            m_Distributions.put(sName, distributionStats);
         }

      }


      for (int i = 0; i < m_Elements.size(); i++) {
         final DasocraticElement child = m_Elements.get(i);
         final HashMap<String, DistributionStats[]> distributions = child.getDistributions(m_CurrentSpecieForParameterCalculation);
         final Set<String> distParams = m_Distributions.keySet();
         for (final String sName : distParams) {
            final DistributionStats[] childDistribution = distributions.get(sName);
            final DistributionStats[] distribution = m_Distributions.get(sName);
            for (int iClass = 0; iClass < childDistribution.length; iClass++) {
               distribution[iClass].addStats(childDistribution[iClass]);
            }
         }
      }


      //accumulated values
      dArea = getArea();
      for (final String sName : names) {
         final MeigasParameter param = params.getParameter(sName);
         if (param instanceof MeigasNumericalValue) {
            if (((MeigasNumericalValue) param).isAccumulated()) {
               if (dArea == NO_DATA) {
                  m_AccumulatedTreeParameters.put(param.getName(), NO_DATA);
               }
               else {
                  m_AccumulatedTreeParameters.put(param.getName(), m_TreeStats.get(param.getName()).getSum() / dArea);
               }

            }
         }
      }


   }


   public abstract Class[] getParentElementClass();


   public ArrayList getStandsWithLimits() {

      final ArrayList<Stand> standsWithLimits = new ArrayList<Stand>();
      final ArrayList<Stand> stands = new ArrayList<Stand>();

      getElementsOfClassRecursive(stands, Stand.class);

      for (int i = 0; i < stands.size(); i++) {
         final Object geom = stands.get(i).getParameterValue(Stand.POLYGON);
         if (geom != null) {
            standsWithLimits.add(stands.get(i));
         }
      }

      return standsWithLimits;

   }

}
