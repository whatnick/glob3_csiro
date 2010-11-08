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


package es.igosoftware.loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.mutability.IMutable.ChangeListener;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.render.DrawContext;


public abstract class GDisplayListCache<T> {

   private final Map<T, GPair<Integer, IMutable.ChangeListener>> _cache                = new HashMap<T, GPair<Integer, IMutable.ChangeListener>>();

   private final ArrayList<Integer>                              _displayListsToRemove = new ArrayList<Integer>();

   private long                                                  _lastRenderFrameTimeStamp;
   private long                                                  _timeSpentInCurrentFrame;

   private final boolean                                         _verbose;


   public GDisplayListCache(final boolean verbose) {
      _verbose = verbose;
   }


   public synchronized final int getDisplayList(final T model,
                                                final DrawContext dc,
                                                final boolean forceCreation) {

      if (!_displayListsToRemove.isEmpty()) {
         final GL gl = dc.getGL();
         for (final Integer displayList : _displayListsToRemove) {
            gl.glDeleteLists(displayList.intValue(), 1);
         }
         _displayListsToRemove.clear();
      }

      final GPair<Integer, ChangeListener> displayList = _cache.get(model);
      if (displayList != null) {
         return displayList._first.intValue();
      }


      final long frameTimeStamp = dc.getFrameTimeStamp();

      if (frameTimeStamp != _lastRenderFrameTimeStamp) {
         _lastRenderFrameTimeStamp = frameTimeStamp;

         _timeSpentInCurrentFrame = 0;
      }

      if (forceCreation || (_timeSpentInCurrentFrame <= 25)) {
         final long start = System.currentTimeMillis();

         beforeRenderingToDisplayList(model, dc);

         final GL gl = dc.getGL();

         final int newDisplayList = gl.glGenLists(1);

         if (_verbose) {
            System.out.println("Rendering " + model + " to display list #" + newDisplayList);
         }

         gl.glNewList(newDisplayList, GL.GL_COMPILE);
         renderToDisplayList(model, dc);
         gl.glEndList();


         final IMutable.ChangeListener listener;
         if (model instanceof IMutable<?>) {
            listener = new IMutable.ChangeListener() {
               @Override
               public void mutableChanged() {
                  removeDisplayList(model);
               }
            };
            ((IMutable<?>) model).addChangeListener(listener);
         }
         else {
            listener = null;
         }

         _cache.put(model, new GPair<Integer, ChangeListener>(newDisplayList, listener));

         _timeSpentInCurrentFrame += (System.currentTimeMillis() - start);

         return newDisplayList;
      }

      return -1;
   }


   public synchronized final void removeDisplayList(final T model) {

      final GPair<Integer, ChangeListener> displayList = _cache.remove(model);

      if (displayList != null) {
         if (_verbose) {
            System.out.println("Removing display list #" + displayList._first.intValue() + " for " + model);
         }

         if (model instanceof IMutable<?>) {
            ((IMutable<?>) model).removeChangeListener(displayList._second);
         }

         _displayListsToRemove.add(displayList._first);

         //         final int TODO_Delete_DisplayList_in_OpenGL;
         //         final GL gl = dc.getGL();
         //         Threading.invokeOnOpenGLThread(new Runnable() {
         //            @Override
         //            public void run() {
         //               gl.glDeleteLists(displayList._first.intValue(), 1);
         //            }
         //         });

      }
   }


   protected abstract void beforeRenderingToDisplayList(final T model,
                                                        final DrawContext dc);


   protected abstract void renderToDisplayList(final T model,
                                               final DrawContext dc);

}
