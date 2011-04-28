

package es.igosoftware.euclid.experimental.measurement;


public enum GArea implements IUnit<GArea> {
   SquareCentimeter("cm²", 0.0001),
   SquareMeter("m²", 1),
   Hectare("ha", 10000),
   SquareKilometer("km²", 1000000);


   private final String _name;
   private final double _convertionFactor;


   private GArea(final String name,
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
   public IMeasure<GArea> value(final double value) {
      return new GMeasure<GArea>(value, this).simplified();
   }


   @Override
   public String toString() {
      return getName();
   }


   public static void main(final String[] args) {
      final IMeasure<GArea> m2 = GArea.SquareMeter.value(1);
      System.out.println(m2);

      final IMeasure<GArea> ha2 = GArea.Hectare.value(1);
      System.out.println(ha2);

      final IMeasure<GArea> km2 = GArea.SquareKilometer.value(1);
      System.out.println(km2);


      System.out.println();
      process(m2, m2);
      process(m2, ha2);
      process(m2, km2);

      System.out.println();
      process(ha2, m2);
      process(ha2, ha2);
      process(ha2, km2);

      System.out.println();
      process(km2, m2);
      process(km2, ha2);
      process(km2, km2);
   }


   private static void process(final IMeasure<GArea> m1,
                               final IMeasure<GArea> m2) {

      System.out.println(" " + m1 + " + " + m2 + " = " + m1.add(m2));
      System.out.println(" " + m1 + " - " + m2 + " = " + m1.sub(m2));
   }


}
