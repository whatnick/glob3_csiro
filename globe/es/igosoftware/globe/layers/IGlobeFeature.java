

package es.igosoftware.globe.layers;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;


public interface IGlobeFeature {

   public Geometry getGeometry();


   public List<Object> getAttributes();


   public Object getAttribute(final int index);

}
