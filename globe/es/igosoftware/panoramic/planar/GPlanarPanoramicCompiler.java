

package es.igosoftware.panoramic.planar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import es.igosoftware.io.GIOUtils;


public class GPlanarPanoramicCompiler {
   private static final int TILE_WIDTH  = 256;
   private static final int TILE_HEIGHT = 256;


   private static GPlanarPanoramicZoomLevel[] getZoomLevels(final BufferedImage bi) {
      int currentWidth = bi.getWidth();
      int currentHeight = bi.getHeight();
      int levelCount = 1;
      do {
         currentWidth /= 2;
         currentHeight /= 2;
         levelCount++;
      }
      while ((currentWidth > TILE_WIDTH) && (currentHeight > TILE_HEIGHT));

      final GPlanarPanoramicZoomLevel[] levels = new GPlanarPanoramicZoomLevel[levelCount];

      currentWidth = bi.getWidth();
      currentHeight = bi.getHeight();

      for (int i = 0; i < levelCount; i++) {
         levels[i] = new GPlanarPanoramicZoomLevel(levelCount - i, currentWidth, currentHeight, TILE_WIDTH, TILE_HEIGHT);

         currentWidth /= 2;
         currentHeight /= 2;
      }

      return levels;
   }


   //   private static final GLogger log = GLogger.instance();


   public static void main(final String[] args) throws IOException {
      System.out.println("Image Tiler 0.1");
      System.out.println("---------------\n");

      if (args.length < 2) {
         System.err.println("Invalid arguments: SourceImageFileName and OutputDirectoryName are mandatory, maxLevel is optional");
         System.exit(1);
      }

      System.out.println("------------------------------------------------------------------");
      System.out.println("Running parameters");
      final String imageFullName = args[0];
      System.out.println("  Source Image    : " + imageFullName);
      final String outputDirectoryName = args[1];
      System.out.println("  Output Directory: " + outputDirectoryName);

      final int maxLevel;
      if (args.length > 2) {
         maxLevel = Integer.parseInt(args[2]);
         System.out.println("  Max Level       : " + maxLevel);
      }
      else {
         maxLevel = Integer.MAX_VALUE;
         System.out.println("  Max Level       : ALL");
      }
      System.out.println("------------------------------------------------------------------");

      processImage(imageFullName, outputDirectoryName, maxLevel);
   }


   private static void processImage(final String imageFullName,
                                    final String outputDirectoryName,
                                    final int maxLevel) throws IOException {
      final long started = System.currentTimeMillis();
      System.out.println("Processing " + imageFullName + "...");

      final File file = new File(imageFullName);

      final BufferedImage bi = ImageIO.read(file);

      final String imageName = file.getName();

      final String outputDirectory = outputDirectoryName + "/" + imageName + "/";

      final GPlanarPanoramicZoomLevel[] zoomLevels = getZoomLevels(bi);

      //new File(outputDirectory).mkdirs();
      GIOUtils.assureEmptyDirectory(outputDirectory, false);

      System.out.println("  Generating zoom levels information...");
      createZoomLevelsInfo(outputDirectory, zoomLevels);

      for (final GPlanarPanoramicZoomLevel zoomLevel : zoomLevels) {
         if (zoomLevel.getLevel() <= maxLevel) {
            System.out.println("  Processing zoom level #" + zoomLevel.getLevel());
            System.out.println("    Zoom Level Info: " + zoomLevel);

            final String levelDirectory = outputDirectory + zoomLevel.getLevel() + "/";
            System.out.println("    Zoom Level Directory: " + levelDirectory);
            new File(levelDirectory).mkdirs();

            final Image scaledImage;
            if ((zoomLevel.getWidth() == bi.getWidth()) && (zoomLevel.getHeight() == bi.getHeight())) {
               System.out.println("    No need to scale image");
               scaledImage = bi;
            }
            else {
               System.out.println("    Scaling image...");
               scaledImage = bi.getScaledInstance(zoomLevel.getWidth(), zoomLevel.getHeight(), Image.SCALE_SMOOTH);
            }

            // System.out.println(" Saving scaled image...");
            // final File scaledFile = new File(levelDirectory + "scaled.png");
            final BufferedImage scaledRenderedImage = getRenderedImage(scaledImage);
            // ImageIO.write(scaledRenderedImage, "png", scaledFile);

            System.out.println("    Saving tiles...");
            final int scaleImageWidth = scaledRenderedImage.getWidth();
            final int scaleImageHeight = scaledRenderedImage.getHeight();

            for (int widthIndex = 0; widthIndex < zoomLevel.getWidthInTiles(); widthIndex++) {
               for (int heightIndex = 0; heightIndex < zoomLevel.getHeightInTiles(); heightIndex++) {
                  final int tileX = TILE_WIDTH * widthIndex;
                  final int tileY = TILE_HEIGHT * heightIndex;

                  int tileWidth = TILE_WIDTH;
                  if ((tileX + tileWidth) > scaleImageWidth) {
                     tileWidth += (scaleImageWidth - (tileX + tileWidth));
                  }

                  int tileHeight = TILE_HEIGHT;
                  if ((tileY + tileHeight) > scaleImageHeight) {
                     tileHeight += (scaleImageHeight - (tileY + tileHeight));
                  }

                  //                  System.out.println("      Saving tile " + widthIndex + "@" + heightIndex + ", Width=" + tileWidth + ", Height="
                  //                                     + tileHeight + "...");

                  final BufferedImage tileImage = resize(scaledRenderedImage.getSubimage(tileX, tileY, tileWidth, tileHeight),
                           TILE_WIDTH, TILE_HEIGHT);

                  final File tileFile = new File(levelDirectory + "tile-" + widthIndex + "-" + heightIndex + ".jpg");
                  ImageIO.write(tileImage, "jpeg", tileFile);
               }
            }
         }
         else {
            System.out.println("  Ignoring zoom level #" + zoomLevel.getLevel());
            System.out.println("    Zoom Level Info: " + zoomLevel);
         }
      }

      final long ellapsed = System.currentTimeMillis() - started;
      System.out.println("Processed in " + ((float) ellapsed / 1000) + "s");
   }


   private static void createZoomLevelsInfo(final String outputDirectory,
                                            final GPlanarPanoramicZoomLevel[] zoomLevels) throws IOException {

      final BufferedWriter info = new BufferedWriter(new FileWriter(outputDirectory + "info.txt"));
      info.write("[");
      info.newLine();
      boolean first = true;
      for (final GPlanarPanoramicZoomLevel zoomLevel : zoomLevels) {
         if (first) {
            first = false;
         }
         else {
            info.write(",");
            info.newLine();
         }
         info.write("  {level:" + zoomLevel.getLevel());
         info.write(", width:" + zoomLevel.getWidth());
         info.write(", height:" + zoomLevel.getHeight());
         info.write(", widthInTiles:" + zoomLevel.getWidthInTiles());
         info.write(", heightInTiles:" + zoomLevel.getHeightInTiles());
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
      if (image instanceof BufferedImage) {
         return (BufferedImage) image;
      }

      final BufferedImage renderedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
               BufferedImage.TYPE_3BYTE_BGR);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.dispose();

      return renderedImage;
   }
}
