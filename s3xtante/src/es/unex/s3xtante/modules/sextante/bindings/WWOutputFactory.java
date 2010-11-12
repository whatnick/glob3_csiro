package es.unex.s3xtante.modules.sextante.bindings;

import javax.swing.JDialog;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GField;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.dataObjects.IRasterLayer;
import es.unex.sextante.dataObjects.ITable;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.UnsupportedOutputChannelException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;
import es.unex.sextante.rasterWrappers.GridExtent;

public class WWOutputFactory
         extends
            OutputFactory {

   @Override
   public IVectorLayer getNewVectorLayer(final String sName,
                                         final int iShapeType,
                                         final Class[] types,
                                         final String[] sFields,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      final GField[] fields = new GField[types.length];
      for (int i = 0; i < fields.length; i++) {
         fields[i] = new GField(sFields[i], types[i]);
      }

      if (channel instanceof FileOutputChannel) {
         final String sFilename = ((FileOutputChannel) channel).getFilename();
         final WWVectorLayer vectorLayer = new WWVectorLayer();
         vectorLayer.create(sName, iShapeType, fields, sFilename, crs);
         return vectorLayer;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   public IRasterLayer getNewRasterLayer(final String sName,
                                         final int iDataType,
                                         final GridExtent extent,
                                         final int iBands,
                                         final IOutputChannel channel,
                                         final Object crs) throws UnsupportedOutputChannelException {

      GProjection proj;

      if ((crs == null) || !(crs instanceof GProjection)) {
         proj = (GProjection) getDefaultCRS();
      }
      else {
         proj = (GProjection) crs;
      }
      if (channel instanceof FileOutputChannel) {
         final String sFilename = ((FileOutputChannel) channel).getFilename();
         final WWRasterLayer layer = new WWRasterLayer();
         layer.create(sName, sFilename, extent, iDataType, iBands, proj);
         return layer;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   public ITable getNewTable(final String sName,
                             final Class types[],
                             final String[] sFields,
                             final IOutputChannel channel) throws UnsupportedOutputChannelException {

      if (channel instanceof FileOutputChannel) {
         final String sFilename = ((FileOutputChannel) channel).getFilename();
         final WWTable table = new WWTable();
         table.create(sName, sFilename, types, sFields);
         return table;
      }
      throw new UnsupportedOutputChannelException();

   }


   @Override
   protected String getTempFolder() {

      return System.getProperty("java.io.tmpdir");

   }


   @Override
   public String[] getRasterLayerOutputExtensions() {

      return new String[] { "asc" };

   }


   @Override
   public String[] getVectorLayerOutputExtensions() {

      return new String[] { "shp" };

   }


   @Override
   public String[] getTableOutputExtensions() {

      return new String[] { "csv" };

   }


   @Override
   public DefaultTaskMonitor getTaskMonitor(final String sTitle,
                                            final boolean bDeterminate,
                                            final JDialog parent) {

      return new DefaultTaskMonitor(sTitle, bDeterminate, parent);

   }


   @Override
   public Object getDefaultCRS() {

      //this is useless right now
      return GProjection.EPSG_4326;

   }


   @Override
   public IVectorLayer getNewVectorLayer(final String name,
                                         final int shapeType,
                                         final Class[] types,
                                         final String[] fields,
                                         final IOutputChannel channel,
                                         final Object crs,
                                         final int[] fieldSize) throws UnsupportedOutputChannelException {

      return getNewVectorLayer(name, shapeType, types, fields, channel, crs);

   }

}
