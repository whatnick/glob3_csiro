/**
 * 
 */
package es.igosoftware.euclid.verticescontainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import es.igosoftware.euclid.vector.IVector;

final class GVertexIterator<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         implements
            Iterator<VertexT> {

   private final IVertexContainer<VectorT, VertexT, ?> _container;
   private final int                                   _size;


   private int                                         _cursor = 0;


   public GVertexIterator(final IVertexContainer<VectorT, VertexT, ?> container) {
      _container = container;
      _size = _container.size();
   }


   @Override
   public boolean hasNext() {
      return _cursor < _size;
   }


   @Override
   public VertexT next() {
      if (_cursor >= _size) {
         throw new NoSuchElementException();
      }
      return _container.getVertex(_cursor++);
   }


   @Override
   public void remove() {
      throw new RuntimeException("remove not supported");
   }
}
