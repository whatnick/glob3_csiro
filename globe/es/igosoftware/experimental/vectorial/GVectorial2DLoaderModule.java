/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.experimental.vectorial;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.wizard.JWizard;
import org.xml.sax.SAXException;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GPair;


public class GVectorial2DLoaderModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {


   @Override
   public String getName() {
      return "Globe Vectorial Module";
   }


   @Override
   public String getVersion() {
      return "experimental";
   }


   @Override
   public String getDescription() {
      return "Module for handling different vectorial formats";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {

      return null;
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {


      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {

   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeApplication application) {
      return GLayerInfo.createFromNames("SHP layer", "Postgis layer", "KML layer", "GML2 layer", "GML3 layer");
   }


   @Override
   public GVectorial2DLayer addNewLayer(final IGlobeApplication application,
                                        final ILayerInfo layerInfo) {

      if (layerInfo.getName().equalsIgnoreCase("SHP layer") || layerInfo.getName().equalsIgnoreCase("KML layer")
          || layerInfo.getName().equalsIgnoreCase("GML2 layer") || layerInfo.getName().equalsIgnoreCase("GML3 layer")) {

         final JFileChooser fileChooser = createFileChooser(application, layerInfo);

         final int returnVal = fileChooser.showOpenDialog(application.getFrame());
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
               parseFile(selectedFile, application, layerInfo);
            }
         }
      }
      else if (layerInfo.getName().equalsIgnoreCase("Postgis layer")) {
         connectoToDataSource(application, layerInfo);
      }

      return null;
   }


   private void parseFile(final File file,
                          final IGlobeApplication application,
                          final ILayerInfo layerInfo) {

      final Thread worker = new Thread("Globe vectorial layer loader") {
         @Override
         public void run() {
            // TODO: read projection or ask user
            final GProjection projection = GProjection.EPSG_4326;

            try {

               //IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = null;
               Collection<IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> featuresCollection = new ArrayList<IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

               if (layerInfo.getName().equalsIgnoreCase("SHP layer")) {
                  final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GShapeLoader.readFeatures(
                           file, projection);
                  featuresCollection.add(features);
               }
               else if (layerInfo.getName().equalsIgnoreCase("KML layer")) {
                  final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GKmlLoader.readFeatures(
                           file, projection);
                  featuresCollection.add(features);
               }
               else if (layerInfo.getName().equalsIgnoreCase("GML2 layer")) {
                  featuresCollection = GGmlLoader.readGml2Features(file, projection);
               }
               else if (layerInfo.getName().equalsIgnoreCase("GML3 layer")) {
                  featuresCollection = GGmlLoader.readGml3Features(file, projection);
               }

               int index = 0;
               for (final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> fc : featuresCollection) {
                  index++;
                  String layerName = file.getName();
                  if (featuresCollection.size() > 1) {
                     layerName = layerName + " (" + index + ")";
                  }
                  final GVectorial2DLayer layer = new GVectorial2DLayer(layerName, fc);
                  //               layer.setShowExtents(true);
                  application.addLayer(layer);

                  layer.doDefaultAction(application);
               }

            }
            catch (final IOException e) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     JOptionPane.showMessageDialog(application.getFrame(), "Error opening " + file.getAbsolutePath() + "\n\n "
                                                                           + e.getLocalizedMessage(), "Error",
                              JOptionPane.ERROR_MESSAGE);
                  }
               });
            }
            catch (final SAXException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            catch (final ParserConfigurationException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      };

      worker.setDaemon(true);
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.start();
   }


