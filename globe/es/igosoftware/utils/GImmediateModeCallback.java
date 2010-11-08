package es.igosoftware.utils;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import es.igosoftware.util.GAssert;


public class GImmediateModeCallback
         extends
            GLUtessellatorCallbackAdapter {
   protected final GL _gl;


   public GImmediateModeCallback(final GL gl) {
      GAssert.notNull(gl, "gl");

      _gl = gl;
   }


   @Override
   public void begin(final int type) {
      _gl.glBegin(type);
   }


   @Override
   public void vertex(final Object vertexData) {
      if (vertexData instanceof double[]) {
         _gl.glVertex3dv((double[]) vertexData, 0);
      }
      else if (vertexData instanceof float[]) {
         _gl.glVertex3fv((float[]) vertexData, 0);
      }
      else {
         System.out.println("vertexData class not supported (" + vertexData.getClass() + ")");
      }
   }


   @Override
   public void end() {
      _gl.glEnd();
   }


   @Override
   public void combine(final double[] coords,
                       final Object[] data,
                       final float[] weight,
                       final Object[] outData) {
      outData[0] = coords;
   }


}
