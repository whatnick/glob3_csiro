package es.igosoftware.globe.layers;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import gov.nasa.worldwind.render.markers.MarkerRenderer;

import java.util.ArrayList;

public class RenderableMarker
         extends
            BasicMarker
         implements
            Renderable {

   private final MarkerRenderer markerRenderer = new MarkerRenderer();
   private ArrayList<Marker>    markerList;


   public RenderableMarker(final Position position,
                           final MarkerAttributes attrs) {

      super(position, attrs);

   }


   @Override
   public void render(final DrawContext dc) {

      if (this.markerList == null) {

         this.markerList = new ArrayList<Marker>();
         this.markerList.add(this);

      }
      markerRenderer.render(dc, this.markerList);

   }

}
