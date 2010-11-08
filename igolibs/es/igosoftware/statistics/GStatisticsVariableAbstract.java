package es.igosoftware.statistics;

import java.util.List;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.Logger;

public abstract class GStatisticsVariableAbstract<TypeT extends Number> {

   public static final int MAX                = 1;
   public static final int MIN                = 2;
   public static final int AVERAGE            = 4;
   public static final int TOTAL              = 8;
   public static final int STANDARD_DEVIATION = 16;
   public static final int HISTOGRAM          = 32;


   protected final String  _name;
   protected final int     _flags;
   protected long          _counter           = 0;


   public GStatisticsVariableAbstract(final String name,
                                      final int flags) {
      GAssert.notNull(name, "name");

      _name = name;
      _flags = flags;
   }


   public GStatisticsVariableAbstract(final String name) {

      this(name, 0);
   }


   public abstract void show(final Logger logger);


   public String getName() {
      return _name;
   }


   public long getCounter() {
      return _counter;
   }


   public abstract List<TypeT> getVarList();


   public abstract Number getMax();


   public abstract Number getMin();


   public abstract Double getAverage();


   public abstract Double getStandardDeviation();


   protected boolean isFlaged(final int reference) {
      return (_flags & reference) != 0;
   }


   public abstract void sample(final TypeT delta);


}
