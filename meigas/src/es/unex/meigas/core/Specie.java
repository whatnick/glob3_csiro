package es.unex.meigas.core;

public class Specie
         implements
            Comparable {

   public Specie(final String sName) {

      name = sName;

   }


   @Override
   public String toString() {

      return name;

   }

   public String name;


   @Override
   public int compareTo(final Object o) {

      final String sName = ((Specie) o).name;
      return sName.compareTo(name);

   }

}
