

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;
import es.igosoftware.util.GImageUtils;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GPair;
import es.igosoftware.util.LRUCache;


public class GIconRenderingSymbol
         extends
            GRenderingSymbol {


   private static class ImageData {
      private final double _percentFilled;
      private final Color  _averageColor;


      private ImageData(final double percentFilled,
                        final Color averageColor) {
         _percentFilled = percentFilled;
         _averageColor = averageColor;
      }
   }


   private static final LRUCache<BufferedImage, ImageData, RuntimeException>                      imageDataCache;
   private static final LRUCache<GPair<BufferedImage, IVector2>, BufferedImage, RuntimeException> scaleCache;

   static {
      imageDataCache = new LRUCache<BufferedImage, ImageData, RuntimeException>(20,
               new LRUCache.ValueFactory<BufferedImage, ImageData, RuntimeException>() {
                  @Override
                  public ImageData create(final BufferedImage image) {
                     return calculateImageData(image);
                  }
               });

      scaleCache = new LRUCache<GPair<BufferedImage, IVector2>, BufferedImage, RuntimeException>(20,
               new LRUCache.ValueFactory<GPair<BufferedImage, IVector2>, BufferedImage, RuntimeException>() {
                  @Override
                  public BufferedImage create(final GPair<BufferedImage, IVector2> key) {
                     final BufferedImage image = key._first;
                     final IVector2 extent = key._second;

                     return GImageUtils.asBufferedImage(
                              image.getScaledInstance((int) extent.x(), (int) extent.y(), Image.SCALE_SMOOTH), image.getType());
                  }
               });
   }


   private static ImageData calculateImageData(final BufferedImage image) {
      final int pixelsCount = image.getWidth() * image.getHeight();
      final double weightPerPixel = 1d / pixelsCount;

      double percentFilled = 0;

      double acumRed = 0;
      double acumGreen = 0;
      double acumBlue = 0;
      double acumAlpha = 0;

      for (int x = 0; x < image.getWidth(); x++) {
         for (int y = 0; y < image.getHeight(); y++) {
            final int pixel = image.getRGB(x, y);

            final int alpha = (pixel >>> 24) & 0xFF;
            final int red = (pixel >>> 16) & 0xFF;
            final int green = (pixel >>> 8) & 0xFF;
            final int blue = (pixel >>> 0) & 0xFF;

            percentFilled += weightPerPixel * (alpha / 255d);

            acumRed += red;
            acumGreen += green;
            acumBlue += blue;
            acumAlpha += alpha;
         }
      }

      final Color averageColor = new Color(//
               (int) (acumRed / pixelsCount), //
               (int) (acumGreen / pixelsCount), //
               (int) (acumBlue / pixelsCount), //
               (int) (acumAlpha / pixelsCount));

      return new ImageData(percentFilled, averageColor);
   }


   private final BufferedImage _scaledIcon;
   private final IVector2      _position;
   private final IVector2      _extent;
   private final ImageData     _imageData;


   public GIconRenderingSymbol(final BufferedImage icon,
                               final IVector2 point,
                               final IMeasure<GArea> pointSize,
                               final IRenderingStyle renderingStyle,
                               final IVectorial2DRenderingContext rc) {

      _imageData = imageDataCache.get(icon);

      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double extent = GMath.sqrt(areaInSquaredMeters / _imageData._percentFilled);
      final IVector2 pointPlusExtent = renderingStyle.increment(point, rc.getProjection(), extent, extent);
      _extent = rc.scaleExtent(pointPlusExtent.sub(point)).rounded();

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
      _position = scaledPoint.sub(_extent.div(2)).rounded();

      _scaledIcon = scaleCache.get(new GPair<BufferedImage, IVector2>(icon, _extent));
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return ((_extent.x() * _extent.y() * _imageData._percentFilled) > lodMinSize);
   }


   @Override
   protected void rawDraw(final IVector2 point,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final IRenderingStyle renderingStyle,
                          final IVectorial2DRenderingContext rc) {

      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, rc);

      rc.drawImage(_scaledIcon, _position.x(), _position.y(), pointOpacity);
   }


   @Override
   protected void renderLODIgnore(final IVector2 point,
                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                  final IRenderingStyle renderingStyle,
                                  final IVectorial2DRenderingContext rc) {

      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, rc);


      final Color color = GAWTUtils.mixAlpha(_imageData._averageColor, pointOpacity);

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
      //            rc.setPixel(scaledPoint, color);
      rc.setColor(color);
      rc.fillRect(scaledPoint.x(), scaledPoint.y(), 1, 1);
   }

}
