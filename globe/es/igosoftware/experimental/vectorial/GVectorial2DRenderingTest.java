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
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorialRenderingAttributes;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GStringUtils;


public class GVectorial2DRenderingTest {
   public static void main(final String[] args) throws IOException {
      System.out.println("Shape Loader 0.1");
      System.out.println("----------------\n");


      //      final GFileName fileName = GFileName.absoluteFromParts("home", "dgd", "Desktop", "sample-shp", "cartobrutal",
      //               "world-modified", "world.shp");
      //      final GFileName fileName = GFileName.absoluteFromParts("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp",
      //      "roads.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "roads.shp");
      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "roads.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "cartobrutal", "world-modified",
      //               "world4326.shp");

      final GProjection projection = GProjection.EPSG_4326;


      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features = GShapeLoader.readFeatures(
               fileName, projection);


      final GAxisAlignedOrthotope<IVector2, ?> featuresBounds = features.getBounds();


      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(features);


      final GAxisAlignedRectangle region = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(featuresBounds),
               featuresBounds.getCenter()));

      //      // south Africa centered
      //      final Sector sector = Sector.fromDegrees(-35.265239262942075, -21.36546519870571, 15.529599777907615, 34.09720589668197);
      //      final GAxisAlignedRectangle region = GWWUtils.toAxisAlignedRectangle(sector, GProjection.EPSG_4326);
      //      //      (-35.265239262942075°, 15.529599777907615°), (-21.36546519870571°, 34.09720589668197°)


      final GFileName directoryName = GFileName.relative("render");
      final boolean renderLODIgnores = true;
      final float borderWidth = 1.5f;
      final Color fillColor = new Color(0.5f, 0.5f, 1, 0.75f);
      //      final Color fillColor = new Color(0.5f, 0.5f, 1);
      final Color borderColor = fillColor.darker().darker().darker().darker().darker();
      final double lodMinSize = 5;
      final boolean debugLODRendering = true;
      final int textureDimension = 1024;
      final boolean renderBounds = false;

      final IVector2 extent = region.getExtent();

      final int textureWidth;
      final int textureHeight;

      if (extent.x() > extent.y()) {
         textureHeight = textureDimension;
         textureWidth = (int) Math.round(extent.x() / extent.y() * textureDimension);
      }
      else {
         textureWidth = textureDimension;
         textureHeight = (int) Math.round(extent.y() / extent.x() * textureDimension);
      }

      final GVectorialRenderingAttributes attributes = new GVectorialRenderingAttributes(renderLODIgnores, borderWidth,
               fillColor, borderColor, lodMinSize, debugLODRendering, textureWidth, textureHeight, renderBounds);


      GIOUtils.assureEmptyDirectory(directoryName, false);


      final int depth = 0;
      final int maxDepth = 2;
      render(renderer, region, directoryName, attributes, depth, maxDepth);
   }


   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> centerBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                                                       final VectorT center) {
      final VectorT delta = bounds.getCenter().sub(center);
      return bounds.translatedBy(delta.negated());
   }


   private static <VectorT extends IVector<VectorT, ?>> GAxisAlignedOrthotope<VectorT, ?> multipleOfSmallestDimention(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds.getCenter();

      double smallestExtension = Double.POSITIVE_INFINITY;
      for (byte i = 0; i < bounds.dimensions(); i++) {
         final double ext = extent.get(i);
         if (ext < smallestExtension) {
            smallestExtension = ext;
         }
      }

      final VectorT newExtent = smallestBiggerMultipleOf(extent, smallestExtension);
      final VectorT newUpper = bounds._lower.add(newExtent);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   @SuppressWarnings("unchecked")
   private static <VectorT extends IVector<VectorT, ?>> VectorT smallestBiggerMultipleOf(final VectorT lower,
                                                                                         final double smallestExtension) {

      final byte dimensionsCount = lower.dimensions();

      final double[] dimensionsValues = new double[dimensionsCount];
      for (byte i = 0; i < dimensionsCount; i++) {
         dimensionsValues[i] = smallestBiggerMultipleOf(lower.get(i), smallestExtension);
      }

      return (VectorT) GVectorUtils.createD(dimensionsValues);
   }


   private static double smallestBiggerMultipleOf(final double value,
                                                  final double multiple) {
      if (GMath.closeTo(value, multiple)) {
         return multiple;
      }

      final int times = (int) (value / multiple);

      double result = times * multiple;
      if (value < 0) {
         if (result > value) {
            result -= multiple;
         }
      }
      else {
         if (result < value) {
            result += multiple;
         }
      }

      return result;
   }


   private static void render(final GVectorial2DRenderer renderer,
                              final GAxisAlignedRectangle region,
                              final GFileName directoryName,
                              final GVectorialRenderingAttributes attributes,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();
      final BufferedImage renderedImage = renderer.render(region, attributes);

      final String imageName = depth + "_" + region.asParseableString();
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(renderedImage, "png", fileName.asFile());

      System.out.println(GStringUtils.spaces(depth * 2) + "Rendered " + imageName + " in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start));

      if (depth < maxDepth) {
         final GAxisAlignedRectangle[] subRegions = region.subdivideAtCenter();
         for (final GAxisAlignedRectangle subRegion : subRegions) {
            render(renderer, subRegion, directoryName, attributes, depth + 1, maxDepth);
         }
      }
   }
}
