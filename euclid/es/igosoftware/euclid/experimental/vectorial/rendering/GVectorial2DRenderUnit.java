

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.graph.GGraph;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


class GVectorial2DRenderUnit
         implements
            IVectorial2DRenderUnit {


   //   private static final boolean CLUSTER_RENDERING = true;


   @Override
   public void render(final BufferedImage renderedImage,
                      final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> quadtree,
                      final GProjection projection,
                      final IProjectionTool projectionTool,
                      final GAxisAlignedRectangle viewport,
                      final IRenderingStyle2D renderingStyle,
                      final IVectorial2DDrawer drawer) {

      final IVectorial2DRenderingScaler scaler = new GVectorial2DRenderingScaler(viewport, projection, projectionTool,
               renderedImage.getWidth(), renderedImage.getHeight());

      final GAxisAlignedRectangle extendedViewport = calculateExtendedViewport(viewport, scaler, renderingStyle);

      final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> allSymbols = new LinkedList<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

      final GHolder<Boolean> hasGroupableSymbols = new GHolder<Boolean>(false);
      processNode(quadtree.getRoot(), extendedViewport, renderingStyle, scaler, drawer, allSymbols, hasGroupableSymbols);

      renderSymbols(allSymbols, hasGroupableSymbols.get(), renderingStyle, drawer);
   }


   private static void renderSymbols(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                     final boolean hasGroupableSymbols,
                                     final IRenderingStyle2D renderingStyle,
                                     final IVectorial2DDrawer drawer) {

      final double lodMinSize = renderingStyle.getLODMinSize();
      final boolean debugRendering = renderingStyle.isDebugRendering();
      final boolean renderLODIgnores = renderingStyle.isRenderLODIgnores();

      if (renderingStyle.isClusterSymbols() && hasGroupableSymbols) {
         renderSymbolsInClusters(symbols, drawer, lodMinSize, debugRendering, renderLODIgnores);
      }
      else {
         renderSymbolsIndividually(symbols, drawer, lodMinSize, debugRendering, renderLODIgnores);
      }
   }


   protected static void renderSymbolsIndividually(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                                   final IVectorial2DDrawer drawer,
                                                   final double lodMinSize,
                                                   final boolean debugRendering,
                                                   final boolean renderLODIgnores) {
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols) {
         renderSymbol(symbol, drawer, lodMinSize, debugRendering, renderLODIgnores);
      }

      System.out.println("  - Rendered " + symbols.size() + " symbols");
   }


   protected static void renderSymbolsInClusters(final List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                                 final IVectorial2DDrawer drawer,
                                                 final double lodMinSize,
                                                 final boolean debugRendering,
                                                 final boolean renderLODIgnores) {
      final Collection<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clusters = createClusters(
               symbols, false);

      final List<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> sortedClusters = new ArrayList<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>(
               clusters);
      final Comparator<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> comparator = new Comparator<Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>() {
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
      };
      Collections.sort(sortedClusters, comparator);

      int clusteredCount = 0;
      for (final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster : sortedClusters) {
         final int size = cluster.size();

         if (size == 0) {
            continue;
         }
         else if (size == 1) {
            renderSymbol(GCollections.theOnlyOne(cluster), drawer, lodMinSize, debugRendering, renderLODIgnores);
         }
         else {
            renderCluster(cluster, drawer, lodMinSize, debugRendering, renderLODIgnores);
         }
         clusteredCount += size;
      }

      System.out.println("  - Rendered " + clusteredCount + " symbols in " + clusters.size() + " clusters");
   }


   private static void renderCluster(final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster,
                                     final IVectorial2DDrawer drawer,
                                     final double lodMinSize,
                                     final boolean debugRendering,
                                     final boolean renderLODIgnores) {
      final int __Diego_at_work;

      //      final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = cluster.iterator().next();
      //
      //      exemplar.drawGroup(cluster, drawer, lodMinSize, debugRendering, renderLODIgnores);

      if (isSameClass(cluster)) {
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


   private static boolean isSameClass(final Set<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster) {
      final Iterator<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> iterator = cluster.iterator();

      final Class<? extends GStyled2DGeometry> klass = iterator.next().getClass();
      while (iterator.hasNext()) {
         if (klass != iterator.next().getClass()) {
            return false;
         }
      }

      return true;
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


   private static List<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> calculateNeighborhood(final GGeometryQuadtree<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbolsQuadtree,
                                                                                                                            final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol,
                                                                                                                            final boolean considerIsGroupableWith) {
      final GAxisAlignedRectangle bounds = toRoundedInt(symbol.getBounds());
      final double minOverlapArea = bounds.area() * 0.3;

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
                                  && (bounds.intersection(geometryBounds).area() > minOverlapArea)) {
                                 neighborhood.add(element);
                              }
                           }
                        }
                     }

                  }
               });

      return neighborhood;
   }


   private static GAxisAlignedRectangle toRoundedInt(final GAxisAlignedRectangle rectangle) {
      return new GAxisAlignedRectangle(toRoundedInt(rectangle._lower), toRoundedInt(rectangle._upper));
   }


   private static IVector2 toRoundedInt(final IVector2 vector) {
      return new GVector2F(GMath.toRoundedInt(vector.x()), GMath.toRoundedInt(vector.y()));
   }


   private static GAxisAlignedRectangle calculateExtendedViewport(final GAxisAlignedRectangle viewport,
                                                                  final IVectorial2DRenderingScaler scaler,
                                                                  final IRenderingStyle2D renderingStyle) {
      final IMeasure<GArea> maximumSize = renderingStyle.getMaximumSize();

      final double areaInSquaredMeters = maximumSize.getValueInReferenceUnits();
      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 lower = scaler.increment(viewport._lower, -extent, -extent);
      final IVector2 upper = scaler.increment(viewport._upper, extent, extent);

      return new GAxisAlignedRectangle(lower, upper);
   }


   private static void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                   final GAxisAlignedRectangle extendedRegion,
                                   final IRenderingStyle2D renderingStyle,
                                   final IVectorial2DRenderingScaler scaler,
                                   final IVectorial2DDrawer drawer,
                                   final List<GStyled2DGeometry<?>> allSymbols,
                                   final GHolder<Boolean> hasGroupableSymbols) {

      final GAxisAlignedRectangle nodeBounds = node.getMinimumBounds().asRectangle();

      if (!nodeBounds.touches(extendedRegion)) {
         return;
      }

      //      if (!renderingStyle.processNode(node, scaler, drawer)) {
      //         return;
      //      }


      final IVector2 nodeExtent = nodeBounds.asRectangle().getExtent();
      final IVector2 scaledExtent = scaler.scaleExtent(nodeExtent);

      if (scaledExtent.length() <= renderingStyle.getLODMinSize()) {
         if (renderingStyle.isDebugRendering()) {
            final GAxisAlignedOrthotope<IVector2, ?> scaledNodeBounds = scaler.scaleAndTranslate(nodeBounds);
            drawer.fillRect(scaledNodeBounds, Color.RED);
         }

         return;
      }

      final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols = renderingStyle.getNodeSymbols(
               node, scaler);
      addSymbols(symbols, allSymbols, hasGroupableSymbols);


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> child : inner.getChildren()) {
            processNode(child, extendedRegion, renderingStyle, scaler, drawer, allSymbols, hasGroupableSymbols);
         }
      }

      drawNode(node, extendedRegion, renderingStyle, scaler, drawer, allSymbols, hasGroupableSymbols);
   }


   private static void drawNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                final GAxisAlignedRectangle extendedRegion,
                                final IRenderingStyle2D renderingStyle,
                                final IVectorial2DRenderingScaler scaler,
                                final IVectorial2DDrawer drawer,
                                final List<GStyled2DGeometry<?>> allSymbols,
                                final GHolder<Boolean> hasGroupableSymbols) {


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pair : node.getElements()) {
         final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry = pair.getGeometry();
         drawGeometry(geometry, pair.getElement(), extendedRegion, renderingStyle, scaler, drawer, allSymbols,
                  hasGroupableSymbols);
      }

   }


   private static void drawGeometry(final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry,
                                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                    final GAxisAlignedRectangle extendedRegion,
                                    final IRenderingStyle2D renderingStyle,
                                    final IVectorial2DRenderingScaler scaler,
                                    final IVectorial2DDrawer drawer,
                                    final List<GStyled2DGeometry<?>> allSymbols,
                                    final GHolder<Boolean> hasGroupableSymbols) {

      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) geometry;
         for (final IBoundedGeometry2D<? extends IFinite2DBounds<?>> child : multigeometry) {
            drawGeometry(child, feature, extendedRegion, renderingStyle, scaler, drawer, allSymbols, hasGroupableSymbols);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;

         final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols = renderingStyle.getPointSymbols(
                  point, feature, scaler);
         addSymbols(symbols, allSymbols, hasGroupableSymbols);
      }
      else if (geometry instanceof ICurve2D<?>) {
         final ICurve2D<? extends IFinite2DBounds<?>> curve = (ICurve2D<? extends IFinite2DBounds<?>>) geometry;

         final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols = renderingStyle.getCurveSymbols(
                  curve, feature, scaler);
         addSymbols(symbols, allSymbols, hasGroupableSymbols);
      }
      else if (geometry instanceof ISurface2D<?>) {
         final ISurface2D<? extends IFinite2DBounds<?>> surface = (ISurface2D<? extends IFinite2DBounds<?>>) geometry;

         final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols = renderingStyle.getSurfaceSymbols(
                  surface, feature, scaler);
         addSymbols(symbols, allSymbols, hasGroupableSymbols);
      }
      else {
         System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
      }

   }


   private static void addSymbols(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                  final List<GStyled2DGeometry<?>> allSymbols,
                                  final GHolder<Boolean> hasGroupableSymbols) {
      if (symbols == null) {
         return;
      }

      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols) {
         if (symbol != null) {
            allSymbols.add(symbol);
            if (symbol.isGroupable()) {
               hasGroupableSymbols.set(true);
            }
         }
      }
   }


}
