package es.igosoftware.euclid.rendering;

import java.nio.FloatBuffer;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public interface IRenderer {

   public static final int GL_POINTS         = 0x0;

   public static final int GL_POINT_SMOOTH   = 0xb10;

   public static final int GL_LIGHTING       = 0xb50;

   public static final int GL_FLOAT          = 0x1406;

   public static final int GL_VERTEX_ARRAY   = 0x8074;
   public static final int GL_NORMAL_ARRAY   = 0x8075;
   public static final int GL_COLOR_ARRAY    = 0x8076;
   public static final int GL_COLOR_MATERIAL = 0xb57;

   public static final int GL_COMPILE        = 0x1300;


   public void glPointSize(final float pointSize);


   public void glEnable(final int cap);


   public void glEnableClientState(final int cap);


   public void glDisable(final int cap);


   public void glDisableClientState(final int cap);


   public void glVertexPointer(final int size,
                               final int stride,
                               final FloatBuffer buffer);


   public void glNormalPointer(final int stride,
                               final FloatBuffer buffer);


   public void glColorPointer(final int size,
                              final int stride,
                              final FloatBuffer buffer);


   public void glDrawArrays(final int mode,
                            final int first,
                            final int count);


   public void glColor(final float red,
                       final float green,
                       final float blue);


   public void glRotate(final float angle,
                        final float x,
                        final float y,
                        final float z);


   public void glTranslate(final float x,
                           final float y,
                           final float z);


   public void glVertex(final float x,
                        final float y,
                        final float z);


   public void glVertex(final float x,
                        final float y);


   public void glNormal(final float nx,
                        final float ny,
                        final float nz);


   public void glBegin(final int mode);


   public void glEnd();


   public int glGenLists(final int range);


   public void glNewList(final int list,
                         final int mode);


   public void glEndList();


   public void glCallList(final int list);


   // high level functions
   public void renderPointsNormalsColors(final float pointSize,
                                         final boolean antialiasing,
                                         final byte dimensions,
                                         final FloatBuffer points,
                                         final FloatBuffer normals,
                                         final FloatBuffer colors);


   public <VectorT extends IVector<VectorT, ?>> int createDisplayList(final float pointSize,
                                                                      final IVertexContainer<VectorT, IVertexContainer.Vertex<VectorT>, ?> vertices);

}
