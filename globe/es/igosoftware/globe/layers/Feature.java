package es.igosoftware.globe.layers;

import com.vividsolutions.jts.geom.Geometry;

public class Feature {

   public final Geometry _geometry;
   public final Object[] _attributes;


   public Feature(final Geometry geometry,
                  final Object[] attributes) {
      _geometry = geometry;
      _attributes = attributes;
   }


}
