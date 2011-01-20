package es.unex.meigas.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SpeciesCatalog {

   private Specie[] m_Species = new Specie[] { new Specie("Pinus pinaster") };


   public Specie[] getSpecies() {

      return m_Species;

   }


   public void open(final String file) {

      final ArrayList<Specie> species = new ArrayList<Specie>();

      try {

         final BufferedReader input = new BufferedReader(new FileReader(file));
         try {
            String line = null;
            while ((line = input.readLine()) != null) {
               species.add(new Specie(line));
            }
         }
         finally {
            input.close();
         }
      }
      catch (final IOException ex) {
         ex.printStackTrace();
      }

      m_Species = species.toArray(new Specie[0]);
      Arrays.sort(m_Species);

   }


   public Specie getSpecieFromName(final String sName) {

      for (int i = 0; i < m_Species.length; i++) {
         if (m_Species[i].name.toLowerCase().equals(sName.toLowerCase())) {
            return m_Species[i];
         }
      }

      return null;

   }


   public Specie getDefaultSpecie() {

      return m_Species[0];


   }

}
