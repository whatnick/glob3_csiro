/**
 * 
 */
package es.igosoftware.euclid.verticescontainer;

import java.util.Iterator;
import java.util.NoSuchElementException;

import es.igosoftware.euclid.vector.IVector;

final class GPointsIterator<VectorT extends IVector<VectorT, ?>>
         implements
            Iterator<VectorT> {

   private final IVertexContainer<VectorT, ?, ?> _container;
   private final int                             _size;


   private int                                   _cursor = 0;


   public GPointsIterator(final IVertexContainer<VectorT, ?, ?> container) {
      _container = container;
      _size = _container.size();
   }


   @Override
   public boolean hasNext() {
      return _cursor < _size;
   }


   @Override
   public VectorT next() {
      if (_cursor >= _size) {
         throw new NoSuchElementException();
      }
      return _container.getPoint(_cursor++);
   }


   @Override
   public void remove() {
      throw new RuntimeException("remove not supported");
   }
}
