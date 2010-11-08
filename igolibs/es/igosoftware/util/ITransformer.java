/**
 * 
 */
package es.igosoftware.util;


public interface ITransformer<ElementT, ResultT> {
   public ResultT transform(final ElementT element);
}
