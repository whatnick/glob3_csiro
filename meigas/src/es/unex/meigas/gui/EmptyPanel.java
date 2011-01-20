package es.unex.meigas.gui;

import java.awt.Graphics;

import javax.swing.ImageIcon;

public class EmptyPanel
         extends
            DasocraticElementPanel {

   ImageIcon m_Image;


   public EmptyPanel() {

      super(null, null);

   }


   @Override
   protected void initializeContent() {

      m_Image = new ImageIcon("images/splash.png");

   }


   @Override
   protected void initGUI() {}


   @Override
   public void paint(final Graphics g) {

      final int x = (this.getWidth() - m_Image.getIconWidth()) / 2;
      final int y = (this.getHeight() - m_Image.getIconHeight()) / 2;

      m_Image.paintIcon(this, g, x, y);

   }


   @Override
   public boolean checkDataAndUpdate() {

      return true;

   }


   @Override
   protected void updateContent() {}

}
