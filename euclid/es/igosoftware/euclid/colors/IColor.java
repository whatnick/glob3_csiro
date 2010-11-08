package es.igosoftware.euclid.colors;


public interface IColor {

   /**
    * @return the red component of this color, a float in the range [0.0..1.0]
    */
   public float getRed();


   /**
    * @return the green component of this color, a float in the range [0.0..1.0]
    */
   public float getGreen();


   /**
    * @return the blue component of this color, a float in the range [0.0..1.0]
    */
   public float getBlue();


   /**
    * @return the brightness of this color, a float in the range [0.0..1.0]
    */
   public float getBrightness();


   /**
    * @return the hue of this color, an angle in the range [0.0..2PI]
    */
   public double getHue();


   /**
    * @return the saturation of this color, a float in the range [0.0..1.0]
    */
   public float getSaturation();


   /**
    * @return the luminance of this color, a brightness value weighted by the human eye's color sensitivity, a float in the range
    *         [0.0..1.0]
    */
   public float getLuminance();


   public IColor add(final float delta);


   public IColor add(final IColor that);


   public IColor mul(final float delta);


   public IColor div(final float delta);


   public boolean between(final IColor min,
                          final IColor max);


   public IColor clamp(final IColor lower,
                       final IColor upper);


   public boolean closeTo(final IColor that);


   public IColor interpolatedTo(final IColor that,
                                final float alpha);


   public IColor max(final IColor that);


   public IColor min(final IColor that);


   public IColor sub(final float delta);


   public IColor sub(final IColor that);


   public IColor adjustSaturation(final float saturation);


   public IColor adjustBrightness(final float brightness);


   public IColor adjustSaturationBrightness(final float saturation,
                                            final float brightness);


   public float precision();


   /**
    * An array of thisMany colors around the color wheel starting at the receiver and ending all the way around the hue space just
    * before self. Very useful for displaying color based on a variable in your program.
    */
   public IColor[] wheel(final int thisMany);


   /**
    * Return an array of thisMany colors from the receiver to otherColor. Very useful for displaying color based on a variable in
    * your program.
    */
   public IColor[] mix(final IColor otherColor,
                       final int thisMany);


   public IColor darker();


   public IColor twiceDarker();


   public IColor muchDarker();


   public IColor lighter();


   public IColor twiceLighter();


   public IColor muchLighter();

}
