

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.ntree.quadtree.IQuadtreeBreadFirstVisitor;
import es.igosoftware.euclid.shape.GComplexPolytope;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GProgress;


class GPolygon2DRenderUnit {


   private final GGeometryQuadtree<IPolygon2D<?>> _quadtree;
   private final String                           _directoryName;
   private final GRenderingAttributes             _attributes;


   public GPolygon2DRenderUnit(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                               final String directoryName,
                               final GRenderingAttributes attributes) {
      _quadtree = quadtree;
      _directoryName = directoryName;
      _attributes = attributes;
   }


   void render() throws IOException {

      GIOUtils.assureEmptyDirectory(_directoryName, false);

      final File directory = new File(_directoryName);


      final int renderingCount = calculateRenderingCount();
      final GProgress renderingProgress = new GProgress(renderingCount) {

         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            System.out.println("Rendering : " + progressString(percent, elapsed, estimatedMsToFinish));
         }
      };


      renderQuadtree(directory, renderingProgress);
   }


   private int calculateRenderingCount() {
      final GIntHolder innerCounter = new GIntHolder(0);
      final GIntHolder leafCounter = new GIntHolder(0);

      _quadtree.breadthFirstAcceptVisitor(new IQuadtreeBreadFirstVisitor<IPolygon2D<?>>() {

         @Override
         public void visitOctree(final GGeometryNTree<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> inner)
                                                                                                                throws IGTBreadFirstVisitor.AbortVisiting {
            if (inner.getDepth() > _attributes._maxDepth) {
               throw new IGTBreadFirstVisitor.AbortVisiting();
            }

            innerCounter.increment();
         }


         @Override
         public void visitLeafNode(final GGTLeafNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> leaf)
                                                                                                             throws IGTBreadFirstVisitor.AbortVisiting {
            if (_attributes._renderLeafs) {
               if (leaf.getDepth() > _attributes._maxDepth) {
                  throw new IGTBreadFirstVisitor.AbortVisiting();
               }

               leafCounter.increment();
            }
         }
      });

      return innerCounter.get() + leafCounter.get();
   }


   private void renderQuadtree(final File directory,
                               final GProgress renderingProgress) {


      final ExecutorService executor = GConcurrent.getDefaultExecutor();
      final LinkedList<Future<?>> futures = new LinkedList<Future<?>>();

      _quadtree.breadthFirstAcceptVisitor(new IQuadtreeBreadFirstVisitor<IPolygon2D<?>>() {

         @Override
         public void visitOctree(final GGeometryNTree<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> inner)
                                                                                                                throws IGTBreadFirstVisitor.AbortVisiting {
            if (inner.getDepth() > _attributes._maxDepth) {
               throw new IGTBreadFirstVisitor.AbortVisiting();
            }

            final Future<?> future = executor.submit(new Runnable() {
               @Override
               public void run() {
                  try {
                     renderNode(directory, inner, renderingProgress);
                  }
                  catch (final IOException e) {
                     e.printStackTrace();
                  }
               }
            });

            futures.add(future);
         }


         @Override
         public void visitLeafNode(final GGTLeafNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> leaf)
                                                                                                             throws IGTBreadFirstVisitor.AbortVisiting {
            if (_attributes._renderLeafs) {
               if (leaf.getDepth() > _attributes._maxDepth) {
                  throw new IGTBreadFirstVisitor.AbortVisiting();
               }

               final Future<?> future = executor.submit(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        renderNode(directory, leaf, renderingProgress);
                     }
                     catch (final IOException e) {
                        e.printStackTrace();
                     }
                  }
               });

               futures.add(future);
            }
         }
      });

      // wait for threads finalization
      while (!futures.isEmpty()) {
         try {
            futures.pop().get();
         }
         catch (final InterruptedException e) {
            e.printStackTrace();
         }
         catch (final ExecutionException e) {
            e.printStackTrace();
         }
      }
   }


   private void renderNode(final File directory,
                           final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                           final GProgress renderingProgress) throws IOException {
      //      final long start = System.currentTimeMillis();

      final String name = "node_" + node.getDepth() + "_" + node.getId();

      final GAxisAlignedRectangle bounds = node.getBounds();
      final IVector2<?> extent = bounds.getExtent();


      final int width;
      final int height;

      if (extent.x() > extent.y()) {
         height = _attributes._textureDimension;
         width = (int) Math.round(extent.x() / extent.y()) * _attributes._textureDimension;
      }
      else {
         width = _attributes._textureDimension;
         height = (int) Math.round(extent.y() / extent.x()) * _attributes._textureDimension;
      }

      final IVector2<?> scale = new GVector2D(width, height).div(extent);


      //      final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      //      final GraphicsDevice gs = ge.getDefaultScreenDevice();
      //      final GraphicsConfiguration gc = gs.getDefaultConfiguration();
      //
      //      final BufferedImage renderedImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      renderedImage.setAccelerationPriority(1);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -height));

      final AffineTransform translation = AffineTransform.getTranslateInstance(-bounds._lower.x(), -bounds._lower.y());
      final AffineTransform scaling = AffineTransform.getScaleInstance(scale.x(), scale.y());

      final AffineTransform transform = new AffineTransform();
      transform.concatenate(transformFlipY);
      transform.concatenate(scaling);
      transform.concatenate(translation);

      g2d.setTransform(transform);

      //      g2d.setTransform(transformFlipY);


      renderNodeWithGeometries(node, bounds, scale, g2d, renderedImage, true);

      g2d.dispose();

      final File file = new File(directory, name + ".png");
      //ImageIO.write(resize(renderedImage, width, height), "png", file);
      ImageIO.write(renderedImage, "png", file);


      //      System.out.println("Rendered " + name + " in " + GUtils.getTimeMessage(System.currentTimeMillis() - start));
      renderingProgress.stepDone();
   }


   private void renderNodeWithGeometries(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                                         final GAxisAlignedRectangle bounds,
                                         final IVector2<?> scale,
                                         final Graphics2D g2d,
                                         final BufferedImage renderedImage,
                                         final boolean renderParent) {
      final GAxisAlignedRectangle nodeBounds = node.getBounds();

      final IVector2<?> scaledNodeExtent = nodeBounds.getExtent().scale(scale);
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if ((projectedSize < _attributes._lodMinSize)) {
         if (_attributes._renderLODIgnores || _attributes._debugRendering) {
            //            final Color color = scaleColor(_attributes._debugRendering ? Color.RED : _attributes._borderColor, projectedSize / _attributes._lodMinSize);
            final Color color = _attributes._debugRendering ? Color.RED : _attributes._borderColor;

            //            final IVector2<?> projectedCenter = nodeBounds._center;
            //            g2d.setColor(color);
            //            g2d.fillOval(Math.round((float) projectedCenter.x()), Math.round((float) projectedCenter.y()), 1, 1);


            final IVector2<?> projectedCenter = nodeBounds._center.sub(bounds._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      if (_attributes._debugRendering && _attributes._renderBounds) {
         final IVector2<?> nodeLower = nodeBounds._lower;
         final IVector2<?> nodeUpper = nodeBounds._upper;

         g2d.setStroke(new BasicStroke(1));
         g2d.setColor(Color.GREEN);
         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         g2d.drawRect(x, y, width, height);
      }


      if (renderParent) {
         GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> parent = node.getParent();
         while (parent != null) {
            final Collection<IPolygon2D<?>> parentGeometries = parent.getGeometries();
            //            if (!parentGeometries.isEmpty()) {
            for (final IPolygon2D<?> parentGeometry : parentGeometries) {
               if (parentGeometry.getBounds().touches(bounds)) {
                  renderGeometry(parentGeometry, bounds, scale, renderedImage, g2d);
               }
            }
            //            }

            parent = parent.getParent();
         }
      }


      for (final IPolygon2D<?> geometry : node.getGeometries()) {
         renderGeometry(geometry, bounds, scale, renderedImage, g2d);
      }

      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> innerNode = (GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>>) node;
         for (final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> child : innerNode.getChildren()) {
            renderNodeWithGeometries(child, bounds, scale, g2d, renderedImage, false);
         }
      }
   }


   private void renderGeometry(final IPolygon2D<?> geometry,
                               final GAxisAlignedRectangle bounds,
                               final IVector2<?> scale,
                               final Graphics2D g2d,
                               final BufferedImage renderedImage) {
      final IVector2<?> scaledGeometryExtent = geometry.getBounds().getExtent().scale(scale);
      final double projectedSize = scaledGeometryExtent.x() * scaledGeometryExtent.y();
      if (projectedSize < _attributes._lodMinSize) {
         if (_attributes._renderLODIgnores || _attributes._debugRendering) {
            //            final Color color = scaleColor(_attributes._debugRendering ? Color.MAGENTA : _attributes._borderColor, projectedSize / _attributes._lodMinSize);
            final Color color = _attributes._debugRendering ? Color.MAGENTA : _attributes._borderColor;

            //            final IVector2<?> projectedCenter = geometry.getBounds()._center;
            //            g2d.setColor(color);
            //            g2d.fillOval(Math.round((float) projectedCenter.x()), Math.round((float) projectedCenter.y()), 1, 1);


            final IVector2<?> projectedCenter = geometry.getBounds()._center.sub(bounds._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      final int nPoints = geometry.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      int i = 0;
      for (final IVector2<?> point : geometry.getPoints()) {
         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());

         i++;
      }


      if (_attributes._stroke != null) {
         g2d.setStroke(_attributes._stroke);
         g2d.setColor(_attributes._fillColor);
         g2d.fillPolygon(xPoints, yPoints, nPoints);
      }

      g2d.setColor(_attributes._borderColor);
      g2d.drawPolygon(xPoints, yPoints, nPoints);
   }


   //   private Color scaleColor(final Color color,
   //                            final double alpha) {
   //      final float alphaF = GMath.clamp((float) alpha, 0.5f, 1);
   //
   //      //      final int r = Math.round(color.getRed() * alphaF);
   //      //      final int g = Math.round(color.getGreen() * alphaF);
   //      //      final int b = Math.round(color.getBlue() * alphaF);
   //
   //      final int r = color.getRed();
   //      final int g = color.getGreen();
   //      final int b = color.getBlue();
   //
   //      final int a = Math.round(color.getAlpha() * alphaF);
   //      //      final int a = color.getAlpha();
   //
   //      return new Color(r, g, b, a);
   //   }


   private void setPixel(final BufferedImage renderedImage,
                         final IVector2<?> point,
                         final Color color) {

      final int imageX = Math.round((float) point.x());
      final int imageY = Math.round((float) point.y());

      if ((imageX >= 0) && (imageY >= 0)) {
         final int imageWidth = renderedImage.getWidth();
         final int imageHeight = renderedImage.getHeight();

         if ((imageX < imageWidth) && (imageY < imageHeight)) {
            final int oldRGB = renderedImage.getRGB(imageX, imageY);
            if (oldRGB == 0) {
               renderedImage.setRGB(imageX, imageHeight - 1 - imageY, color.getRGB());
            }
            else {
               final Color oldColor = new Color(oldRGB);
               final Color mixed = mix(oldColor, color);
               renderedImage.setRGB(imageX, imageHeight - 1 - imageY, mixed.getRGB());
            }
         }
      }

   }


   private Color mix(final Color colorA,
                     final Color colorB) {

      final int r = average(colorA.getRed(), colorB.getRed());
      final int g = average(colorA.getGreen(), colorB.getGreen());
      final int b = average(colorA.getBlue(), colorB.getBlue());
      //final int a = average(colorA.getAlpha(), colorB.getAlpha());
      final int a = Math.max(colorA.getAlpha(), colorB.getAlpha());
      return new Color(r, g, b, a);
   }


   private int average(final int a,
                       final int b) {
      return (a + b) / 2;
   }


   private void renderGeometry(final IPolygon2D<?> geometry,
                               final GAxisAlignedRectangle bounds,
                               final IVector2<?> scale,
                               final BufferedImage renderedImage,
                               final Graphics2D g2d) {
      if (geometry instanceof GComplexPolytope) {
         renderGeometry((IPolygon2D<?>) geometry.getHull(), bounds, scale, g2d, renderedImage);
      }
      else {
         renderGeometry(geometry, bounds, scale, g2d, renderedImage);
      }
   }

}
