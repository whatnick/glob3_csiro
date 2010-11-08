package es.igosoftware.euclid.rendering;

import java.nio.FloatBuffer;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public abstract class GRendererAbstract
         implements
            IRenderer {

   @Override
   public void renderPointsNormalsColors(final float pointSize,
                                         final boolean antialiasing,
                                         final byte dimensions,
                                         final FloatBuffer points,
                                         final FloatBuffer normals,
                                         final FloatBuffer colors) {
      if ((points == null) || (points.capacity() == 0)) {
         return;
      }

      glPointSize(pointSize);

      if (antialiasing) {
         glEnable(GL_POINT_SMOOTH);
      }
      else {
         glDisable(GL_POINT_SMOOTH);
      }

      glEnableClientState(GL_VERTEX_ARRAY);
      glVertexPointer(dimensions, 0, points);

      final boolean useNormals = (dimensions == 3) && (normals != null);
      if (useNormals) {
         glEnableClientState(GL_NORMAL_ARRAY);

         glNormalPointer(0, normals);
      }
      else {
         glDisable(GL_LIGHTING);
      }

      if (colors != null) {
         glEnableClientState(GL_COLOR_ARRAY);
         glEnable(GL_COLOR_MATERIAL);

         glColorPointer(3, 0, colors);
      }


      glDrawArrays(GL_POINTS, 0, points.capacity() / dimensions);


      //clean up
      if (colors != null) {
         glDisableClientState(GL_COLOR_ARRAY);
         glDisable(GL_COLOR_MATERIAL);
      }

      if (useNormals) {
         glDisableClientState(GL_NORMAL_ARRAY);
      }
      else {
         glEnable(GL_LIGHTING);
      }

      glDisableClientState(GL_VERTEX_ARRAY);
   }


   @Override
   public <VectorT extends IVector<VectorT, ?>> int createDisplayList(final float pointSize,
                                                                      final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices) {
      final int listID = glGenLists(1);

      glNewList(listID, GL_COMPILE);
      {
         glPointSize(pointSize);

         glBegin(GL_POINTS);
         {
            final int verticesCount = vertices.size();
            final byte dimensions = vertices.dimensions();

            final byte x = (byte) 0;
            final byte y = (byte) 1;
            final byte z = (byte) 2;

            for (int i = 0; i < verticesCount; i++) {
               final VectorT point = vertices.getPoint(i);
               if (dimensions == 3) {
                  glVertex((float) point.get(x), (float) point.get(y), (float) point.get(z));
               }
               else {
                  glVertex((float) point.get(x), (float) point.get(y));
               }

               if (vertices.hasColors()) {
                  final IColor color = vertices.getColor(i);
                  glColor(color.getRed(), color.getGreen(), color.getBlue());
               }

               if ((dimensions == 3) && vertices.hasNormals()) {
                  final VectorT normal = vertices.getNormal(i);
                  glNormal((float) normal.get(x), (float) normal.get(y), (float) normal.get(z));
               }
            }
         }
         glEnd();
      }
      glEndList();

      return listID;
   }
}
