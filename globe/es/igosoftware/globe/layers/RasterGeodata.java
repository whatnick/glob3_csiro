package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;

public class RasterGeodata {

   public final double _cellsize;
   public final double _yllcorner;
   public final double _xllcorner;
   public final int    _rows;
   public final int    _cols;
   public GProjection  _crs;


   public RasterGeodata(final double xllcorner,
                        final double yllcorner,
                        final double cellsize,
                        final int rows,
                        final int cols,
                        final GProjection proj) {
      _xllcorner = xllcorner;
      _yllcorner = yllcorner;
      _cellsize = cellsize;
      _cols = cols;
      _rows = rows;
      _crs = proj;
   }


   public Sector getAsSector() {

      final IVector2<?> min = _crs.transformPoint(GProjection.EPSG_4326, new GVector2D(_xllcorner, _yllcorner));
      final IVector2<?> max = _crs.transformPoint(GProjection.EPSG_4326, new GVector2D(_xllcorner + _cols * _cellsize,
               _yllcorner + _rows * _cellsize));

      final Sector sector = new Sector(Angle.fromRadians(min.y()), Angle.fromRadians(max.y()), Angle.fromRadians(min.x()),
               Angle.fromRadians(max.x()));

      return sector;

   }

}
