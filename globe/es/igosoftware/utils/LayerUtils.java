package es.igosoftware.utils;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.view.orbit.OrbitView;

public class LayerUtils {

   public static void zoomTo(final WorldWindowGLCanvas wwd,
                             final Sector sector) {

      double altitude;

      final OrbitView view = (OrbitView) wwd.getView();
      final Globe globe = wwd.getModel().getGlobe();

      final double t = sector.getDeltaLonRadians() > sector.getDeltaLonRadians() ? sector.getDeltaLonRadians()
                                                                                : sector.getDeltaLonRadians();
      final double w = 0.5 * t * 6378137.0;
      altitude = w / wwd.getView().getFieldOfView().tanHalfAngle();

      if ((globe != null) && (view != null)) {
         wwd.getView().goTo(new Position(sector.getCentroid(), 0), altitude);
      }

   }

}
