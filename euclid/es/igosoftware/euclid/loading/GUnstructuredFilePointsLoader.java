package es.igosoftware.euclid.loading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.GCompositeVertexContainer;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;


public abstract class GUnstructuredFilePointsLoader<VectorT extends IVector<VectorT, ?>>
         extends
            GFilePointsLoader<VectorT, IVertexContainer.Vertex<VectorT>> {


   private final GCompositeVertexContainer<VectorT> _verticesComposite = new GCompositeVertexContainer<VectorT>();


   protected GUnstructuredFilePointsLoader(final String fileNames,
                                           final int flags) {
      super(fileNames, flags);
   }


   @Override
   protected final void rawLoad() throws IOException {
      final String[] fileNames = getFileNames();
      final int filesCount = fileNames.length;

      startLoad(filesCount);

      if (filesCount == 1) {
         final String fileName = fileNames[0];
         logInfo("Reading vertices from \"" + fileName + "\"...");

         final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices = loadVerticesFromFile(fileName);
         _verticesComposite.addChild(vertices);
      }
      else {
         final ExecutorService executor = GConcurrent.createExecutor(Runtime.getRuntime().availableProcessors() * 8);
         //final ExecutorService executor = GConcurrent.getDefaultExecutor();

         final List<Future<IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>>> futuresVertices = new ArrayList<Future<IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>>>(
                  filesCount);

         for (int i = 0; i < filesCount; i++) {
            final int finalI = i;

            final Future<IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>> futureVertices = executor.submit(new Callable<IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>>() {
               @Override
               public IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> call() throws IOException {
                  final String fileName = fileNames[finalI];
                  logInfo("Reading vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount + ")...");

                  final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices = loadVerticesFromFile(fileName);

                  logInfo("Read " + vertices.size() + " vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount
                          + ")...");

                  return vertices;
               }
            });

            futuresVertices.add(futureVertices);
         }


         for (final Future<IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?>> futureVertices : futuresVertices) {
            try {
               final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices = futureVertices.get();
               _verticesComposite.addChild(vertices);
            }
            catch (final InterruptedException e) {
               throw new IOException(e);
            }
            catch (final ExecutionException e) {
               throw new IOException(e);
            }
         }
      }

      endLoad();
      _verticesComposite.makeImmutable();

      logInfo("Read " + _verticesComposite.size() + " vertices from " + filesCount + " files");
   }


   @Override
   protected final IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> getRawVertices() {
      return (_verticesComposite.childrenCount() == 1) ? _verticesComposite.getChild(0) : _verticesComposite;
   }


   @Override
   protected abstract IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> loadVerticesFromFile(final String fileName)
                                                                                                                                            throws IOException;


   @Override
   public final synchronized IUnstructuredVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> getVertices() {
      if (!isLoaded()) {
         throw new RuntimeException("Not yet loaded!");
      }

      return getRawVertices();
   }

}
