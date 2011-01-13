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


package es.igosoftware.globe.demo;


import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.experimental.ndimensional.G3DImageMultidimensionalData;
import es.igosoftware.experimental.ndimensional.GMultidimensionalDataModule;
import es.igosoftware.experimental.ndimensional.IMultidimensionalData;
import es.igosoftware.experimental.vectorial.GPolygon2DModule;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GHomePositionModule;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.modules.GFullScreenModule;
import es.igosoftware.globe.modules.view.GAnaglyphViewerModule;
import es.igosoftware.globe.modules.view.GFlatWorldModule;
import es.igosoftware.globe.modules.view.GShowLatLonGraticuleModule;
import es.igosoftware.globe.modules.view.GShowUTMGraticuleModule;
import es.igosoftware.globe.modules.view.ShowMeasureTool;
import es.igosoftware.globe.view.customView.GCustomView;
import es.igosoftware.io.GPointsCloudFileLoader;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.loading.GModelLoadException;
import es.igosoftware.loading.GObjLoader;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.panoramic.GPanoramicLayer;
import es.igosoftware.pointscloud.GPointsCloudModule;
import es.igosoftware.scenegraph.G3DModelNode;
import es.igosoftware.scenegraph.GElevationAnchor;
import es.igosoftware.scenegraph.GGroupNode;
import es.igosoftware.scenegraph.GPositionRenderableLayer;
import es.igosoftware.scenegraph.GTransformationOrder;
import es.igosoftware.util.GUtils;
import gov.nasa.worldwind.AnaglyphSceneController;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;


