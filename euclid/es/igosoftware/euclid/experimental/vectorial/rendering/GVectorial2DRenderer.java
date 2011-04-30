

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.graph.GGraph;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


public class GVectorial2DRenderer {

   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>           _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _quadtree;


   public GVectorial2DRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features,
                               final boolean verbose) {
      _features = features;

      _quadtree = createQuadtree(verbose);
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> createQuadtree(final boolean verbose) {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<

      IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>,

      IBoundedGeometry2D<? extends IFinite2DBounds<?>>

      >() {

         @Override
         public boolean acceptLeafNodeCreation(final int depth,
                                               final GAxisAlignedOrthotope<IVector2, ?> bounds,
                                               final Collection<GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> elements) {
            if (depth >= 12) {
               return true;
            }

            return (elements.size() <= 50);
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(verbose, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.GIVEN, true);

      final GAxisAlignedRectangle bounds = _features.getBounds().asRectangle().expandedByPercent(0.05);
      return new GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               "Rendering", _features, parameters, bounds);
   }


   public BufferedImage getRenderedImage(final GAxisAlignedRectangle viewport,
                                         final int imageWidth,
                                         final int imageHeight,
                                         final IProjectionTool projectionTool,
                                         final IRenderingStyle2D renderingStyle) {
      GAssert.notNull(viewport, "viewport");
      GAssert.isPositive(imageWidth, "imageWidth");
      GAssert.isPositive(imageHeight, "imageHeight");
      GAssert.notNull(renderingStyle, "renderingStyle");

      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image, true);

      render(viewport, image, projectionTool, renderingStyle, drawer);

      return image;
   }


   public void render(final GAxisAlignedRectangle viewport,
                      final BufferedImage image,
                      final IProjectionTool projectionTool,
                      final IRenderingStyle2D renderingStyle,
                      final IVectorial2DDrawer drawer) {
      GAssert.notNull(viewport, "viewport");
      GAssert.notNull(image, "image");
      GAssert.notNull(renderingStyle, "renderingStyle");

      renderingStyle.preprocessFeatures(_features);

      renderingStyle.preRenderImage(image);

      final IVectorial2DRenderUnit renderUnit = new GVectorial2DRenderUnit();
      final GRenderUnitResult renderUnitResult = renderUnit.render(image, _quadtree, _features.getProjection(), projectionTool,
               viewport, renderingStyle, drawer);


      if (renderingStyle.isClusterSymbols() && renderUnitResult.hasGroupableSymbols()) {
         renderSymbolsInClusters(renderUnitResult.getSymbols(), drawer, renderingStyle);
      }
      else {
         renderSymbolsIndividually(renderUnitResult.getSymbols(), drawer, renderingStyle);
      }


      renderingStyle.postRenderImage(image);
   }


   private static void renderSymbolsIndividually(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                                 final IVectorial2DDrawer drawer,
                                                 final IRenderingStyle2D renderingStyle) {
      final double lodMinSize = renderingStyle.getLODMinSize();
      final boolean debugRendering = renderingStyle.isDebugRendering();
      final boolean renderLODIgnores = renderingStyle.isRenderLODIgnores();

      final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> sortedSymbols = GCollections.asSorted(
               symbols, new Comparator<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>() {
                  @Override
                  public int compare(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> o1,
                                     final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> o2) {
                     final int priority1 = o1.getPriority();
                     final int priority2 = o2.getPriority();
                     if (priority1 > priority2) {
                        return 1;
                     }
                     else if (priority1 < priority2) {
                        return -1;
                     }
                     else {
                        final int position1 = o1.getPosition();
                        final int position2 = o2.getPosition();
                        if (position1 > position2) {
                           return 1;
                        }
                        else if (position1 < position2) {
                           return -1;
                        }
                        return 0;
                     }
                  }
               });

      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : sortedSymbols) {
         renderSymbol(symbol, drawer, lodMinSize, debugRendering, renderLODIgnores);
      }

      System.out.println("  - Rendered " + symbols.size() + " symbols");
   }


   private static void renderSymbolsInClusters(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                               final IVectorial2DDrawer drawer,
                                               final IRenderingStyle2D renderingStyle) {

      final Collection<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clusters = createClusters(
               symbols, false);

      final List<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> sortedClusters = GCollections.asSorted(
               clusters, new Comparator<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>() {
                  @Override
                  public int compare(final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster1,
                                     final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster2) {
                     final int size1 = cluster1.size();
                     final int size2 = cluster2.size();
                     if (size1 == size2) {
                        return 0;
                     }
                     else if (size1 > size2) {
                        return -1;
                     }
                     else {
                        return 1;
                     }
                  }
               });

      int clusteredCount = 0;
      for (final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster : sortedClusters) {
         final int size = cluster.size();

         if (size == 0) {
            continue;
         }
         else if (size == 1) {
            final double lodMinSize = renderingStyle.getLODMinSize();
            final boolean debugRendering = renderingStyle.isDebugRendering();
            final boolean renderLODIgnores = renderingStyle.isRenderLODIgnores();

            renderSymbol(GCollections.theOnlyOne(cluster), drawer, lodMinSize, debugRendering, renderLODIgnores);
         }
         else {
            renderCluster(cluster, drawer, renderingStyle);
         }
         clusteredCount += size;
      }

      System.out.println("  - Rendered " + clusteredCount + " symbols in " + clusters.size() + " clusters");
   }


   private static void renderCluster(final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster,
                                     final IVectorial2DDrawer drawer,
                                     final IRenderingStyle2D renderingStyle) {

      final double lodMinSize = renderingStyle.getLODMinSize();
      final boolean debugRendering = renderingStyle.isDebugRendering();
      final boolean renderLODIgnores = renderingStyle.isRenderLODIgnores();

      final int __Diego_at_work;

      //      final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = cluster.iterator().next();
      //
      //      exemplar.drawGroup(cluster, drawer, lodMinSize, debugRendering, renderLODIgnores);

      if (isHomogenous(cluster)) {
         final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = cluster.iterator().next();
         exemplar.drawGroup(cluster, drawer, lodMinSize, debugRendering, renderLODIgnores);
      }
      else {
         final Collection<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clustersByGroups = createClusters(
                  cluster, true);

         for (final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> clusterByGroup : clustersByGroups) {
            final int size = clusterByGroup.size();

            if (size == 0) {
               continue;
            }
            else if (size == 1) {
               renderSymbol(GCollections.theOnlyOne(clusterByGroup), drawer, lodMinSize, debugRendering, renderLODIgnores);
            }
            else {
               final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = clusterByGroup.iterator().next();

               exemplar.drawGroup(clusterByGroup, drawer, lodMinSize, debugRendering, renderLODIgnores);
            }
         }
      }
   }


   private static Collection<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> createClusters(final Collection<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                                                                                                                final boolean considerIsGroupableWith) {
      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(false, 12, 10,
               GGeometryNTreeParameters.BoundsPolicy.MINIMUM, true);

      final GGeometryQuadtree<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbolsQuadtree = new GGeometryQuadtree<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
               "Symbols Quadtree",
               null,
               symbols,
               new IFunction<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>() {
                  @Override
                  public Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> apply(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> element) {
                     return Collections.singleton(element.getGeometry());
                  }
               }, parameters);


      final GGraph<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> graph = new GGraph<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               symbols);

      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols) {
         final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> neighborhood = calculateNeighborhood(
                  symbolsQuadtree, symbol, considerIsGroupableWith);
         for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> neighbor : neighborhood) {
            graph.addBidirectionalEdge(symbol, neighbor);
         }
      }

      final Collection<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clusters = graph.getConnectedGroupsOfNodes();

      return clusters;
   }


   private static void renderSymbol(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol,
                                    final IVectorial2DDrawer drawer,
                                    final double lodMinSize,
                                    final boolean debugRendering,
                                    final boolean renderLODIgnores) {
      if (symbol != null) {
         symbol.draw(drawer, lodMinSize, debugRendering, renderLODIgnores);
      }
   }


   private static List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> calculateNeighborhood(final GGeometryQuadtree<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbolsQuadtree,
                                                                                                                            final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol,
                                                                                                                            final boolean considerIsGroupableWith) {
      final GAxisAlignedRectangle bounds = toRoundedInt(symbol.getBounds());
      final double minOverlapArea = bounds.area() * 0.25;

      final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> neighborhood = new LinkedList<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

      symbolsQuadtree.breadthFirstAcceptVisitor(
               bounds,
               new IGTBreadFirstVisitor<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>() {
                  @Override
                  public void visitOctree(final GGeometryNTree<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> octree) {
                  }


                  @Override
                  public void visitInnerNode(final GGTInnerNode<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> inner) {
                     processNode(inner);
                  }


                  @Override
                  public void visitLeafNode(final GGTLeafNode<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> leaf) {
                     processNode(leaf);
                  }


                  private void processNode(final GGTNode<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node) {

                     for (final GElementGeometryPair<IVector2, GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> elementAndGeometry : node.getElements()) {
                        final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> element = elementAndGeometry.getElement();
                        if (element == symbol) {
                           continue;
                        }

                        if (element.isGroupable()) {
                           if (!considerIsGroupableWith || element.isGroupableWith(symbol)) {
                              final GAxisAlignedRectangle geometryBounds = toRoundedInt(element.getBounds());
                              if (bounds.touchesBounds(geometryBounds)
                                  && (bounds.intersection(geometryBounds).area() >= minOverlapArea)) {
                                 neighborhood.add(element);
                              }
                           }
                        }
                     }

                  }
               });

      return neighborhood;
   }


   private static boolean isHomogenous(final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster) {
      final Iterator<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> iterator = cluster.iterator();

      final Class<? extends GStyled2DGeometry> klass = iterator.next().getClass();
      while (iterator.hasNext()) {
         if (klass != iterator.next().getClass()) {
            return false;
         }
      }

      return true;
   }


   private static GAxisAlignedRectangle toRoundedInt(final GAxisAlignedRectangle rectangle) {
      return new GAxisAlignedRectangle(toRoundedInt(rectangle._lower), toRoundedInt(rectangle._upper));
   }


   private static IVector2 toRoundedInt(final IVector2 vector) {
      return new GVector2F(GMath.toRoundedInt(vector.x()), GMath.toRoundedInt(vector.y()));
   }


}
