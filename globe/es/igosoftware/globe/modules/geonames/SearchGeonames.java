package es.igosoftware.globe.modules.geonames;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.geonames.Toponym;
import org.geonames.WebService;
import org.geonames.WikipediaArticle;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.MarkerAttributes;

public class SearchGeonames
         extends
            GAbstractGlobeModule {


   public static final String    SEARCH_ANNOTATION_LAYER_NAME = "SEARCH_ANNOTATION_LAYER_NAME";
   private SearchResultMarker    lastHighlit;
   private BasicMarkerAttributes lastAttrs;


   @Override
   public String getName() {
      return "Search GeoNames";
   }


   @Override
   public String getDescription() {
      return "Search GeoNames";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application,
                                             final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Search", new GeonamesPanel(application, new Dimension(250, 500))));

      return panels;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      addListener(application);
   }


   private void addListener(final IGlobeApplication application) {

      application.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {

         @Override
         public void selected(final SelectEvent event) {

            if (event.getTopObject() instanceof GlobeAnnotation) {
               return;
            }

            if ((lastHighlit != null) && ((event.getTopObject() == null) || !event.getTopObject().equals(lastHighlit))) {
               lastHighlit.setAttributes(lastAttrs);
               lastHighlit = null;
               final Layer layer = application.getLayerList().getLayerByName(SEARCH_ANNOTATION_LAYER_NAME);
               if (layer != null) {
                  application.getLayerList().remove(layer);
               }
            }

            if ((event.getTopObject() == null) || (event.getTopPickedObject().getParentLayer() == null)) {
               return;
            }

            if (!event.getEventAction().equals(SelectEvent.ROLLOVER)) {
               return;
            }

            if ((lastHighlit == null) && (event.getTopObject() instanceof SearchResultMarker)) {
               lastHighlit = (SearchResultMarker) event.getTopObject();
               lastAttrs = (BasicMarkerAttributes) lastHighlit.getAttributes();
               final MarkerAttributes highliteAttrs = new BasicMarkerAttributes(lastAttrs);
               highliteAttrs.setMaterial(Material.WHITE);
               highliteAttrs.setOpacity(1d);
               highliteAttrs.setMarkerPixels(lastAttrs.getMarkerPixels() * 1.4);
               highliteAttrs.setMinMarkerSize(lastAttrs.getMinMarkerSize() * 1.4);
               lastHighlit.setAttributes(highliteAttrs);

               final Toponym toponym = lastHighlit.getToponym();
               final AnnotationLayer annotationsLayer = new AnnotationLayer();
               final Position pos = new Position(Angle.fromDegrees(toponym.getLatitude()),
                        Angle.fromDegrees(toponym.getLongitude()), 0);
               String sAnnotationText = toponym.getName();
               try {
                  final List<WikipediaArticle> list = WebService.wikipediaSearchForTitle(toponym.getName(),
                           toponym.getCountryCode());
                  for (int i = 0; i < list.size(); i++) {
                     final WikipediaArticle wiki = list.get(i);
                     if (wiki.getTitle().equalsIgnoreCase(toponym.getName())) {
                        sAnnotationText = "<p>\n<b><font color=\"#664400\">" + toponym.getName()
                                          + "</font></b><br />\n<br />\n<p>" + list.get(0).getSummary() + "</p>";
                        break;
                     }
                  }

               }
               catch (final Exception e) {
                  //ignore
               }
               final GlobeAnnotation annotation = new GlobeAnnotation(sAnnotationText, pos);
               annotation.getAttributes().setSize(new Dimension(200, 0));
               annotationsLayer.addAnnotation(annotation);
               annotationsLayer.setName(SEARCH_ANNOTATION_LAYER_NAME);
               //annotationsLayer.setMaxActiveAltitude(30000d);

               application.getLayerList().add(annotationsLayer);

            }
         }
      });

   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      // TODO Auto-generated method stub

   }

}
