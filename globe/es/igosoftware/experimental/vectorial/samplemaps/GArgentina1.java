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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
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
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyledLabel2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyledRectangle2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GRenderingStyle2DAbstract;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.GCompositeFeatureCollection;
import es.igosoftware.euclid.features.GGeometryType;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.projection.GProjection;
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


public class GArgentina1 {


   public static void main(final String[] args) throws IOException {
      System.out.println("GArgentina1 0.1");
      System.out.println("---------------\n");


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


      @SuppressWarnings("unchecked")
      final IGlobeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> compositeFeatures = new GCompositeFeatureCollection<IVector2, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
               surfacesFeatures, linesFeatures, pointsFeatures);


      final GAxisAlignedRectangle viewport = pointsFeatures.getBounds().asRectangle().expandedByPercent(0.05);


      final GFileName directoryName = GFileName.relative("temporary-data", "render");
      final boolean renderLODIgnores = true;
      final double lodMinSize = 4;
      final int textureDimension = 256;
      final boolean debugRendering = false;
      final boolean drawBackgroundImage = true;

      final IVectorI2 imageExtent = calculateImageExtent(textureDimension, viewport);

      final GVectorial2DRenderer renderer = new GVectorial2DRenderer(compositeFeatures, true);
      final IRenderingStyle2D renderingStyle = createRenderingStyle(viewport, drawBackgroundImage, renderLODIgnores, lodMinSize,
               debugRendering);

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
      final int maxDepth = 4;
      render(renderer, renderingStyle, viewport, imageExtent, directoryName, depth, maxDepth);

      System.out.println();
      System.out.println("- done!");
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


