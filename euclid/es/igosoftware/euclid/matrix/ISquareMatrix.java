package es.igosoftware.euclid.matrix;

import es.igosoftware.euclid.vector.IVector;


public interface ISquareMatrix<MatrixT, VectorT extends IVector<VectorT, ?>>
         extends
            IMatrix<MatrixT, VectorT> {


   public double determinant();


   public MatrixT mul(final MatrixT that);


   public MatrixT inverted();


   public boolean isIdentity();

}
