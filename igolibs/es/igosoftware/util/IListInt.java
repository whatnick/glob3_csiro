package es.igosoftware.util;

import java.io.Serializable;


public interface IListInt
         extends
            Serializable {

   public boolean add(final int element);


   public IListInt subList(final int fromIndex,
                           final int toIndex);


   public boolean addAll(final IListInt elements);


   public boolean addAll(final int[] elements);


   public void clear();


   public boolean contains(final int element);


   public boolean containsAll(final IListInt elements);


   public boolean containsAll(final int[] elements);


   public int get(final int index);


   public int indexOf(final int element);


   public boolean isEmpty();


   public int lastIndexOf(final int element);


   public boolean removeByValue(final int element);


   public int removeByIndex(final int index);


   public boolean removeAll(final IListInt elements);


   public boolean removeAll(final int[] elements);


   public int set(final int index,
                  final int element);


   public int size();


   public int[] toArray();


   public void trimToSize();


   public void ensureCapacity(int minCapacity);

}
