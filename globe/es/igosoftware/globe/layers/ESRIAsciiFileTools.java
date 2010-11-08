package es.igosoftware.globe.layers;

import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.media.jai.RasterFactory;

import es.igosoftware.euclid.projection.GProjection;

public class ESRIAsciiFileTools {

   public static GGlobeRasterLayer readFile(final File file,
                                            final GProjection proj) throws IOException, NumberFormatException {

      String s = new String();
      StringTokenizer st;
      @SuppressWarnings("unused")
      String sXOrigin, sYOrigin;
      int iCols, iRows;
      double dResolution;
      double dNoData;
      double dX, dY;
      String sToken = new String();
      final BufferedReader fin = new BufferedReader(new FileReader(file));

      s = fin.readLine();
      st = new StringTokenizer(s);
      sToken = st.nextToken();
      sToken = st.nextToken();
      iCols = Integer.parseInt(sToken);
      s = fin.readLine();
      st = new StringTokenizer(s);
      sToken = st.nextToken();
      sToken = st.nextToken();
      iRows = Integer.parseInt(sToken);
      s = fin.readLine();
      st = new StringTokenizer(s);
      sXOrigin = st.nextToken();
      sToken = st.nextToken();
      dX = Double.parseDouble(sToken);
      s = fin.readLine();
      st = new StringTokenizer(s);
      sYOrigin = st.nextToken();
      sToken = st.nextToken();
      dY = Double.parseDouble(sToken);
      s = fin.readLine();
      st = new StringTokenizer(s);
      sToken = st.nextToken();
      sToken = st.nextToken();
      dResolution = Double.parseDouble(sToken);
      s = fin.readLine();
      st = new StringTokenizer(s);
      sToken = st.nextToken();
      sToken = st.nextToken();
      dNoData = Double.parseDouble(sToken);
      if (sXOrigin.equals("xllcenter")) {
         dX = dX - dResolution / 2d;
         dY = dY - dResolution / 2d;
      }

      final WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, iCols, iRows, 1, null);

      for (int y = 0; y < iRows; y++) {
         s = fin.readLine();
         st = new StringTokenizer(s);
         for (int x = 0; x < iCols; x++) {
            sToken = st.nextToken();
            raster.setSample(x, y, 0, Double.parseDouble(sToken));
         }
      }
      fin.close();

      final RasterGeodata extent = new RasterGeodata(dX, dY, dResolution, iRows, iCols, proj);
      final GGlobeRasterLayer layer = new GGlobeRasterLayer(raster, extent);
      layer.setName(file.getName());
      layer.setNoDataValue(dNoData);

      return layer;

   }


   public static boolean writeFile(final GGlobeRasterLayer layer,
                                   final File file) {

      try {
         final WritableRaster raster = layer.getRaster();
         final FileWriter writer = new FileWriter(file);
         final BufferedWriter out = new BufferedWriter(writer);
         final RasterGeodata geodata = layer.getRasterGeodata();
         out.write("NCOLS " + Integer.toString(geodata._cols) + "\n");
         out.write("NROWS " + Integer.toString(geodata._rows) + "\n");
         out.write("XLLCORNER " + Double.toString(geodata._xllcorner) + "\n");
         out.write("YLLCORNER " + Double.toString(geodata._yllcorner) + "\n");
         out.write("CELLSIZE " + Double.toString(geodata._cellsize) + "\n");
         out.write("NODATA_VALUE " + Double.toString(layer.getNoDataValue()) + "\n");
         for (int y = 0; y < geodata._rows; y++) {
            for (int x = 0; x < geodata._cols - 1; x++) {
               out.write(Double.toString(raster.getSampleDouble(x, y, 0)) + " ");
            }
            out.write(Double.toString(raster.getSampleDouble(geodata._cols - 1, y, 0)) + "\n");

         }
         out.close();
      }
      catch (final Exception e) {
         e.printStackTrace();
         return false;
      }

      return true;

   }

}
