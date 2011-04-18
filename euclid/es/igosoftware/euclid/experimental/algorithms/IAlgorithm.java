

package es.igosoftware.euclid.experimental.algorithms;

import es.igosoftware.euclid.vector.IVector;


public interface IAlgorithm<

ParametersVectorT extends IVector<ParametersVectorT, ?>,

ParametersT extends IAlgorithmParameters<ParametersVectorT>,

ResultVectorT extends IVector<ResultVectorT, ?>,

ResultT extends IAlgorithmResult<ResultVectorT>

> {


   public ResultT process(final ParametersT parameters);

}
