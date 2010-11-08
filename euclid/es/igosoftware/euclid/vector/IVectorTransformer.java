package es.igosoftware.euclid.vector;

import es.igosoftware.util.ITransformer;

public interface IVectorTransformer<VectorT extends IVector<VectorT, ?>>
         extends
            ITransformer<VectorT, VectorT> {

   //   public VectorT transform(final VectorT vector);

}
