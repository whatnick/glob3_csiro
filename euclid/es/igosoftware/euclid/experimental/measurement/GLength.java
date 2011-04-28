

package es.igosoftware.euclid.experimental.measurement;


public enum GLength implements IUnit<GLength> {
   Millimeter("mm", 0.001),
   Centimeter("cm", 0.01),
   Meter("m", 1),
   Kilometer("km", 1000);


   private final String _name;
   private final double _convertionFactor;


   private GLength(final String name,
                   final double convertionFactor) {
      _name = name;
      _convertionFactor = convertionFactor;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public double convertionFactor() {
      return _convertionFactor;
   }


   @Override
   public IMeasure<GLength> value(final double value) {
      return new GMeasure<GLength>(value, this).simplified();
   }


   @Override
   public String toString() {
      return getName();
   }


   public static void main(final String[] args) {
      final IMeasure<GLength> cm10 = GLength.Centimeter.value(10);
      System.out.println(cm10);

      final IMeasure<GLength> m500 = GLength.Meter.value(500);
      System.out.println(m500);

      final IMeasure<GLength> km20 = GLength.Kilometer.value(20);
      System.out.println(km20);


      System.out.println();
      process(cm10, cm10);
      process(cm10, m500);
      process(cm10, km20);

      System.out.println();
      process(m500, cm10);
      process(m500, m500);
      process(m500, km20);

      System.out.println();
      process(km20, cm10);
      process(km20, m500);
      process(km20, km20);
   }


   private static void process(final IMeasure<GLength> m1,
                               final IMeasure<GLength> m2) {

      System.out.println(" " + m1 + " + " + m2 + " = " + m1.add(m2));
      System.out.println(" " + m1 + " - " + m2 + " = " + m1.sub(m2));
   }


}
