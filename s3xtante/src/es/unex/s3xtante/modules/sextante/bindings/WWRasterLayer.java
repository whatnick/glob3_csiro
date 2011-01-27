package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.media.jai.RasterFactory;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.layers.ESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.globe.layers.RasterGeodata;
import es.unex.s3xtante.utils.ProjectionUtils;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.dataObjects.AbstractRasterLayer;


public class WWRasterLayer
         extends
            AbstractRasterLayer {

   private static final double DEFAULT_NO_DATA_VALUE = -99999.;

   private GProjection         m_CRS;
   private String              m_sFilename;
   private Raster              m_Raster;

   private AnalysisExtent      m_LayerExtent;

   private double              m_dNoDataValue;
   private String              m_sName;


   public void create(final String name,
                      final String filename,
                      final AnalysisExtent ae,
                      int dataType,
                      final int numBands,
                      final GProjection crs) {

      if (crs == null) {
         m_CRS = ProjectionUtils.getDefaultProjection();
      }
      else {
         m_CRS = crs;
      }

      if (dataType == DataBuffer.TYPE_DOUBLE) {
         dataType = DataBuffer.TYPE_FLOAT;
      }

      System.out.println(ae.getNX());
      System.out.println(ae.getNY());
      m_Raster = RasterFactory.createBandedRaster(dataType, ae.getNX(), ae.getNY(), numBands, null);

      m_sFilename = filename;
      m_sName = name;
      m_LayerExtent = ae;
      m_dNoDataValue = DEFAULT_NO_DATA_VALUE;

   }


   public void create(final IGlobeRasterLayer layer) {

      m_BaseDataObject = layer;
      m_CRS = layer.getRasterGeodata()._crs;
      m_Raster = layer.getRaster();
      final RasterGeodata extent = layer.getRasterGeodata();
      m_LayerExtent = new AnalysisExtent();

      m_LayerExtent.setCellSize(extent._cellsize);
      m_LayerExtent.setXRange(extent._xllcorner, extent._xllcorner + extent._cols * extent._cellsize, true);
      m_LayerExtent.setYRange(extent._yllcorner, extent._yllcorner + extent._rows * extent._cellsize, true);


      m_sName = layer.getName();

      m_dNoDataValue = layer.getNoDataValue();

   }


   @Override
   public int getBandsCount() {

      return m_Raster.getNumBands();

   }


   @Override
   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      if (m_Raster != null) {
         return m_Raster.getSampleDouble(x, y, band);
      }
      return getNoDataValue();

   }


   @Override
   public int getDataType() {

      if (m_Raster != null) {
         return m_Raster.getDataBuffer().getDataType();
      }
      return DataBuffer.TYPE_DOUBLE;

   }


   @Override
   public double getLayerCellSize() {

      if (m_LayerExtent != null) {
         return m_LayerExtent.getCellSize();
      }
      return 0;

   }


   @Override
   public AnalysisExtent getLayerGridExtent() {

      return m_LayerExtent;

   }


   @Override
   public double getNoDataValue() {

      return m_dNoDataValue;

   }


   @Override
   public void setCellValue(final int x,
                            final int y,
                            final int band,
                            final double value) {

      if (isInWindow(x, y)) {
         ((WritableRaster) m_Raster).setSample(x, y, band, value);
      }

   }


   @Override
   public void setNoDataValue(final double noDataValue) {

      m_dNoDataValue = noDataValue;

   }


   @Override
   public Object getCRS() {

      return m_CRS;

   }


   @Override
   public Rectangle2D getFullExtent() {

      return m_LayerExtent.getAsRectangle2D();

   }


   @Override
   public void open() {

   }


   @Override
   public void close() {

   }


   @Override
   public void postProcess() {

      try {

         // BufferedImage bi = new
         // BufferedImage(RasterRenderer.getDefaultColorModel(
         // (WritableRaster)m_Raster), (WritableRaster)m_Raster, false,
         // null);
         // Sector sector = Sector.fromDegrees(m_LayerExtent.getYMin(),
         // m_LayerExtent.getYMax(),
         // m_LayerExtent.getXMin(), m_LayerExtent.getXMax());
         // GeotiffWriter writer;
         // File file = new File(m_sFilename);
         // writer = new GeotiffWriter(file);
         // writer.write(bi);
         // TODO:Sector???????????
         final File file = new File(m_sFilename);
         final RasterGeodata geodata = new RasterGeodata(m_LayerExtent.getXMin(), m_LayerExtent.getYMin(),
                  m_LayerExtent.getCellSize(), m_LayerExtent.getNY(), m_LayerExtent.getNX(), m_CRS);
         /*  final RasterGeodata geodata = new RasterGeodata(Math.toRadians(m_LayerExtent.getXMin()),
                    Math.toRadians(m_LayerExtent.getYMin()), Math.toRadians(m_LayerExtent.getCellSize()), m_LayerExtent.getNY(),
                    m_LayerExtent.getNX(), ProjectionUtils.getDefaultProjection());*/
         final GGlobeRasterLayer layer = new GGlobeRasterLayer(m_Raster, geodata);
         layer.setNoDataValue(m_dNoDataValue);
         layer.setName(m_sName);
         ESRIAsciiFileTools.writeFile(layer, file);
         this.create(layer);

         //GGlobeRasterLayer.getRasterLayerFromFile(file));
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public String getFilename() {

      return m_sFilename;

   }


   @Override
   public String getName() {

      return m_sName;

   }


   @Override
   public void setName(final String sName) {

      m_sName = sName;
      if (m_BaseDataObject != null) {
         ((GGlobeRasterLayer) m_BaseDataObject).setName(sName);
      }

   }

}
