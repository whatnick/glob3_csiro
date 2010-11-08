package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;
import gov.nasa.worldwind.render.airspaces.Polygon;

import java.awt.Color;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class GPolygonsRenderer
         extends
            GVectorRenderer {

   private final Color _borderColor     = Color.black;
   private int         _borderThickness = 1;


   public GPolygonsRenderer(final Feature[] features) {

      super(features);

   }


   @Override
   public Renderable[] getRenderables(final Feature feature,
                                      final GProjection crs) {

      final Geometry geom = feature._geometry;
      final ArrayList<Renderable> polygons = new ArrayList<Renderable>();
      for (int iGeom = 0; iGeom < geom.getNumGeometries(); iGeom++) {
         final Geometry subgeom = geom.getGeometryN(iGeom);
         final ArrayList<LatLon> list = new ArrayList<LatLon>();
         for (int i = 0; i < subgeom.getNumPoints(); i++) {
            final Coordinate coord = subgeom.getCoordinates()[i];
            final IVector2<?> transformedPt = crs.transformPoint(GProjection.EPSG_4326, new GVector2D(coord.x, coord.y));
            final LatLon latlon = new LatLon(Angle.fromRadians(transformedPt.y()), Angle.fromRadians(transformedPt.x()));
            //final LatLon latlon = new LatLon(Angle.fromDegrees(coord.y), Angle.fromDegrees(coord.x));
            list.add(latlon);
         }
         final Polygon poly = new Polygon(list);
         final AirspaceAttributes attrs = poly.getAttributes();
         // TODO:Fix this
         poly.setAltitude(200d);

         double dValue = 0d;
         try {
            dValue = ((Number) feature._attributes[_fieldIndex]).doubleValue();
         }
         catch (final Exception e) {}
         final Color color;
         if (_coloringMethod == COLORING_METHOD_COLOR_RAMP) {
            color = new Color(getColorFromColorRamp(dValue));
         }
         else {
            color = _color;
         }
         attrs.setMaterial(new Material(color));
         attrs.setOutlineMaterial(new Material(_borderColor));
         attrs.setOutlineWidth(_borderThickness);
         attrs.setDrawOutline(true);
         attrs.setDrawInterior(true);
         polygons.add(poly);
      }

      return polygons.toArray(new Renderable[0]);

   }


   public int getBorderThickness() {
      return _borderThickness;
   }


   public void setBorderThickness(final int borderThickness) {
      _borderThickness = borderThickness;
   }

}
