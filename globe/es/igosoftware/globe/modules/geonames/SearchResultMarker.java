package es.igosoftware.globe.modules.geonames;

import org.geonames.Toponym;

import es.igosoftware.globe.layers.RenderableMarker;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.markers.MarkerAttributes;

public class SearchResultMarker
         extends
            RenderableMarker {

   private final Toponym m_Toponym;


   public SearchResultMarker(final Position position,
                             final MarkerAttributes attrs,
                             final Toponym toponym) {

      super(position, attrs);

      m_Toponym = toponym;

   }


   public Toponym getToponym() {

      return m_Toponym;

   }

}
