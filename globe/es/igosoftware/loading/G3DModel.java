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


package es.igosoftware.loading;


import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.mutability.GMutableAbstract;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.loading.modelparts.GFace;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.scenegraph.G3DModelNode;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GLogger;
import es.igosoftware.utils.GTexturesCache;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;


public class G3DModel {

   private static abstract class RenderUnit
            extends
               GMutableAbstract<RenderUnit>
            implements
               IMutable.ChangeListener {

      protected final List<GModelMesh> _meshes;


      private RenderUnit(final List<GModelMesh> meshes) {
         super();
         _meshes = meshes;

         for (final GModelMesh mesh : _meshes) {
            mesh.addChangeListener(this);
         }
      }


      private List<GModelMesh> getMeshes() {
         return _meshes;
      }


      protected abstract void prepareGL(final DrawContext dc);


      protected abstract void prepareMeshGL(final DrawContext dc,
                                            final GModelMesh mesh);


      protected abstract void restoreMeshGL(final DrawContext dc,
                                            final GModelMesh mesh);


      protected abstract void restoreGL(final DrawContext dc);


      @Override
      public void mutableChanged() {
         changed();
      }

   }


   private static final class MaterialRenderUnit
            extends
               RenderUnit {
      private final GMaterial _material;


      private MaterialRenderUnit(final GMaterial material,
                                 final List<GModelMesh> meshes) {
         super(meshes);
         _material = material;
      }


      @Override
      protected void prepareGL(final DrawContext dc) {
         applyMaterial(dc, _material);
      }


      @Override
      protected void prepareMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
      }


