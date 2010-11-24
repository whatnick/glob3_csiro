

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GPair;


class GPolygon2DRenderUnitSmooth
         implements
            IPolygon2DRenderUnit {


   @Override
   public BufferedImage render(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {

      final IVector2<?> extent = region.getExtent();

      final int width;
      final int height;

      if (extent.x() > extent.y()) {
         height = attributes._textureDimension;
         width = (int) Math.round(extent.x() / extent.y() * attributes._textureDimension);
      }
      else {
         width = attributes._textureDimension;
         height = (int) Math.round(extent.y() / extent.x() * attributes._textureDimension);
      }


      final BufferedImage renderedImage = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_4BYTE_ABGR);
      renderedImage.setAccelerationPriority(1);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);


      final GAxisAlignedRectangle[] subregions = region.subdivideAtCenter();

      //      for (int i = 0; i < subregions.length; i++) {
      //         final GPair<BufferedImage, GVector2I> result = getSubregionRendering(quadtree, attributes, width, height, subregions, i);
      //
      //         g2d.drawImage(result._first, result._second._x, result._second._y, null);
      //      }

      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      final List<Future<GPair<BufferedImage, GVector2I>>> futures = new ArrayList<Future<GPair<BufferedImage, GVector2I>>>(
               subregions.length);

      for (int i = 0; i < subregions.length; i++) {
         final int finalI = i;
         final Future<GPair<BufferedImage, GVector2I>> future = executor.submit(new Callable<GPair<BufferedImage, GVector2I>>() {
            @Override
            public GPair<BufferedImage, GVector2I> call() throws Exception {
               return getSubregionRendering(quadtree, attributes, width, height, subregions, finalI);
            }
         });
         futures.add(future);
      }

      for (final Future<GPair<BufferedImage, GVector2I>> future : futures) {
         try {
            final GPair<BufferedImage, GVector2I> result = future.get();
            g2d.drawImage(result._first, result._second._x, result._second._y, null);
         }
         catch (final InterruptedException e) {
            e.printStackTrace();
         }
         catch (final ExecutionException e) {
            e.printStackTrace();
         }
      }

      return asRenderedImage(renderedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH));
      //return renderedImage;
   }


   private static GPair<BufferedImage, GVector2I> getSubregionRendering(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                                                                        final GRenderingAttributes attributes,
                                                                        final int width,
                                                                        final int height,
                                                                        final GAxisAlignedRectangle[] subregions,
                                                                        final int i) {
      final BufferedImage subrenderedImage = new GPolygon2DRenderUnit().render(quadtree, subregions[i], attributes);


      final GVector2I position;
      switch (i) {
         case 0:
            position = new GVector2I(0, height);
            break;
         case 1:
            position = new GVector2I(0, 0);
            break;
         case 2:
            position = new GVector2I(width, height);
            break;
         case 3:
            position = new GVector2I(width, 0);
            break;
         default:
            throw new RuntimeException();
      }

      return new GPair<BufferedImage, GVector2I>(subrenderedImage, position);
   }


   private static BufferedImage asRenderedImage(final Image image) {
      if (image instanceof BufferedImage) {
         return (BufferedImage) image;
      }

      final BufferedImage renderedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
               BufferedImage.TYPE_4BYTE_ABGR);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.dispose();

      return renderedImage;
   }


   //   private static BufferedImage resize(final BufferedImage bi,
   //                                       final int width,
   //                                       final int height) {
   //      if ((bi.getWidth() == width) && (bi.getHeight() == height)) {
   //         return bi;
   //      }
   //
   //      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
   //
   //      final Graphics2D g2d = renderedImage.createGraphics();
   //      g2d.drawImage(bi, 0, 0, null);
   //      g2d.dispose();
   //
   //      return renderedImage;
   //   }


}
