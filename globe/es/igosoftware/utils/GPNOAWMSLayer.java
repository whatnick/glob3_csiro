/**
 * 
 */
package es.igosoftware.utils;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;
import gov.nasa.worldwind.wms.WMSTiledImageLayer;

import java.net.MalformedURLException;
import java.net.URL;

public class GPNOAWMSLayer
         extends
            WMSTiledImageLayer {


   public static enum ImageFormat {
      JPEG(".jpg", "image/jpeg"),
      PNG(".png", "image/png");

      private final String _extension;
      private final String _format;


      private ImageFormat(final String extension,
                          final String format) {
         _extension = extension;
         _format = format;
      }


      public String getExtension() {
         return _extension;
      }


      public String getFormat() {
         return _format;
      }
   }


   public GPNOAWMSLayer(final GPNOAWMSLayer.ImageFormat imageFormat) {
      super(makeParams(imageFormat));

      //      setMaxActiveAltitude(5000);
      setMaxActiveAltitude(5000);
      setCompressTextures(true);
      //      setOpacity(0.5);
      //            setUseMipMaps(false);
   }


   public GPNOAWMSLayer(final GPNOAWMSLayer.ImageFormat imageFormat,
                        final double maxActiveAltitude) {
      super(makeParams(imageFormat));

      //      setMaxActiveAltitude(5000);
      setMaxActiveAltitude(maxActiveAltitude);
      setCompressTextures(true);
      //      setOpacity(0.5);
      //            setUseMipMaps(false);
   }


   private static AVList makeParams(final GPNOAWMSLayer.ImageFormat imageFormat) {
      final AVList params = new AVListImpl();

      params.setValue(AVKey.TILE_WIDTH, 512);
      params.setValue(AVKey.TILE_HEIGHT, 512);
      params.setValue(AVKey.DATA_CACHE_NAME, "Earth/PNOA");
      params.setValue(AVKey.SERVICE, "http://www.idee.es/wms/PNOA/PNOA");
      params.setValue(AVKey.DATASET_NAME, "PNOA WMS Online");
      params.setValue(AVKey.FORMAT_SUFFIX, imageFormat.getExtension());

      params.setValue(AVKey.NUM_LEVELS, 16);
      params.setValue(AVKey.NUM_EMPTY_LEVELS, 5);
      params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(36), Angle.fromDegrees(36)));
      params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);

      params.setValue(AVKey.TILE_URL_BUILDER, new GPNOAWMSLayer.URLBuilder(imageFormat));

      return params;
   }


   private static class URLBuilder
            implements
               TileUrlBuilder {


      private final GPNOAWMSLayer.ImageFormat _imageFormat;


      private URLBuilder(final ImageFormat imageFormat) {
         _imageFormat = imageFormat;
      }


      @Override
      public URL getURL(final Tile tile,
                        final String altImageFormat) throws MalformedURLException {

         //         http://www.idee.es/wms/PNOA/PNOA?REQUEST=GetMap&VERSION=1.1.1&SERVICE=WMS&SRS=EPSG:25830&BBOX=621273.70693,4415651.72439,624119.18488,4418025.77891&WIDTH=1008&HEIGHT=841&LAYERS=pnoa&STYLES=default&FORMAT=image/jpeg

         final Level level = tile.getLevel();

         final StringBuffer sb = new StringBuffer(level.getService());
         if (sb.lastIndexOf("?") != sb.length() - 1) {
            sb.append("?");
         }

         sb.append("REQUEST=GetMap");
         sb.append("&VERSION=1.1.1");
         sb.append("&SERVICE=WMS");
         sb.append("&SRS=EPSG:4326");

         final Sector sector = tile.getSector();
         sb.append("&BBOX=");
         sb.append(sector.getMinLongitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMinLatitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMaxLongitude().getDegrees());
         sb.append(",");
         sb.append(sector.getMaxLatitude().getDegrees());

         sb.append("&WIDTH=");
         sb.append(level.getTileWidth());
         sb.append("&HEIGHT=");
         sb.append(level.getTileHeight());

         sb.append("&LAYERS=pnoa");
         sb.append("&STYLES=default");
         //         sb.append("&FORMAT=image/jpeg");
         sb.append("&FORMAT=");
         sb.append(_imageFormat.getFormat());

         //            System.out.println(sb);

         return new URL(sb.toString());
      }
   }


   @Override
   public String toString() {
      return "PNOA WMS Online";
   }

}
