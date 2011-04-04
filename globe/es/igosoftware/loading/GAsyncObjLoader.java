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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;
import es.igosoftware.loading.modelparts.GFace;
import es.igosoftware.loading.modelparts.GMaterial;
import es.igosoftware.loading.modelparts.GModelData;
import es.igosoftware.loading.modelparts.GModelMesh;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GLogger;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.XStringTokenizer;


public class GAsyncObjLoader {


   public static interface IHandler {
      public void loadError(final IOException e);


      public void loaded(final GModelData modelData);
   }


   private static final GLogger logger          = GLogger.instance();


   public static final String   VERTEX_DATA     = "v ";
   public static final String   NORMAL_DATA     = "vn ";
   public static final String   TEXTURE_DATA    = "vt ";
   public static final String   FACE_DATA       = "f ";
   public static final String   SMOOTHING_GROUP = "s ";
   public static final String   GROUP           = "g ";
   public static final String   OBJECT          = "o ";
   public static final String   COMMENT         = "#";
   public static final String   EMPTY           = "";


   private GModelMesh           _currentMesh    = null;
   private final ILoader        _loader;
   GModelData                   _model;


   public GAsyncObjLoader(final ILoader loader) {
      _loader = loader;
   }


   public void load(final GFileName fileName,
                    final GAsyncObjLoader.IHandler handler,
                    final boolean verbose) {


      _loader.load(fileName, -1, false, Integer.MAX_VALUE, new ILoader.IHandler() {
         @Override
         public void loaded(final File objFile,
                            final long bytesLoaded,
                            final boolean completeLoaded) {
            if (!completeLoaded) {
               return;
            }

            try {
               final GModelData data = processObjFile(fileName, objFile, verbose);

               handler.loaded(data);
            }
            catch (final IOException e) {
               handler.loadError(e);
            }
         }


         @Override
         public void loadError(final IOException e) {
            handler.loadError(e);
         }
      });

   }


   private GModelData processObjFile(final GFileName objFileName,
                                     final File objFile,
                                     final boolean verbose) throws IOException {
      final long start = System.currentTimeMillis();

      final GFileName objDirectory = objFileName.getParent();

      _model = new GModelData(objFileName);
      _currentMesh = null;

      final InputStream stream = new FileInputStream(objFile);
      //      if (stream == null) {
      //         throw new IOException("Stream is null");
      //      }

      String currentName = null;
      BufferedReader br = null;
      try {
         // Open a file handle and read the models data
         br = new BufferedReader(new InputStreamReader(stream));
         String line = null;
         int linecount = 0;
         while ((line = br.readLine()) != null) {
            linecount++;
            //System.out.println("Parsing Line Nr.: " + linecount);

            //Comments are ignored
            //            if (lineIs(WaveFrontLoader.COMMENT, line)) {
            if (line.startsWith(COMMENT)) {
               continue;
            }

            //Empty lines are ignored
            if (line.isEmpty()) {
               continue;
            }

            //Object 
            if (line.startsWith(OBJECT)) {
               if (line.length() > 3) {
                  currentName = line.substring(2).trim();
               }
            }


            if (line.startsWith(GROUP)) {
               if (line.length() > 3) {
                  currentName = line.substring(2).trim();
               }
            }

            //Vertex
            //if (lineIs(WaveFrontLoader.VERTEX_DATA, line)) {
            if (line.startsWith("v ")) {
               //System.out.println("Reading Vertex");
               final String dataString = line.substring(1);
               _model.addVertex(parsePoint(dataString));
               continue;
            }

            //UV-Coordinates
            //if (lineIs(WaveFrontLoader.TEXTURE_DATA, line)) {
            if (line.startsWith(TEXTURE_DATA)) {
               //System.out.println("Reading TexCoord");
               final String dataString = line.substring(2);
               _model.addTexCoord(parseUV(dataString));
               continue;
            }

            //Normals
            //if (lineIs(WaveFrontLoader.NORMAL_DATA, line)) {
            if (line.startsWith("vn ")) {
               //getNormals(WaveFrontLoader.NORMAL_DATA, line, br);
               final String dataString = line.substring(2);
               _model.addNormal(parseNormal(dataString));
               continue;
            }

            //Faces
            //if (lineIs(WaveFrontLoader.FACE_DATA, line)) {
            if (line.startsWith(FACE_DATA)) {
               if (_currentMesh == null) {
                  _currentMesh = new GModelMesh(currentName);
               }
               //getFaces(line, currentMesh, br);
               _currentMesh.addFace(parseFace(line));
               continue;
            }

            if (line.startsWith("mtllib ")) {
               processMaterialLib(_model, objDirectory, line);
               continue;
            }

            if (line.startsWith("usemtl ")) {
               if (_currentMesh != null) {
                  _model.addmesh(_currentMesh);
               }
               final GModelMesh newMesh = new GModelMesh(currentName);
               processMaterialType(_model, line, newMesh);
               _currentMesh = newMesh;
               continue;
            }

            if (line.startsWith("s ") && (_currentMesh != null)) {
               if (line.length() > 2) {
                  final String shadeMode = line.substring(2).toLowerCase();
                  if (shadeMode.equals("off")) {
                     _currentMesh._smoothShadeMode = false;
                  }
                  else {
                     _currentMesh._smoothShadeMode = true;
                  }
               }
            }
         }
      }
      finally {
         GIOUtils.gentlyClose(br);
      }
      _model.addmesh(_currentMesh);


      //model.setCenterPoint(center);

      if (verbose) {
         final long elapsed = System.currentTimeMillis() - start;

         logger.info("------------------------------------------------------------------------------------");
         logger.info("Model \"" + objFile + "\" loaded in " + GStringUtils.getTimeMessage(elapsed));

         _model.showStatistics();

         logger.info("------------------------------------------------------------------------------------");
      }

      return _model;
   }


