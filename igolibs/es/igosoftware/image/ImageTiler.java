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

package es.igosoftware.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

//import es.igosoftware.util.Logger;

public class ImageTiler {
   static private final int TILE_WIDTH  = 256;
   static private final int TILE_HEIGHT = 256;


   private static class ZoomLevel {
      private final int level;
      private final int width;
      private final int height;
      private final int widthInTiles;
      private final int heightInTiles;


      private ZoomLevel(final int l,
                        final int w,
                        final int h) {
         level = l;
         width = w;
         height = h;

         int widthT = width / ImageTiler.TILE_WIDTH;
         if ((widthT * ImageTiler.TILE_WIDTH) < width) {
            widthT++;
         }
         widthInTiles = widthT;

         int heightT = height / ImageTiler.TILE_HEIGHT;
         if ((heightT * ImageTiler.TILE_HEIGHT) < height) {
            heightT++;
         }
         heightInTiles = heightT;
      }


      @Override
      public String toString() {
         return "Level=" + level + ", Pixels=" + width + "x" + height + ", " + ((float) (width * height) / 1024 / 1024)
                + "Mpx, Tiles=" + widthInTiles + "x" + heightInTiles;
      }
   }


   private static ZoomLevel[] getZoomLevels(final BufferedImage bi) {
      int currentWidth = bi.getWidth();
      int currentHeight = bi.getHeight();
      int levelCount = 1;
      do {
         currentWidth /= 2;
         currentHeight /= 2;
         levelCount++;
      }
      while ((currentWidth > ImageTiler.TILE_WIDTH) && (currentHeight > ImageTiler.TILE_HEIGHT));

      final ZoomLevel[] levels = new ZoomLevel[levelCount];

      currentWidth = bi.getWidth();
      currentHeight = bi.getHeight();

      for (int i = 0; i < levelCount; i++) {
         levels[i] = new ZoomLevel(levelCount - i, currentWidth, currentHeight);

         currentWidth /= 2;
         currentHeight /= 2;
      }

      return levels;
   }


   //private static final Logger log = Logger.instance();


   public static void main(final String[] args) throws IOException {
      System.out.println("Image Tiler 0.1");
      System.out.println("---------------\n");

      if (args.length < 2) {
         //ImageTiler.log.severe("Invalid arguments: SourceImageFileName and OutputDirectoryName are mandatory, maxLevel is optional");
         System.exit(1);
      }

      //ImageTiler.log.info("------------------------------------------------------------------");
      //ImageTiler.log.info("Running parameters");
      final String imageFullName = args[0];
      //ImageTiler.log.info("  Source Image    : " + imageFullName);
      final String outputDirectoryName = args[1];
      //ImageTiler.log.info("  Output Directory: " + outputDirectoryName);

      final int maxLevel;
      if (args.length > 2) {
         maxLevel = Integer.parseInt(args[2]);
         //ImageTiler.log.info("  Max Level       : " + maxLevel);
      }
      else {
         maxLevel = Integer.MAX_VALUE;
         //ImageTiler.log.info("  Max Level       : ALL");
      }
      //ImageTiler.log.info("------------------------------------------------------------------");

      processImage(imageFullName, outputDirectoryName, maxLevel);
   }


   public static void processImage(final String imageFullName,
                                   final String outputDirectoryName) throws IOException {
      processImage(imageFullName, outputDirectoryName, 5);
   }


