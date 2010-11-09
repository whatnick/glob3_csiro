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

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GHomePositionModule;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GPanoramicLayerOLD;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.modules.GFullScreenModule;
import es.igosoftware.globe.modules.view.GAnaglyphViewerModule;
import es.igosoftware.globe.utils.GAreasEventsLayer;
import es.igosoftware.globe.view.GBasicOrbitView;
import es.igosoftware.globe.view.GBasicOrbitViewLimits;
import es.igosoftware.io.GPointsCloudFileLoader;
import es.igosoftware.loading.G3DModel;
import es.igosoftware.loading.GModelLoadException;
import es.igosoftware.loading.GObjLoader;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
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
import gov.nasa.worldwind.examples.sunlight.RectangularNormalTessellator;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;


public class GGlobeDemo
         extends
            GGlobeApplication {
   private static final long serialVersionUID = 1L;


   static {
      Configuration.setValue(AVKey.SCENE_CONTROLLER_CLASS_NAME, AnaglyphSceneController.class.getName());

      Configuration.setValue(AVKey.VIEW_CLASS_NAME, GBasicOrbitView.class.getName());

      Configuration.setValue(AVKey.TESSELLATOR_CLASS_NAME, RectangularNormalTessellator.class.getName());
   }


   private GGroupNode        _caceres3DRootNode;


   public GGlobeDemo() {
      super("en");

      final GBasicOrbitView view = (GBasicOrbitView) getWorldWindowGLCanvas().getView();
      //      view.setFieldOfView(Angle.fromDegrees(70));
      view.setDetectCollisions(false);
      view.setOrbitViewLimits(new GBasicOrbitViewLimits());
      view.getViewInputHandler().setStopOnFocusLost(false);
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

      //      layers.getLayerByName("MS Virtual Earth Aerial").setEnabled(true);

      //      layers.add(new OSMMapnikLayer());

      //      layers.add(new TerrainProfileLayer());

      //layers.add(new GPNOAWMSLayer(GPNOAWMSLayer.ImageFormat.JPEG));

      final GPositionRenderableLayer caceres3DLayer = new GPositionRenderableLayer("Caceres 3D Model", true);
      layers.add(caceres3DLayer);
      caceres3DLayer.setEnabled(false);

      GConcurrent.getDefaultExecutor().submit(new Runnable() {
         @Override
         public void run() {
            loadCaceres3DModel(caceres3DLayer);
         }
      });

      //      final GPositionRenderableLayer video3DLayer = new GPositionRenderableLayer("Videos", "video.png", true);
      //      video3DLayer.setEnabled(false);
      //
      //      video3DLayer.addPickListener(new GPositionRenderableLayer.PickListener() {
      //         @Override
      //         public void picked(final List<GPositionRenderableLayer.PickResult> result) {
      //            if (result.isEmpty()) {
      //               return;
      //            }
      //
      //            final GPositionRenderableLayer.PickResult nearestPickResult = result.get(0);
      //
      //            final Object userData = nearestPickResult.getUserData();
      //
      //            if (userData instanceof VideoData) {
      //               showVideo((VideoData) userData);
      //            }
      //         }
      //      });
      //
      //
      //      loadVideo3DModel(video3DLayer);
      //      layers.add(video3DLayer);


      final GAreasEventsLayer areasEventsLayer = new GAreasEventsLayer();
      layers.add(areasEventsLayer);

      final GPanoramicLayerOLD panoramicLayer = new GPanoramicLayerOLD(areasEventsLayer, "Sample Panoramic",
               "data/panoramics/example", 1000, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0),
               GElevationAnchor.SURFACE);
      //      panoramicLayer.setRenderWireframe(true);
      //      panoramicLayer.setRenderNormals(true);
      layers.add(panoramicLayer);
      panoramicLayer.setEnabled(false);


      //      final GVideoLayer videoLayer = new GVideoLayer("Video", "videos/example.avi", new Position(Angle.fromDegrees(39.4737),
      //               Angle.fromDegrees(-6.3710), 0), GElevationAnchor.SURFACE);
      //      layers.add(videoLayer);


      return layers;
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


   @Override
   public IGlobeModule[] getModules() {
      final Position homePosition = new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0);
      final Angle heading = Angle.ZERO;
      final Angle pitch = Angle.fromDegrees(90);
      final double homeElevation = 1000;
      final GHomePositionModule homePositionModule = new GHomePositionModule(homePosition, heading, pitch, homeElevation, false);

      final GPointsCloudFileLoader loader = new GPointsCloudFileLoader("data/pointsclouds");

      final GPointsCloudModule pointsCloudModule = new GPointsCloudModule(loader);

      return new IGlobeModule[] { homePositionModule, new GLayersManagerModule(), new GFullScreenModule(), pointsCloudModule,
               new GAnaglyphViewerModule(false), new GStatisticsModule() };
   }


   private void loadCaceres3DModel(final GPositionRenderableLayer layer) {
      try {


         final GModelData modelData = new GObjLoader().load("data/models/caceres3d.obj", true);
         hackCaceres3DModel(modelData);

         final G3DModel model = new G3DModel(modelData, true);
         final G3DModelNode caceres3DModelNode = new G3DModelNode("Caceres3D", GTransformationOrder.ROTATION_SCALE_TRANSLATION,
                  model);

         _caceres3DRootNode = new GGroupNode("Caceres3D root", GTransformationOrder.ROTATION_SCALE_TRANSLATION);
         _caceres3DRootNode.setHeading(-90);
         _caceres3DRootNode.addChild(caceres3DModelNode);

         layer.addNode(_caceres3DRootNode, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 17.7 + 7),
                  GElevationAnchor.SEA_LEVEL);
         //         layer.addNode(caceres3DRootNode, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0),
         //                  GElevationAnchor.SURFACE);
      }
      catch (final GModelLoadException e) {
         e.printStackTrace();
      }
   }


   //   private static class VideoData {
   //
   //      private final String _videoName;
   //
   //
   //      private VideoData(final String videoName) {
   //         _videoName = videoName;
   //      }
   //
   //   }


   //   private void hackVideo3DDModel(final GModelData model,
   //                                  final G3DModelNode modelNode,
   //                                  final String videoName) {
   //
   //      modelNode.setPickableBoundsType(G3DModelNode.PickableBoundsType.SPHERE);
   //
   //      for (final GModelMesh mesh : model.getMeshes()) {
   //         GMaterial material = mesh.getMaterial();
   //
   //         modelNode.addPickableMesh(mesh, new VideoData(videoName));
   //
   //         if (material == null) {
   //            material = new GMaterial("Video Material");
   //            mesh.setMaterial(material);
   //         }
   //
   //         material._specularColor = Color.WHITE;
   //         material._ambientColor = Color.RED;
   //         material._diffuseColor = new Color(0.5f, 0.5f, 0.5f, 0.8f);
   //         material._emissiveColor = new Color(0.3f, 0.3f, 0.3f);
   //      }
   //   }


   //   private void showVideo(final VideoData videoData) {
   //      System.out.println("SHOW VIDEO: " + videoData._videoName);
   //
   //      try {
   //         Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
   //
   //         final URL url = new File(videoData._videoName).toURI().toURL();
   //
   //         final Player player = Manager.createRealizedPlayer(url);
   //
   //
   //         final JFrame frame = new JFrame("Video Testing 0.1");
   //         frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   //         frame.setIconImage(GUtils.getImage("../globe/bitmaps/icons/video.png", getClass().getClassLoader()));
   //
   //
   //         final Component videoPlayer = player.getVisualComponent();
   //         if (videoPlayer != null) {
   //            frame.getContentPane().add(videoPlayer, BorderLayout.CENTER);
   //         }
   //
   //
   //         frame.addWindowListener(new WindowAdapter() {
   //            @Override
   //            public void windowClosed(final WindowEvent e) {
   //               System.out.println("Closing video...");
   //
   //               if (videoPlayer != null) {
   //                  frame.getContentPane().remove(videoPlayer);
   //               }
   //
   //               player.stop();
   //               player.deallocate();
   //               player.close();
   //            }
   //         });
   //
   //         frame.setSize(640, 480);
   //         frame.setVisible(true);
   //
   //         player.start();
   //      }
   //      catch (final IOException e) {
   //         e.printStackTrace();
   //      }
   //      catch (final CannotRealizeException e) {
   //         e.printStackTrace();
   //      }
   //      catch (final NoPlayerException e) {
   //         e.printStackTrace();
   //      }
   //
   //   }


   //   private void loadVideo3DModel(final GPositionRenderableLayer layer) {
   //      try {
   //
   //
   //         final GModelData modelData = new GObjLoader().load("../globe/models/video.obj", true);
   //
   //         final G3DModel model = new G3DModel(modelData, true);
   //         final G3DModelNode video3DModelNode = new G3DModelNode("Video3D", GTransformationOrder.ROTATION_SCALE_TRANSLATION, model);
   //
   //         hackVideo3DDModel(modelData, video3DModelNode, "videos/example.avi");
   //
   //         final GGroupNode caceres3DRootNode = new GGroupNode("Video3D Root", GTransformationOrder.ROTATION_SCALE_TRANSLATION);
   //         //         caceres3DRootNode.setHeading(-90);
   //         caceres3DRootNode.addChild(video3DModelNode);
   //
   //         layer.addNode(caceres3DRootNode, new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 0),
   //                  GElevationAnchor.SURFACE);
   //      }
   //      catch (final GModelLoadException e) {
   //         e.printStackTrace();
   //      }
   //   }


   public static void main(final String[] args) {


      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {

            final File dataDirectory = new File("data");
            if (!dataDirectory.exists()) {
               JOptionPane.showMessageDialog(null, "Can't find the directory data\n\n"
                                                   + "- Go to https://sourceforge.net/projects/glob3/files_beta/globe-demo/\n"
                                                   + "- Download the file data.zip\n" + "- Uncompress the file in the directory "
                                                   + new File("data").getAbsolutePath(), "ERROR", JOptionPane.ERROR_MESSAGE);
               System.exit(1);
            }


            new GGlobeDemo().openInFrame();
         }
      });
   }


}
