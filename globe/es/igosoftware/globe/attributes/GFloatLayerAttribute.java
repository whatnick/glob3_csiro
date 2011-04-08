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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.EventListener;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;


public abstract class GFloatLayerAttribute
         extends
            GAbstractLayerAttribute<Float> {


   public static enum WidgetType {
      SPINNER,
      SLIDER,
      TEXTBOX;
   }


   private final float      _minimum;
   private final float      _maximum;
   private final float      _stepSize;
   private final WidgetType _widgetType;


   public GFloatLayerAttribute(final String label,
                               final boolean readOnly,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label, readOnly);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   public GFloatLayerAttribute(final String label,
                               final String propertyName,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label, propertyName);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   public GFloatLayerAttribute(final String label,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   public GFloatLayerAttribute(final String label,
                               final String propertyName,
                               final boolean readOnly,
                               final float minimum,
                               final float maximum,
                               final GFloatLayerAttribute.WidgetType widgetType,
                               final float stepSize) {
      super(label, propertyName, readOnly);
      _minimum = minimum;
      _maximum = maximum;
      _stepSize = stepSize;
      _widgetType = widgetType;
   }


   @Override
   public final void cleanupWidget(final IGlobeLayer layer,
                                   final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }


   @Override
   public final GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                             final IGlobeLayer layer) {

      Component widget = null;
      switch (_widgetType) {
         case SLIDER:
            // TODO: create slider, now just pass trought to spinner

         case SPINNER:
            widget = createSpinner();
            break;

         case TEXTBOX:
            widget = createTextBox();
            break;
      }


      final EventListener listener = subscribeToEvents(layer);
      return new GPair<Component, EventListener>(widget, listener);
   }


   private JTextField createTextBox() {
      final JTextField text = new JTextField();
      text.setMinimumSize(new Dimension(100, 20));
      text.setText(get().toString());

      if (isReadOnly()) {
         text.setEnabled(false);
      }
      else {
         text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
               final JTextField textField = (JTextField) e.getSource();
               final String content = textField.getText();
               if (content.length() != 0) {
                  try {
                     final float f = Float.parseFloat(content);
                     if (f > _maximum) {
                        textField.setText(Float.toString(_maximum));
                     }
                     if (f < _minimum) {
                        textField.setText(Float.toString(_minimum));
                     }
                     set(Float.parseFloat(textField.getText()));
                  }
                  catch (final NumberFormatException nfe) {
                     Toolkit.getDefaultToolkit().beep();
                     textField.requestFocus();
                  }
               }
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            text.setText(get().toString());
         }
      });

      return text;
   }


   private JSpinner createSpinner() {
      final SpinnerNumberModel model = new SpinnerNumberModel(get(), Float.valueOf(_minimum), Float.valueOf(_maximum),
               Float.valueOf(_stepSize));

      final JSpinner spinner = new JSpinner(model);

      if (isReadOnly()) {
         spinner.setEnabled(false);
      }
      else {
         spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
               set((Float) spinner.getValue());
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            spinner.setValue(get());
         }
      });

      return spinner;
   }


}
