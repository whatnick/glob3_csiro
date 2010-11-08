package es.igosoftware.globe.modules.locationManager;

import java.util.ArrayList;

public class Locations {

   private static final ArrayList<NamedLocation> m_Locations = new ArrayList<NamedLocation>();
   private static NamedLocation                  m_Default;


   public static NamedLocation[] getLocations() {

      return m_Locations.toArray(new NamedLocation[0]);

   }


   public static void addLocation(final NamedLocation namedLocation) {

      m_Locations.add(namedLocation);

   }


   public static void removeLocation(final NamedLocation namedLocation) {

      m_Locations.remove(namedLocation);

   }


   public static void setDefaultLocation(final NamedLocation namedLocation) {

      m_Default = namedLocation;

   }


   public static NamedLocation getDefaultLocation() {

      return m_Default;

   }

}