   private File loadMTLFile(final GFileName mtlPath) throws IOException {
      final GHolder<Boolean> done = new GHolder<Boolean>(false);
      final GHolder<File> result = new GHolder<File>(null);
      final GHolder<IOException> exception = new GHolder<IOException>(null);

      _loader.load(mtlPath, -1, false, Integer.MAX_VALUE, new ILoader.IHandler() {
         @Override
         public void loaded(final File file,
                            final long bytesLoaded,
                            final boolean completeLoaded) {
            if (!completeLoaded) {
               return;
            }

            try {
               result.set(file);
               done.set(true);
            }
            catch (final Exception e) {
               e.printStackTrace();
               //LOGGER.severe("error loading " + file, e);
            }
         }


         @Override
         public void loadError(final IOException e) {
            exception.set(e);
            done.set(true);
         }
      });

      while (!done.get()) {
         GUtils.delay(10);
      }

      if (exception.hasValue()) {
         throw exception.get();
      }

      if (result.isEmpty()) {
         throw new IOException("Can't read " + mtlPath);
      }

      return result.get();
   }


   private IVector3 parsePoint(final String line) {
      final XStringTokenizer tokenizer = new XStringTokenizer(line, " ");

      final double x = tokenizer.nextDoubleToken();
      final double y = tokenizer.nextDoubleToken();
      final double z = tokenizer.nextDoubleToken();

      return new GVector3D(x, y, z);
   }


   private IVector3 parseNormal(final String line) {
      final XStringTokenizer tokenizer = new XStringTokenizer(line, " ");

      final double x = tokenizer.nextDoubleToken();
      final double y = tokenizer.nextDoubleToken();
      final double z = tokenizer.nextDoubleToken();

      return new GVector3D(x, y, z).normalized();
   }


   private IVector2 parseUV(final String line) {
      final XStringTokenizer tokenizer = new XStringTokenizer(line, " ");

      try {
         final double u = tokenizer.nextDoubleToken();
         final double v = tokenizer.nextDoubleToken();

         return new GVector2D(u, v);
      }
      catch (final NumberFormatException e) {
         //         System.err.println("warning: " + e);
         return GVector2D.ZERO;
      }
   }


   private GFace parseFace(final String line) {
      final String s[] = line.split("\\s+");
      if (line.contains("//")) {
         // Pattern is present if obj has no texture
         for (int i = 1; i < s.length; i++) {
            s[i] = s[i].replaceAll("//", "/*/"); //insert * as a flag for missing uv data
         }
      }

      final GFace face = new GFace(s.length - 1);

      for (int i = 1; i < s.length; i++) {
         final String[] tokens = s[i].split("/");

         if (tokens.length > 0) {
            // we have vertex data
            final int index = Integer.parseInt(tokens[0]);
            if (index < 0) {
               throw new RuntimeException("Relative vertex data is not supported");
            }

            face._vertexIndices[i - 1] = index - 1;
         }

         if (tokens.length > 1) {
            if (!tokens[1].equals("*")) {
               // we have texture data
               final int index = Integer.parseInt(tokens[1]);
               if (index < 0) {
                  throw new RuntimeException("Relative textCoord data is not supported");
               }

               face._texCoordIndices[i - 1] = index - 1;
               _currentMesh._hasTexCoords = true;
            }
         }

         if (tokens.length > 2) {
            // we have normal data
            final int index = Integer.parseInt(tokens[2]);
            if (index < 0) {
               throw new RuntimeException("Relative normal data is not supported");
            }

            face._normalIndices[i - 1] = index - 1;
            _currentMesh._hasNormals = true;
         }
      }

      return face;
   }


