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


package es.igosoftware.pointscloud.compiler;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.kdtree.GKDInnerNode;
import es.igosoftware.euclid.kdtree.GKDLeafNode;
import es.igosoftware.euclid.kdtree.GKDTree;
import es.igosoftware.euclid.kdtree.IKDTreeVisitor;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.octree.IOctreeVisitor;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.IEvaluator;
import es.igosoftware.util.GLoggerObject;
import es.igosoftware.utils.GConverter;
import gov.nasa.worldwind.geom.Position;


public class GPointsCloudLODGenerator
         extends
            GLoggerObject {


   private static class Tile {
      private final String                                                                 _id;
      private final int                                                                    _pointsCount;
      private final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> _vertices;
      private final GProjection                                                            _projection;
      //      private final GAxisAlignedBox                  _bounds;

      //      private final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> _sortedVertices;
      private final List<Integer>                                                          _sortedVertices;
      private final List<Integer>                                                          _lodIndices;


      private Tile(final GProjection projection,
                   final GOTLeafNode leaf) {
         _id = leaf.getId();

         _projection = projection;
         _vertices = leaf.getVertices();
         _pointsCount = _vertices.size();

         //         _bounds = leaf.getBounds();

         //         final IVector3<?> referencePoint = _vertices.getAverage()._point;
         //         _sortedVertices = _vertices.newEmptyContainer(_pointsCount, referencePoint);
         _sortedVertices = new ArrayList<Integer>(_pointsCount);
         _lodIndices = new ArrayList<Integer>();
      }


      //      @Override
      //      public String toString() {
      //         return "Tile # " + _id + " [bounds=" + _bounds + ", projection=" + _projection + ", vertices=" + _vertices + "]";
      //         //return "Tile #" + _id + ", vertices=" + _vertices.size();
      //      }


      private void process(final GProgress progress,
                           final String outputDirectoryName,
                           final Map<String, GPCLeafNode> leafNodes) {
         if (_vertices.size() == 1) {
            // just one vertex, no need to sort
            _lodIndices.add(0);
            _sortedVertices.add(0);
         }
         else {
            sortVertices();
         }

         // System.out.println(_lodIndices + ", " + _sortedVertices.size());

         validate();

         final IVector3<?> referencePoint = _vertices.getAverage()._point;
         save(outputDirectoryName, referencePoint);

         final GPCLeafNode leafNode = leafNodes.get(_id);
         leafNode.setLodIndices(_lodIndices);
         leafNode.setReferencePoint(referencePoint);

         progress.stepDone();
      }


      private void validate() {
         if (_sortedVertices.size() != _vertices.size()) {
            throw new RuntimeException("Internal Error, sortedVertices (" + _sortedVertices.size()
                                       + ") has different size than vertices (" + _vertices.size() + ")");
         }
      }


      private void save(final String outputDirectoryName,
                        final IVector3<?> referencePoint) {
         final String outputFileNameSansExtension = new File(new File(outputDirectoryName), "tile-" + _id).getPath();

         try {
            //            saveHeader(outputFileNameSansExtension);

            //            final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> sortedVertices = _vertices.createSubContainer(GCollections.toArray(_sortedVertices));
            //            GBinaryPoints3Loader.save(sortedVertices, _projection, outputFileNameSansExtension + ".bp", false);
            savePoints(outputFileNameSansExtension, referencePoint);
         }
         catch (final IOException e) {
            e.printStackTrace(System.err);
         }
      }


      private void savePoints(final String outputFileNameSansExtension,
                              final IVector3<?> referencePoint) throws IOException {
         final String pointsFileName = outputFileNameSansExtension + ".points";
         final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pointsFileName)));

         final boolean hasIntensities = _vertices.hasIntensities();
         final boolean hasNormals = _vertices.hasNormals();
         final boolean hasColors = _vertices.hasColors();

         final Position referenceReprojected = GConverter.toPosition(_projection, referencePoint);

         for (final int index : _sortedVertices) {
            final Position pointReprojected = GConverter.toPosition(_projection, _vertices.getPoint(index));
            //            final IVector3<?> point = _vertices.getPoint(index).sub(referencePoint);
            //            saveVector3F(output, point);
            output.writeFloat((float) (pointReprojected.latitude.radians - referenceReprojected.latitude.radians));
            output.writeFloat((float) (pointReprojected.longitude.radians - referenceReprojected.longitude.radians));
            output.writeFloat((float) (pointReprojected.elevation - referenceReprojected.elevation));

            if (hasIntensities) {
               output.writeFloat(_vertices.getIntensity(index));
            }
            if (hasNormals) {
               saveVector3F(output, _vertices.getNormal(index));
            }
            if (hasColors) {
               saveColorInt(output, _vertices.getColor(index));
            }
         }

         output.close();
      }


      private void saveVector3F(final DataOutputStream output,
                                final IVector3<?> vector) throws IOException {
         output.writeFloat((float) vector.x());
         output.writeFloat((float) vector.y());
         output.writeFloat((float) vector.z());
      }


      private void saveColorInt(final DataOutputStream output,
                                final IColor color) throws IOException {
         output.writeInt(GColorI.getRGB(color));
      }


      //      private void saveHeader(final String outputFileNameSansExtension) throws IOException {
      //         final String headerFileName = outputFileNameSansExtension + ".header";
      //
      //         final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(headerFileName)));
      //
      //         // save points count
      //         output.writeInt(_pointsCount);
      //
      //         // save bounds
      //         saveVector3D(output, _bounds._lower);
      //         saveVector3D(output, _bounds._upper);
      //
      //         // save lod indices
      //         output.writeInt(_lodIndices.size());
      //         for (final int lodIndex : _lodIndices) {
      //            output.writeInt(lodIndex);
      //         }
      //
      //         output.close();
      //      }


      //      private void saveVector3D(final DataOutputStream output,
      //                                final IVector3<?> vector) throws IOException {
      //         output.writeDouble(vector.x());
      //         output.writeDouble(vector.y());
      //         output.writeDouble(vector.z());
      //      }


      private void sortVertices() {
         final GKDTree<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>> tree = new GKDTree<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>>(
                  "Tile #" + _id, _vertices, false);


         //         final List<Integer> toProcessIndices = new LinkedList<Integer>();
         //         //         final List<Integer> toProcessIndices = new ArrayList<Integer>(_pointsCount);
         //         final IVector3<?>[] points = new IVector3<?>[_pointsCount];
         //         for (int i = 0; i < _pointsCount; i++) {
         //            toProcessIndices.add(i);
         //            points[i] = _vertices.getPoint(i);
         //         }

         tree.breadthFirstAcceptVisitor(new IKDTreeVisitor<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>>() {
            private int _lastDepth = 0;


            @Override
            public void visitInnerNode(final GKDInnerNode<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>> innerNode) {
               //               pushVertex(innerNode);
               pushVertexIndex(innerNode.getVertexIndex(), innerNode.getDepth());
            }


            @Override
            public void visitLeafNode(final GKDLeafNode<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>> leafNode) {
               //               pushVertex(leafNode);
               pushVertexIndex(leafNode.getVerticesIndexes()[0], leafNode.getDepth());
            }


            //            private void pushVertex(final GKDNode<IVector3<?>> node) throws IKDTreeVisitor.AbortVisiting {
            //               if (toProcessIndices.isEmpty()) {
            //                  throw new IKDTreeVisitor.AbortVisiting();
            //               }
            //
            //               final int depth = node.getDepth();
            //
            //               if (_lastDepth != depth) {
            //                  _lastDepth = depth;
            //
            //                  final int sortedVerticesCount = _sortedVertices.size();
            //                  if (sortedVerticesCount > 0) {
            //                     _lodIndices.add(sortedVerticesCount - 1);
            //                  }
            //               }
            //
            //
            //               final IVector3<?> target;
            //               if (node instanceof GKDInnerNode) {
            //                  target = ((GKDInnerNode<IVector3<?>>) node).getBounds()._center;
            //               }
            //               else if (node instanceof GKDLeafNode) {
            //                  target = ((GKDLeafNode<IVector3<?>>) node).getAverageVertex()._point;
            //               }
            //               else {
            //                  throw new RuntimeException("Invalid node type " + node);
            //               }
            //
            //
            //               int nearestIndex = -1;
            //               double shortestDistance = Double.POSITIVE_INFINITY;
            //               for (final int i : toProcessIndices) {
            //                  final double currentDistance = points[i].squaredDistance(target);
            //
            //                  if (currentDistance < shortestDistance) {
            //                     shortestDistance = currentDistance;
            //                     nearestIndex = i;
            //                  }
            //               }
            //
            //
            //               toProcessIndices.remove(new Integer(nearestIndex)); // Integer (instead of of int) to remove by contents and not by index
            //               _sortedVertices.add(nearestIndex);
            //            }


            private void pushVertexIndex(final int vertexIndex,
                                         final int depth) {
               if (_lastDepth != depth) {
                  _lastDepth = depth;

                  final int sortedVerticesCount = _sortedVertices.size();
                  if (sortedVerticesCount > 0) {
                     _lodIndices.add(sortedVerticesCount - 1);
                  }
               }

               _sortedVertices.add(vertexIndex);
            }


            @Override
            public void endVisiting(final GKDTree<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>> kdtree) {
            }


            @Override
            public void startVisiting(final GKDTree<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>> kdtree) {
            }

         });

         //         _lodIndices.add(sortedVerticesSize.intValue() - 1);
         _lodIndices.add(_sortedVertices.size() - 1);
      }
   }


   private final double                                                                 _maxLeafSideLength;
   private final int                                                                    _maxLeafVertices;
   private final String                                                                 _outputDirectoryName;
   private final GProjection                                                            _projection;
   private final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> _vertices;


   public GPointsCloudLODGenerator(final String fileNames,
                                   final double maxLeafSideLength,
                                   final int maxLeafVertices,
                                   final String outputDirectoryName) throws IOException {
      _maxLeafSideLength = maxLeafSideLength;
      _maxLeafVertices = maxLeafVertices;
      _outputDirectoryName = outputDirectoryName;

      final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(fileNames, GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE);

      loader.load();

      _projection = loader.getProjection();
      _vertices = loader.getVertices();
   }


   public GPointsCloudLODGenerator(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices,
                                   final GProjection projection,
                                   final double maxLeafSideLength,
                                   final int maxLeafVertices,
                                   final String outputDirectoryName) {
      _maxLeafSideLength = maxLeafSideLength;
      _maxLeafVertices = maxLeafVertices;
      _outputDirectoryName = outputDirectoryName;

      _projection = projection;
      _vertices = vertices;
   }


   @Override
   public boolean logVerbose() {
      return true;
   }


   public void process() throws IOException {
      //      cleanupOutputDirectory();
      GIOUtils.assureEmptyDirectory(_outputDirectoryName, false);

      processVertices();
   }


   private void processVertices() {
      logInfo("Processing vertices: " + _vertices + "...");


      final GHolder<GPCPointsCloud> octreeRepresentation = new GHolder<GPCPointsCloud>(null);


      final Map<String, GPCLeafNode> leafNodes = new HashMap<String, GPCLeafNode>();
      final List<GPointsCloudLODGenerator.Tile> tiles = createTiles(leafNodes, octreeRepresentation);
      processTiles(tiles, leafNodes);


      try {
         // octree.save(_outputDirectoryName);
         saveOctree(octreeRepresentation.get());
      }
      catch (final IOException e) {
         e.printStackTrace();
      }
   }


   private void processTiles(final List<GPointsCloudLODGenerator.Tile> tiles,
                             final Map<String, GPCLeafNode> leafNodes) {
      logInfo("Processing " + tiles.size() + " tiles...");

      final GProgress progress = new GProgress(tiles.size()) {
         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logInfo("  Processing Tiles: " + progressString(percent, elapsed, estimatedMsToFinish));
         }
      };

      GCollections.concurrentEvaluate(tiles, new IEvaluator<GPointsCloudLODGenerator.Tile>() {
         @Override
         public void evaluate(final GPointsCloudLODGenerator.Tile tile) {
            tile.process(progress, _outputDirectoryName, leafNodes);
         }
      }, GConcurrent.AVAILABLE_PROCESSORS * 8);

   }


   private List<GPointsCloudLODGenerator.Tile> createTiles(final Map<String, GPCLeafNode> leafNodes,
                                                           final GHolder<GPCPointsCloud> octreeRepresentation) {
      final ArrayList<GPointsCloudLODGenerator.Tile> tiles = new ArrayList<GPointsCloudLODGenerator.Tile>();

      final GOctree.DuplicatesPolicy duplicatesPolicy = new GOctree.DuplicatesPolicy() {
         @Override
         public int[] removeDuplicates(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices1,
                                       final int[] verticesIndexes) {
            final Set<IVector3<?>> selectedPoints = new HashSet<IVector3<?>>();
            final List<Integer> selectedIndices = new ArrayList<Integer>();

            for (final int index : verticesIndexes) {
               final IVector3<?> point = vertices1.getPoint(index);
               if (!selectedPoints.contains(point)) {
                  //                  final boolean anyCloseTo = GCollections.anySatisfy(selectedPoints, new IPredicate<IVector3<?>>() {
                  //                     @Override
                  //                     public boolean evaluate(final IVector3<?> element) {
                  //                        return element.closeTo(point, GMath.DEFAULT_NUMERICAL_PRECISION_FLOAT);
                  //                     }
                  //                  });
                  //
                  //                  if (!anyCloseTo) {
                  selectedPoints.add(point);
                  selectedIndices.add(index);
                  //                  }
               }
            }

            return GCollections.toArray(selectedIndices);
         }
      };

      //      final GOctree.Parameters parameters = new GOctree.Parameters(_maxLeafSideLength, _maxLeafVertices, true, true, false);
      final GOctree.Parameters parameters = new GOctree.Parameters(-1, _maxLeafSideLength, _maxLeafVertices, true, true, false,
               true, duplicatesPolicy, new GOctree.DynamicAxisNodesCreationPolicy(true));

      final GOctree octree = new GOctree("Tiles", _vertices, parameters);

      octreeRepresentation.set(generateOctreeRepresentation(octree, leafNodes));

      octree.breadthFirstAcceptVisitor(new IOctreeVisitor() {
         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            final GPointsCloudLODGenerator.Tile tile = new GPointsCloudLODGenerator.Tile(_projection, leaf);
            tiles.add(tile);
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
         }


         @Override
         public void visitOctree(final GOctree octree1) {
         }
      });

      tiles.trimToSize();

      return tiles;
   }


   private GPCPointsCloud generateOctreeRepresentation(final GOctree octree,
                                                       final Map<String, GPCLeafNode> leafNodes) {

      final int verticesCount = _vertices.size();

      final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices = octree.getOriginalVertices();

      float minIntensity = Float.POSITIVE_INFINITY;
      float maxIntensity = Float.NEGATIVE_INFINITY;
      double minElevation = Double.POSITIVE_INFINITY;
      double maxElevation = Double.NEGATIVE_INFINITY;
      //      if (vertices.hasIntensities()) {
      for (int i = 0; i < vertices.size(); i++) {
         final float intensity = vertices.getIntensity(i);
         if (intensity > maxIntensity) {
            maxIntensity = intensity;
         }
         if (intensity < minIntensity) {
            minIntensity = intensity;
         }

         final double elevation = vertices.getPoint(i).z();
         if (elevation > maxElevation) {
            maxElevation = elevation;
         }
         if (elevation < minElevation) {
            minElevation = elevation;
         }
      }
      //      }

      System.out.println("Intensity: min=" + minIntensity + ", max=" + maxIntensity);
      System.out.println("Elevation: min=" + minElevation + ", max=" + maxElevation);

      return new GPCPointsCloud(octree.getRoot(), leafNodes, _projection, verticesCount, vertices.hasIntensities(),
               vertices.hasNormals(), vertices.hasColors(), minIntensity, maxIntensity, minElevation, maxElevation);
   }


   private void saveOctree(final GPCPointsCloud pointsCloud) throws IOException {
      ObjectOutputStream output = null;
      try {
         final String fileName = _outputDirectoryName + "/tree.object.gz";
         //output = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(fileName))));
         output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName), 2048));

         output.writeObject(pointsCloud);
      }
      finally {
         GIOUtils.gentlyClose(output);
      }
   }


   //   private static GOctree.NodesCreationPolicy createNodesCreationPolicy(final boolean splitByOneAxis) {
   //      return new GOctree.NodesCreationPolicy() {
   //         private int _xySubdivisions  = 0;
   //         private int _xyzSubdivisions = 0;
   //         private int _xSubdivisions   = 0;
   //         private int _ySubdivisions   = 0;
   //         private int _zSubdivisions   = 0;
   //
   //
   //         @Override
   //         public Object createAuxiliaryObject(final GOctree octree,
   //                                             final int[] verticesIndexes,
   //                                             final GAxisAlignedBox innerBounds,
   //                                             final int depth) {
   //            final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> subVertices = octree.getOriginalVertices().asSubContainer(verticesIndexes);
   //            final IVector3<?> average = subVertices.getAverage()._point;
   //            final GAxisAlignedBox bounds = (GAxisAlignedBox) subVertices.getBounds();
   //            //            final GAxisAlignedBox bounds = innerBounds;
   //            final IVector3<?> extents = bounds._extent;
   //
   //            if (splitByOneAxis) {
   //               if ((extents.x() > extents.y()) && (extents.x() > extents.z())) {
   //                  _xSubdivisions++;
   //                  return bounds.subdividedByX(average.x());
   //               }
   //               else if ((extents.y() > extents.x()) && (extents.y() > extents.z())) {
   //                  _ySubdivisions++;
   //                  return bounds.subdividedByY(average.y());
   //               }
   //               else {
   //                  _zSubdivisions++;
   //                  return bounds.subdividedByZ(average.z());
   //               }
   //            }
   //
   //
   //            final double minXYExtent = Math.min(extents.x(), extents.y());
   //
   //            if (minXYExtent >= extents.z()) {
   //               _xySubdivisions++;
   //               return bounds.subdividedByXY(average.asVector2());
   //            }
   //
   //            _xyzSubdivisions++;
   //            return bounds.subdividedByXYZ(average);
   //
   //         }
   //
   //
   //         @Override
   //         public int getMaxNodes(final Object auxiliaryObject) {
   //            return splitByOneAxis ? 2 : 8;
   //            //            return 2;
   //         }
   //
   //
   //         @Override
   //         public byte getNodeKey(final Object auxiliaryObject,
   //                                final GOctree octree,
   //                                final int vertexIndex) {
   //            final GAxisAlignedBox[] bounds = (GAxisAlignedBox[]) auxiliaryObject;
   //
   //            final IVector3<?> point = octree.getPoint(vertexIndex);
   //            for (int i = 0; i < bounds.length; i++) {
   //               if (bounds[i].contains(point)) {
   //                  return (byte) i;
   //               }
   //            }
   //
   //            throw new RuntimeException("No bounds contains the point=" + point + " Bounds=" + Arrays.toString(bounds));
   //         }
   //
   //
   //         @Override
   //         public GAxisAlignedBox getNodeBounds(final Object auxiliaryObject,
   //                                              final GAxisAlignedBox innerBounds,
   //                                              final byte nodeKey) {
   //            final GAxisAlignedBox[] bounds = (GAxisAlignedBox[]) auxiliaryObject;
   //            return bounds[nodeKey];
   //         }
   //
   //
   //         @Override
   //         public String getStatistics() {
   //            //            return "X Subdivisions=" + _xSubdivisions + ", Y Subdivisions=" + _ySubdivisions + ", Z Subdivisions="
   //            //                   + _zSubdivisions;
   //            //            return "XY Subdivisions=" + _xySubdivisions + ", XYZ Subdivisions=" + _xyzSubdivisions;
   //
   //            return "XY Subdivisions=" + _xySubdivisions + ", XYZ Subdivisions=" + _xyzSubdivisions + ", X Subdivisions="
   //                   + _xSubdivisions + ", Y Subdivisions=" + _ySubdivisions + ", Z Subdivisions=" + _zSubdivisions;
   //         }
   //      };
   //}
}