      @Override
      protected void restoreMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
      }


      @Override
      protected void restoreGL(final DrawContext dc) {
      }


      @Override
      public String toString() {
         return "MaterialRenderUnit [material=" + _material._name + ", meshes=" + _meshes.size() + "]";
      }
   }


   private static final class SlowRenderUnit
            extends
               RenderUnit {
      private Texture _texture;


      private SlowRenderUnit(final List<GModelMesh> meshes) {
         super(meshes);
      }


      @Override
      protected void prepareGL(final DrawContext dc) {
      }


      @Override
      protected void prepareMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
         _texture = getTexture(mesh);
         applyTexture(dc, _texture);

         applyMaterial(dc, mesh.getMaterial());
      }


      @Override
      protected void restoreMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
         restoreTexture(dc, _texture);
         _texture = null;
      }


      @Override
      protected void restoreGL(final DrawContext dc) {
      }


      @Override
      public String toString() {
         return "SlowRenderUnit [meshes=" + _meshes.size() + "]";
      }
   }


   private final static class TextureRenderUnit
            extends
               RenderUnit {
      private final GPair<URL, Boolean> _textureData;
      private Texture                   _texture;


      private TextureRenderUnit(final GPair<URL, Boolean> textureData,
                                final List<GModelMesh> meshes) {
         super(meshes);
         _textureData = textureData;
      }


      @Override
      protected void prepareGL(final DrawContext dc) {
         _texture = GTexturesCache.getTexture(_textureData._first, _textureData._second);
         applyTexture(dc, _texture);
      }


      @Override
      protected void prepareMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
         applyMaterial(dc, mesh.getMaterial());
      }


      @Override
      protected void restoreGL(final DrawContext dc) {
         restoreTexture(dc, _texture);
         _texture = null;
      }


      @Override
      protected void restoreMeshGL(final DrawContext dc,
                                   final GModelMesh mesh) {
      }


      @Override
      public String toString() {
         return "TextureRenderUnit [textureData=" + _textureData + ", meshes=" + _meshes.size() + "]";
      }

   }


   private class MeshOrderedRenderable
            implements
               OrderedRenderable {

      private final GModelMesh   _mesh;
      private final double       _distanceFromEye;
      private final float[]      _modelViewMatrixArray;
      private final boolean      _hasScaleTransformation;
      private final G3DModelNode _modelNode;


      public MeshOrderedRenderable(final GModelMesh mesh,
                                   final float[] modelViewMatrixArray,
                                   final boolean hasScaleTransformation,
                                   final G3DModelNode modelNode,
                                   final double distanceFromEye) {
         _mesh = mesh;

         _modelViewMatrixArray = modelViewMatrixArray;
         _hasScaleTransformation = hasScaleTransformation;

         _modelNode = modelNode;

         _distanceFromEye = distanceFromEye;
      }


      @Override
      public double getDistanceFromEye() {
         return _distanceFromEye;
      }


      @Override
      public void pick(final DrawContext dc,
                       final Point pickPoint) {
         //         render(dc);
      }


      @SuppressWarnings("null")
      @Override
      public void render(final DrawContext dc) {
         prepareOpenGL(dc, _modelViewMatrixArray, _hasScaleTransformation);

         final GL gl = dc.getGL();

         gl.glDepthMask(false);

         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

         try {
            //            final List<GModelMesh> meshes = new ArrayList<GModelMesh>();
            //            meshes.add(_mesh);
            //
            //            // Draw as many as we can in a batch to save ogl state switching.
            //            final Queue<OrderedRenderable> queue = dc.getOrderedRenderables();
            //            while (true) {
            //               final OrderedRenderable peeked = queue.peek();
            //               final boolean isSameType = (peeked != null) && (peeked instanceof MeshOrderedRenderable)
            //                                          && (((MeshOrderedRenderable) peeked)._modelViewMatrixArray == _modelViewMatrixArray);
            //               if (isSameType) {
            //                  meshes.add(((MeshOrderedRenderable) peeked)._mesh);
            //                  queue.poll();
            //               }
            //               else {
            //                  break;
            //               }
            //            }
            //
            //            final List<RenderUnit> renderUnits = getRenderingUnits("ALPHA PASS", meshes);
            //            final boolean forceRedraw = renderRenderingUnits(dc, renderUnits);
            //
            //            if (forceRedraw) {
            //               _modelNode.redraw();
            //            }


            boolean redraw = false;
            int displayList = _meshListCache.getDisplayList(_mesh, dc, false);
            if (displayList >= 0) {
               gl.glCallList(displayList);
            }
            else {
               redraw = true;
            }

            // Draw as many as we can in a batch to save ogl state switching.
            while (true) {
               final OrderedRenderable peeked = dc.peekOrderedRenderables();
               final boolean isSameType = (peeked != null) && (peeked instanceof MeshOrderedRenderable)
                                          && (((MeshOrderedRenderable) peeked)._modelViewMatrixArray == _modelViewMatrixArray);
               if (isSameType) {
                  displayList = _meshListCache.getDisplayList(((MeshOrderedRenderable) peeked)._mesh, dc, false);
                  if (displayList >= 0) {
                     gl.glCallList(displayList);
                  }
                  else {
                     redraw = true;
                  }
                  dc.pollOrderedRenderables();
               }
               else {
                  break;
               }
            }

            if (redraw) {
               _modelNode.redraw();
            }
         }
         catch (final Throwable e) {
            e.printStackTrace();
         }
         finally {
            gl.glDepthMask(true);
            restoreOpenGL(dc);
         }
      }
   }

   private static final GLogger  logger                         = GLogger.instance();


   private static final float[] LIGHT1_POSITION                = { 2, 1, 1, 0 };
   private static final float[] LIGHT1_DIFFUSE                 = { 1, 1, 1, 1 };

   private static final float[] LIGHT2_POSITION                = { -2, -1, -1, 0 };
   private static final float[] LIGHT2_DIFFUSE                 = { 0.8f, 0.8f, 0.8f, 1 };

   private static final float[] BLACK                          = { 0, 0, 0, 1.0f };

   private final static float[] RGBA                           = new float[4];


   private static final float[] FLIP_VERTICALLY_TRANSFORMATION = GWWUtils.toGLArray(Matrix.fromScale(1, -1, 1).multiply(
                                                                        Matrix.fromTranslation(0, -1, 0)));


   private static void applyMaterial(final DrawContext dc,
                                     final GMaterial material) {

      if (material == null) {
         return;
      }

      final GL gl = dc.getGL();

      gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, material._diffuseColor.getRGBComponents(RGBA), 0);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, material._ambientColor.getRGBComponents(RGBA), 0);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, material._specularColor.getRGBComponents(RGBA), 0);
      gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, material._emissiveColor.getRGBComponents(RGBA), 0);
      gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, material._shininess);
   }


   private static void applyTexture(final DrawContext dc,
                                    final Texture texture) {
      if (texture == null) {
         return;
      }


      final GL gl = dc.getGL();

      if (texture.getMustFlipVertically()) {
         gl.glMatrixMode(GL.GL_TEXTURE);
         gl.glPushMatrix();
         //         gl.glLoadIdentity();

         //         gl.glScalef(1, -1, 1);
         //         gl.glTranslatef(0, -1, 0);
         gl.glLoadMatrixf(FLIP_VERTICALLY_TRANSFORMATION, 0);
      }

      // This is required to repeat textures...because some are not and so only
      // part of the model gets filled in....Might be a way to check if this is
      // required per object but I'm not sure...would need to research 
      // gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

      texture.enable();
      texture.bind();


   }


   private static void restoreTexture(final DrawContext dc,
                                      final Texture texture) {
      if (texture == null) {
         return;
      }

      texture.disable();

      if (texture.getMustFlipVertically()) {
         final GL gl = dc.getGL();

         gl.glMatrixMode(GL.GL_TEXTURE);
         gl.glPopMatrix();
      }
   }


   private static Texture getTexture(final GModelMesh mesh) {
      if (!mesh._hasTexCoords) {
         return null;
      }

      final GMaterial material = mesh.getMaterial();
      if (material == null) {
         return null;
      }

      final URL url = getTextureURL(mesh);
      return GTexturesCache.getTexture(url, material._mipmap);
   }


   private static URL getTextureURL(final GModelMesh mesh) {
      final GMaterial material = mesh.getMaterial();
      if (material == null) {
         return null;
      }

      final String textureFileName = material.getTextureFileName();
      if (textureFileName == null) {
         return null;
      }

      final File textureFile = new File(textureFileName);
      if (textureFile.exists()) {
         try {
            return textureFile.toURI().toURL();
         }
         catch (final MalformedURLException e) {
            e.printStackTrace();
         }
      }


      final String modelName = mesh.getModel().getName();

      final String subFileName;
      final int slashIndex = modelName.lastIndexOf('/');
      if (slashIndex == -1) {
         final int backSlashIndex = modelName.lastIndexOf('\\');
         if (backSlashIndex == -1) {
            subFileName = "";
         }
         else {
            subFileName = modelName.substring(0, backSlashIndex + 1);
         }
      }
      else {
         subFileName = modelName.substring(0, slashIndex + 1);
      }


      final String filename = subFileName + textureFileName;
      try {
         //  return GResourceRetriever.getResourceAsUrl(filename);
         return GResourceRetriever.getResourceAsUrl(filename);

      }
      catch (final IOException e) {
         logger.severe("Load of texture " + filename + " failed", e);
      }

      return null;
   }


   private static void initializeLight(final GL gl,
                                       final int lightNumber,
                                       final float[] position,
                                       final float[] ambient,
                                       final float[] diffuse) {
      gl.glLightfv(lightNumber, GL.GL_AMBIENT, ambient, 0);
      gl.glLightfv(lightNumber, GL.GL_DIFFUSE, diffuse, 0);
      gl.glLightfv(lightNumber, GL.GL_SPECULAR, diffuse, 0);
      gl.glLightfv(lightNumber, GL.GL_POSITION, position, 0);
      gl.glEnable(lightNumber);

      gl.glLightf(lightNumber, GL.GL_CONSTANT_ATTENUATION, 1.0f);
      gl.glLightf(lightNumber, GL.GL_LINEAR_ATTENUATION, 0.0f);
      gl.glLightf(lightNumber, GL.GL_QUADRATIC_ATTENUATION, 0.0f);
      gl.glLightf(lightNumber, GL.GL_SPOT_EXPONENT, 0.0f);
      gl.glLightf(lightNumber, GL.GL_SPOT_CUTOFF, 180.0f);
   }


   private static void initializeLights(final GL gl) {
      initializeLight(gl, GL.GL_LIGHT1, LIGHT1_POSITION, BLACK, LIGHT1_DIFFUSE);
      initializeLight(gl, GL.GL_LIGHT2, LIGHT2_POSITION, BLACK, LIGHT2_DIFFUSE);
   }


   private static void restoreLights(final GL gl) {
      gl.glDisable(GL.GL_LIGHT1);
      gl.glDisable(GL.GL_LIGHT2);
   }


   private static final Map<List<GModelMesh>, List<RenderUnit>> _renderUnitsCache = new HashMap<List<GModelMesh>, List<RenderUnit>>();

   private static final GDisplayListCache<GModelMesh>           _meshListCache;
   private static final GDisplayListCache<RenderUnit>           _renderUnitListCache;

   static {
      _meshListCache = new GDisplayListCache<GModelMesh>(false) {
         @Override
         protected void beforeRenderingToDisplayList(final GModelMesh mesh,
                                                     final DrawContext dc) {
            forceLoadTextures(mesh);
         }


         @Override
         protected void renderToDisplayList(final GModelMesh mesh,
                                            final DrawContext dc) {
            renderMesh(dc, mesh);
         }
      };


      _renderUnitListCache = new GDisplayListCache<RenderUnit>(false) {
         @Override
         protected void beforeRenderingToDisplayList(final RenderUnit renderUnit,
                                                     final DrawContext dc) {
            forceLoadTextures(renderUnit);
         }


         @Override
         protected void renderToDisplayList(final RenderUnit renderUnit,
                                            final DrawContext dc) {
            renderRenderUnit(dc, renderUnit);
         }
      };
   }


   private final GModelData                                     _modelData;
   private final boolean                                        _preFetchTextures;

   private ArrayList<GModelMesh>                                _opaqueMeshes;
   private ArrayList<GModelMesh>                                _transparentMeshes;


   public G3DModel(final GModelData modelData,
                   final boolean preFetchTextures) {
      _modelData = modelData;

      _preFetchTextures = preFetchTextures;

   }


   public G3DModel(final String path,
                   final boolean verbose,
                   final boolean preFetchTextures) throws GModelLoadException {
      this(new GObjLoader().load(path, verbose), preFetchTextures);
   }


   private static List<RenderUnit> calculateRenderingUnits(final List<GModelMesh> meshes) {

      final HashMap<GPair<URL, Boolean>, List<GModelMesh>> textureRenderUnitsMap = new HashMap<GPair<URL, Boolean>, List<GModelMesh>>();
      final HashMap<GMaterial, List<GModelMesh>> materialRenderUnitsMap = new HashMap<GMaterial, List<GModelMesh>>();

      for (final GModelMesh mesh : meshes) {
         if (hasTexture(mesh)) {
            final GPair<URL, Boolean> textureData = getTextureData(mesh);

            List<GModelMesh> list = textureRenderUnitsMap.get(textureData);
            if (list == null) {
               list = new ArrayList<GModelMesh>();
               textureRenderUnitsMap.put(textureData, list);
            }

            list.add(mesh);
         }
         else {
            GMaterial material = mesh.getMaterial();
            if (material == null) {
               material = new GMaterial("");
            }

            List<GModelMesh> list = materialRenderUnitsMap.get(material);
            if (list == null) {
               list = new ArrayList<GModelMesh>();
               materialRenderUnitsMap.put(material, list);
            }

            list.add(mesh);
         }
      }

      final ArrayList<RenderUnit> renderUnits = new ArrayList<RenderUnit>(textureRenderUnitsMap.size()
                                                                          + materialRenderUnitsMap.size());

      final ArrayList<GModelMesh> singleMeshes = new ArrayList<GModelMesh>();

      for (final Entry<GMaterial, List<GModelMesh>> entry : materialRenderUnitsMap.entrySet()) {
         final List<GModelMesh> unitMeshes = entry.getValue();
         if (unitMeshes.size() < 2) {
            singleMeshes.addAll(unitMeshes);
         }
         else {
            renderUnits.add(new MaterialRenderUnit(entry.getKey(), unitMeshes));
         }
      }

      for (final Entry<GPair<URL, Boolean>, List<GModelMesh>> entry : textureRenderUnitsMap.entrySet()) {
         final List<GModelMesh> unitMeshes = entry.getValue();
         if (unitMeshes.size() < 2) {
            singleMeshes.addAll(unitMeshes);
         }
         else {
            renderUnits.add(new TextureRenderUnit(entry.getKey(), unitMeshes));
         }
         //         singleMeshes.addAll(unitMeshes);
      }

      if (!singleMeshes.isEmpty()) {
         singleMeshes.trimToSize();

         for (final List<GModelMesh> group : GCollections.split(singleMeshes, 25)) {
            renderUnits.add(new SlowRenderUnit(group));
         }

         //         renderUnits.add(new SlowRenderUnit(singleMeshes));
      }

      renderUnits.trimToSize();

      //      Collections.sort(renderUnits, new Comparator<RenderUnit>() {
      //         @Override
      //         public int compare(final RenderUnit o1,
      //                            final RenderUnit o2) {
      //            final int size1 = o1._meshes.size();
      //            final int size2 = o2._meshes.size();
      //
      //            if (size1 > size2) {
      //               return -1;
      //            }
      //            if (size1 < size2) {
      //               return 1;
      //            }
      //            return 0;
      //         }
      //      });

      //      for (final RenderUnit renderUnit : renderUnits) {
      //         System.out.println(prefixMessage + ": " + renderUnit);
      //      }

      return renderUnits;
   }


   public void dispose() {

   }


   public GAxisAlignedBox getBounds() {
      return getModelData().getBounds();
   }


   public GModelData getModelData() {
      return _modelData;
   }

   private static final IMutable.ChangeListener meshChangeListener = new IMutable.ChangeListener() {
                                                                      @Override
                                                                      public void mutableChanged() {
                                                                         synchronized (_renderUnitsCache) {
                                                                            for (final Entry<List<GModelMesh>, List<RenderUnit>> entry : _renderUnitsCache.entrySet()) {
                                                                               final List<RenderUnit> renderUnits = entry.getValue();
                                                                               for (final RenderUnit renderUnit : renderUnits) {
                                                                                  _renderUnitListCache.removeDisplayList(renderUnit);
                                                                               }
                                                                               final List<GModelMesh> meshes = entry.getKey();
                                                                               for (final GModelMesh mesh : meshes) {
                                                                                  mesh.removeChangeListener(meshChangeListener);
                                                                               }
                                                                            }
                                                                            _renderUnitsCache.clear();
                                                                         }
                                                                      }
                                                                   };


   private static List<RenderUnit> getRenderingUnits(final List<GModelMesh> meshes) {
      synchronized (_renderUnitsCache) {
         List<RenderUnit> renderUnits = _renderUnitsCache.get(meshes);

         if (renderUnits == null) {

            for (final GModelMesh mesh : meshes) {
               mesh.addChangeListener(meshChangeListener);
            }

            renderUnits = calculateRenderingUnits(meshes);
            _renderUnitsCache.put(meshes, renderUnits);
         }

         return renderUnits;
      }
   }


   private static GPair<URL, Boolean> getTextureData(final GModelMesh mesh) {
      final GMaterial material = mesh.getMaterial();

      return new GPair<URL, Boolean>(getTextureURL(mesh), material._mipmap);
   }


   public List<IVector3<?>> getVertices() {
      return _modelData.getVertices();
   }


   private static boolean hasTexture(final GModelMesh mesh) {
      final GMaterial material = mesh.getMaterial();
      if (material == null) {
         return false;
      }

      final String textureName = material.getTextureFileName();
      if (textureName == null) {
         return false;
      }

      return mesh._hasTexCoords;
   }


   private void prepareOpenGL(final DrawContext dc,
                              final float[] modelViewMatrixArray,
                              final boolean hasScaleTransformation) {
      final GL gl = dc.getGL();

      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPushMatrix();
      gl.glLoadMatrixf(modelViewMatrixArray, 0);

      // gl.glPushAttrib(GL.GL_TEXTURE_BIT | GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_HINT_BIT | GL.GL_POLYGON_BIT
      //                 | GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_LIGHTING_BIT | GL.GL_TRANSFORM_BIT);

      gl.glPushAttrib(GL.GL_TEXTURE_BIT | GL.GL_LIGHTING_BIT | GL.GL_DEPTH_BUFFER_BIT /*| GL.GL_HINT_BIT*/);


      //      gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);

      if (hasScaleTransformation) {
         gl.glEnable(GL.GL_NORMALIZE);
      }


      gl.glEnable(GL.GL_LIGHTING);
      initializeLights(gl);

      // check lighting
      if (_modelData.isUsingLighting()) {
         gl.glEnable(GL.GL_LIGHTING);
      }
      else {
         gl.glDisable(GL.GL_LIGHTING);
      }

      if (_modelData.isUsingTexture()) {
         gl.glEnable(GL.GL_TEXTURE_2D);
         //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
      }
      else {
         gl.glDisable(GL.GL_TEXTURE_2D);
      }

      // check wireframe
      if (_modelData.isRenderAsWireframe()) {
         gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
      }
      else {
         switch (_modelData.getFaceCullingMode()) {
            case BACK:
               gl.glEnable(GL.GL_CULL_FACE);
               gl.glCullFace(GL.GL_BACK);
               gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
               break;
            case FRONT:
               gl.glEnable(GL.GL_CULL_FACE);
               gl.glCullFace(GL.GL_FRONT);
               gl.glPolygonMode(GL.GL_BACK, GL.GL_FILL);
               break;
            case FRONT_AND_BACK:
               gl.glDisable(GL.GL_CULL_FACE);
               gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
               break;
         }
      }

      //gl.glDisable(GL.GL_COLOR_MATERIAL);

   }


   public void render(final DrawContext dc,
                      final Matrix modelMatrix,
                      //                      final Matrix modelViewMatrix,
                      final float[] modelViewMatrixArray,
                      final boolean hasScaleTransformation,
                      final G3DModelNode modelNode) {

      if (_modelData.isOpaque()) {
         renderOpaqueModel(dc, modelViewMatrixArray, hasScaleTransformation, modelNode);
      }
      else {
         renderHibridModel(dc, modelMatrix, modelViewMatrixArray, hasScaleTransformation, modelNode);
      }

   }


   private void renderOpaqueModel(final DrawContext dc,
                                  final float[] modelViewMatrixArray,
                                  final boolean hasScaleTransformation,
                                  final G3DModelNode modelNode) {
      prepareOpenGL(dc, modelViewMatrixArray, hasScaleTransformation);

      try {
         renderMeshes(dc, modelNode, _modelData.getMeshes());
      }
      finally {
         restoreOpenGL(dc);
      }
   }


   private void renderHibridModel(final DrawContext dc,
                                  final Matrix modelMatrix,
                                  final float[] modelViewMatrixArray,
                                  final boolean hasScaleTransformation,
                                  final G3DModelNode modelNode) {


      if ((_opaqueMeshes == null) || (_transparentMeshes == null)) {
         _opaqueMeshes = new ArrayList<GModelMesh>();
         _transparentMeshes = new ArrayList<GModelMesh>();

         for (final GModelMesh mesh : _modelData.getMeshes()) {
            if (mesh.isOpaque()) {
               _opaqueMeshes.add(mesh);
            }
            else {
               _transparentMeshes.add(mesh);
            }
         }

         _opaqueMeshes.trimToSize();
         _transparentMeshes.trimToSize();
      }


      final Vec4 eyePoint = dc.getView().getEyePoint();

      for (final GModelMesh mesh : _transparentMeshes) {
         final Vec4 meshCentroid = mesh.getCentroid(modelMatrix);
         final double distanceFromEye = eyePoint.distanceTo3(meshCentroid);
         dc.addOrderedRenderable(new MeshOrderedRenderable(mesh, modelViewMatrixArray, hasScaleTransformation, modelNode,
                  distanceFromEye));
      }


      if (!_opaqueMeshes.isEmpty()) {
         prepareOpenGL(dc, modelViewMatrixArray, hasScaleTransformation);

         try {
            renderMeshes(dc, modelNode, _opaqueMeshes);
         }
         finally {
            restoreOpenGL(dc);
         }
      }

   }


   private void renderMeshes(final DrawContext dc,
                             final G3DModelNode modelNode,
                             final List<GModelMesh> meshes) {
      final List<RenderUnit> renderUnits = getRenderingUnits(meshes);
      final boolean forceRedraw = renderRenderingUnits(dc, renderUnits);

      if (forceRedraw) {
         modelNode.redraw();
      }
   }


   private static void renderMesh(final DrawContext dc,
                                  final GModelMesh mesh) {
      if (mesh.getFaces().isEmpty()) {
         return;
      }

      final Texture texture = getTexture(mesh);
      applyTexture(dc, texture);

      applyMaterial(dc, mesh.getMaterial());

      final GL gl = dc.getGL();
      if (mesh._smoothShadeMode) {
         gl.glShadeModel(GL.GL_SMOOTH);
      }
      else {
         gl.glShadeModel(GL.GL_FLAT);
      }

      final GModelData modelData = mesh.getModel();
      final List<IVector3<?>> normals = modelData.getNormals();
      final List<IVector2<?>> texCoords = modelData.getTexCoords();
      final List<IVector3<?>> vertices = modelData.getVertices();

      for (final GFace face : mesh.getFaces()) {
         final int verticesCount = face._vertexIndices.length;

         final int mode;
         if (verticesCount == 3) {
            mode = GL.GL_TRIANGLES;
         }
         else if (verticesCount == 4) {
            mode = GL.GL_QUADS;
         }
         else {
            mode = GL.GL_POLYGON;
         }
         gl.glBegin(mode);

         try {
            for (int i = 0; i < verticesCount; i++) {
               if (mesh._hasNormals) {
                  final int normalIndex = face._normalIndices[i];
                  final IVector3<?> normal = normals.get(normalIndex);
                  if (normal != null) {
                     gl.glNormal3f((float) normal.x(), (float) normal.y(), (float) normal.z());
                  }
               }

               if ((texture != null) && !texCoords.isEmpty()) {
                  final int textureIndex = face._texCoordIndices[i];
                  final IVector2<?> uv = texCoords.get(textureIndex);
                  if (uv != null) {
                     gl.glTexCoord2f((float) uv.x(), (float) uv.y());
                  }
               }

               final int vertexIndex = face._vertexIndices[i];
               final IVector3<?> vertex = vertices.get(vertexIndex);
               gl.glVertex3f((float) vertex.x(), (float) vertex.y(), (float) vertex.z());
            }
         }
         catch (final IndexOutOfBoundsException e) {
            logger.severe(e);
         }

         gl.glEnd();
      }

      restoreTexture(dc, texture);
   }


   private boolean renderRenderingUnits(final DrawContext dc,
                                        final List<RenderUnit> renderUnits) {
      final GL gl = dc.getGL();

      boolean forceRedraw = false;

      for (final RenderUnit renderUnit : renderUnits) {
         final int displayList = _renderUnitListCache.getDisplayList(renderUnit, dc, false);
         if (displayList >= 0) {
            gl.glCallList(displayList);
         }
         else {
            forceRedraw = true;
         }
      }

      return forceRedraw;
   }


   private static void renderRenderUnit(final DrawContext dc,
                                        final RenderUnit renderUnit) {

      renderUnit.prepareGL(dc);

      final GModelData modelData = renderUnit._meshes.get(0).getModel();
      final List<IVector3<?>> normals = modelData.getNormals();
      final List<IVector2<?>> texCoords = modelData.getTexCoords();
      final List<IVector3<?>> vertices = modelData.getVertices();

      final GL gl = dc.getGL();

      for (final GModelMesh mesh : renderUnit.getMeshes()) {
         renderUnit.prepareMeshGL(dc, mesh);

         //         final int displayList = _meshListCache.getDisplayList(mesh, dc, true);
         //         gl.glCallList(displayList);

         if (mesh._smoothShadeMode) {
            gl.glShadeModel(GL.GL_SMOOTH);
         }
         else {
            gl.glShadeModel(GL.GL_FLAT);
         }

         for (final GFace face : mesh.getFaces()) {
            final int verticesCount = face._vertexIndices.length;

            final int mode;
            if (verticesCount == 3) {
               mode = GL.GL_TRIANGLES;
            }
            else if (verticesCount == 4) {
               mode = GL.GL_QUADS;
            }
            else {
               mode = GL.GL_POLYGON;
            }
            gl.glBegin(mode);

            try {
               for (int i = 0; i < verticesCount; i++) {
                  if (mesh._hasNormals) {
                     final int normalIndex = face._normalIndices[i];
                     final IVector3<?> normal = normals.get(normalIndex);
                     if (normal != null) {
                        gl.glNormal3f((float) normal.x(), (float) normal.y(), (float) normal.z());
                     }
                  }

                  if (!texCoords.isEmpty()) {
                     final int textureIndex = face._texCoordIndices[i];
                     final IVector2<?> uv = texCoords.get(textureIndex);
                     if (uv != null) {
                        gl.glTexCoord2f((float) uv.x(), (float) uv.y());
                     }
                  }

                  final int vertexIndex = face._vertexIndices[i];
                  final IVector3<?> vertex = vertices.get(vertexIndex);
                  gl.glVertex3f((float) vertex.x(), (float) vertex.y(), (float) vertex.z());
               }
            }
            catch (final IndexOutOfBoundsException e) {
               logger.severe(e);
            }

            gl.glEnd();
         }

         renderUnit.restoreMeshGL(dc, mesh);
      }

      renderUnit.restoreGL(dc);
   }


   private void restoreOpenGL(final DrawContext dc) {
      final GL gl = dc.getGL();

      restoreLights(gl);

      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPopMatrix();

      gl.glPopAttrib();
   }


   public void preFetchTextures() {
      if (!_preFetchTextures) {
         return;
      }

      //      final ExecutorService executor = GConcurrent.getDefaultExecutor();
      //
      //      executor.submit(new Runnable() {
      //         @Override
      //         public void run() {
      //            for (final GModelMesh mesh : _modelData.getMeshes()) {
      //               forceLoadTextures(mesh);
      //            }
      //         }
      //      });
   }


   static private void forceLoadTextures(final GModelMesh mesh) {
      getTexture(mesh);
   }


   static private void forceLoadTextures(final RenderUnit renderUnit) {
      for (final GModelMesh mesh : renderUnit._meshes) {
         forceLoadTextures(mesh);
      }
   }

}
