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

import java.util.Arrays;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GProgress;


public class GKDMultipleLeafNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDLeafNode<VectorT, VertexT> {

   private final int[] _verticesIndexes;


   GKDMultipleLeafNode(final GKDInnerNode<VectorT, VertexT> parent,
                       final int[] verticesIndexes,
                       final GProgress progress) {
      super(parent);

      final int verticesCount = verticesIndexes.length;
      _verticesIndexes = Arrays.copyOf(verticesIndexes, verticesCount);
      progress.stepsDone(verticesCount);
   }


   @Override
   public int[] getVerticesIndexes() {
      return Arrays.copyOf(_verticesIndexes, _verticesIndexes.length);
   }


   @Override
   public String toString() {
      return "GKDMultipleLeafNode [key=" + getKeyString() + ", parent=" + getParentKeyString() + ", verticesIndexes="
             + Arrays.toString(_verticesIndexes) + ", size=" + getSize() + "]";
   }


   @Override
   public IVertexContainer<VectorT, VertexT, ?> getVertices() {
      return getOriginalVertices().asSubContainer(_verticesIndexes);
   }


   //   @Override
   //   protected WeightedVertex<VectorT> calculateAverageVertex() {
   //      final IVertexContainer<VectorT, ?> vertices = getVertices().createSubContainer(_verticesIndexes);
   //
   //      return vertices.getAverage();
   //   }
}