   private static void processImage(final String imageFullName,
                                    final String outputDirectoryName,
                                    final int maxLevel) throws IOException {
      //final long started = System.currentTimeMillis();
      //ImageTiler.log.info("Processing " + imageFullName + "...");

      final File file = new File(imageFullName);

      final BufferedImage bi = ImageIO.read(file);

      // final Raster data = bi.getData();

      final String imageName = file.getName();

      final String imageOutputDirectory = outputDirectoryName + "/" + imageName + "/";

      final ZoomLevel[] zoomLevels = getZoomLevels(bi);

      //ImageTiler.log.info("  Generating zoom levels information...");
      createZoomLevelsInfo(imageOutputDirectory, zoomLevels);

      for (final ZoomLevel zoomLevel : zoomLevels) {
         if (zoomLevel.level <= maxLevel) {
            //ImageTiler.log.info("  Processing zoom level #" + zoomLevel.level);
            //ImageTiler.log.info("    Zoom Level Info: " + zoomLevel);

            final String levelDirectory = imageOutputDirectory + zoomLevel.level + "/";
            //ImageTiler.log.info("    Zoom Level Directory: " + levelDirectory);
            new File(levelDirectory).mkdirs();

            final Image scaledImage;
            if ((zoomLevel.width == bi.getWidth()) && (zoomLevel.height == bi.getHeight())) {
               //ImageTiler.log.info("    No need to scale image");
               scaledImage = bi;
            }
            else {
               //ImageTiler.log.info("    Scaling image...");
               scaledImage = bi.getScaledInstance(zoomLevel.width, zoomLevel.height, Image.SCALE_SMOOTH);
            }

            // log.info(" Saving scaled image...");
            // final File scaledFile = new File(levelDirectory + "scaled.png");
            final BufferedImage scaledRenderedImage = getRenderedImage(scaledImage);
            // ImageIO.write(scaledRenderedImage, "png", scaledFile);

            //ImageTiler.log.info("    Saving tiles...");
            final int scaleImageWidth = scaledRenderedImage.getWidth();
            final int scaleImageHeight = scaledRenderedImage.getHeight();

            for (int widthIndex = 0; widthIndex < zoomLevel.widthInTiles; widthIndex++) {
               for (int heightIndex = 0; heightIndex < zoomLevel.heightInTiles; heightIndex++) {
                  final int tileX = ImageTiler.TILE_WIDTH * widthIndex;
                  final int tileY = ImageTiler.TILE_HEIGHT * heightIndex;

                  int tileWidth = ImageTiler.TILE_WIDTH;
                  if ((tileX + tileWidth) > scaleImageWidth) {
                     tileWidth += (scaleImageWidth - (tileX + tileWidth));
                  }

                  int tileHeight = ImageTiler.TILE_HEIGHT;
                  if ((tileY + tileHeight) > scaleImageHeight) {
                     tileHeight += (scaleImageHeight - (tileY + tileHeight));
                  }

                  //ImageTiler.log.info("      Saving tile " + widthIndex + "@" + heightIndex + ", Width=" + tileWidth
                  //                    + ", Height=" + tileHeight + "...");

                  // final Raster tileData = scaledRenderedImage.getData(new Rectangle(tileX, tileY, tileWidth, tileHeight));
                  final BufferedImage tileImage = resize(scaledRenderedImage.getSubimage(tileX, tileY, tileWidth, tileHeight),
                           ImageTiler.TILE_WIDTH, ImageTiler.TILE_HEIGHT);

                  final File tileFile = new File(levelDirectory + "tile-" + widthIndex + "-" + heightIndex + ".jpg");
                  ImageIO.write(tileImage, "jpeg", tileFile);
               }
            }
         }
         else {
            //ImageTiler.log.info("  Ignoring zoom level #" + zoomLevel.level);
            //ImageTiler.log.info("    Zoom Level Info: " + zoomLevel);
         }
      }

      //final long ellapsed = System.currentTimeMillis() - started;
      //ImageTiler.log.info("Processed in " + ((float) ellapsed / 1000) + "s");
   }


   private static void createZoomLevelsInfo(final String imageOutputDirectory,
                                            final ZoomLevel[] zoomLevels) throws IOException {
      new File(imageOutputDirectory).mkdirs();

      final BufferedWriter info = new BufferedWriter(new FileWriter(imageOutputDirectory + "info.txt"));
      info.write("[");
      info.newLine();
      boolean first = true;
      for (final ZoomLevel zoomLevel : zoomLevels) {
         if (first) {
            first = false;
         }
         else {
            info.write(",");
            info.newLine();
         }
         info.write("  {level:" + zoomLevel.level);
         info.write(", width:" + zoomLevel.width);
         info.write(", height:" + zoomLevel.height);
         info.write(", widthInTiles:" + zoomLevel.widthInTiles);
         info.write(", heightInTiles:" + zoomLevel.heightInTiles);
         info.write("}");
      }
      info.newLine();
      info.write("]");
      info.close();
   }


   private static BufferedImage resize(final BufferedImage bi,
                                       final int width,
                                       final int height) {
      if ((bi.getWidth() == width) && (bi.getHeight() == height)) {
         return bi;
      }

      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, width, height);
      g2d.drawImage(bi, 0, 0, null);
      g2d.dispose();

      return renderedImage;
   }


   private static BufferedImage getRenderedImage(final Image image) {
      final BufferedImage renderedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
               BufferedImage.TYPE_3BYTE_BGR);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.dispose();

      return renderedImage;
   }
}
