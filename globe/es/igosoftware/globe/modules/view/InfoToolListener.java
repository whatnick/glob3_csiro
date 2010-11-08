package es.igosoftware.globe.modules.view;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.layers.RasterGeodata;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class InfoToolListener
         implements
            MouseListener {

   private final WorldWindowGLCanvas _ww;
   private final IGlobeApplication   _app;


   public InfoToolListener(final IGlobeApplication application) {
      _app = application;
      _ww = application.getWorldWindowGLCanvas();
   }


   @Override
   public void mouseClicked(final MouseEvent e) {
      if (e.getClickCount() == 1) {
         final PointInfo[] info = getInfo(_ww.getCurrentPosition());
         if (info.length != 0) {
            showInfo(info);
         }
      }
      e.consume();
   }


   @Override
   public void mouseEntered(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mouseExited(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mousePressed(final MouseEvent e) {
      e.consume();
   }


   @Override
   public void mouseReleased(final MouseEvent e) {
      e.consume();
   }


   private void showInfo(final PointInfo[] info) {
      InfoToolDialog dialog = InfoToolDialog.getCurrentInfoDialog();
      if (dialog == null) {
         dialog = new InfoToolDialog(_app.getFrame(), info);
         dialog.setVisible(true);
      }
      else {
         dialog.updateInfo(info);
      }
   }


   @SuppressWarnings("unchecked")
   private PointInfo[] getInfo(final Position currentPosition) {

      final ArrayList<PointInfo> info = new ArrayList<PointInfo>();
      final double elevation = _ww.getModel().getGlobe().getElevation(currentPosition.latitude, currentPosition.longitude);
      PointInfo pinfo = new PointInfo("Terrain model", new GPair[] { new GPair<String, Object>("Elevation",
               Double.valueOf(elevation)) });
      info.add(pinfo);
      final LayerList layers = _app.getLayerList();
      for (int i = 0; i < layers.size(); i++) {
         final Layer layer = layers.get(i);
         if (layer instanceof IGlobeRasterLayer) {
            final IGlobeRasterLayer gRasterLayer = (IGlobeRasterLayer) layer;
            final Sector extent = gRasterLayer.getExtent();
            if (extent.contains(currentPosition)) {
               final RasterGeodata geodata = gRasterLayer.getRasterGeodata();
               final IVector2<?> transformedPt = GProjection.EPSG_4326.transformPoint(geodata._crs, new GVector2D(
                        currentPosition.longitude.degrees, currentPosition.latitude.degrees));
               final double dX = transformedPt.x();
               final double dY = transformedPt.y();
               final int iCol = (int) ((dX - geodata._xllcorner) / geodata._cellsize);
               final int iRow = (int) ((dY - geodata._yllcorner) / geodata._cellsize);
               final WritableRaster raster = gRasterLayer.getRaster();
               final GPair<String, Object>[] values = new GPair[raster.getNumBands()];
               for (int iBand = 0; iBand < values.length; iBand++) {
                  final Double value = new Double(raster.getSampleDouble(iCol, iRow, iBand));
                  values[iBand] = new GPair<String, Object>("Band " + Integer.toString(iBand + 1), value);
               }
               pinfo = new PointInfo(gRasterLayer.getName(), values);
               info.add(pinfo);
            }
         }
         else if (layer instanceof IGlobeVectorLayer) {

         }

      }
      return info.toArray(new PointInfo[0]);
   }
}
