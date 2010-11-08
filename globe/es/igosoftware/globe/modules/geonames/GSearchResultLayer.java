package es.igosoftware.globe.modules.geonames;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.Feature;
import es.igosoftware.globe.layers.GVectorRenderer;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.markers.Marker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GSearchResultLayer
         extends
            MarkerLayer
         implements
            IGlobeVectorLayer {

   private final Feature[] m_Features;


   public GSearchResultLayer(final ArrayList<Marker> list) {

      super(list);

      m_Features = new Feature[list.size()];

      final GeometryFactory gf = new GeometryFactory();

      for (int i = 0; i < m_Features.length; i++) {
         final SearchResultMarker marker = (SearchResultMarker) list.get(i);
         final Point geom = gf.createPoint(new Coordinate(marker.getPosition().longitude.degrees,
                  marker.getPosition().latitude.degrees));
         try {
            final Object[] attribs = new Object[] { marker.getToponym().getName(), marker.getToponym().getPopulation() };
            m_Features[i] = new Feature(geom, attribs);
         }
         catch (final Exception e) {
            m_Features[i] = new Feature(geom, new Object[] { "", new Long(0) });
         }

      }

   }


   @Override
   public Sector getExtent() {
      return Sector.FULL_SPHERE;
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return null;
   }


   @Override
   public GProjection getProjection() {
      return GProjection.EPSG_4326;
   }


   @Override
   public void setProjection(final GProjection proj) {}


   @Override
   public GField[] getFields() {
      return new GField[] { new GField("Name", String.class), new GField("Population", Integer.class) };
   }


   @Override
   public GVectorLayerType getShapeType() {
      return GVectorLayerType.POINT;
   }


   @Override
   public final void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public Feature[] getFeatures() {

      return m_Features;

   }


   @Override
   public GVectorRenderer getRenderer() {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      application.zoomToSector(getExtent());
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }

}
