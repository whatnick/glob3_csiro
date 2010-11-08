package es.igosoftware.euclid.colors;

import es.igosoftware.util.GMath;

public abstract class GColorAbstract
         implements
            IColor {


   @Override
   public final boolean between(final IColor min,
                                final IColor max) {
      final float precision = GMath.maxF(precision(), min.precision(), max.precision());
      return GMath.between(getRed(), min.getRed(), max.getRed(), precision)
             && GMath.between(getGreen(), min.getGreen(), max.getGreen(), precision)
             && GMath.between(getBlue(), min.getBlue(), max.getBlue(), precision);
   }


   @Override
   public final float getBrightness() {
      return GMath.maxF(getRed(), getGreen(), getBlue());
   }


   @Override
   public final float getLuminance() {
      return 0.299f * getRed() + 0.587f * getGreen() + 0.114f * getBlue();
   }


   @Override
   public final double getHue() {
      final float r = getRed();
      final float g = getGreen();
      final float b = getBlue();

      final float max = GMath.maxF(r, g, b);
      final float min = GMath.minF(r, g, b);

      final float span = (max - min);

      if (GMath.closeToZero(span)) {
         return 0;
      }

      final double h;
      if (r == max) {
         h = ((g - b) / span) * GMath.DEGREES_60;
      }
      else if (g == max) {
         h = (GMath.DEGREES_60 * 2) + (((b - r) / span) * GMath.DEGREES_60);
      }
      else {
         h = (GMath.DEGREES_60 * 4) + (((r - g) / span) * GMath.DEGREES_60);
      }

      if (h < 0) {
         return GMath.DEGREES_360 + h;
      }

      return h;


      //      hue
      //      "Return the hue of this color, an angle in the range [0.0..360.0]."
      //
      //      | r g b max min span h |
      //      r := self privateRed.
      //      g := self privateGreen.
      //      b := self privateBlue. 
      //
      //      max := ((r max: g) max: b).
      //      min := ((r min: g) min: b).
      //      span := (max - min) asFloat.
      //      span = 0.0 ifTrue: [ ^ 0.0 ].
      //
      //      r = max ifTrue: [
      //              h := ((g - b) asFloat / span) * 60.0.
      //      ] ifFalse: [
      //              g = max
      //                      ifTrue: [ h := 120.0 + (((b - r) asFloat / span) * 60.0). ]
      //                      ifFalse: [ h := 240.0 + (((r - g) asFloat / span) * 60.0). ].
      //      ].
      //
      //      h < 0.0 ifTrue: [ h := 360.0 + h ].
      //      ^ h
   }


   @Override
   public final float getSaturation() {
      final float r = getRed();
      final float g = getGreen();
      final float b = getBlue();

      final float max = GMath.maxF(r, g, b);
      final float min = GMath.minF(r, g, b);

      if (GMath.closeToZero(max)) {
         return 0;
      }

      return (max - min) / max;
   }


   @Override
   public final boolean closeTo(final IColor that) {
      final float precision = Math.max(precision(), that.precision());
      return GMath.closeTo(getRed(), that.getRed(), precision) && GMath.closeTo(getGreen(), that.getGreen(), precision)
             && GMath.closeTo(getBlue(), that.getBlue(), precision);
   }


   @Override
   public IColor darker() {
      return adjustBrightness(-0.08f);
   }


   @Override
   public IColor twiceDarker() {
      return adjustBrightness(-0.15f);
   }


   @Override
   public IColor muchDarker() {
      return interpolatedTo(GColorF.BLACK, 0.5f);
   }


   @Override
   public IColor lighter() {
      return adjustSaturationBrightness(-0.03f, 0.08f);
   }


   @Override
   public IColor twiceLighter() {
      return adjustSaturationBrightness(-0.06f, 0.15f);
   }


   @Override
   public IColor muchLighter() {
      return interpolatedTo(GColorF.WHITE, 0.233f);
   }


}
