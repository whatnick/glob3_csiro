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


package es.igosoftware.experimental.vectorial.samplemaps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GColorConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GCompositeGeometry2DSymbolizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GConditionalExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GCurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GEmptyExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GExpressionsSymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GFloatConstantExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GLengthToFloatExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GPolygon2DLabelerSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GPolygon2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GPolygonalChain2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GSurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.GTransformerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ICurve2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.IGeometry2DSymbolizerExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISurface2DStyleExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.GCompositeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.experimental.vectorial.GShapeLoader;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;


public class GArgentinaMap1 {


   public static void main(final String[] args) throws IOException {
      System.out.println("Argentina Map 1");
      System.out.println("---------------\n");

      final long start = System.currentTimeMillis();

      final GProjection projection = GProjection.EPSG_4326;
      final GFileName pointsFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "argentina.shapefiles",
               "americas_south_america_argentina_poi.shp");

      final GFileName surfacesFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps",
               "10m-admin-1-states-provinces-shp", "10m_admin_1_states_provinces_shp.shp");

      final GFileName linesFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "argentina.shapefiles",
               "americas_south_america_argentina_highway.shp");


      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pointsFeatures = GShapeLoader.readFeatures(
               pointsFileName, projection);

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> surfacesFeatures = GShapeLoader.readFeatures(
               surfacesFileName, projection);

      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> linesFeatures = GShapeLoader.readFeatures(
               linesFileName, projection);

      System.out.println("Points: " + pointsFeatures.size());

      @SuppressWarnings("unchecked")
      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> compositeFeatures = new GCompositeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
               surfacesFeatures, linesFeatures, pointsFeatures);

      //      System.out.println("---------------------------------------------------------------------------------------");
      //      System.out.println(" Points  : " + pointsFeatures.size());
      //      System.out.println(" Surfaces: " + surfacesFeatures.size());
      //      System.out.println(" Lines   : " + linesFeatures.size());
      //      System.out.println(" All     : " + compositeFeatures.size());
      //      System.out.println("---------------------------------------------------------------------------------------");

      final GAxisAlignedRectangle viewport = pointsFeatures.getBounds().asRectangle().expandedByPercent(0.05);


      final GFileName directoryName = GFileName.relative("temporary-data", "render");
      final boolean renderLODIgnores = true;
      final double lodMinSize = 4;
      final int textureDimension = 256;
      final boolean debugRendering = true;
      final boolean drawBackgroundImage = false;

      final IVectorI2 imageExtent = calculateImageExtent(textureDimension, viewport);

      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(compositeFeatures, true);
      final ISymbolizer2D renderingStyle = createSymbolizer(drawBackgroundImage, renderLODIgnores, lodMinSize, debugRendering);

      GIOUtils.assureEmptyDirectory(directoryName, false);

      System.out.println();

      final boolean profile = false;
      if (profile) {
         System.out.println();
         System.out.println(" CONNECT PROFILER ");
         System.out.println();
         GUtils.delay(30000);
         System.out.println("- Running... ");
      }

      final int depth = 0;
      final int maxDepth = 3;
      render(renderer, renderingStyle, viewport, imageExtent, directoryName, depth, maxDepth);

      System.out.println();
      System.out.println("- done in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
   }


   private static IVectorI2 calculateImageExtent(final int textureDimension,
                                                 final GAxisAlignedRectangle viewport) {
      final IVector2 viewportExtent = viewport.getExtent();

      final int imageWidth;
      final int imageHeight;
      if (viewportExtent.x() > viewportExtent.y()) {
         imageHeight = textureDimension;
         imageWidth = (int) Math.round(viewportExtent.x() / viewportExtent.y() * textureDimension);
      }
      else {
         imageWidth = textureDimension;
         imageHeight = (int) Math.round(viewportExtent.y() / viewportExtent.x() * textureDimension);
      }

      return new GVector2I(imageWidth, imageHeight);
   }


   private static GAxisAlignedRectangle getRegionOfImage(final GAxisAlignedRectangle imageRegion,
                                                         final GVector2D imageExtent,
                                                         final GAxisAlignedRectangle viewPort) {
      final IVector2 imageRegionLower = imageRegion._lower;
      final IVector2 imageRegionExtent = imageRegion._extent;

      final IVector2 regionLower = viewPort._lower.sub(imageRegionLower).div(imageRegionExtent).scale(imageExtent);
      final IVector2 regionUpper = viewPort._upper.sub(imageRegionLower).div(imageRegionExtent).scale(imageExtent);

      final IVector2 regionExtent = regionUpper.sub(regionLower);

      final IVector2 regionLowerFlipped = new GVector2D(regionLower.x(), imageExtent.y() - regionLower.y() - regionExtent.y());

      return new GAxisAlignedRectangle(regionLowerFlipped, regionLowerFlipped.add(regionExtent));
   }


   private static ISymbolizer2D createSymbolizer(final boolean drawBackgroundImage,
                                                 final boolean renderLODIgnores,
                                                 final double lodMinSize,
                                                 final boolean debugRendering) throws IOException {

      final boolean clusterSymbols = true;

      return new GExpressionsSymbolizer2D(debugRendering, lodMinSize, renderLODIgnores, clusterSymbols,
               createPointSymbolizerExpression(), createPolygonalChainSymbolizerExpression(), createPolygonSymbolizerExpression());
   }


   private static GCompositeGeometry2DSymbolizer<IPolygon2D> createPolygonSymbolizerExpression() {

      final String PROVINCE = "NAME_1";

      final GEmptyExpression<IPolygon2D, Boolean> isArgentinaCondition = new GEmptyExpression<IPolygon2D, Boolean>() {
         private static final String COUNTRY = "NEV_Countr";


         @Override
         public Boolean evaluate(final IPolygon2D polygon,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
            final String country = (String) feature.getAttribute(COUNTRY);
            return ((country != null) && country.trim().toLowerCase().equals("argentina"));
         }
      };


      final GConditionalExpression<IPolygon2D, IColor> surfaceColorExpression;
      surfaceColorExpression = new GConditionalExpression<IPolygon2D, IColor>(//
               isArgentinaCondition, //
               new GColorConstantExpression<IPolygon2D>(GColorF.newRGB256(204, 224, 143)), //
               new GColorConstantExpression<IPolygon2D>(GColorF.GRAY));


      final ICurve2DStyleExpression<IPolygon2D> curveStyleExpression;
      curveStyleExpression = new GCurve2DStyleExpression<IPolygon2D>(//
               new GConditionalExpression<IPolygon2D, Float>(//
                        isArgentinaCondition, //
                        new GLengthToFloatExpression<IPolygon2D>(GLength.Kilometer.value(2)), //
                        new GLengthToFloatExpression<IPolygon2D>(GLength.Kilometer.value(0.5))), //
               new GTransformerExpression<IPolygon2D, IColor, IColor>(surfaceColorExpression, new IFunction<IColor, IColor>() {
                  @Override
                  public IColor apply(final IColor color) {
                     return color.muchDarker();
                  }
               }), //
               new GFloatConstantExpression<IPolygon2D>(1));

      final ISurface2DStyleExpression<IPolygon2D> surfaceStyleExpression = new GSurface2DStyleExpression<IPolygon2D>(
               surfaceColorExpression, new GFloatConstantExpression<IPolygon2D>(1));

      final GPolygon2DSymbolizerExpression polygonSymbolizer = new GPolygon2DSymbolizerExpression(curveStyleExpression,
               surfaceStyleExpression);


      final GConditionalExpression<IPolygon2D, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> argentinaPolygonsLabeler;
      argentinaPolygonsLabeler = new GConditionalExpression<IPolygon2D, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>(
               isArgentinaCondition, //
               new GPolygon2DLabelerSymbolizerExpression(PROVINCE), //
               null);

      @SuppressWarnings("unchecked")
      final GCompositeGeometry2DSymbolizer<IPolygon2D> composite = new GCompositeGeometry2DSymbolizer<IPolygon2D>(
               polygonSymbolizer, argentinaPolygonsLabeler);

      return composite;
   }


   private static IGeometry2DSymbolizerExpression<IVector2> createPointSymbolizerExpression() {
      final int TODO_symbolize_points;

      final IGeometry2DSymbolizerExpression<IVector2> pointSymbolizerExpression = null;
      return pointSymbolizerExpression;
   }


   private static GPolygonalChain2DSymbolizerExpression createPolygonalChainSymbolizerExpression() {
      final ICurve2DStyleExpression<IPolygonalChain2D> curveStyleExpression = new GCurve2DStyleExpression<IPolygonalChain2D>(
               new GLengthToFloatExpression<IPolygonalChain2D>(GLength.Meter.value(5)), //
               new GColorConstantExpression<IPolygonalChain2D>(GColorF.GRAY), //
               new GFloatConstantExpression<IPolygonalChain2D>(1));

      return new GPolygonalChain2DSymbolizerExpression(curveStyleExpression);
   }


   protected static BufferedImage createBackgroundImage(final GAxisAlignedRectangle viewport) throws IOException {
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "2_no_clouds_8k.jpg");
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "etopo2ShadedBlueMarbleLight4left10.jpg");
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "world.topo.bathy.200407.3x21600x10800.jpg");
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //               "world.topo.bathy.200407.3x21600x10800.jpg");
      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
               "oceans.jpg");

      final BufferedImage fullBlueMarble = ImageIO.read(blueMarbleFileName.asFile());
      final GAxisAlignedRectangle blueMarbleBounds = new GAxisAlignedRectangle(new GVector2D(-Math.PI, -Math.PI / 2),
               new GVector2D(Math.PI, Math.PI / 2));
      final GVector2D blueMarbleImageExtent = new GVector2D(fullBlueMarble.getWidth(), fullBlueMarble.getHeight());

      final GAxisAlignedRectangle imageRegion = getRegionOfImage(blueMarbleBounds, blueMarbleImageExtent, viewport);

      final int x = GMath.toRoundedInt(imageRegion._lower.x());
      final int y = GMath.toRoundedInt(imageRegion._lower.y());
      final int w = GMath.toRoundedInt(imageRegion._extent.x());
      final int h = GMath.toRoundedInt(imageRegion._extent.y());

      final BufferedImage result = fullBlueMarble.getSubimage(x, y, w, h);
      System.out.println("- Created background image (" + result.getWidth() + "x" + result.getHeight() + ")");
      return result;
   }


   private static void render(final GVectorial2DRenderer renderer,
                              final ISymbolizer2D renderingStyle,
                              final GAxisAlignedRectangle viewport,
                              final IVectorI2 renderExtent,
                              final GFileName directoryName,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage image = new BufferedImage(renderExtent.x(), renderExtent.y(), BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image);

      final IProjectionTool projectionTool = new IProjectionTool() {
         @Override
         public IVector2 increment(final IVector2 position,
                                   final GProjection projection,
                                   final double deltaEasting,
                                   final double deltaNorthing) {
            return GWWUtils.increment(position, projection, deltaEasting, deltaNorthing);
         }
      };

      renderer.render(viewport, renderExtent, projectionTool, renderingStyle, drawer);

      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(image, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + renderExtent.x() + "x" + renderExtent.y() + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         render(renderer, renderingStyle, viewport, renderExtent.scale(2), directoryName, depth + 1, maxDepth);
      }
   }


}
