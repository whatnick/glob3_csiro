/**
 * Decompiled Cone+Cylinder Vector Glyph renderer from
 * http://people.eecs.ku.edu/~miller/WorldWind/
 */


package es.igosoftware.experimental.ndimensional;

import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import javax.media.opengl.GL;


public class VectorRenderer
         implements
            Renderable {
   private final Vec4    vec;
   private final Vec4    base;
   private static Vec4[] uvw = null;


   public VectorRenderer(final Vec4 vec,
                         final Vec4 base) {
      this.vec = vec;
      this.base = base;
   }


   private static void drawBoundedSolidCone(final GL gl,
                                            final Vec4 currentVec,
                                            final Vec4 vertex,
                                            final double coneHeight,
                                            final double rAtBase,
                                            int nSides) {
      Vec4 base = vertex;
      if (coneHeight > 0.0D) {
         base = vertex.add3(currentVec.multiply3(-coneHeight));
      }
      if (nSides < 3) {
         nSides = 3;
      }
      final double dTheta = 6.283185307179586D / nSides;
      double theta = 0.0D;
      gl.glBegin(GL.GL_TRIANGLE_FAN);
      gl.glVertex3d(vertex.x, vertex.y, vertex.z);
      Vec4 first = null;
      for (int i = 0; i < nSides; i++) {
         final Vec4 offset = uvw[0].multiply3(rAtBase * Math.cos(theta)).add3(uvw[1].multiply3(rAtBase * Math.sin(theta)));
         final Vec4 p = base.add3(offset);

         final Vec4 ruling = p.subtract3(vertex);
         final Vec4 n = uvw[2].perpendicularTo3(ruling);
         gl.glNormal3d(n.x, n.y, n.z);
         if (i == 0) {
            first = p;
         }
         gl.glVertex3d(p.x, p.y, p.z);
         theta += dTheta;
      }
      if (first != null) {
         gl.glVertex3d(first.x, first.y, first.z);
      }
      gl.glEnd();
   }


   private static void drawBoundedSolidCylinder(final GL gl,
                                                final Vec4 currentVec,
                                                final Vec4 base,
                                                final double height,
                                                final double radius,
                                                int nSides) {
      if (nSides < 3) {
         nSides = 3;
      }
      final double dTheta = 6.283185307179586D / nSides;
      double theta = 0.0D;

      final Vec4 top = base.add3(currentVec.multiply3(height));
      gl.glBegin(GL.GL_QUAD_STRIP);
      for (int i = 0; i <= nSides; i++) {
         final Vec4 normal = uvw[0].multiply3(Math.cos(theta)).add3(uvw[1].multiply3(Math.sin(theta)));
         gl.glNormal3d(normal.x, normal.y, normal.z);
         final Vec4 offset = normal.multiply3(radius);

         Vec4 pt = base.add3(offset);
         gl.glVertex3d(pt.x, pt.y, pt.z);

         pt = top.add3(offset);
         gl.glVertex3d(pt.x, pt.y, pt.z);

         theta += dTheta;
      }
      gl.glEnd();

      gl.glBegin(GL.GL_POLYGON);
      gl.glNormal3d(-currentVec.x, -currentVec.y, -currentVec.z);
      for (int i = 0; i <= nSides; i++) {
         final Vec4 normal = uvw[0].multiply3(Math.cos(theta)).add3(uvw[1].multiply3(Math.sin(theta)));
         final Vec4 pt = base.add3(normal.multiply3(radius));
         gl.glVertex3d(pt.x, pt.y, pt.z);
         theta += dTheta;
      }
      gl.glEnd();
   }


   public static void drawVector(final GL gl,
                                 final Vec4 currentVec,
                                 final Vec4 base,
                                 double h1,
                                 double h2,
                                 final double rCyl,
                                 final double rCon) {
      makeCoordinateSystem(currentVec);
      if (h1 < 0.0D) {
         h1 = 0.0D;
      }
      if (h1 > 0.0D) {
         drawBoundedSolidCylinder(gl, currentVec, base, h1, rCyl, 10);
      }
      if (h2 < 0.0D) {
         h2 = 0.0D;
      }
      drawBoundedSolidCone(gl, currentVec, base.add3(currentVec.multiply3(h1 + h2)), h2, rCon, 10);
   }


   private static void makeCoordinateSystem(final Vec4 vecIN) {
      if (uvw == null) {
         uvw = new Vec4[3];
      }
      final double[] coeff = { vecIN.x, vecIN.y, vecIN.z };
      final int[] coeffOrder = { 0, 1, 2 };
      boolean proceed;
      do {
         proceed = false;
         for (int i = 0; i < 2; i++) {
            if (Math.abs(coeff[coeffOrder[i]]) >= Math.abs(coeff[coeffOrder[(i + 1)]])) {
               continue;
            }
            final int t = coeffOrder[i];
            coeffOrder[i] = coeffOrder[(i + 1)];
            coeffOrder[(i + 1)] = t;
            proceed = true;
         }
      }

      while (proceed);

      coeff[coeffOrder[2]] = 0.0D;
      final double t = -coeff[coeffOrder[0]];
      coeff[coeffOrder[0]] = coeff[coeffOrder[1]];
      coeff[coeffOrder[1]] = t;

      uvw[0] = new Vec4(coeff[0], coeff[1], coeff[2]).normalize3();
      uvw[2] = vecIN.normalize3();
      uvw[1] = vecIN.cross3(uvw[0]).normalize3();
   }


   public void render(final DrawContext dc) {
      final double scale = 1000.0D;
      drawVector(dc.getGL(), this.vec, this.base, 12.0D * scale, 4.0D * scale, 2.0D * scale, 3.0D * scale);
   }

}

/* Location:           VectorFieldVis.jar
 * Qualified Name:     gov.nasa.worldwind.applications.vectorfieldvis.VectorRenderer
 * JD-Core Version:    0.6.0
 */
