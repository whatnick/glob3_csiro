

package es.unex.s3xtante.modules.sextante.bindings;

import java.util.ArrayList;

import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.core.AbstractInputFactory;
import es.unex.sextante.core.NamedExtent;
import es.unex.sextante.dataObjects.IDataObject;
import es.unex.sextante.dataObjects.ITable;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;


/**
 * An input factory to get data objects from WW into SEXTANTE
 * 
 * @author volaya
 * 
 */
public class WWInputFactory
         extends
            AbstractInputFactory {

   Model m_Model = null;


   public WWInputFactory(final Model model) {

      m_Model = model;

   }


   @Override
   public void createDataObjects() {

      IDataObject obj;
      final ArrayList<IDataObject> layers = new ArrayList<IDataObject>();
      final LayerList layerList = m_Model.getLayers();

      for (int i = 0; i < layerList.size(); i++) {
         final Layer layer = layerList.get(i);
         if (layer instanceof IGlobeVectorLayer) {
            obj = new WWVectorLayer();
            ((WWVectorLayer) obj).create((IGlobeVectorLayer) layer);
            layers.add(obj);
         }
         else if (layer instanceof IGlobeRasterLayer) {
            obj = new WWRasterLayer();
            ((WWRasterLayer) obj).create((IGlobeRasterLayer) layer);
            layers.add(obj);
         }
      }

      final ITable[] tables = Tables.getTables();

      m_Objects = new IDataObject[layers.size() + tables.length];

      for (int i = 0; i < layers.size(); i++) {
         m_Objects[i] = layers.get(i);
      }

      for (int i = 0; i < tables.length; i++) {
         m_Objects[i + layers.size()] = tables[i];
      }

   }


   @Override
   public NamedExtent[] getPredefinedExtents() {

      return new NamedExtent[0];

   }


   @Override
   public String[] getRasterLayerInputExtensions() {

      return new String[] { "tif", "asc" };

   }


   @Override
   public String[] getTableInputExtensions() {

      return new String[] { "csv" };

   }


   @Override
   public String[] getVectorLayerInputExtensions() {

      return new String[] { "shp" };

   }


   @Override
   public void close(final String sName) {

      // ///TODO

   }


   @Override
   public IDataObject openDataObjectFromFile(final String sFilename) {

      return null;
      //return (IDataObject) FileTools.openFile(sFilename);

   }

}
