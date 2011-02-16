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


package es.igosoftware.panoramic.planar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GUtils;


public class GPlanarPanoramicViewer {


   private class Tile
            extends
               JLabel {
      private static final long               serialVersionUID = 1L;

      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;

      private Image                           _image;


      private Tile(final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {
         super(zoomLevel.getLevel() + " " + x + "@" + y);

         setBorder(new LineBorder(Color.BLUE, 1));

         _zoomLevel = zoomLevel;
         _x = x;
         _y = y;
      }


      private void positionate(final int offsetX,
                               final int offsetY) throws IOException {
         if (_image == null) {
            final URL url = createURL();
            _image = getImageFrom(url);
            setIcon(new ImageIcon(_image));
            setText("");
         }
         setBounds(offsetX + (_x * 256), offsetY + (_y * 256), 256, 256);
      }


      private Image getImageFrom(final URL url) throws IOException {
         InputStream is = null;
         try {
            is = url.openStream();
            return ImageIO.read(is);
         }
         finally {
            GIOUtils.gentlyClose(is);
         }
      }


      private URL createURL() throws MalformedURLException {
         return new URL(_url, _zoomLevel.getLevel() + "/tile-" + _x + "-" + _y + ".jpg");
      }


      private void remove() {
         final int TODO_CancelDownloading;
      }
   }


   private final String                          _name;
   private final URL                             _url;
   private final List<GPlanarPanoramicZoomLevel> _zoomLevels;
   private int                                   _currentLevel;
   private final List<Tile>                      _tiles   = new ArrayList<Tile>();
   private int                                   _offsetX = 0;
   private int                                   _offsetY = 0;
   private final int                             _minLevel;
   private final int                             _maxLevel;

   private JLabel                                _zoomInButton;
   private JLabel                                _zoomOutButton;


   public GPlanarPanoramicViewer(final String name,
                                 final URL url) throws IOException {
      _name = name;
      _url = url;

      _zoomLevels = readZoomLevels();

      int minLevel = Integer.MAX_VALUE;
      int maxLevel = Integer.MIN_VALUE;

      for (final GPlanarPanoramicZoomLevel zoomLevel : _zoomLevels) {
         final int currentLevel = zoomLevel.getLevel();
         minLevel = Math.min(minLevel, currentLevel);
         maxLevel = Math.max(maxLevel, currentLevel);
      }

      _minLevel = minLevel;
      _maxLevel = maxLevel;
      _currentLevel = minLevel;

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            createTiles();
         }
      });
   }


   private void createTiles() {
      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();

      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
            _tiles.add(new Tile(currentZoomLevel, x, y));
         }
      }
   }


   private GPlanarPanoramicZoomLevel getCurrentZoomLevel() {
      for (final GPlanarPanoramicZoomLevel zoomLevel : _zoomLevels) {
         if (zoomLevel.getLevel() == _currentLevel) {
            return zoomLevel;
         }
      }
      return null;
   }


   private List<GPlanarPanoramicZoomLevel> readZoomLevels() throws IOException {
      final URL infoURL = new URL(_url, "info.txt");

      final InputStream ingoIS = infoURL.openStream();

      try {
         final String infoString = GIOUtils.getContents(ingoIS);

         final Gson gson = new Gson();

         final Type type = new TypeToken<List<GPlanarPanoramicZoomLevel>>() {
         }.getType();

         final List<GPlanarPanoramicZoomLevel> result = gson.fromJson(infoString, type);
         return result;
      }
      finally {
         GIOUtils.gentlyClose(ingoIS);
      }
   }


   public void open() throws IOException {
      open(800, 600);
   }


   public void open(final int width,
                    final int height) throws IOException {
      final JFrame frame = createFrame(width, height);

      final Container container = frame.getContentPane();

      container.addMouseWheelListener(new MouseWheelListener() {
         @Override
         public void mouseWheelMoved(final MouseWheelEvent e) {
            final Point point = e.getPoint();

            try {
               if (e.getWheelRotation() < 0) {
                  setZoomLevel(container, _currentLevel + 1, point.x, point.y);
               }
               else {
                  setZoomLevel(container, _currentLevel - 1, point.x, point.y);
               }
            }
            catch (final IOException e1) {
               e1.printStackTrace();
            }
         }
      });


      //      container.addKeyListener(new KeyAdapter() {
      //         @Override
      //         public void keyTyped(final KeyEvent keyEvent) {
      //            final int keyCode = keyEvent.getKeyCode();
      //            //            final int eventID = keyEvent.getID();
      //
      //            //            if (eventID == KeyEvent.KEY_RELEASED) {
      //            final int targetX = (int) container.getBounds().getCenterX();
      //            final int targetY = (int) container.getBounds().getCenterY();
      //
      //            System.out.println(keyEvent + "--->" + keyCode);
      //
      //            switch (keyCode) {
      //               case KeyEvent.VK_ADD:
      //               case KeyEvent.VK_PLUS:
      //                  try {
      //                     setZoomLevel(container, _currentLevel + 1, targetX, targetY);
      //                  }
      //                  catch (final IOException e) {
      //                     e.printStackTrace();
      //                  }
      //
      //                  break;
      //               case KeyEvent.VK_SUBTRACT:
      //               case KeyEvent.VK_MINUS:
      //                  try {
      //                     setZoomLevel(container, _currentLevel - 1, targetX, targetY);
      //                  }
      //                  catch (final IOException e) {
      //                     e.printStackTrace();
      //                  }
      //
      //                  break;
      //            }
      //            //            }
      //         }
      //      });

      createHUD(container);

      frame.setVisible(true);

      final Dimension paneSize = container.getSize();
      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      _offsetX = (paneSize.width - currentZoomLevel.getWidth()) / 2;
      _offsetY = (paneSize.height - currentZoomLevel.getHeight()) / 2;

      updateZoomButtons();

      addTiles(container);
   }


   private JFrame createFrame(final int width,
                              final int height) {
      final JFrame frame = new JFrame(_name);

      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.getRootPane().registerKeyboardAction(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            frame.dispose();
         }
      }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


      frame.setIconImage(getImage("icons/panoramic.png"));

      frame.setSize(width, height);
      frame.setMinimumSize(new Dimension(320, 240));
      frame.setLocationRelativeTo(null);

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            frame.requestFocus();
            frame.requestFocusInWindow();
         }
      });

      frame.getContentPane().setLayout(null);
      frame.getContentPane().setBackground(Color.WHITE);

      return frame;
   }


   private Image getImage(final String imageName) {
      return GUtils.getImage(imageName, getClass().getClassLoader());
   }


   private ImageIcon getImageIcon(final String iconName,
                                  final int width,
                                  final int height) {
      final ImageIcon icon = GUtils.getImageIcon(iconName, getClass().getClassLoader());

      final Image image = icon.getImage();
      if (image == null) {
         return icon;
      }
      final int imageWidth = image.getWidth(null);
      final int imageHeight = image.getHeight(null);
      if ((imageWidth == -1) || (imageHeight == -1)) {
         return icon;
      }
      if ((width == imageWidth) && (height == imageHeight)) {
         return icon;
      }

      final Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      return new ImageIcon(resizedImage);
   }


   private void createHUD(final Container container) {
      final int buttonExtent = 24;
      final int margin = 2;

      createNavigationButtons(container, buttonExtent, margin);
      createZoomButtons(container, buttonExtent, margin);
   }


   private void createNavigationButtons(final Container container,
                                        final int buttonExtent,
                                        final int margin) {
      final JLabel buttonUp = new JLabel(getImageIcon("icons/go-up.png", buttonExtent, buttonExtent));
      hackComponent(buttonUp);
      container.add(buttonUp);
      setPosition(buttonUp, margin + buttonExtent, margin + 0);


      final JLabel buttonDown = new JLabel(getImageIcon("icons/go-down.png", buttonExtent, buttonExtent));
      hackComponent(buttonDown);
      container.add(buttonDown);
      setPosition(buttonDown, margin + buttonExtent, margin + buttonExtent * 2);


      final JLabel buttonLeft = new JLabel(getImageIcon("icons/go-left.png", buttonExtent, buttonExtent));
      hackComponent(buttonLeft);
      container.add(buttonLeft);
      setPosition(buttonLeft, margin + 0, margin + buttonExtent);


      final JLabel buttonRight = new JLabel(getImageIcon("icons/go-right.png", buttonExtent, buttonExtent));
      hackComponent(buttonRight);
      container.add(buttonRight);
      setPosition(buttonRight, margin + buttonExtent * 2, margin + buttonExtent);
   }


   private void hackComponent(final JLabel button) {
      //      button.setBorder(new LineBorder(Color.RED, 1));
      button.setCursor(new Cursor(Cursor.HAND_CURSOR));
   }


   private void createZoomButtons(final Container container,
                                  final int buttonExtent,
                                  final int margin) {
      _zoomInButton = new JLabel(getImageIcon("icons/zoom-in.png", buttonExtent, buttonExtent));
      _zoomInButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            try {
               final int targetX = (int) container.getBounds().getCenterX();
               final int targetY = (int) container.getBounds().getCenterY();
               setZoomLevel(container, _currentLevel + 1, targetX, targetY);
            }
            catch (final IOException e1) {
               e1.printStackTrace();
            }
         }
      });
      hackComponent(_zoomInButton);
      container.add(_zoomInButton);
      setPosition(_zoomInButton, margin + buttonExtent, margin + buttonExtent * 4 + 0);


      _zoomOutButton = new JLabel(getImageIcon("icons/zoom-out.png", buttonExtent, buttonExtent));
      _zoomOutButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            try {
               final int targetX = (int) container.getBounds().getCenterX();
               final int targetY = (int) container.getBounds().getCenterY();
               setZoomLevel(container, _currentLevel - 1, targetX, targetY);
            }
            catch (final IOException e1) {
               e1.printStackTrace();
            }
         }
      });
      hackComponent(_zoomOutButton);
      container.add(_zoomOutButton);
      setPosition(_zoomOutButton, margin + buttonExtent, margin + buttonExtent * 4 + buttonExtent);
   }


   private void setZoomLevel(final Container container,
                             final int newLevel,
                             final int targetX,
                             final int targetY) throws IOException {
      if (newLevel == _currentLevel) {
         return;
      }

      if ((newLevel < _minLevel) || (newLevel > _maxLevel)) {
         return;
      }

      final int oldLevel = _currentLevel;
      _currentLevel = newLevel;

      final double zoomFactor = Math.pow(2, _currentLevel - oldLevel);

      final double targetXForNewZoom = (targetX - _offsetX) * zoomFactor;
      final double targetYForNewZoom = (targetY - _offsetY) * zoomFactor;

      _offsetX = (int) (targetXForNewZoom - targetX) * -1;
      _offsetY = (int) (targetYForNewZoom - targetY) * -1;

      updateZoomButtons();

      recreateTiles(container);
   }


   private void updateZoomButtons() {
      _zoomInButton.setEnabled(_currentLevel < _maxLevel);
      _zoomOutButton.setEnabled(_currentLevel > _minLevel);
   }


   private void recreateTiles(final Container container) throws IOException {
      removeTiles(container);
      createTiles();
      addTiles(container);

      container.invalidate();
      container.doLayout();
      container.repaint();
   }


   private void addTiles(final Container container) throws IOException {
      for (final Tile tile : _tiles) {
         tile.positionate(_offsetX, _offsetY);
         container.add(tile);
      }
   }


   private void removeTiles(final Container container) {
      for (final Tile tile : _tiles) {
         tile.remove();
         container.remove(tile);
      }
      _tiles.clear();
   }


   private void setPosition(final Component component,
                            final int x,
                            final int y) {
      final Dimension size = component.getPreferredSize();
      component.setBounds(x, y, size.width, size.height);
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GPlanarPanoramicViewer 0.1");
      System.out.println("--------------------------\n");


      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            try {
               UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());

               SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
            }
            catch (final UnsupportedLookAndFeelException e) {
               e.printStackTrace();
            }
         }
      });


      // http://82.165.133.233:8888/gigapixel/
      // http://82.165.133.233:8888/gigapixel/panoramas/MartirioDeSanPelayo/


      //      final URL url = new URL("file:///home/dgd/Escritorio/PruebaPanoramicas/PLANAR/PANOS/cantabria1.jpg/");
      final URL url = new URL("http://localhost/PANOS/cantabria1.jpg/");
      //      final URL url = new URL("http://82.165.133.233:8888/gigapixel/panoramas/MartirioDeSanPelayo/");

      final GPlanarPanoramicViewer viewer = new GPlanarPanoramicViewer("Panorámica de Cantabria", url);


      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            try {
               viewer.open();
            }
            catch (final IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      });

   }


}
