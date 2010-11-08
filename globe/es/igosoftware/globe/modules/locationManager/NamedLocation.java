package es.igosoftware.globe.modules.locationManager;

import gov.nasa.worldwind.geom.Position;

public class NamedLocation {

   public final String _name;
   public Position     _position;
   public double       _elevation;


   public NamedLocation(final String name,
                        final Position position,
                        final double elevation) {
      _name = name;
      _position = position;
      _elevation = elevation;
   }


   @Override
   public String toString() {
      return _name;
   }

}
