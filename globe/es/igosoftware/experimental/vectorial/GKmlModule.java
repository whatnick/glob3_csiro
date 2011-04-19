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
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
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


public class GKmlModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {


   @Override
   public String getName() {
      return "KML Vectorial Module";
   }


   @Override
   public String getVersion() {
      return "experimental";
   }


   @Override
   public String getDescription() {
      return "Module for handling KML vectorial layers";
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
      return GLayerInfo.createFromNames("Vectorial layer");
   }


   @Override
   public GVectorial2DLayer addNewLayer(final IGlobeApplication application,
                                        final ILayerInfo layerInfo) {


      final JFileChooser fileChooser = createFileChooser(application);

      final int returnVal = fileChooser.showOpenDialog(application.getFrame());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final File selectedFile = fileChooser.getSelectedFile();
         if (selectedFile != null) {
            parseFile(selectedFile, application);
         }
      }

      return null;
   }


   private void parseFile(final File file,
                          final IGlobeApplication application) {

      final Thread worker = new Thread("KML vectorial layer loader") {
         @Override
         public void run() {
            // TODO: read projection or ask user
            final GProjection projection = GProjection.EPSG_4326;

            try {
               final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features = GKmlLoader.readFeatures(
                        file, projection);

               final GVectorial2DLayer layer = new GVectorial2DLayer(file.getName(), features);
               //               layer.setShowExtents(true);
               application.addLayer(layer);

               layer.doDefaultAction(application);
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


   private JFileChooser createFileChooser(final IGlobeApplication application) {
      final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")) {
         private static final long serialVersionUID = 1L;


         @Override
         public String getTypeDescription(final File f) {
            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("kml")) {
               return "KML File";
            }

            return super.getDescription(f);
         }


         @Override
         public Icon getIcon(final File f) {
            if (f.isDirectory()) {
               return super.getIcon(f);
            }

            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("kml")) {
               return application.getSmallIcon(GFileName.relative("vectorial.png"));
            }

            return super.getIcon(f);
         }
      };

      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(new GGenericFileFilter("kml", "KML files (*.kml)"));

      return fileChooser;
   }

}
