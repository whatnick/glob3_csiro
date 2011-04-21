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
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.GColorBrewerColorSchemeSet;
import es.igosoftware.euclid.experimental.vectorial.rendering.GColorScheme;
import es.igosoftware.euclid.experimental.vectorial.rendering.GIconSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRectangleSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.GRenderingStyleAbstract;
import es.igosoftware.euclid.experimental.vectorial.rendering.GSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.GUniqueValuesColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorial2DRenderer;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorialRenderingAttributes;
import es.igosoftware.euclid.experimental.vectorial.rendering.GVectorialRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.IColorizer;
import es.igosoftware.euclid.experimental.vectorial.rendering.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.IFunction;
import es.igosoftware.utils.GWWUtils;


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
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "shp", "argentina.shp", "places.shp");
      //      final GFileName fileName = GFileName.absolute("home", "dgd", "Desktop", "sample-shp", "cartobrutal", "world-modified",
      //               "world4326.shp");

      final GFileName pointsFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps", "argentina.shapefiles",
               "americas_south_america_argentina_poi.shp");

      final GFileName surfacesFileName = GFileName.absolute("home", "dgd", "Desktop", "Data For Maps",
               "10m-admin-1-states-provinces-shp", "10m_admin_1_states_provinces_shp.shp");

      final GProjection projection = GProjection.EPSG_4326;

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> pointsFeatures = loadFeatures(
               pointsFileName, projection);

      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> surfacesFeatures = loadFeatures(
               surfacesFileName, projection);

      final GAxisAlignedOrthotope<IVector2, ?> pointsFeaturesBounds = pointsFeatures.getBounds();


      final GVectorial2DRenderer pointsRenderer = createRenderer(pointsFeatures);
      final GVectorial2DRenderer surfacesRenderer = createRenderer(surfacesFeatures);


      //      final GAxisAlignedRectangle region = ((GAxisAlignedRectangle) centerBounds(multipleOfSmallestDimention(featuresBounds),
      //               featuresBounds.getCenter()));
      final GAxisAlignedRectangle region = pointsFeaturesBounds.asRectangle().expandedByPercent(0.05);


      final GFileName directoryName = GFileName.relative("render");
      final boolean renderLODIgnores = true;
      final float borderWidth = 1.5f;
      final Color fillColor = new Color(0.5f, 0.5f, 1, 0.75f);
      //      final Color fillColor = new Color(0.5f, 0.5f, 1);
      final Color borderColor = fillColor.darker().darker().darker().darker().darker();
      final double lodMinSize = 1;
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

      final IRenderingStyle renderingStyle = createRenderingStyle(renderLODIgnores, lodMinSize, debugRendering);

      @SuppressWarnings("unchecked")
      final GPair<GVectorial2DRenderer, IRenderingStyle>[] renderers = (GPair<GVectorial2DRenderer, IRenderingStyle>[]) new GPair<?, ?>[] {
               new GPair<GVectorial2DRenderer, IRenderingStyle>(surfacesRenderer, renderingStyle),
               new GPair<GVectorial2DRenderer, IRenderingStyle>(pointsRenderer, renderingStyle) };

      final int depth = 0;
      final int maxDepth = 3;
      render(renderers, region, imageWidth, imageHeight, directoryName, attributes, depth, maxDepth);
   }


   private static GRenderingStyleAbstract createRenderingStyle(final boolean renderLODIgnores,
                                                               final double lodMinSize,
                                                               final boolean debugRendering) throws IOException {

      final GFileName symbologyDirectory = GFileName.absolute("home", "dgd", "Desktop", "GIS Symbology");

      final BufferedImage automotiveIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "automotive-128x128.png").asFile());
      final BufferedImage governmentIcon = ImageIO.read(GFileName.fromParentAndParts(symbologyDirectory, "government-128x128.png").asFile());


      final GColorScheme colorScheme = GColorBrewerColorSchemeSet.INSTANCE.getSchemes(9, GColorScheme.Type.Qualitative).get(2);

      return new GRenderingStyleAbstract() {


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


         @Override
         public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {
            //            System.out.println("FIELDS: " + features.getFields());

            _pointColorizer.preprocessFeatures(features);

            _categoryIndex = features.getFieldIndex("CATEGORY");
         }


         @Override
         public String uniqueName() {
            final int TODO_PUT_ALL_DATA_ON_UNIQUE_NAME;
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


         private boolean isCategory(final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
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
         public GSymbol getPointSymbol(final IVector2 point,
                                       final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                       final GVectorialRenderingContext rc) {
            if (isCategory(feature, "automotive")) {
               final IMeasure<GArea> pointSize = getPointSize(point, feature, rc);
               return new GIconSymbol(automotiveIcon, point, pointSize, rc);
            }
            else if (isCategory(feature, "government and public services")) {
               final IMeasure<GArea> pointSize = getPointSize(point, feature, rc);
               return new GIconSymbol(governmentIcon, point, pointSize, rc);
            }
            else if (isCategory(feature, "tourism")) {
               final IMeasure<GArea> pointSize = getPointSize(point, feature, rc);
               final IMeasure<GLength> pointBorderSize = getPointBorderSize(point, feature, rc);
               return new GRectangleSymbol(point, pointSize, pointBorderSize, rc);
            }
            else {
               return super.getPointSymbol(point, feature, rc);
            }
         }


         @Override
         public IMeasure<GArea> getPointSize(final IVector2 point,
                                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                             final GVectorialRenderingContext rc) {
            return GArea.SquareKilometer.value(250);
         }


         @Override
         public IMeasure<GLength> getPointBorderSize(final IVector2 point,
                                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                     final GVectorialRenderingContext rc) {
            return GLength.Kilometer.value(2);
         }


         @Override
         public float getPointOpacity(final IVector2 point,
                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                      final GVectorialRenderingContext rc) {
            return 0.75f;
         }


         @Override
         public IColor getPointColor(final IVector2 point,
                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                     final GVectorialRenderingContext rc) {
            return _pointColorizer.getColor(feature);
         }


         @Override
         public IColor getPointBorderColor(final IVector2 point,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                           final GVectorialRenderingContext rc) {
            return _pointColorizer.getColor(feature).muchDarker();
         }


         @Override
         public IColor getLODColor() {
            return GColorI.MAGENTA;
         }


         @Override
         public double getLODMinSize() {
            return lodMinSize;
         }


         @Override
         public IVector2 increment(final IVector2 position,
                                   final GProjection projection1,
                                   final double deltaEasting,
                                   final double deltaNorthing) {
            return GWWUtils.increment(position, projection1, deltaEasting, deltaNorthing);
         }


         @Override
         public void preRenderImage(final BufferedImage renderedImage) {
            //            final Graphics2D g2d = renderedImage.createGraphics();
            //            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            //
            //            g2d.setBackground(Color.WHITE);
            //            g2d.clearRect(0, 0, renderedImage.getWidth(), renderedImage.getHeight());
            //
            //            g2d.dispose();


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


      };

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


   private static void render(final GPair<GVectorial2DRenderer, IRenderingStyle>[] renderers,
                              final GAxisAlignedRectangle region,
                              final int imageWidth,
                              final int imageHeight,
                              final GFileName directoryName,
                              final GVectorialRenderingAttributes attributes,
                              final int depth,
                              final int maxDepth) throws IOException {

      final long start = System.currentTimeMillis();

      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      for (final GPair<GVectorial2DRenderer, IRenderingStyle> renderer : renderers) {
         renderer._first.render(region, image, attributes, renderer._second);
      }

      final String imageName = "" + depth;
      final GFileName fileName = GFileName.fromParentAndParts(directoryName, imageName + ".png");
      ImageIO.write(image, "png", fileName.asFile());

      System.out.println("- Rendered \"" + imageName + ".png\" (" + imageWidth + "x" + imageHeight + ") in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      if (depth < maxDepth) {
         render(renderers, region, imageWidth * 2, imageHeight * 2, directoryName, attributes, depth + 1, maxDepth);
      }
   }


}
