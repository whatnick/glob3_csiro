/*
 * Cáceres 3D
 * 
 * Copyright (c) 2008 Junta de Extremadura.
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Funded by European Union. FEDER program.
 * Developed by: IGO SOFTWARE, S.L.
 * 
 * For more information, contact: 
 * 
 *    Junta de Extremadura
 *    Consejería de Cultura y Turismo
 *    C/ Almendralejo 14 Mérida
 *    06800 Badajoz
 *    SPAIN
 * 
 *    Tel: +34 924007009
 *    http://www.culturaextremadura.com
 * 
 *   or
 * 
 *    IGO SOFTWARE, S.L.
 *    Calle Santiago Caldera Nro 4
 *    Cáceres
 *    Spain
 *    Tel: +34 927 629 436
 *    e-mail: support@igosoftware.es
 *    http://www.igosoftware.es
 */

package es.igosoftware.euclid.octree;

import java.util.ArrayList;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GBall;
import es.igosoftware.euclid.bounding.IBoundingVolume;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.Vertex;
import es.igosoftware.euclid.verticescontainer.IVertexContainer.WeightedVertex;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GIntHolder;

public abstract class GOTNode
         extends
            GOTComponent {

   protected final GOTInnerNode        _parent;
   protected final GAxisAlignedBox     _bounds;


   private WeightedVertex<IVector3<?>> _averageVertex;


   protected GOTNode(final GOTInnerNode parent,
                     final GAxisAlignedBox bounds) {
      _parent = parent;
      _bounds = bounds;
   }


   public final GOTInnerNode getParent() {
      return _parent;
   }


   @Override
   public final GAxisAlignedBox getBounds() {
      return _bounds;
   }


   @Override
   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final String getId() {
      if (_parent == null) {
         return "";
      }

      final byte myId = _parent.getChildIndex(this);

      final String parentId = _parent.getId();
      if ((parentId == null) || parentId.isEmpty()) {
         return Byte.toString(myId);
      }

      return parentId + "-" + myId;
   }


   @Override
   public int[] getVerticesIndexes() {
      final List<Integer> list = new ArrayList<Integer>(getVerticesIndexesCount());
      putVerticesIndexesIn(list);
      return GCollections.toArray(list);
   }


   @Override
   public final GOTInnerNode getRoot() {
      if (_parent == null) {
         return (GOTInnerNode) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final int[] getVerticesIndexesInRegion(final IBoundingVolume<?> region) {
      final List<Integer> list = new ArrayList<Integer>();
      putRegionVerticesIndexesIn(region, list);
      return GCollections.toArray(list);
   }


   @Override
   protected final int[] getVerticesIndexesInRegion(final IBoundingVolume<?> region,
                                                    final GOTLeafNode excludedLeaf) {
      final List<Integer> list = new ArrayList<Integer>();
      putRegionVerticesIndexesIn(region, list, excludedLeaf);
      return GCollections.toArray(list);
   }


   @Override
   public final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> getVertices() {
      return getOctree().getOriginalVertices().asSubContainer(getVerticesIndexes());
   }


   protected abstract void putVerticesIndexesIn(final List<Integer> verticesIndexesContainer);


   protected abstract void putRegionVerticesIndexesIn(final IBoundingVolume<?> region,
                                                      final List<Integer> verticesIndexesContainer);


   protected abstract void putRegionVerticesIndexesIn(final IBoundingVolume<?> region,
                                                      final List<Integer> verticesIndexesContainer,
                                                      final GOTLeafNode excludedLeaf);


   @Override
   public final boolean logVerbose() {
      return getOctree().logVerbose();
   }


   //   protected abstract boolean getNearestLeaf(final IVector3<?> point,
   //                                             final GHolder<GOTLeafNode> nearestLeafHolder,
   //                                             final GHolder<Double> shortestSquaredDistance);


   protected abstract WeightedVertex<IVector3<?>> calculateAverageVertex();


   public final synchronized WeightedVertex<IVector3<?>> getAverageVertex() {
      if (_averageVertex == null) {
         _averageVertex = calculateAverageVertex();
      }
      return _averageVertex;

      //      return calculateAverageVertex();
   }


   public GOctree getOctree() {
      return _parent.getOctree();
   }


   protected abstract boolean removeVertex(final Vertex<IVector3<?>> vertex,
                                           final int index);


   public abstract boolean isEmpty();


   protected abstract int getAnyVertexIndex();


   protected abstract boolean getNearestVertexIndex(final GOctree octree,
                                                    final GHolder<GBall> hotRegionHolder,
                                                    final GIntHolder candidateIndexHolder);


   //   protected abstract void save(final String rootDirectoryName) throws IOException;

}
