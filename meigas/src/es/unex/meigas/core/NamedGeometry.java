package es.unex.meigas.core;

import com.vividsolutions.jts.geom.Geometry;

public class NamedGeometry {

   public String   name;
   public Geometry geom;


   public NamedGeometry(final String name,
                        final Geometry geom) {

      this.name = name;
      this.geom = geom;

   }
}
