package es.igosoftware.euclid.loading;

import java.io.IOException;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GUtils;
import es.igosoftware.util.LoggerObject;

public abstract class GPointsLoader<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            LoggerObject {

   public static final int DEFAULT_FLAGS = 0;
   public static final int VERBOSE       = 1;


   private final int       _flags;
   private boolean         _loaded       = false;


   protected GPointsLoader(final int flags) {
      _flags = flags;
   }


   protected final boolean isFlagged(final int flags) {
      return (_flags & flags) != 0;
   }


   @Override
   public final boolean logVerbose() {
      return isFlagged(GPointsLoader.VERBOSE) || GUtils.isDevelopment();
   }


   public final synchronized void load() throws IOException {
      if (_loaded) {
         throw new RuntimeException("Already loaded!");
      }
      _loaded = true;

      final long start = System.currentTimeMillis();
      rawLoad();
      final long elapsed = System.currentTimeMillis() - start;
      //logInfo("Read in " + GMath.roundTo(elapsed / 1000f, 2) + " seconds");
      logInfo("Read in " + elapsed + " ms. ( " + GUtils.getTimeMessage(elapsed) + " )");

   }


   protected abstract void rawLoad() throws IOException;


   //   public final synchronized IVertexContainer<VectorT, VertexT, ?> getVertices() {
   //      if (!_loaded) {
   //         throw new RuntimeException("Not yet loaded!");
   //      }
   //
   //      return getRawVertices();
   //   }

   public abstract IVertexContainer<VectorT, VertexT, ?> getVertices();


   protected abstract IVertexContainer<VectorT, VertexT, ?> getRawVertices();


   public final boolean isLoaded() {
      return _loaded;
   }

}
