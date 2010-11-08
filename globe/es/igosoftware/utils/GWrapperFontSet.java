/**
 * 
 */
package es.igosoftware.utils;

import javax.swing.plaf.FontUIResource;

import org.pushingpixels.substance.api.fonts.FontSet;

public class GWrapperFontSet
         implements
            FontSet {

   private final int     _delta;
   private final FontSet _delegate;


   public GWrapperFontSet(final FontSet delegate,
                          final int delta) {
      _delegate = delegate;
      _delta = delta;
   }


   private FontUIResource getWrappedFont(final FontUIResource systemFont) {
      return new FontUIResource(systemFont.getFontName(), systemFont.getStyle(), systemFont.getSize() + _delta);
   }


   @Override
   public FontUIResource getControlFont() {
      return getWrappedFont(_delegate.getControlFont());
   }


   @Override
   public FontUIResource getMenuFont() {
      return getWrappedFont(_delegate.getMenuFont());
   }


   @Override
   public FontUIResource getMessageFont() {
      return getWrappedFont(_delegate.getMessageFont());
   }


   @Override
   public FontUIResource getSmallFont() {
      return getWrappedFont(_delegate.getSmallFont());
   }


   @Override
   public FontUIResource getTitleFont() {
      return getWrappedFont(_delegate.getTitleFont());
   }


   @Override
   public FontUIResource getWindowTitleFont() {
      return getWrappedFont(_delegate.getWindowTitleFont());
   }
}
