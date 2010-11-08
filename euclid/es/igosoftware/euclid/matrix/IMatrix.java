package es.igosoftware.euclid.matrix;

import java.io.PrintStream;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVectorTransformer;

public interface IMatrix<MatrixT, VectorT extends IVector<VectorT, ?>>
         extends
            IVectorTransformer<VectorT> {


   public MatrixT add(final MatrixT that);


   public MatrixT sub(final MatrixT that);


   public MatrixT mul(final double scale);


   public MatrixT transposed();


   public MatrixT negated();


   public void show(final PrintStream out);


   public double get(final int i,
                     final int j);


   public int getRowsCount();


   public int getColumnsCount();


   public boolean isZero();


   //public <VectorT extends IVector<VectorT>> VectorT transform(final VectorT vec);

}
