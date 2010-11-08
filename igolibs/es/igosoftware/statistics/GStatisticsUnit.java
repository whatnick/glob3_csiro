package es.igosoftware.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.igosoftware.util.Logger;

public class GStatisticsUnit {

   private static final Logger                                              LOGGER        = Logger.instance();
   private static final String                                              LINE          = "-------------------------------------------------------------------------------";


   private final String                                                     _name;
   //private final GStatisticsVariableAbstract<? extends Number>[]            _variables;
   private final List<GStatisticsVariableAbstract<? extends Number>>        _variables    = new ArrayList<GStatisticsVariableAbstract<? extends Number>>();
   private final Map<String, GStatisticsVariableAbstract<? extends Number>> _variablesMap = new HashMap<String, GStatisticsVariableAbstract<? extends Number>>();


   public GStatisticsUnit(final String name) {
      _name = name;
   }


   //   public GStatisticsUnit(final String name,
   //                          final GStatisticsVariableAbstract<? extends Number>... variables) {
   //      _name = name;
   //      _variables = variables;
   //
   //      _variablesMap = new HashMap<String, GStatisticsVariableAbstract<? extends Number>>(_variables.length);
   //      for (final GStatisticsVariableAbstract<? extends Number> variable : _variables) {
   //         _variablesMap.put(variable.getName(), variable);
   //      }
   //
   //   }


   public void addVariable(final GStatisticsVariableAbstract<? extends Number> var) {
      _variables.add(var);
      _variablesMap.put(var.getName(), var);
   }


   public void show() {
      show(LOGGER);
   }


   public void show(final Logger logger) {
      logger.info(LINE);
      if (_name != null) {
         logger.info(_name + ":");
      }

      for (final GStatisticsVariableAbstract<?> variable : _variables) {
         variable.show(logger);
         //logger.info("\n");
      }

      logger.info(LINE);
   }


   public void sample(final String varName,
                      final long delta) {

      final GStatisticsVariableL var = (GStatisticsVariableL) _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
      }
      else {
         var.sample(delta);
      }
   }


   //   public void sample(final String varName,
   //                      final int delta) {
   //      sample(varName, delta);
   //   }


   public void sample(final String varName) {
      sample(varName, 1);
   }


   public void sample(final String varName,
                      final double delta) {
      final GStatisticsVariableD var = (GStatisticsVariableD) _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
      }
      else {
         var.sample(delta);
      }
   }


   public long getCounter(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return -1;
      }

      return var.getCounter();
   }


   public List<? extends Number> getVariableList(final String varName) {
      final GStatisticsVariableAbstract<? extends Number> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return null;
      }

      return var.getVarList();
   }


   public Number getMax(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return null;
      }

      return var.getMax();
   }


   public Number getMin(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return null;
      }

      return var.getMin();
   }


   public double getAverage(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return Double.NaN;
      }

      return var.getAverage().doubleValue();
   }


   public double getStandarDeviation(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.warning(varName + " variable not found");
         return Double.NaN;
      }

      return var.getStandardDeviation().doubleValue();
   }


   public static void main(final String[] args) {
      System.out.println("GStatisticsUnit 0.1");
      System.out.println("-------------------\n");

      final GStatisticsUnit unit = new GStatisticsUnit("Test");

      unit.addVariable(new GStatisticsVariableL("var1", GStatisticsVariableAbstract.MAX | GStatisticsVariableAbstract.MIN
                                                        | GStatisticsVariableAbstract.AVERAGE
                                                        | GStatisticsVariableAbstract.STANDARD_DEVIATION));

      unit.addVariable(new GStatisticsVariableD("var2", GStatisticsVariableAbstract.AVERAGE | GStatisticsVariableAbstract.TOTAL));

      unit.sample("var1", 1);
      unit.sample("var1", 5);
      unit.sample("var1", 9);

      unit.sample("var2", 0.2);
      unit.sample("var2", 8.5);
      unit.sample("var2", 5.7);

      unit.show();
   }

}
