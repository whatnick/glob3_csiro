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


package es.igosoftware.euclid.kdtree;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GProgress;


public class GKDInnerNode<VectorT extends IVector<VectorT, ?, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDNode<VectorT, VertexT> {

   private final byte                              _splitAxis;

   private final GKDNode<VectorT, VertexT>         _left;
   private final GKDNode<VectorT, VertexT>         _right;

   private final GAxisAlignedOrthotope<VectorT, ?> _bounds;

   private final int                               _vertexIndex;


   public GKDInnerNode(final GKDTree<VectorT, VertexT> tree,
                       final GKDInnerNode<VectorT, VertexT> parent,
                       final byte splitAxis,
                       final int vertexIndex,
                       final IVertexContainer<VectorT, VertexT, ?> vertices,
                       final GAxisAlignedOrthotope<VectorT, ?> bounds,
                       final GAxisAlignedOrthotope<VectorT, ?> leftBounds,
                       final GHolder<int[]> leftVerticesIndexes,
                       final GAxisAlignedOrthotope<VectorT, ?> rightBounds,
                       final GHolder<int[]> rightVerticesIndexes,
                       final double[][] axisValues,
                       final GProgress progress,
                       final ExecutorService executor) throws InterruptedException, ExecutionException {
      super(parent);
      _bounds = bounds;
      _splitAxis = splitAxis;
      _vertexIndex = vertexIndex;
      progress.stepDone();

      final Future<GKDNode<VectorT, VertexT>> leftFuture = executor.submit(new Callable<GKDNode<VectorT, VertexT>>() {
         @Override
         public GKDNode<VectorT, VertexT> call() throws Exception {
            return createNode(tree, GKDInnerNode.this, vertices, leftBounds, leftVerticesIndexes, axisValues, progress, executor);
         }
      });

      final Future<GKDNode<VectorT, VertexT>> rightFuture = executor.submit(new Callable<GKDNode<VectorT, VertexT>>() {
         @Override
         public GKDNode<VectorT, VertexT> call() throws Exception {
            return createNode(tree, GKDInnerNode.this, vertices, rightBounds, rightVerticesIndexes, axisValues, progress,
                     executor);
         }
      });

      _left = leftFuture.get();
      _right = rightFuture.get();
   }


   @Override
   protected void depthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor) throws IKDTreeVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      if (_left != null) {
         _left.depthFirstAcceptVisitor(visitor);
      }

      if (_right != null) {
         _right.depthFirstAcceptVisitor(visitor);
      }
   }


   @Override
   public String toString() {
      //      return "GKDInnerNode [location=" + _location + ", splitAxis=" + _splitAxis + ", size=" + getSize() + ", left="
      //             + (_left != null) + ", right=" + (_right != null) + "]";
      //      return "GKDInnerNode [id=" + _id + ", parent=" + parentID() + ", location=" + _location + ", splitAxis=" + _splitAxis
      //      + ", size=" + getSize() + ", chidren=" + getChildrenCount() + "]";
      return "GKDInnerNode [key=" + getKeyString() + ", parent=" + getParentKeyString() + ", bounds=" + _bounds + ", splitAxis="
             + _splitAxis + ", size=" + getSize() + ", chidren=" + getChildrenCount() + ", vertexIndex=" + _vertexIndex + "]";
   }


   public byte getChildrenCount() {
      return (byte) (((_left == null) ? 0 : 1) + ((_right == null) ? 0 : 1));
   }


   @Override
   public boolean isLeaf() {
      return false;
   }


   @Override
   public int getSize() {
      return 1 /*itself counts*/+ (_left == null ? 0 : _left.getSize()) + (_right == null ? 0 : _right.getSize());
   }


   @Override
   protected void breadthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor,
                                            final LinkedList<GKDNode<VectorT, VertexT>> queue)
                                                                                              throws IKDTreeVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      if (_left != null) {
         queue.addLast(_left);
      }
      if (_right != null) {
         queue.addLast(_right);
      }
   }


   final int getKeyForChild(final GKDNode<VectorT, VertexT> child) {
      if (child == _left) {
         return 0;
      }
      if (child == _right) {
         return 1;
      }
      throw new IllegalArgumentException(child + " is not child of " + this);
   }


   //   @Override
   //   protected WeightedVertex<VectorT> calculateAverageVertex() {
   //      // force lazy initialization
   //      if (_left != null) {
   //         _left.getAverageVertex();
   //      }
   //      if (_right != null) {
   //         _right.getAverageVertex();
   //      }
   //
   //      final List<WeightedVertex<VectorT>> nodesAverages = new ArrayList<WeightedVertex<VectorT>>(2);
   //
   //
   //      //      final IVertexContainer<VectorT, VertexT, ?> vertices = getOriginalVertices();
   //
   //      //      final VectorT point = vertices.getPoint(_vertexIndex);
   //      //      final float intentity = vertices.getIntensity(_vertexIndex);
   //      //      final VectorT normal = vertices.getNormal(_vertexIndex);
   //      //      final IColor color = vertices.getColor(_vertexIndex);
   //      //      final int weight = 1;
   //      //
   //      //      nodesAverages.add(new WeightedVertex<VectorT>(point, intentity, normal, color, weight));
   //
   //      if (_left != null) {
   //         nodesAverages.add(_left.getAverageVertex());
   //      }
   //      if (_right != null) {
   //         nodesAverages.add(_right.getAverageVertex());
   //      }
   //
   //      return IVertexContainer.WeightedVertex.getAverage(nodesAverages);
   //
   //      //      final List<Future<WeightedVertex<IVector3<?>>>> futures = new ArrayList<Future<WeightedVertex<IVector3<?>>>>(
   //      //               _children.length);
   //      //      final ExecutorService executor = GConcurrent.getDefaultExecutor();
   //      //      for (final GOTNode child : _children) {
   //      //         if (node != null) {
   //      //            final Future<WeightedVertex<IVector3<?>>> future = executor.submit(new Callable<WeightedVertex<IVector3<?>>>() {
   //      //               @Override
   //      //               public WeightedVertex<IVector3<?>> call() throws Exception {
   //      //                  return node.getAverageVertex();
   //      //               }
   //      //            });
   //      //            futures.add(future);
   //      //         }
   //      //      }
   //      //
   //      //      return IVertexContainer.WeightedVertex.getAverage(GConcurrent.resolve(futures));
   //   }


   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return _bounds;
   }


   public int getVertexIndex() {
      return _vertexIndex;
   }


   public byte getSplitAxis() {
      return _splitAxis;
   }


}
