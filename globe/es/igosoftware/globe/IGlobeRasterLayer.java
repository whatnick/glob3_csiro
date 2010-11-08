package es.igosoftware.globe;

import java.awt.image.WritableRaster;

import es.igosoftware.globe.layers.RasterGeodata;

public interface IGlobeRasterLayer
         extends
            IGlobeLayer {

   public WritableRaster getRaster();


   public RasterGeodata getRasterGeodata();


   public double getNoDataValue();

}
