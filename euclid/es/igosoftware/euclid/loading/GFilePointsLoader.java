/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.loading;

import java.io.IOException;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.XStringTokenizer;


public abstract class GFilePointsLoader<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GPointsLoader<VectorT, VertexT> {

   private final String _fileNames;


   //private final GCompositeVertexContainer<VectorT> _verticesComposite = new GCompositeVertexContainer<VectorT>();


   protected GFilePointsLoader(final String fileNames,
                               final int flags) {
      super(flags);
      _fileNames = fileNames;
   }


   protected final String[] getFileNames() {
      return new XStringTokenizer(_fileNames).getAllTokens();
   }


   //   @Override
   //   protected final void rawLoad() throws IOException {
   //      final String[] fileNames = getFileNames();
   //      final int filesCount = fileNames.length;
   //
   //      startLoad(filesCount);
   //
   //      if (filesCount == 1) {
   //         final String fileName = fileNames[0];
   //         logInfo("Reading vertices from \"" + fileName + "\"...");
   //
   //         final IVertexContainer<VectorT, VertexT, ?> vertices = loadVerticesFromFile(fileName);
   //         _verticesComposite.addChild(vertices);
   //      }
   //      else {
   //         final ExecutorService executor = GConcurrent.createExecutor(Runtime.getRuntime().availableProcessors() * 8);
   //         //final ExecutorService executor = GConcurrent.getDefaultExecutor();
   //
   //         final List<Future<IVertexContainer<VectorT, VertexT, ?>>> futuresVertices = new ArrayList<Future<IVertexContainer<VectorT, VertexT, ?>>>(
   //                  filesCount);
   //
   //         for (int i = 0; i < filesCount; i++) {
   //            final int finalI = i;
   //
   //            final Future<IVertexContainer<VectorT, VertexT, ?>> futureVertices = executor.submit(new Callable<IVertexContainer<VectorT, VertexT, ?>>() {
   //               @Override
   //               public IVertexContainer<VectorT, VertexT, ?> call() throws IOException {
   //                  final String fileName = fileNames[finalI];
   //                  logInfo("Reading vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount + ")...");
   //
   //                  final IVertexContainer<VectorT, VertexT, ?> vertices = loadVerticesFromFile(fileName);
   //
   //                  logInfo("Read " + vertices.size() + " vertices from \"" + fileName + "\" (" + (finalI + 1) + "/" + filesCount
   //                          + ")...");
   //
   //                  return vertices;
   //               }
   //            });
   //
   //            futuresVertices.add(futureVertices);
   //         }
   //
   //
   //         for (final Future<IVertexContainer<VectorT, VertexT, ?>> futureVertices : futuresVertices) {
   //            try {
   //               final IVertexContainer<VectorT, VertexT, ?> vertices = futureVertices.get();
   //               _verticesComposite.addChild(vertices);
   //            }
   //            catch (final InterruptedException e) {
   //               throw new IOException(e);
   //            }
   //            catch (final ExecutionException e) {
   //               throw new IOException(e);
   //            }
   //         }
   //      }
   //
   //      endLoad();
   //      _verticesComposite.makeImmutable();
   //
   //      logInfo("Read " + _verticesComposite.size() + " vertices from " + filesCount + " files");
   //   }
   //
   //
   //   @Override
   //   protected final IVertexContainer<VectorT, VertexT, ?> getRawVertices() {
   //      return (_verticesComposite.childrenCount() == 1) ? _verticesComposite.getChild(0) : _verticesComposite;
   //   }


   protected abstract void startLoad(final int filesCount) throws IOException;


   protected abstract void endLoad() throws IOException;


   protected abstract IVertexContainer<VectorT, VertexT, ?> loadVerticesFromFile(final String fileName) throws IOException;

}