   private void processMaterialLib(final GModelData model,
                                   final GFileName objDirectory,
                                   final String mtlData) throws IOException {
      final String s[] = mtlData.split("\\s+");

      final File mtlFile = loadMTLFile(GFileName.fromParentAndParts(objDirectory, s[1]));

      InputStream stream = null;
      try {
         try {
            stream = GResourceRetriever.getResourceAsInputStream(mtlFile.getPath());
         }
         catch (final MalformedURLException ex) {
            ex.printStackTrace();
         }

         if (stream == null) {
            try {
               stream = new FileInputStream(mtlFile.getPath());
            }
            catch (final FileNotFoundException ex) {
               ex.printStackTrace();
               return;
            }
         }
         loadMaterialFile(model, objDirectory, stream);
      }
      finally {
         GIOUtils.gentlyClose(stream);
      }
   }


   private void processMaterialType(final GModelData model,
                                    final String line,
                                    final GModelMesh mesh) {
      final String s[] = line.split("\\s+");

      mesh._hasTexCoords = false;

      for (final GMaterial material : model.getMaterials()) {
         if ((material != null) && material._name.equals(s[1])) {
            if (material.hasTexture()) {
               mesh._hasTexCoords = true;
            }
            else {
               mesh._hasTexCoords = false;
            }
            mesh.setMaterial(material);
            break;
         }
      }
   }


   private GMaterial loadMaterialFile(final GModelData model,
                                      final GFileName objDirectory,
                                      final InputStream stream) throws IOException {
      GMaterial material = null;


      final BufferedReader br = new BufferedReader(new InputStreamReader(stream));

      String line;
      while ((line = br.readLine()) != null) {

         final String parts[] = line.trim().split("\\s+");

         if (parts[0].equals("newmtl")) {
            if (material != null) {
               model.addMaterial(material);
            }

            material = new GMaterial(parts[1]);
         }
         else if (parts[0].equals("Ka") && (material != null)) {
            material._ambientColor = parseColor(line);
         }
         else if (parts[0].equals("Kd") && (material != null)) {
            material._diffuseColor = parseColor(line);
         }
         else if (parts[0].equals("Ks") && (material != null)) {
            material._specularColor = parseColor(line);
         }
         else if (parts[0].equals("Ns") && (material != null)) {
            if (parts.length > 1) {
               material._shininess = Float.parseFloat(parts[1]);
            }
         }
         else if ((parts[0].equals("d") || parts[0].equals("Tr")) && (material != null)) {
            final float alpha = Float.parseFloat(parts[1]);
            if (alpha < 1.0) {
               if (material._diffuseColor == null) {
                  material._diffuseColor = new Color(1, 1, 1, alpha);
               }
               else {
                  final float red = ((float) material._diffuseColor.getRed()) / 255;
                  final float green = ((float) material._diffuseColor.getGreen()) / 255;
                  final float blue = ((float) material._diffuseColor.getBlue()) / 255;
                  material._diffuseColor = new Color(red, green, blue, alpha);
               }
            }
         }
         else if (parts[0].equals("illum")) {

         }
         else if (parts[0].equals("map_Kd") && (material != null)) {
            if (parts.length > 1) {

               //_model.setUseTexture(true);
               final String texPath = line.substring(6).trim();
               final String[] pathParts = texPath.split("[/\\\\]");

               final GFileName textureFileName = GFileName.fromParentAndParts(objDirectory, pathParts);


               final GMaterial finalMaterial = material;

               finalMaterial.setHasTexture(true);
               _loader.load(textureFileName, -1, false, 1, new ILoader.IHandler() {
                  @Override
                  public void loaded(final File file,
                                     final long bytesLoaded,
                                     final boolean completeLoaded) {
                     if (!completeLoaded) {
                        return;
                     }

                     finalMaterial.setTextureFileName(GFileName.fromFile(file));
                  }


                  @Override
                  public void loadError(final IOException e) {
                     e.printStackTrace();
                  }
               });

            }
         }
         else if (parts[0].equals("map_Ka") && (material != null)) {
            if (parts.length > 1) {

               //                  final File texFile = loadTexFileToDisk(new GFileName(line.substring(6).trim()));
               //                  //                  material.setTextureFileName(line.substring(6).trim());
               //                  material.setTextureFileName(texFile.getPath());
            }
         }
      }


      br.close();
      model.addMaterial(material);

      return material;
   }


   private Color parseColor(final String line) {
      final String parts[] = line.trim().split("\\s+");

      final float red = GMath.clamp(Float.parseFloat(parts[1]), 0, 1);
      final float green = GMath.clamp(Float.parseFloat(parts[2]), 0, 1);
      final float blue = GMath.clamp(Float.parseFloat(parts[3]), 0, 1);

      return new Color(red, green, blue);
   }

}
