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
