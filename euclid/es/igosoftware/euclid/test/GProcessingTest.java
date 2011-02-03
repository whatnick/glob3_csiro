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


package es.igosoftware.euclid.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.GBall;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.loading.GXYZLoader;
import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.octree.GOctree;
import es.igosoftware.euclid.octree.IOctreeVisitor;
import es.igosoftware.euclid.octree.IOctreeVisitorWithFinalization;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GColinearException;
import es.igosoftware.euclid.shape.GInsufficientPointsException;
import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.utils.GResolution;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GLoggerObject;
import es.igosoftware.util.GMath;


public class GProcessingTest
         extends
            GLoggerObject {


   @Override
   public boolean logVerbose() {

      return true;
   }


   protected static boolean convertFromXYZToBinaryFormat(final String sourceFileName,
                                                         final String targetFileName,
                                                         final GProjection projection) throws IOException {

      // System.out.println(binaryFilesNames);
      final GXYZLoader loader = new GXYZLoader(sourceFileName, GVectorPrecision.DOUBLE, GColorPrecision.INT, projection,
               GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE);

      loader.load();

      final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices = loader.getVertices();

      //      logInfo("Calculating reference point...");
      System.out.println("Calculating reference point...");
      final IVector3<?> referencePoint = vertices.getAverage()._point;
      //      logInfo("Reference point: " + referencePoint);
      System.out.println("Reference point: " + referencePoint);

      //      logInfo("Converting to float with reference point...");
      System.out.println("Converting to float with reference point...");
      final GVertex3Container floatVertices = new GVertex3Container(GVectorPrecision.FLOAT, vertices.colorPrecision(),
               projection, referencePoint, vertices.size(), vertices.hasIntensities(), vertices.hasColors(),
               vertices.hasNormals());

      for (int i = 0; i < vertices.size(); i++) {
         //floatVertices.addPoint(vertices.getPoint(i), vertices.getIntensity(i), vertices.getNormal(i), vertices.getColor(i));
         floatVertices.addPoint(vertices.getVertex(i));
      }

      System.out.println("Saving binary file..");
      try {
         GBinaryPoints3Loader.save(floatVertices, projection, targetFileName);
      }
      catch (final IOException e) {

         e.printStackTrace();
      }

      return true;
   }


   protected static void convertFromPTSToXYZFormat(final String sourceFileName,
                                                   final GProjection projection) throws IOException {
      String xyzFileName;
      // System.out.println(binaryFilesNames);
      final GXYZLoader loader = new GXYZLoader(sourceFileName, GVectorPrecision.DOUBLE, GColorPrecision.INT, projection,
               GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE);

      loader.load();

      xyzFileName = sourceFileName.substring(0, sourceFileName.indexOf('.')) + ".xyz";

      GXYZLoader.save(loader.getVertices(), xyzFileName);

   }


   private static IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> loadVertices(final String sourceFileName)
                                                                                                                                  throws IOException {


      final GBinaryPoints3Loader loader = new GBinaryPoints3Loader(sourceFileName, GPointsLoader.DEFAULT_FLAGS
                                                                                   | GPointsLoader.VERBOSE);

      loader.load();

      final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices = loader.getVertices();

      //      if (_filterBounds != null) {
      //         final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> filteredVertices = vertices.select(new IPredicate<IVertexContainer.Vertex<IVector3<?>>>() {
      //            @Override
      //            public boolean evaluate(final IVertexContainer.Vertex<IVector3<?>> vertex) {
      //               return _filterBounds.contains(vertex._point);
      //            }
      //         });
      //
      //         return filteredVertices;
      //      }

      return vertices;
   }


   private static void processVertices(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices,
                                       final String fileName) {

      System.out.println("Processing " + vertices);
      //final GAxisAlignedOrthotope<IVector3<?>, ?> bounds = vertices.getBounds();
      //System.out.println("Orthotope Bounds: " + bounds);
      //System.out.println("Orthotope Extent: " + bounds.getExtent());

      System.out.println("Calculamos resolución de la nube: ");
      final GResolution resolucion = new GResolution(vertices, true);

      final GOctree.DuplicatesPolicy duplicatesPolicy = new GOctree.DuplicatesPolicy() {
         @Override
         public int[] removeDuplicates(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices1,
                                       final int[] verticesIndexes) {


            final double estimatedResolution = Math.min(resolucion.getAverageResolutions()._x,
                     resolucion.getAverageResolutions()._y);
            final double estimatedRadio = estimatedResolution - (estimatedResolution * 0.2);
            //final Set<IVector3<?>> selectedPoints = new HashSet<IVector3<?>>();
            final List<IVector3<?>> selectedPoints = new ArrayList<IVector3<?>>();
            final List<Integer> selectedIndices = new ArrayList<Integer>();

            //            for (final int index : verticesIndexes) {
            //               final IVector3<?> point = vertices1.getPoint(index);
            //               if (!selectedPoints.contains(point)) {
            //
            //                  selectedPoints.add(point);
            //                  selectedIndices.add(index);
            //               }
            //            }

            for (final int index : verticesIndexes) {

               final IVector3<?> point = vertices1.getPoint(index);
               final GBall bola = new GBall(point, estimatedRadio);
               boolean skip_point = false;

               final Iterator<IVector3<?>> it = selectedPoints.iterator();
               while (it.hasNext() && !skip_point) {
                  final IVector3<?> point2 = it.next();

                  if (bola.contains(point2)) {
                     skip_point = true;
                  }
               }

               if (!skip_point) {
                  selectedPoints.add(point);
                  selectedIndices.add(index);
               }

            }

            //            if (selectedIndices.size() < verticesIndexes.length) {
            //               System.out.println("OJO ! .. ELIMINADOS DUPLICADOS !!!");
            //            }

            return GCollections.toIntArray(selectedIndices);
         }
      };


      final GOctree.CreateLeafPolicy planesEstimationLeafPolicy = new GOctree.CreateLeafPolicy() {

         private int    _numNewNodesCreated = 0;
         private int    _numNewNodesSkiped  = 0;

         private double _estimatedResolution;


         private boolean planeComplianceCriteriaFulfilled(final GPlane plane,
                                                          final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices1,
                                                          final int[] verticesIndexes) {


            final double maxAllowedDistance = 5 * _estimatedResolution;
            final double maxAllowedSquareDistance = maxAllowedDistance * maxAllowedDistance;// 0.01; // para distancia = 10cm
            final float maxRelativeError = 0.2f; // relative error = 20%

            int conformingPoints = 0;

            for (final int index : verticesIndexes) {
               final double squaredDistance = plane.squaredDistance(vertices1.getPoint(index));

               if (GMath.lessOrEquals(squaredDistance, maxAllowedSquareDistance)) {
                  conformingPoints++;
               }
            }

            final float relativeError = (float) (verticesIndexes.length - conformingPoints) / verticesIndexes.length;

            //            if ((relativeError <= maxRelativeError) && (verticesIndexes.length > 100)) {
            //               System.out.print("Num points: " + verticesIndexes.length + " / ");
            //               System.out.print("Conforming points: " + conformingPoints + " / ");
            //               System.out.println("Relative Error: " + relativeError);
            //            }
            return (GMath.lessOrEquals(relativeError, maxRelativeError));
         }


         @Override
         public void beforeStart() {
            _numNewNodesCreated = 0;
            _numNewNodesSkiped = 0;
            _estimatedResolution = Math.max(resolucion.getAverageResolutions()._x, resolucion.getAverageResolutions()._y);
         }


         @Override
         public boolean acceptLeafCreation(final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices1,
                                           final int[] verticesIndexes) {

            if (verticesIndexes.length >= 3) {

               try {

                  final GPlane bestPlane = GPlane.getBestFitPlane(vertices1, verticesIndexes);

                  if (planeComplianceCriteriaFulfilled(bestPlane, vertices1, verticesIndexes)) {
                     //                     if (verticesIndexes.length > 100) {
                     //                        System.out.println("Accepted leaf: " + bestPlane + " " + verticesIndexes.length);
                     //                     }
                     _numNewNodesCreated++;
                     return true;
                  }

                  _numNewNodesSkiped++;
               }
               catch (final GColinearException e) {
                  System.out.println("GColinearException. vertices: " + verticesIndexes.length);
                  _numNewNodesSkiped++;
               }
               catch (final GInsufficientPointsException e) {
                  System.out.println("GInsufficientPointsException. vertices: " + verticesIndexes.length);
                  _numNewNodesSkiped++;
               }
            }

            return false;
         }


         @Override
         public void afterEnd() {
            System.out.println("Nuevos nodos creados: " + _numNewNodesCreated + ", Nodos descartados: " + _numNewNodesSkiped);
         }

      };


      //final GOctree octree = new GOctree(fileName, vertices, null, new GOctree.Parameters(Double.POSITIVE_INFINITY, 2048, true));

      //final GOctree octree = new GOctree(fileName, vertices, null, new GOctree.Parameters(Double.POSITIVE_INFINITY, 2048, true,
      //         duplicatesPolicy));

      //final GOctree octree = new GOctree(fileName, vertices, null, planesEstimationLeafPolicy, new GOctree.Parameters(
      //         Double.POSITIVE_INFINITY, 2048, true));

      final GOctree octree = new GOctree(fileName, vertices, null, planesEstimationLeafPolicy, new GOctree.Parameters(
               Double.POSITIVE_INFINITY, 2048, true, duplicatesPolicy));


      final IOctreeVisitor visitor = new IOctreeVisitor() {
         @Override
         public void visitOctree(final GOctree octree1) {
            System.out.println("Empezando a visitar octree breadth-first");
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
            //System.out.print(inner.getId());
            //System.out.println("VisitInnerNode");
         }


         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            //final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices2 = leaf.getVertices();

            //System.out.print(leaf.getId());
            //System.out.println("VisitLeafNode");
         }
      };

      final IOctreeVisitorWithFinalization visitor2 = new IOctreeVisitorWithFinalization() {
         @Override
         public void visitOctree(final GOctree octree1) {
            System.out.println("Empezando a visitar octree depth-first");
         }


         @Override
         public void visitInnerNode(final GOTInnerNode inner) {
            //System.out.print(inner.getId());
            //System.out.println("VisitInnerNode");
         }


         @Override
         public void visitLeafNode(final GOTLeafNode leaf) {
            //System.out.print(leaf.getId());
            //System.out.println("VisitLeafNode");
         }


         @Override
         public void finishedOctree(final GOctree octree1) {
            System.out.println("Terminado de visitar depth-first.");
         }


         @Override
         public void finishedInnerNode(final GOTInnerNode inner) {

            //System.out.println();
         }
      };

      System.out.println("Init octree visit ");
      octree.breadthFirstAcceptVisitor(visitor);
      octree.depthFirstAcceptVisitor(visitor2);
      //System.out.println("Numero de nodos interiores: " + String.valueOf(octree.getInnerNodesCount()));
      System.out.println("Finish octree visit ");
   }


   /**
    * @param args
    * @throws IOException
    */
   public static void main(final String[] args) throws IOException {


      //final String sourceDirectoryName = "/home/fpulido/Escritorio/Los-putos-puntos/";
      //final String targetDirectoryName = "/home/fpulido/Escritorio/Los-putos-puntos/";
      final String sourceDirectoryName = "/home/fpgalan/Escritorio/Nubes-lidar/";
      final String targetDirectoryName = "/home/fpgalan/Escritorio/Nubes-lidar/";

      //final String fileName = "72-Foro-de-los-Balbos.xyz";
      //final String fileName = "Guadatux2.xyz";
      //final String fileName = "Alardos1x1.pts";

      //final String fileName = "LiDAR-FOREST-Zone1.xyz";
      //final String fileName = "LiDAR-FOREST-Zone2.xyz";
      //final String fileName = "LiDAR-FOREST-Zone3.xyz";
      //final String fileName = "MDT-LiDAR-FOREST-Zone1.xyz";
      //final String fileName = "MDT-LiDAR-FOREST-Zone2.xyz";
      final String fileName = "MDT-LiDAR-FOREST-Zone3.xyz";

      //final GProjection projection = GProjection.EPSG_23029;
      final GProjection projection = GProjection.EPSG_23030;

      String octreeFileName;
      if (fileName.contains(".")) {
         octreeFileName = fileName.substring(0, fileName.indexOf('.'));
      }
      else {
         octreeFileName = fileName;
      }
      final String sourceFileName = sourceDirectoryName + fileName;
      final String targetFileName = targetDirectoryName + octreeFileName + ".bp";
      System.out.println("Tarjet file name= " + targetFileName);


      System.out.println("Starting points cloud loading test..");
      System.out.println("----------------\n");

      //      System.out.println("Converting Alardos1x1.pts to XYZ format \n");
      //      convertFromPTSToXYZFormat(sourceDirectoryName + "Alardos1x1.pts", projection);
      //      System.out.println("File conversion finished");

      final File targetFile = new File(targetFileName);
      if (!targetFile.exists()) {
         if (convertFromXYZToBinaryFormat(sourceFileName, targetFileName, projection)) {
            System.out.println("Conversión completada correctamente..");
         }
      }

      System.out.println("Loading binary file..");
      final IVertexContainer<IVector3<?>, IVertexContainer.Vertex<IVector3<?>>, ?> vertices = loadVertices(targetFileName);

      System.out.println("Launched vertices processing..");
      processVertices(vertices, octreeFileName);
      System.out.println("Finished vertices processing..");

   }
}
