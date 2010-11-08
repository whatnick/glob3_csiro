package es.igosoftware.euclid.vector;

public interface IVectorI<VectorT> {

   public String asParseableString();


   @Override
   public boolean equals(final Object obj);


   public boolean greaterOrEquals(final VectorT that);


   public boolean lessOrEquals(final VectorT that);


   public boolean between(final VectorT min,
                          final VectorT max);


   public VectorT scale(final VectorT that);


   public VectorT scale(final int scale);


   public VectorT add(final VectorT that);


   public VectorT add(final int delta);


   public VectorT sub(final VectorT that);


   public VectorT sub(final int delta);


   public int get(final byte i);


   public int[] getCoordinates();


   public byte dimensions();


}
