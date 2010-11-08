package es.igosoftware.globe.layers;

import java.awt.image.BufferedImage;

public class GSocialNetworkUser {

   private BufferedImage _img;
   private String        _name;


   public BufferedImage getImg() {
      return _img;
   }


   public void setImg(final BufferedImage img) {
      _img = img;
   }


   public String getName() {
      return _name;
   }


   public void setName(final String name) {
      _name = name;
   }


}
