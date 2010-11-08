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
import es.igosoftware.euclid.verticescontainer.GStructuredCompositeVertexContainer;
import es.igosoftware.euclid.verticescontainer.IStructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;


public abstract class GStructuredFilePointsLoader<VectorT extends IVector<VectorT, ?>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>>
         extends
            GFilePointsLoader<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> {


   private final GStructuredCompositeVertexContainer<VectorT, GroupT> _verticesComposite = new GStructuredCompositeVertexContainer<VectorT, GroupT>();


   protected GStructuredFilePointsLoader(final String fileNames,
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

         final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = loadVerticesFromFile(fileName);
         _verticesComposite.addChild(vertices);
      }
      else {
         final ExecutorService executor = GConcurrent.createExecutor(Runtime.getRuntime().availableProcessors() * 8);
         //final ExecutorService executor = GConcurrent.getDefaultExecutor();

         final List<Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>> futuresVertices = new ArrayList<Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>>(
                  filesCount);

         for (int i = 0; i < filesCount; i++) {
            final int finalI = i;

            final Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>> futureVertices = executor.submit(new Callable<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>() {
               @Override
               public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> call()
                                                                                                                                         throws IOException {
                  final String fileName = fileNames[finalI];
                  logInfo("Reading vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount + ")...");

                  final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = loadVerticesFromFile(fileName);

                  logInfo("Read " + vertices.size() + " vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount
                          + ")...");

                  return vertices;
               }
            });

            futuresVertices.add(futureVertices);
         }


         for (final Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>> futureVertices : futuresVertices) {
            try {
               final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = futureVertices.get();
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
   protected final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> getRawVertices() {
      return (_verticesComposite.childrenCount() == 1) ? _verticesComposite.getChild(0) : _verticesComposite;
   }


   @Override
   protected abstract IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> loadVerticesFromFile(final String fileName)
                                                                                                                                                                              throws IOException;


   @Override
   public final synchronized IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> getVertices() {
      if (!isLoaded()) {
         throw new RuntimeException("Not yet loaded!");
      }

      return getRawVertices();
   }

}
