package es.igosoftware.ootemplate;

import java.awt.image.RenderedImage;

public class OONamedImage {
   private final String        _name;
   private final RenderedImage _image;


   public OONamedImage(final String name,
                       final RenderedImage image) {
      _name = name;
      _image = image;
   }


   public String getName() {
      return _name;
   }


   public RenderedImage getImage() {
      return _image;
   }
}
