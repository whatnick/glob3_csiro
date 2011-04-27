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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorBrewerColorSchemeSet;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorScheme;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GUniqueValuesColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.IColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GIconUtils;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyledIcon2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyledRectangle2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GRenderingStyle2DAbstract;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;


public class GVectorial2DRenderingTest {
   public static void main(final String[] args) throws IOException {
      System.out.println("Vectorial2D Rendering Test 0.1");
      System.out.println("------------------------------\n");


      final GFileName pointsFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "argentina.shapefiles",
               "americas_south_america_argentina_poi.shp");

      final GFileName surfacesFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps",
               "10m-admin-1-states-provinces-shp", "10m_admin_1_states_provinces_shp.shp");

      final GFileName linesFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "argentina.shapefiles",
               "americas_south_america_argentina_highway.shp");

      final GProjection projection = GProjection.EPSG_4326;

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pointsFeatures = loadFeatures(
               pointsFileName, projection);

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> surfacesFeatures = loadFeatures(
               surfacesFileName, projection);

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> linesFeatures = loadFeatures(
               linesFileName, projection);

      final GAxisAlignedOrthotope<IVector2, ?> pointsFeaturesBounds = pointsFeatures.getBounds();


      final GVectorial2DRenderer pointsRenderer = createRenderer(pointsFeatures);
      final GVectorial2DRenderer surfacesRenderer = createRenderer(surfacesFeatures);
      final GVectorial2DRenderer linesRenderer = createRenderer(linesFeatures);


      //      final GAxisAlignedRectangle viewport = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(featuresBounds),
      //               featuresBounds.getCenter()));
      final GAxisAlignedRectangle viewport = pointsFeaturesBounds.asRectangle().expandedByPercent(0.05);


      final GFileName directoryName = GFileName.relative("temporary-data", "render");
      final boolean renderLODIgnores = true;
      final double lodMinSize = 4;
      final int textureDimension = 256;
      final boolean debugRendering = false;

      final IVector2 extent = viewport.getExtent();

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


      GIOUtils.assureEmptyDirectory(directoryName, false);

      System.out.println();

      final IRenderingStyle2D renderingStyle = createRenderingStyle(renderLODIgnores, lodMinSize, debugRendering);

      @SuppressWarnings("unchecked")
      final GPair<GVectorial2DRenderer, IRenderingStyle2D>[] renderers = (GPair<GVectorial2DRenderer, IRenderingStyle2D>[]) new GPair<?, ?>[] { new GPair<GVectorial2DRenderer, IRenderingStyle2D>(
               surfacesRenderer, renderingStyle), new GPair<GVectorial2DRenderer, IRenderingStyle2D>(linesRenderer,
               renderingStyle), new GPair<GVectorial2DRenderer, IRenderingStyle2D>(pointsRenderer, renderingStyle) };

      final boolean profile = false;
      if (profile) {
         System.out.println();
         System.out.println(" CONNECT PROFILER ");
         System.out.println();
         GUtils.delay(30000);
         System.out.println("- Running... ");
      }

      final int depth = 0;
      final int maxDepth = 4;
      render(renderers, viewport, imageWidth, imageHeight, directoryName, depth, maxDepth);

      System.out.println();
      System.out.println("- done!");
   }


   private static GRenderingStyle2DAbstract createRenderingStyle(final boolean renderLODIgnores,
                                                                 final double lodMinSize,
                                                                 final boolean debugRendering) throws IOException {

      final GFileName symbologyDirectory = GFileName.absolute("home", "dgd", "Desktop", "GIS Symbology");

      final BufferedImage automotiveIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "automotive-128x128.png").asFile());
      final BufferedImage governmentIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "government-128x128.png").asFile());


      final GColorScheme colorScheme = GColorBrewerColorSchemeSet.INSTANCE.getSchemes(9, GColorScheme.Type.Qualitative).get(2);


      return new GRenderingStyle2DAbstract() {

         private final IColorizer _pointColorizer = new GUniqueValuesColorizer("CATEGORY", colorScheme, GColorI.WHITE, true,
                                                           new IFunction<Object, String>() {
                                                              @Override
                                                              public String apply(final Object element) {
                                                                 if (element == null) {
                                                                    return "";
                                                                 }

                                                                 return element.toString().trim().toLowerCase();
                                                              }
                                                           });

         private int              _categoryIndex  = -1;
         private int              _countryIndex   = -1;


         @Override
         public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
            //            System.out.println("FIELDS: " + features.getFields());

            if (features.getGeometryType().contains(GGeometryType.POINT)) {
               _pointColorizer.preprocessFeatures(features);

               _categoryIndex = features.getFieldIndex("CATEGORY");
            }

            if (features.getGeometryType().contains(GGeometryType.SURFACE)) {
               _countryIndex = features.getFieldIndex("NEV_Countr");
            }

         }


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


         private boolean isCategory(final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                    final String category) {
            if (_categoryIndex >= 0) {
               final Object categoryO = feature.getAttribute(_categoryIndex);
               if (categoryO instanceof String) {
                  if (((String) categoryO).trim().toLowerCase().equals(category.trim().toLowerCase())) {
                     return true;
                  }
               }
            }

            return false;
         }


         @Override
         public Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getPointSymbols(final IVector2 point,
                                                                                                                                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                                    final IVectorial2DRenderingScaler scaler) {

            if (isCategory(feature, "automotive")) {
               return Collections.singleton(createStyledIcon(point, feature, scaler, "automotive", automotiveIcon));
            }
            else if (isCategory(feature, "government and public services")) {
               return Collections.singleton(createStyledIcon(point, feature, scaler, "government", governmentIcon));
            }
            else if (isCategory(feature, "tourism")) {
               final IVector2 extent = calculateRectangleExtent(point, feature, scaler);
               final IVector2 position = calculatePosition(point, feature, scaler, extent);

               final ISurface2DStyle surfaceStyle = getPointSurfaceStyle(point, feature, scaler);

               final ICurve2DStyle curveStyle = getPointCurveStyle(point, feature, scaler);

               final GAxisAlignedRectangle rectangle = new GAxisAlignedRectangle(position, position.add(extent));
               return Collections.singleton(new GStyledRectangle2D(rectangle, surfaceStyle, curveStyle));
            }
            else {
               return super.getPointSymbols(point, feature, scaler);
            }
         }


         private GStyled2DGeometry<? extends IGeometry2D> createStyledIcon(final IVector2 point,
                                                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                           final IVectorial2DRenderingScaler scaler,
                                                                           final String iconName,
                                                                           final BufferedImage icon) {
            final float percentFilled = GIconUtils.getPercentFilled(icon);
            final IVector2 extent = calculateRectangleExtent(point, feature, scaler).div(percentFilled);
            final IVector2 position = calculatePosition(point, feature, scaler, extent);

            final BufferedImage scaledIcon = GIconUtils.getScaledImage(icon, extent);


            return new GStyledIcon2D(position, iconName, scaledIcon, 0.75f);
         }


         @Override
         protected IMeasure<GArea> getPointSize(final IVector2 point,
                                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                final IVectorial2DRenderingScaler scaler) {
            return GArea.SquareKilometer.value(50);
         }


         @Override
         protected IMeasure<GLength> getPointBorderSize(final IVector2 point,
                                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                        final IVectorial2DRenderingScaler scaler) {
            return GLength.Kilometer.value(0.5);
         }


         @Override
         protected float getPointOpacity(final IVector2 point,
                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                         final IVectorial2DRenderingScaler scaler) {
            return 0.75f;
         }


         @Override
         protected IColor getPointColor(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {
            return _pointColorizer.getColor(feature);
         }


         @Override
         protected IColor getPointBorderColor(final IVector2 point,
                                              final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                              final IVectorial2DRenderingScaler scaler) {
            return _pointColorizer.getColor(feature).muchDarker();
         }


         @Override
         public double getLODMinSize() {
            return lodMinSize;
         }


         @Override
         public void preRenderImage(final BufferedImage renderedImage) {
            _pointColorizer.preRenderImage(renderedImage);
         }


         @Override
         public void postRenderImage(final BufferedImage renderedImage) {
            _pointColorizer.postRenderImage(renderedImage);
         }


         @Override
         public IMeasure<GArea> getMaximumSize() {
            return getPointSize(null, null, null);
         }


         @Override
         protected IMeasure<GLength> getSurfaceBorderSize(final ISurface2D<?> surface,
                                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                          final IVectorial2DRenderingScaler scaler) {
            final String country = (String) feature.getAttribute(_countryIndex);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               return GLength.Kilometer.value(2);
            }
            return GLength.Kilometer.value(1);
         }


         @Override
         protected IColor getSurfaceColor(final ISurface2D<?> surface,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {
            final String country = (String) feature.getAttribute(_countryIndex);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               return GColorF.newRGB256(204, 224, 143).lighter();
            }
            return GColorF.newRGB256(204, 224, 143).muchDarker();
         }


         @Override
         protected float getSurfaceOpacity(final ISurface2D<?> surface,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                           final IVectorial2DRenderingScaler scaler) {
            return 1;
         }


         @Override
         protected IColor getSurfaceBorderColor(final ISurface2D<?> surface,
                                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                final IVectorial2DRenderingScaler scaler) {
            return getSurfaceColor(surface, feature, scaler).muchDarker();
         }


         @Override
         protected IMeasure<GLength> getCurveBorderSize(final ICurve2D<?> curve,
                                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                        final IVectorial2DRenderingScaler scaler) {
            return GLength.Kilometer.value(0.5f);
         }


         @Override
         protected IColor getCurveColor(final ICurve2D<?> curve,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {
            return GColorF.GRAY;
         }


         @Override
         protected float getCurveOpacity(final ICurve2D<?> curve,
                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                         final IVectorial2DRenderingScaler scaler) {
            return 1;
         }


         @Override
         public boolean isClusterSymbols() {
            return true;
         }

      };

   }


   private static GVectorial2DRenderer createRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      final long start = System.currentTimeMillis();
      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(features, true);
      System.out.println();
      System.out.println("- Created renderer in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      return renderer;
   }


   private static IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> loadFeatures(final GFileName fileName,
                                                                                                                             final GProjection projection)
                                                                                                                                                          throws IOException {

      final long start = System.currentTimeMillis();
      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GShapeLoader.readFeatures(
               fileName, projection);
      System.out.println("- Features loaded in " + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));
      System.out.println();
      return features;
   }


   private static void render(final GPair<GVectorial2DRenderer, IRenderingStyle2D>[] renderers,
                              final GAxisAlignedRectangle viewport,
                              final int imageWidth,
                              final int imageHeight,
                              final GFileName directoryName,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      //      fillImage(image, GColorF.newRGB256(135, 183, 219).asAWTColor());
      fillImage(image, GColorF.newRGB256(211, 237, 249).darker().asAWTColor());

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image, true);

      for (final GPair<GVectorial2DRenderer, IRenderingStyle2D> renderer : renderers) {
         final IProjectionTool projectionTool = new IProjectionTool() {
            @Override
            public IVector2 increment(final IVector2 position,
                                      final GProjection projection,
                                      final double deltaEasting,
                                      final double deltaNorthing) {
               return GWWUtils.increment(position, projection, deltaEasting, deltaNorthing);
            }
         };
         renderer._first.render(viewport, image, projectionTool, renderer._second, drawer);
      }

      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(image, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + imageWidth + "x" + imageHeight + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         render(renderers, viewport, imageWidth * 2, imageHeight * 2, directoryName, depth + 1, maxDepth);
      }
   }


   private static void fillImage(final BufferedImage image,
                                 final Color color) {
      final Graphics2D g2d = image.createGraphics();
      //      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      //      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      g2d.setBackground(color);
      g2d.clearRect(0, 0, image.getWidth(), image.getHeight());

      g2d.dispose();
   }


}