   private JFileChooser createFileChooser(final IGlobeApplication application,
                                          final ILayerInfo layerInfo) {

      final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")) {
         private static final long serialVersionUID = 1L;


         @Override
         public String getTypeDescription(final File f) {
            final String extension = GIOUtils.getExtension(f);

            if (extension != null) {
               if (extension.toLowerCase().equals("shp")) {
                  return "SHP File";
               }
               if (extension.toLowerCase().equals("kml")) {
                  return "KML File";
               }
               if (extension.toLowerCase().equals("gml")) {
                  return "GML File";
               }
            }

            return super.getDescription(f);
         }


         @Override
         public Icon getIcon(final File f) {
            if (f.isDirectory()) {
               return super.getIcon(f);
            }

            final String extension = GIOUtils.getExtension(f);

            if (extension != null) {
               if (extension.toLowerCase().equals("shp") || extension.toLowerCase().equals("kml")
                   || extension.toLowerCase().equals("gml")) {
                  return application.getSmallIcon(GFileName.relative("vectorial.png"));
               }
            }

            return super.getIcon(f);
         }
      };

      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (layerInfo.getName().equalsIgnoreCase("SHP layer")) {
         fileChooser.setFileFilter(new GGenericFileFilter("shp", "SHP files (*.shp)"));
      }
      else if (layerInfo.getName().equalsIgnoreCase("KML layer")) {
         fileChooser.setFileFilter(new GGenericFileFilter("kml", "KML files (*.kml)"));
      }
      else if (layerInfo.getName().equalsIgnoreCase("GML2 layer") || layerInfo.getName().equalsIgnoreCase("GML3 layer")) {
         fileChooser.setFileFilter(new GGenericFileFilter("gml", "GML files (*.gml)"));
      }

      return fileChooser;
   }


   private void connectoToDataSource(final IGlobeApplication application,
                                     final ILayerInfo layerInfo) {

      //// TODO: read projection or ask user
      final GProjection projection = GProjection.EPSG_4326;

      final JDataStoreWizard wizard = new JDataStoreWizard(new PostgisNGDataStoreFactory());
      //final JDataStoreWizard wizard = new JDataStoreWizard(new OracleNGDataStoreFactory());
      //final JDataStoreWizard wizard = new JDataStoreWizard();

      //wizard.setNextEnabled(true);
      //wizard.setBackEnabled(true);
      //wizard.getContentPane().setSize(300, 200);
      final int result = wizard.showModalDialog();

      if (result == JWizard.FINISH) {

         final Map<String, Object> connectionParameters = wizard.getConnectionParameters();

         try {

            final DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);

            if (dataStore == null) {
               JOptionPane.showMessageDialog(application.getFrame(), "Could not connect. Check parameters", "Connection error",
                        JOptionPane.ERROR_MESSAGE);
               return;
            }

            final String[] layerList = dataStore.getTypeNames();

            //final LayerSelectionDialog layerDialog = new LayerSelectionDialog(application, "PostGIS layer selection", layerList);
            //final String selectedLayer = layerDialog.showLayerSelectionDialog();
            String selectedLayer = null;

            if (layerList.length == 0) {
               JOptionPane.showMessageDialog(application.getFrame(), "No data available", "Data Storage warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            else if (layerList.length == 1) {
               selectedLayer = layerList[0];
            }
            else {

               selectedLayer = (String) JOptionPane.showInputDialog(application.getFrame(),
                        application.getTranslation("Select a layer"), application.getTranslation("Datasource layer selection"),
                        JOptionPane.PLAIN_MESSAGE, application.getIcon(GFileName.relative("new-vectorial.png"), 24, 24),
                        layerList, null);
            }

            final String layerName = selectedLayer;

            final Thread worker = new Thread("Globe vectorial layer loader") {
               @Override
               public void run() {

                  try {

                     IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = null;

                     if (layerInfo.getName().equalsIgnoreCase("Postgis layer")) {
                        features = GPostgisLoader.readFeatures(dataStore, layerName, projection);
                     }

                     final GVectorial2DLayer layer = new GVectorial2DLayer(layerName, features);
                     //               layer.setShowExtents(true);
                     application.getLayerList().add(layer);

                     layer.doDefaultAction(application);
                  }
                  catch (final IOException e) {
                     SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           JOptionPane.showMessageDialog(application.getFrame(),
                                    "Error opening data storage" + "\n\n " + e.getLocalizedMessage(), "Data storage error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                     });
                  }
                  catch (final Exception e) {
                     SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           JOptionPane.showMessageDialog(application.getFrame(),
                                    "Error opening data storage" + "\n\n " + e.getLocalizedMessage(), "Data storage error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                     });
                  }

               }
            };

            worker.setDaemon(true);
            worker.setPriority(Thread.MIN_PRIORITY);
            worker.start();

         }
         catch (final IOException e) {
            JOptionPane.showMessageDialog(application.getFrame(), "Could not connect. Check parameters", "Connection error",
                     JOptionPane.ERROR_MESSAGE);
            //e.printStackTrace();
         }

      }
   }
}
