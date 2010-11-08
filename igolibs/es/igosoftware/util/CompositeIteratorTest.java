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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class CompositeIteratorTest {

   @Test
   public void testEmptyIterator() {
      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      assertFalse("Exausted Iterator", iterator.hasNext());
   }


   @Test
   public void testEmptyIterator2() {
      final List<Integer> list1 = new ArrayList<Integer>();
      final List<Integer> list2 = new ArrayList<Integer>();
      final List<Integer> list3 = new ArrayList<Integer>();
      list3.add(1);
      list3.add(2);
      list3.add(3);
      list3.add(4);
      list3.add(5);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exausted Iterator", iterator.hasNext());
   }


   @Test
   public void testEmptyIterator3() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);
      list1.add(4);
      list1.add(5);
      final List<Integer> list2 = new ArrayList<Integer>();
      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator1() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);
      list1.add(4);
      list1.add(5);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator2() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);

      final List<Integer> list2 = new ArrayList<Integer>();
      list2.add(4);
      list2.add(5);

      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Integer> list4 = new ArrayList<Integer>();
      list2.add(6);

      final List<List<Integer>> lists = new ArrayList<List<Integer>>();
      lists.add(list1);
      lists.add(list2);
      lists.add(list3);
      lists.add(list4);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());
      listsIterator.add(list4.iterator());

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 6; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator3() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);

      final List<Integer> list2 = new ArrayList<Integer>();
      list2.add(4);
      list2.add(5);

      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Integer> list4 = new ArrayList<Integer>();
      list2.add(6);

      final List<List<Integer>> lists = new ArrayList<List<Integer>>();
      lists.add(list1);
      lists.add(list2);
      lists.add(list3);
      lists.add(list4);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());
      listsIterator.add(list4.iterator());

      final Iterator<Integer> iterator = new CompositeIterator<Integer>(listsIterator);

      int i = 0;
      while (iterator.hasNext()) {
         i++;
         iterator.next();
      }

      assertEquals("i " + i, 6, i);
   }
}
