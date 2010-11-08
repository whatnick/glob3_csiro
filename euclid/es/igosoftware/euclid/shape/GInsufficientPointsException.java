package es.igosoftware.euclid.shape;

import java.util.Arrays;
import java.util.Collection;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public class GInsufficientPointsException
         extends
            Exception {

   private static final long serialVersionUID = 1L;


   public GInsufficientPointsException(final IVector<?, ?>... vectors) {
      super("GInsufficientPointsException, vectors=" + Arrays.toString(vectors));
   }


   public GInsufficientPointsException(final Collection<?> vectors) {
      super("GInsufficientPointsException, vectors=" + vectors);
   }


   public GInsufficientPointsException(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices) {
      super("GInsufficientPointsException, vertices=" + vertices);
   }

}