   private static GRenderingStyle2DAbstract createRenderingStyle(final GAxisAlignedRectangle viewport,
                                                                 final boolean drawBackgroundImage,
                                                                 final boolean renderLODIgnores,
                                                                 final double lodMinSize,
                                                                 final boolean debugRendering) throws IOException {

      final GFileName symbologyDirectory = GFileName.absolute("home", "dgd", "Desktop", "GIS Symbology");

      final BufferedImage automotiveIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "automotive-128x128.png").asFile());
      final BufferedImage governmentIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "government-128x128.png").asFile());


      final BufferedImage backgroundImage = drawBackgroundImage ? createBackgroundImage(viewport) : null;

      final GColorScheme colorScheme = GColorBrewerColorSchemeSet.INSTANCE.getSchemes(9, GColorScheme.Type.Qualitative).get(2);


      return new GRenderingStyle2DAbstract() {
         private static final String COUNTRY         = "NEV_Countr";
         private static final String PROVINCE        = "NAME_1";
         private static final String CATEGORY        = "CATEGORY";


         private final IColorizer    _pointColorizer = new GUniqueValuesColorizer(CATEGORY, colorScheme, GColorI.WHITE, true,
                                                              new IFunction<Object, String>() {
                                                                 @Override
                                                                 public String apply(final Object element) {
                                                                    if (element == null) {
                                                                       return "";
                                                                    }

                                                                    return element.toString().trim().toLowerCase();
                                                                 }
                                                              });


         //         private int                 _categoryIndex  = -1;


         //         private int                 _countryIndex   = -1;
         //         private int                 _provinceIndex  = -1;


         @Override
         public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
            //            System.out.println("FIELDS: " + features.getFields());

            if (features.getGeometryType().contains(GGeometryType.POINT)) {
               _pointColorizer.preprocessFeatures(features);

               //               _categoryIndex = features.getFieldIndex(CATEGORY);
            }

            if (features.getGeometryType().contains(GGeometryType.SURFACE)) {
               /*
               OBJECTID,   Integer]
               VertexCou,  Double]
               ISO,        String]
               NAME_0,     String]
               NAME_1,     String]
               VARNAME_1,  String]
               NL_NAME_1,  String]
               HASC_1,     String]
               TYPE_1,     String]
               ENGTYPE_1,  String]
               VALIDFR_1,  String]
               VALIDTO_1,  String]
               REMARKS_1,  String]
               Region,     String]
               RegionVar,  String]
               ProvNumber, Integer]
               NEV_Countr, String]
               FIRST_FIPS, String]
               FIRST_HASC, String]
               FIPS_1,     String]
               gadm_level, Double]
               CheckMe,    Integer]
               Region_Cod, String]
               Region_C_1, String]
               ScaleRank,  Integer]
               Region_C_2, String]
               Region_C_3, String]
               Country_Pr, String]
               DataRank,   Integer]
               Abbrev,     String]
               Postal,     String]
               Area_sqkm,  Double]
               sameAsCity, Integer]
               ADM0_A3,    String]
               MAP_COLOR,  Integer]
               LabelRank,  Integer]
               Shape_Leng, Double]
               Shape_Area, Double]
               */

               //               _countryIndex = features.getFieldIndex(COUNTRY);
               //               _provinceIndex = features.getFieldIndex(PROVINCE);
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
            //            if (_categoryIndex >= 0) {
            final Object categoryO = feature.getAttribute(CATEGORY);
            if (categoryO instanceof String) {
               if (((String) categoryO).trim().toLowerCase().equals(category.trim().toLowerCase())) {
                  return true;
               }
            }
            //            }

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
               return Collections.singleton(new GStyledRectangle2D(rectangle, null, surfaceStyle, curveStyle, 1000));
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


            return new GStyledIcon2D(position, null, iconName, scaledIcon, 0.75f, 1000);
         }


         @Override
         protected IMeasure<GArea> getPointSize(final IVector2 point,
                                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                final IVectorial2DRenderingScaler scaler) {
            return GArea.SquareKilometer.value(30);
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
            //            return GColorF.RED;
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
            fillImage(renderedImage, GColorF.newRGB256(211, 237, 249).darker().asAWTColor());

            if (backgroundImage != null) {
               final Graphics2D g2d = renderedImage.createGraphics();
               g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
               g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
               g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
               g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


               g2d.drawImage(backgroundImage, //
                        0, 0, renderedImage.getWidth(), renderedImage.getHeight(), //
                        null);

               g2d.dispose();
            }

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
            final String country = (String) feature.getAttribute(COUNTRY);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               return GLength.Kilometer.value(2);
            }
            return GLength.Kilometer.value(1);
         }


         @Override
         protected IColor getSurfaceColor(final ISurface2D<?> surface,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {
            final String country = (String) feature.getAttribute(COUNTRY);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               return GColorF.newRGB256(204, 224, 143).lighter();
            }
            //return GColorF.newRGB256(204, 224, 143).muchDarker().muchDarker();
            return GColorF.GRAY;
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
            final String country = (String) feature.getAttribute(COUNTRY);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               return getSurfaceColor(surface, feature, scaler).muchDarker();
            }
            return getSurfaceColor(surface, feature, scaler).darker();
         }


         @Override
         protected IMeasure<GLength> getCurveBorderSize(final ICurve2D<?> curve,
                                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                        final IVectorial2DRenderingScaler scaler) {
            return GLength.Meter.value(5);
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


         @Override
         public Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                                                                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                                      final IVectorial2DRenderingScaler scaler) {
            final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> superSymbols = super.getSurfaceSymbols(
                     surface, feature, scaler);

            final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> allSymbols = new ArrayList<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
                     superSymbols);


            final String country = (String) feature.getAttribute(COUNTRY);
            if ((country != null) && country.trim().toLowerCase().equals("argentina")) {
               final String provinceName = (String) feature.getAttribute(PROVINCE);
               if ((provinceName != null) && !provinceName.trim().isEmpty()) {
                  boolean addLabel = true;
                  if (feature.getDefaultGeometry() instanceof GMultiGeometry2D) {
                     @SuppressWarnings("unchecked")
                     final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) feature.getDefaultGeometry();
                     final IBoundedGeometry2D<? extends IFinite2DBounds<?>> biggestGeometry = getBiggestGeometry(multigeometry);
                     if (biggestGeometry != surface) {
                        addLabel = false;
                     }
                  }

                  if (addLabel) {
                     final IVector2 position = scaler.scaleAndTranslate(surface.getCentroid());

                     //               final Font font = new Font("Dialog", Font.BOLD, 25);
                     final Font font = new Font("Serif", Font.BOLD, 25);

                     //               FontRenderContext frc;
                     //               font.getStringBounds(provinceName, frc);

                     //               LayoutPath
                     //                                    TextLayout

                     //                     final LineBreakMeasurer

                     allSymbols.add(new GStyledLabel2D(position, provinceName, font));
                  }
               }
            }

            return allSymbols;
         }

      };

   }


   protected static BufferedImage createBackgroundImage(final GAxisAlignedRectangle viewport) throws IOException {
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "2_no_clouds_8k.jpg");
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "etopo2ShadedBlueMarbleLight4left10.jpg");
      //      final GFileName blueMarbleFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "bluemarble",
      //      "world.topo.bathy.200407.3x21600x10800.jpg");
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


   private static IBoundedGeometry2D<? extends IFinite2DBounds<?>> getBiggestGeometry(final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry) {
      IBoundedGeometry2D<? extends IFinite2DBounds<?>> biggestGeometry = null;
      double biggestArea = Double.NEGATIVE_INFINITY;
      for (final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry : multigeometry) {
         final double currentArea = geometry.getBounds().area();
         if (currentArea > biggestArea) {
            biggestArea = currentArea;
            biggestGeometry = geometry;
         }
      }

      return biggestGeometry;
   }


   private static void render(final GVectorial2DRenderer rendeder,
                              final IRenderingStyle2D renderingStyle,
                              final GAxisAlignedRectangle viewport,
                              final IVectorI2 imageExtent,
                              final GFileName directoryName,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage image = new BufferedImage(imageExtent.x(), imageExtent.y(), BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image, true);

      final IProjectionTool projectionTool = new IProjectionTool() {
         @Override
         public IVector2 increment(final IVector2 position,
                                   final GProjection projection,
                                   final double deltaEasting,
                                   final double deltaNorthing) {
            return GWWUtils.increment(position, projection, deltaEasting, deltaNorthing);
         }
      };

      rendeder.render(viewport, image, projectionTool, renderingStyle, drawer);


      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(image, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + imageExtent.x() + "x" + imageExtent.y() + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         render(rendeder, renderingStyle, viewport, imageExtent.scale(2), directoryName, depth + 1, maxDepth);
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
