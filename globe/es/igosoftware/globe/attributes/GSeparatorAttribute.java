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


package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.util.EventListener;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;


public class GSeparatorAttribute
         implements
            ILayerAttribute<Object> {

   @Override
   public boolean isVisible() {
      return true;
   }


   @Override
   public final String getLabel() {
      return null;
   }


   @Override
   public final String getDescription() {
      return null;
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                             final IGlobeLayer layer) {
      final JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
      return new GPair<Component, EventListener>(separator, null);
   }


   @Override
   public final void cleanupWidget(final IGlobeLayer layer,
                                   final GPair<Component, EventListener> widget) {

   }


   @Override
   public final Object get() {
      return null;
   }


   @Override
   public final void set(final Object value) {
   }


   @Override
   public final boolean isReadOnly() {
      return true;
   }


   @Override
   public final void setListener(final ILayerAttribute.IChangeListener listener) {

   }


   @Override
   public final void changed() {

   }
}
