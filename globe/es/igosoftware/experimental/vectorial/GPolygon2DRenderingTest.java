

package es.igosoftware.experimental.vectorial;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.geotools.factory.GeoTools;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.GPolygon2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRenderingAttributes;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.StringUtils;


public class GPolygon2DRenderingTest {
   public static void main(final String[] args) throws IOException {
      System.out.println("Shape Loader 0.1");
      System.out.println("----------------\n");

      System.out.println("GeoTools version: " + GeoTools.getVersion() + "\n");


      final String fileName = "/home/dgd/Escritorio/trastero/cartobrutal/world-modified/world.shp";
      final GProjection projection = GProjection.EPSG_4326;


      final List<IPolygon2D<?>> polygons = GShapeLoader.readPolygons(fileName, projection)._second;

      //      System.out.println(">>>>>>>>>> CONNECT PROFILER");
      //      GUtils.delay(20 * 1000);


      final GAxisAlignedRectangle polygonsBounds = GAxisAlignedRectangle.minimumBoundingRectangle(polygons);


      final GPolygon2DRenderer renderer = new GPolygon2DRenderer(polygons);


      final GAxisAlignedRectangle region = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(polygonsBounds),
               polygonsBounds._center));
      final String directoryName = "render";
      final boolean renderLODIgnores = true;
      final float borderWidth = 0.0001f;
      final Color fillColor = new Color(borderWidth, borderWidth, 1, 0.75f);
      final Color borderColor = Color.BLACK;
      final double lodMinSize = 5;
      final boolean debugLODRendering = true;
      final int textureDimension = 256;
      final boolean renderBounds = false;

      final IVector2<?> extent = region.getExtent();

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

      final GRenderingAttributes attributes = new GRenderingAttributes(renderLODIgnores, borderWidth, fillColor, borderColor,
               lodMinSize, debugLODRendering, textureWidth, textureHeight, renderBounds);


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
      final VectorT extent = bounds._extent;

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


   private static void render(final GPolygon2DRenderer renderer,
                              final GAxisAlignedRectangle region,
                              final String directoryName,
                              final GRenderingAttributes attributes,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();
      final BufferedImage renderedImage = renderer.render(region, attributes);

      final String imageName = depth + "_" + region.asParseableString();
      final File file = new File(directoryName, imageName + ".png");
      ImageIO.write(renderedImage, "png", file);

      System.out.println(StringUtils.spaces(depth * 2) + "Rendered " + imageName + " in "
                         + GUtils.getTimeMessage(System.currentTimeMillis() - start));

      if (depth < maxDepth) {
         final GAxisAlignedRectangle[] subRegions = region.subdivideAtCenter();
         for (final GAxisAlignedRectangle subRegion : subRegions) {
            render(renderer, subRegion, directoryName, attributes, depth + 1, maxDepth);
         }
      }
   }

}