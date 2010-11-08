package es.igosoftware.euclid.shape;

import java.util.Arrays;
import java.util.Collection;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public class GColinearException
         extends
            Exception {

   private static final long serialVersionUID = 1L;


   public GColinearException(final IVector<?, ?>... vectors) {
      super("GColinearException, vectors=" + Arrays.toString(vectors));
   }


   public GColinearException(final Collection<?> vectors) {
      super("GInsufficientPointsException, vectors=" + vectors);
   }


   public GColinearException(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices) {
      super("GInsufficientPointsException, vertices=" + vertices);
   }


}
