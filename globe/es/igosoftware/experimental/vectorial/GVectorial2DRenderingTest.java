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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorialRenderingAttributes;
import es.igosoftware.euclid.experimental.vectorial.rendering.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GStringUtils;


public class GVectorial2DRenderingTest {
   public static void main(final String[] args) throws IOException {
      System.out.println("Vectorial2D Rendering Test 0.1");
      System.out.println("------------------------------\n");


      //      final GFileName fileName = GFileName.absoluteFromParts("home", "dgd", "Desktop", "sample-shp", "cartobrutal",
      //               "world-modified", "world.shp");
      //      final GFileName fileName = GFileName.absoluteFromParts("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp",
      //      "roads.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "roads.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "roads.shp");
      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "places.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "cartobrutal", "world-modified",
      //               "world4326.shp");

      final GProjection projection = GProjection.EPSG_4326;


      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features = loadFeatures(
               fileName, projection);


      final GAxisAlignedOrthotope<IVector2, ?> featuresBounds = features.getBounds();


      final GVectorial2DRenderer renderer = createRenderer(features);


      //      final GAxisAlignedRectangle region = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(featuresBounds),
      //               featuresBounds.getCenter()));
      final GAxisAlignedRectangle region = featuresBounds.asRectangle();


      final GFileName directoryName = GFileName.relative("render");
      final boolean renderLODIgnores = true;
      final float borderWidth = 1.5f;
      final Color fillColor = new Color(0.5f, 0.5f, 1, 0.75f);
      //      final Color fillColor = new Color(0.5f, 0.5f, 1);
      final Color borderColor = fillColor.darker().darker().darker().darker().darker();
      final double lodMinSize = 5;
      final int textureDimension = 256;
      final boolean debugRendering = false;

      final IVector2 extent = region.getExtent();

      final int imageWidth;
      final int imageHeight;
      if (extent.x() > extent.y()) {
         imageHeight = textureDimension;
         imageWidth = (int) Math.round(extent.x() / extent.y() * textureDimension);
      }
      else {
         imageWidth = textureDimension;
         imageHeight = (int) Math.round(extent.y() / extent.x() * textureDimension);
      }

      final GVectorialRenderingAttributes attributes = new GVectorialRenderingAttributes(borderWidth, fillColor, borderColor);


      GIOUtils.assureEmptyDirectory(directoryName, false);

      System.out.println();

      final int depth = 0;
      final int maxDepth = 3;
      final IRenderingStyle renderingStyle = new IRenderingStyle() {
         @Override
         public String uniqueName() {
            return null;
         }


         @Override
         public boolean isRenderLODIgnores() {
            return renderLODIgnores;
         }


         @Override
         public boolean isDebugRendering() {
            return debugRendering;
         }


         @Override
         public IMeasure<GLength> getPointSize() {
            return null;
         }


         @Override
         public float getPointOpacity() {
            return 0;
         }


         @Override
         public IColor getPointColor() {
            return null;
         }


         @Override
         public IColor getLODColor() {
            return GColorI.MAGENTA;
         }


         @Override
         public double getLODMinSize() {
            return lodMinSize;
         }
      };

      render(renderer, region, imageWidth, imageHeight, directoryName, attributes, renderingStyle, depth, maxDepth);
   }


   private static GVectorial2DRenderer createRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {
      final long start = System.currentTimeMillis();
      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(features, true);
      System.out.println();
      System.out.println("- Created renderer in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      return renderer;
   }


   private static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> loadFeatures(final GFileName fileName,
                                                                                                                                             final GProjection projection)
                                                                                                                                                                          throws IOException {

      final long start = System.currentTimeMillis();
      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features = GShapeLoader.readFeatures(
               fileName, projection);
      System.out.println("- Features loaded in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      System.out.println();
      return features;
   }


   private static void render(final GVectorial2DRenderer renderer,
                              final GAxisAlignedRectangle region,
                              final int imageWidth,
                              final int imageHeight,
                              final GFileName directoryName,
                              final GVectorialRenderingAttributes attributes,
                              final IRenderingStyle renderingStyle,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage renderedImage = renderer.render(region, imageWidth, imageHeight, attributes, renderingStyle);

      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(renderedImage, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + imageWidth + "x" + imageHeight + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         final GVectorialRenderingAttributes newAttributes = new GVectorialRenderingAttributes( //
                  attributes._borderWidth, //
                  attributes._fillColor, //
                  attributes._borderColor);

         render(renderer, region, imageWidth * 2, imageHeight * 2, directoryName, newAttributes, renderingStyle, depth + 1,
                  maxDepth);
      }
   }


}
