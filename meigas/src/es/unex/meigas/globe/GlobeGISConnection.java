

package es.unex.meigas.globe;

import es.igosoftware.globe.IGlobeApplication;
import es.unex.meigas.core.gis.IGISConnection;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;


public class GlobeGISConnection
         implements
            IGISConnection {

   private final IGlobeApplication _application;


   public GlobeGISConnection(final IGlobeApplication application) {
      // TODO Auto-generated constructor stub
      _application = application;
   }


   @Override
   public void zoomToPosition(final double x,
                              final double y,
                              final double elevation) {
      // TODO Auto-generated method stub
      final Position position = new Position(Angle.fromDegrees(x), Angle.fromDegrees(y), elevation);
      _application.goTo(position, elevation);
   }
}