public class GGlobeDemo
         extends
            GGlobeApplication {
   private static final long              serialVersionUID = 1L;


   static {
      Configuration.setValue(AVKey.SCENE_CONTROLLER_CLASS_NAME, AnaglyphSceneController.class.getName());
   }


   private static IMultidimensionalData[] _multidimentionaldata;


   public GGlobeDemo() {
      super("en");
   }


   @Override
   public String getApplicationName() {
      return "Globe Demo";
   }


   @Override
   public String getApplicationVersion() {
      return "0.1";
   }


   @Override
   public Image getImageIcon() {
      return GUtils.getImage("globe-icon.png", getClass().getClassLoader());
   }


   @Override
   protected LayerList getDefaultLayers() {
      final LayerList layers = super.getDefaultLayers();

      // layers.getLayerByName("MS Virtual Earth Aerial").setEnabled(true);

      //      layers.add(new OSMMapnikLayer());

      // layers.add(new TerrainProfileLayer());

      // layers.add(new GPNOAWMSLayer(GPNOAWMSLayer.ImageFormat.JPEG));


      final GPositionRenderableLayer caceres3DLayer = createCaceres3DModelLayer();
      layers.add(caceres3DLayer);


      final GPanoramicLayer panoramicLayer = createPanoramicLayer();
      layers.add(panoramicLayer);

      //      final IconLayer iconLayer = new IconLayer();
      //      final Position iconPos = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3910), 0.0);
      //      final UserFacingIcon icon = new UserFacingIcon(
      //               "/home/oliver/Desktop/GLOB3-Repository/glob3/media/logo/bitmaps/logo32x32.png", iconPos);
      //      iconLayer.addIcon(icon);
      //      layers.add(iconLayer);

      //      createVectorialLayer(layers);


      return layers;
   }


   private GPositionRenderableLayer createCaceres3DModelLayer() {
      final GPositionRenderableLayer caceres3DLayer = new GPositionRenderableLayer("Caceres 3D Model", true);
      //      caceres3DLayer.setEnabled(false);

      GConcurrent.getDefaultExecutor().submit(new Runnable() {
         @Override
         public void run() {
            loadCaceres3DModel(caceres3DLayer);
            //            caceres3DLayer.setEnabled(true);
         }
      });

      return caceres3DLayer;
   }


   private GPanoramicLayer createPanoramicLayer() {
      final GPanoramicLayer panoramicLayer = new GPanoramicLayer("Panoramics");

      panoramicLayer.addPanoramic(new GPanoramic(panoramicLayer, "Sample Panoramic", "data/panoramics/example", 500,
               new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3910), 0)));

      // panoramicLayer.setEnabled(false);

      panoramicLayer.addPickListener(new GPanoramicLayer.PickListener() {
         @Override
         public void picked(final GPanoramic pickedPanoramic) {
            if (pickedPanoramic != null) {
               panoramicLayer.enterPanoramic(pickedPanoramic, (GCustomView) getView());
            }
         }
      });
      return panoramicLayer;
   }


   private void hackCaceres3DModel(final GModelData model) {
      for (final GModelMesh mesh : model.getMeshes()) {
         GMaterial material = mesh.getMaterial();

         if (material == null) {
            material = new GMaterial("");
            material._diffuseColor = Color.WHITE;
            mesh.setMaterial(material);
         }
         else {
            if (material.getTextureFileName() != null) {
               material._diffuseColor = Color.WHITE;
            }
         }

         material._emissiveColor = new Color(0.2f, 0.2f, 0.2f);
      }
   }


   //   private void createVectorialLayer(final LayerList layers) {
   //
   //      // final String fileName = "data/shp/world.shp";
   //      // final GProjection projection = GProjection.EPSG_4326;
   //      // final boolean convertToRadians = true;
   //
   //      // final String fileName = "data/shp/S_Naturales_forestales.shp";
   //      // final GProjection projection = GProjection.EPSG_23030;
   //      // final boolean convertToRadians = false;
   //
   //      final String fileName = "data/shp/S_Naturales_forestales_WG84.shp";
   //      final GProjection projection = GProjection.EPSG_4326;
   //      final boolean convertToRadians = true;
   //
   //      // final String fileName = "data/shp/parcelasEdificadas.shp";
   //      // final GProjection projection = GProjection.EPSG_23029;
   //      // final boolean convertToRadians = false;
   //
   //
   //      if (!new File(fileName).exists()) {
   //         logWarning("Can't find file " + fileName);
   //         return;
   //      }
   //
   //      final Thread worker = new Thread() {
   //         @Override
   //         public void run() {
   //            try {
   //               final List<IPolygon2D<?>> polygons = GShapeLoader.readPolygons(fileName, convertToRadians);
   //
   //               final File file = new File(fileName);
   //               final GPolygon2DLayer layer = new GPolygon2DLayer(file.getName(), file.getName()
   //                                                                                 + Long.toHexString(file.lastModified()),
   //                        polygons, projection);
   //               layer.setShowExtents(true);
   //               layers.add(layer);
   //            }
   //            catch (final IOException e) {
   //               e.printStackTrace();
   //            }
   //         }
   //      };
   //      worker.setPriority(Thread.MIN_PRIORITY);
   //      worker.setDaemon(true);
   //      worker.start();
   //   }


   @Override
   public IGlobeModule[] getModules() {
      final Position homePosition = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0);
      final Angle heading = Angle.ZERO;
      final Angle pitch = Angle.fromDegrees(45);
      final double homeElevation = 2000;
      final GHomePositionModule homePositionModule = new GHomePositionModule(homePosition, heading, pitch, homeElevation, true);

      final GPointsCloudFileLoader loader = new GPointsCloudFileLoader("data/pointsclouds");
      //      final GPointsCloudFileLoader loader = new GPointsCloudFileLoader("/home/dgd/Escritorio/LOD/");


      final GPointsCloudModule pointsCloudModule = new GPointsCloudModule(loader);

      return new IGlobeModule[] { homePositionModule, new GLayersManagerModule(), new GPolygon2DModule(),
               new GFullScreenModule(), pointsCloudModule, new GAnaglyphViewerModule(false), new GStatisticsModule(),
               new GFlatWorldModule(), new GShowLatLonGraticuleModule(), new GShowUTMGraticuleModule(),
               new GMultidimensionalDataModule(_multidimentionaldata), new ShowMeasureTool() };
   }


   private void loadCaceres3DModel(final GPositionRenderableLayer layer) {
      try {

         final GModelData modelData = new GObjLoader().load("data/models/caceres3d.obj", true);
         hackCaceres3DModel(modelData);

         final G3DModel model = new G3DModel(modelData, true);
         final G3DModelNode caceres3DModelNode = new G3DModelNode("Caceres3D", GTransformationOrder.ROTATION_SCALE_TRANSLATION,
                  model);

         final GGroupNode caceres3DRootNode = new GGroupNode("Caceres3D root", GTransformationOrder.ROTATION_SCALE_TRANSLATION);
         caceres3DRootNode.setHeading(-90);
         caceres3DRootNode.addChild(caceres3DModelNode);

         layer.addNode(caceres3DRootNode, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 24.7),
                  GElevationAnchor.SEA_LEVEL);
      }
      catch (final GModelLoadException e) {
         e.printStackTrace();
      }
   }


   private static void checkDataDirectory() {
      final File dataDirectory = new File("data");
      if (!dataDirectory.exists()) {
         final String message = "Can't find the directory data\n\n"
                                + "- Go to http://sourceforge.net/projects/glob3/files_beta/globe-demo/\n"
                                + "- Download the file data.zip\n" + "- Uncompress the file in the directory "
                                + new File("data").getAbsolutePath();
         System.out.println(message);
         JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }
   }


   private static void loadMultidimensionalData() {
      try {
         //         final String[] valueVariablesNames = new String[] { "salt", "temp", "dens" };
         //         final GNetCDFMultidimentionalData.VectorVariable[] vectorVariables = new GNetCDFMultidimentionalData.VectorVariable[] { new GNetCDFMultidimentionalData.VectorVariable(
         //                  "Water Velocity", "u", "v") };
         //
         //         final IMultidimensionalData cfData = new GNetCDFMultidimentionalData("data/mackenzie_depth_out_cf.nc", "longitude",
         //                  "latitude", "zc", "eta", valueVariablesNames, vectorVariables, "n", true, true);


         final Position headPosition = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0);
         final IMultidimensionalData headData = new G3DImageMultidimensionalData("Mr Head", "data/cthead-8bit", ".png",
                  headPosition, 10, 10, 20);


         //         _multidimentionaldata = new IMultidimensionalData[] { cfData, headData };
         _multidimentionaldata = new IMultidimensionalData[] { headData };
      }
      catch (final IOException e) {
         e.printStackTrace();
         System.exit(1);
      }
   }


   @Override
   public void init() {
      super.init();

      checkDataDirectory();

      loadMultidimensionalData();
   }


   public static void main(final String[] args) {

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {

            checkDataDirectory();

            loadMultidimensionalData();

            new GGlobeDemo().openInFrame();
         }
      });
   }

}
