package es.igosoftware.globe.layers;

import com.vividsolutions.jts.geom.Coordinate;

public class GSocialObject {

   public GSocialObject(final Coordinate coords,
                        final GSocialNetworkUser user,
                        final String message) {
      super();
      _coords = coords;
      _user = user;
      _message = message;
   }


   private Coordinate         _coords;
   private GSocialNetworkUser _user;
   private String             _message;


   public Coordinate getCoords() {
      return _coords;
   }


   public void setCoords(final Coordinate coords) {
      _coords = coords;
   }


   public GSocialNetworkUser getUser() {
      return _user;
   }


   public void setUser(final GSocialNetworkUser user) {
      _user = user;
   }


   public String getMessage() {
      return _message;
   }


   public void setMessage(final String message) {
      _message = message;
   }


}
