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

package es.igosoftware.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class FilterIterator<T>
         implements
            Iterator<T> {

   final private Iterator<T>         _iterator;
   final private List<IPredicate<T>> _predicates   = new ArrayList<IPredicate<T>>();

   private T                         _currentValue;
   private boolean                   _finished     = false;
   private boolean                   _nextConsumed = true;


   public FilterIterator(final Iterator<T> iterator,
                         final IPredicate<T> predicate) {
      // iterator = iterator1;
      _predicates.add(predicate);

      if (iterator instanceof FilterIterator<?>) {
         final FilterIterator<T> predicateIterator = (FilterIterator<T>) iterator;
         _iterator = predicateIterator._iterator;
         _predicates.addAll(predicateIterator._predicates);
      }
      else {
         _iterator = iterator;
      }
   }


   private boolean accept(final T value) {
      for (final IPredicate<T> predicate : _predicates) {
         if (!predicate.evaluate(value)) {
            return false;
         }
      }
      return true;
   }


   public boolean moveToNextValid() {
      boolean found = false;
      while (!found && _iterator.hasNext()) {
         final T currentValue1 = _iterator.next();
         if (accept(currentValue1)) {
            found = true;
            _currentValue = currentValue1;
            _nextConsumed = false;
         }
      }
      if (!found) {
         _finished = true;
      }
      return found;
   }


   @Override
   public T next() {
      if (!_nextConsumed) {
         _nextConsumed = true;
         return _currentValue;
      }

      if (!_finished) {
         if (moveToNextValid()) {
            _nextConsumed = true;
            return _currentValue;
         }
      }

      throw new NoSuchElementException();
   }


   @Override
   public boolean hasNext() {
      return !_finished && (!_nextConsumed || moveToNextValid());
   }


   @Override
   public void remove() {
      throw new RuntimeException("remove not supported");
   }

}
