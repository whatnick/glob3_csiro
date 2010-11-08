package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.SurfaceShapeLayer;
import gov.nasa.worldwind.render.Renderable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GSocialLayer
         extends
            SurfaceShapeLayer
         implements
            IGlobeVectorLayer {

   private final ArrayList<GSocialObject> _socialObjects;
   private final Feature[]                _features;
   private final GPointsRenderer          _renderer;

   public static final String             TWITTER_ANNOTATION_LAYER_NAME = "TWITTER_ANNOTATION_LAYER_NAME";


   //   private static SearchResultMarker      lastHighlit;
   //   private static BasicMarkerAttributes   lastAttrs;


   public GSocialLayer(final ArrayList<GSocialObject> socialObjects) {

      super(/*"#twitter", true*/);
      _socialObjects = socialObjects;
      _features = getFeatures();
      _renderer = new GPointsRenderer(_features);
      setName("Twitter layer");

   }


   @Override
   public Feature[] getFeatures() {

      if (_features != null) {
         return _features;
      }

      final GeometryFactory gf = new GeometryFactory();

      final Feature[] features = new Feature[_socialObjects.size()];

      for (int i = 0; i < features.length; i++) {
         final Point pt = gf.createPoint(_socialObjects.get(i).getCoords());
         features[i] = new Feature(pt, new String[] { "Manuel de la calle"/* _socialObjects.get(i).getUser().getName()*/,
                  _socialObjects.get(i).getMessage() });
      }

      return features;
   }


   @Override
   public GField[] getFields() {

      return new GField[] { new GField("User", String.class), new GField("Message", String.class) };

   }


   @Override
   public GVectorLayerType getShapeType() {

      return GVectorLayerType.POINT;

   }


   @Override
   public Sector getExtent() {

      Sector sector = Sector.EMPTY_SECTOR;

      for (int i = 0; i < _socialObjects.size(); i++) {
         final GSocialObject socialObject = _socialObjects.get(i);
         final double x = socialObject.getCoords().x;
         final double y = socialObject.getCoords().y;
         final Sector newSector = new Sector(Angle.fromDegrees(y), Angle.fromDegrees(y), Angle.fromDegrees(x),
                  Angle.fromDegrees(x));

         sector = sector.union(newSector);
      }

      return sector;

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
   public final void redraw() {

      this.removeAllRenderables();

      //m_Renderer.calculateExtremeValues(m_Features);

      try {
         for (final Feature element2 : _features) {
            final Renderable[] ren = _renderer.getRenderables(element2, getProjection());
            for (final Renderable element : ren) {
               this.addRenderable(element);
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }


      //this.removeAllRenderables();

      /*try {

         final GModelData modelData = new GObjLoader().load("models/tdetwitter.obj", true);

         for (final GModelMesh mesh : modelData.getMeshes()) {
            final GMaterial m = mesh.getMaterial();
            if (m != null) {
               final Color color = new Color(m._diffuseColor.getRed() / 255f, m._diffuseColor.getGreen() / 255f,
                        m._diffuseColor.getBlue() / 255f, 0.75f);
               m._diffuseColor = color;

            }
         }

         final G3DModel model = new G3DModel(modelData, true);


         final GGroupNode root = new GGroupNode("root", GTransformationOrder.ROTATION_SCALE_TRANSLATION);

         root.setScale(50);

         root.addChild(new G3DModelNode("", GTransformationOrder.ROTATION_SCALE_TRANSLATION, model));

         for (int i = 0; i < _socialObjects.size(); i++) {
            final GSocialObject socialObject = _socialObjects.get(i);
            final double x = socialObject.getCoords().x;
            final double y = socialObject.getCoords().y;
            this.addNode(root, new Position(Angle.fromDegrees(y), Angle.fromDegrees(x), 50), GElevationAnchor.SURFACE);
         }

      }
      catch (final GModelLoadException e) {
         e.printStackTrace();
      }*/

   }


   @Override
   public void setProjection(final GProjection proj) {
      //Projection is fixed and cannot be changed
   }


   @Override
   public GVectorRenderer getRenderer() {

      return _renderer;

   }


   public static void addLayerListener(final IGlobeApplication application) {

      /*application.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {

         public void selected(final SelectEvent event) {

            if (event.getTopObject() instanceof GlobeAnnotation) {
               return;
            }

            if ((lastHighlit != null) && ((event.getTopObject() == null) || !event.getTopObject().equals(lastHighlit))) {
               //lastHighlit.setAttributes(lastAttrs);
               lastHighlit = null;
               final Layer layer = application.getLayerList().getLayerByName(TWITTER_ANNOTATION_LAYER_NAME);
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

            if ((lastHighlit == null) && (event.getTopObject() instanceof Marker)) {
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
               annotationsLayer.setName(TWITTER_ANNOTATION_LAYER_NAME);
               //annotationsLayer.setMaxActiveAltitude(30000d);

               application.getLayerList().add(annotationsLayer);

            }
         }
      });*/

   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {

      application.zoomToSector(getExtent());

   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application) {
      // TODO Auto-generated method stub
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      // TODO Auto-generated method stub
      return null;
   }


}
