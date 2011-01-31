

package es.igosoftware.experimental.wms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GLogger;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerCapabilities;
import gov.nasa.worldwind.ogc.wms.WMSLayerStyle;
import gov.nasa.worldwind.wms.CapabilitiesRequest;


public class GWMSServersManager {

   private final static GLogger logger   = GLogger.instance();
   private final static String  fileName = "data/wmsservers.dat";


   //private final Set<GWMSServerData> _serversSet = new HashSet();


   /**
    * 
    */
   public GWMSServersManager() {
      super();
      // TODO load servers list from file
   }


   //   public static WMSCapabilities getCapabilitiesforServer(final String serverName) {
   //      return retrieveCapabilities(GWMSDefaultServers.getURL(serverName));
   //   }


   public static WMSCapabilities getCapabilitiesforServer(final GWMSServerData server) {
      return retrieveCapabilities(server.getURL());
   }


   private static WMSCapabilities retrieveCapabilities(final String uRL) {
      try {
         final CapabilitiesRequest request = new CapabilitiesRequest(new URI(uRL));
         logger.info("GetCapabilities URI: " + request.getUri());
         System.out.println();
         final WMSCapabilities caps = new WMSCapabilities(request);
         caps.parse();
         return caps;

      }
      catch (final URISyntaxException e) {
         e.printStackTrace();
      }
      catch (final MalformedURLException e) {
         e.printStackTrace();
      }
      catch (final XMLStreamException e) {
         e.printStackTrace();
      }

      return null;
   }


   public static GWMSLayerData[] getLayersForServer(final GWMSServerData server) {

      final WMSCapabilities caps = retrieveCapabilities(server.getURL());
      if (caps != null) {
         try {
            caps.parse();
            //System.out.println(caps.getCapabilityInformation().toString());
            logger.info(caps.getServiceInformation().toString());
            final List<WMSLayerCapabilities> layersCapsList = caps.getNamedLayers();
            final GWMSLayerData[] layersDataList = new GWMSLayerData[layersCapsList.size()];

            int index = 0;
            final StringBuffer layers = new StringBuffer("LAYERS: ");
            for (final WMSLayerCapabilities WMSLayer : layersCapsList) {
               layers.append(WMSLayer.getName());
               if (layersCapsList.indexOf(WMSLayer) < layersCapsList.size() - 1) {
                  layers.append(", ");
               }

               final List<GWMSLayerStyleData> stylesList = new ArrayList<GWMSLayerStyleData>();
               final Set<WMSLayerStyle> stylesSet = WMSLayer.getStyles();

               for (final Object element : stylesSet) {
                  final WMSLayerStyle layerStyle = (WMSLayerStyle) element;
                  final GWMSLayerStyleData styleData = new GWMSLayerStyleData(layerStyle.getName(), layerStyle.getTitle(),
                           layerStyle.getStyleAbstract());
                  stylesList.add(styleData);
               }

               layersDataList[index] = new GWMSLayerData(Integer.toString(index), WMSLayer.getName(), WMSLayer.getTitle(),
                        WMSLayer.getLayerAbstract(), stylesList);
               index++;
            }
            logger.info(layers.toString());
            return layersDataList;
         }
         catch (final XMLStreamException e) {
            e.printStackTrace();
         }
      }

      return null;

   }


   public static String[] getWMSServersNames(final Map<String, GWMSServerData> serversMap) {

      final String[] serversNames = new String[serversMap.size()];
      final Collection<GWMSServerData> serversCollection = serversMap.values();

      int index = 0;
      for (final GWMSServerData serverData : serversCollection) {
         serversNames[index] = serverData.getName();
         index++;
      }

      return serversNames;
   }


   public static Map<String, GWMSServerData> getWMSAvailableServers() {

      final Map<String, GWMSServerData> serversMap = new HashMap<String, GWMSServerData>();
      final List<GWMSServerData> serversList = loadWMSServers();

      for (final GWMSServerData server : serversList) {
         serversMap.put(server.getName(), server);
      }

      return serversMap;
   }


   protected static List<GWMSServerData> loadWMSServers() {

      logger.info("Loading WMS server list..");

      final File serversFile = new File(fileName);
      final List<GWMSServerData> emptyList = GCollections.createList();

      if (serversFile.exists()) {

         if (serversFile.length() > 0) {

            ObjectInputStream input = null;
            try {
               input = new ObjectInputStream(new FileInputStream(fileName));
               @SuppressWarnings("unchecked")
               final List<GWMSServerData> serversList = (List<GWMSServerData>) input.readObject();

               return serversList;

            }
            catch (final IOException e) {
               logger.info("IOException !");
               e.printStackTrace();
            }
            catch (final ClassNotFoundException e) {
               logger.info("Class Not found Exception !");
               e.printStackTrace();
            }
            finally {
               GIOUtils.gentlyClose(input);
            }
         }

         return emptyList;

      }

      try {
         serversFile.createNewFile();
      }
      catch (final IOException e) {
         System.out.println("Invalid file: " + fileName);
         e.printStackTrace();
      }

      return emptyList;

   }


   public static void saveWMSServers(final Map<String, GWMSServerData> serversMap) {

      logger.info("Saving " + serversMap.size() + " WMS servers");

      final Collection<GWMSServerData> serversCollection = serversMap.values();
      final List<GWMSServerData> serversList = new ArrayList<GWMSServerData>(serversCollection.size());
      serversList.addAll(serversCollection);

      ObjectOutputStream output = null;
      try {
         output = new ObjectOutputStream(new FileOutputStream(fileName));
         output.writeObject(serversList);
         logger.info("Saved!");
      }
      catch (final IOException e) {
         logger.info("IOException !");
         e.printStackTrace();
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }
}
