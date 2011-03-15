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


package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Renderable;

import java.awt.Color;
import java.awt.LinearGradientPaint;


public abstract class GVectorRenderingTheme {

   public static enum ColoringMethod {
      UNIQUE,
      COLOR_RAMP,
      COLOR_LUT;
   }


   protected ColoringMethod    _coloringMethod                = ColoringMethod.UNIQUE;
   protected Color             _color                         = Color.red;
   private LinearGradientPaint _gradient;
   protected int               _fieldIndex                    = 0;

   private double              _min;
   private double              _max;
   private boolean             _hasToRecalculateExtremeValues = true;


   public GVectorRenderingTheme() {
      _gradient = new LinearGradientPaint(0f, 0f, 1f, 1f, new float[] { 0f, 1f }, new Color[] { Color.yellow, Color.red });
      //calculateExtremeValues(features);
   }


   public void calculateExtremeValues(final GFeature[] features) {

      if (_hasToRecalculateExtremeValues) {
         double dMin = Double.POSITIVE_INFINITY;
         double dMax = Double.NEGATIVE_INFINITY;

         for (final GFeature element : features) {
            final String sValue = element.getAttributes()[_fieldIndex].toString();
            try {
               final double dValue = Double.parseDouble(sValue);
               dMin = Math.min(dMin, dValue);
               dMax = Math.max(dMax, dValue);
            }
            catch (final Exception e) {
               //ignore wrong field values
            }
         }

         //         if (dMin == Double.POSITIVE_INFINITY) {
         //            dMin = 0;
         //            dMax = 0;
         //         }
         _hasToRecalculateExtremeValues = false;
      }

   }


   protected abstract Renderable[] getRenderables(final GFeature feature,
                                                  final GProjection projection,
                                                  final Globe globe);


   public ColoringMethod getColoringMethod() {
      return _coloringMethod;
   }


   public LinearGradientPaint getGradient() {
      return _gradient;
   }


   public void setColoringMethod(final ColoringMethod coloringMethod) {
      _coloringMethod = coloringMethod;
   }


   public void setGradient(final LinearGradientPaint gradient) {
      _gradient = gradient;
   }


   public int getFieldIndex() {
      return _fieldIndex;
   }


   public void setFieldIndex(final int field) {
      _fieldIndex = field;
      _hasToRecalculateExtremeValues = true;
   }


   public Color getColor() {
      return _color;
   }


   public void setColor(final Color color) {
      _color = color;
   }


   protected int getColorFromColorRamp(final double dValue) {

      final double dNormalizedValue = (dValue - _min) / (_max - _min);
      final float[] fractions = _gradient.getFractions();
      final Color[] colors = _gradient.getColors();
      for (int i = 0; i < fractions.length - 1; i++) {
         if ((dNormalizedValue >= fractions[i]) && (dNormalizedValue <= fractions[i + 1])) {
            return getColorFromColorRamp(fractions[i], fractions[i + 1], colors[i], colors[i + 1], dNormalizedValue);
         }
      }

      return colors[colors.length - 1].getRGB();

   }


   private int getColorFromColorRamp(final double dMin,
                                     final double dMax,
                                     Color color,
                                     final Color color2,
                                     final double dValue) {

      if (dValue <= dMin) {
         return color.getRGB();
      }

      if (dValue >= dMax) {
         return color2.getRGB();
      }

      final double dDif = dMax - dMin;
      if (dDif == 0) {
         return color.getRGB();
      }

      final double dDif2 = dValue - dMin;
      final double dRatio = dDif2 / dDif;
      final int dDifR = color2.getRed() - color.getRed();
      final int dDifG = color2.getGreen() - color.getGreen();
      final int dDifB = color2.getBlue() - color.getBlue();
      color = new Color((int) (color.getRed() + dDifR * dRatio), (int) (color.getGreen() + dDifG * dRatio),
               (int) (color.getBlue() + dDifB * dRatio));

      return color.getRGB();

   }

}
