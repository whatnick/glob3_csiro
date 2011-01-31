package es.unex.meigas.core;

public class Specie
         implements
            Comparable {

   public static final Specie ALL_SPECIES = new Specie("Todas las especies");

   public String              name;


   public Specie(final String sName) {

      name = sName;

   }


   @Override
   public String toString() {

      return name;

   }


   @Override
   public boolean equals(final Object o) {

      if (o instanceof Specie) {
         return ((Specie) o).name.equals(name);
      }
      return false;

   }


   @Override
   public int compareTo(final Object o) {

      final String sName = ((Specie) o).name;
      return sName.compareTo(name);

   }

}
