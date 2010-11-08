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

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IBoundingVolume;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.LoggerObject;

public abstract class GOTComponent
         extends
            LoggerObject {


   public abstract GAxisAlignedBox getBounds();


   public abstract int getLeafNodesCount();


   public abstract int getInnerNodesCount();


   public abstract int getDepth();


   public abstract int[] getVerticesIndexes();


   public abstract int getVerticesIndexesCount();


   public abstract GOTInnerNode getRoot();


   public abstract int[] getVerticesIndexesInRegion(final IBoundingVolume<?> region);


   public abstract IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> getVertices();


   public abstract void depthFirstAcceptVisitor(final IOctreeVisitorWithFinalization visitor) throws IOctreeVisitor.AbortVisiting;


   protected abstract int[] getVerticesIndexesInRegion(final IBoundingVolume<?> region,
                                                       final GOTLeafNode excludedLeaf);

}
