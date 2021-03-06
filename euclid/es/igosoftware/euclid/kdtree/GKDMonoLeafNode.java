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

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GProgress;


public class GKDMonoLeafNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDLeafNode<VectorT, VertexT> {

   private final int _vertexIndex;


   GKDMonoLeafNode(final GKDInnerNode<VectorT, VertexT> parent,
                   final int vertexIndex,
                   final GProgress progress) {
      super(parent);
      _vertexIndex = vertexIndex;
      progress.stepDone();
   }


   @Override
   public String toString() {
      return "GKDMonoLeafNode [key=" + getKeyString() + ", parent=" + getParentKeyString() + ", vertexIndex=" + _vertexIndex
             + ", size=" + getSize() + "]";
   }


   @Override
   public int[] getVerticesIndexes() {
      return new int[] { _vertexIndex };
   }


   @Override
   public IVertexContainer<VectorT, VertexT, ?> getVertices() {
      return getOriginalVertices().asSubContainer(new int[] { _vertexIndex });
   }


   //   @Override
   //   protected WeightedVertex<VectorT> calculateAverageVertex() {
   //      final IVertexContainer<VectorT, ?> vertices = getOriginalVertices();
   //
   //      final VectorT point = vertices.getPoint(_vertexIndex);
   //      final float intentity = vertices.getIntensity(_vertexIndex);
   //      final VectorT normal = vertices.getNormal(_vertexIndex);
   //      final IColor color = vertices.getColor(_vertexIndex);
   //      final int weight = 1;
   //
   //      return new WeightedVertex<VectorT>(point, intentity, normal, color, weight);
   //   }


}
