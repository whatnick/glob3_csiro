

package es.igosoftware.experimental.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableCellRenderer;

import es.igosoftware.util.GLogger;


public class GDatosForTabla {

   GLogger                                  logger          = GLogger.instance();

   private List                             _listElements   = null;
   private Vector<Vector<?>>                _coleccion      = null;
   private Vector<String>                   _nomColumnas    = null;
   private int[]                            _tamColumnas    = null;
   private Vector<DefaultTableCellRenderer> _formatColmunas = null;


   public Vector<DefaultTableCellRenderer> getFormatColmunas() {
      return _formatColmunas;
   }


   public void setFormatColmunas(final Vector<DefaultTableCellRenderer> formatColmunas) {
      this._formatColmunas = formatColmunas;
   }


   public GDatosForTabla() {
   }


   public GDatosForTabla(final List listElements,
                         final Vector<Vector<?>> coleccion,
                         final Vector<String> nomColumnas,
                         final int[] tamColumnas) {
      this._listElements = listElements;
      this._coleccion = coleccion;
      this._nomColumnas = nomColumnas;
      this._tamColumnas = tamColumnas;
   }


   public boolean isOk() {
      boolean isOk = true;
      if ((_tamColumnas == null) || (_nomColumnas == null) || (_coleccion == null) || (_listElements == null)) {
         logger.info("Alguno de los elmentos est� a null");
         isOk = false;
      }
      else {
         if (isOk && (_listElements.size() != _coleccion.size())) {
            logger.severe("El n�mero de Elementos de la Lista (" + _listElements.size()
                          + ") no coincide con el n�mero de Elementos de la Colecci�n (" + _coleccion.size() + ")");
            isOk = false;
         }
         if (isOk && (_tamColumnas.length != _nomColumnas.size())) {
            logger.severe("El n�mero de Columnas (" + _nomColumnas.size()
                          + ") no coincide con el n�mero de tama�os especificados (" + _tamColumnas.length + ")");
            isOk = false;
         }
         if (isOk && (_listElements.size() > 0)) {
            if (_nomColumnas.size() != _coleccion.get(0).size()) {
               logger.severe("El n�mero de Columnas (" + _nomColumnas.size()
                             + ") no coincide con el n�mero de datos que se est�n pasando (" + _coleccion.get(0).size() + ")");
               isOk = false;
            }
         }
      }

      return isOk;
   }


   /**
    * 
    * @param _coleccion
    * @param element
    * @return
    */
   public void deleteElementSelected(final Vector<?> element) {
      final int tamCollection = _coleccion.size();
      for (int i = 0; i < tamCollection; i++) {
         final Vector<?> elementAux = _coleccion.get(i);
         if ((Integer) element.get(0) == (Integer) elementAux.get(0)) {
            _coleccion.remove(i);
            i = tamCollection;
         }
      }
   }


   public void removeObjectByIndex(final int index) {
      try {
         _listElements.remove(index);
      }
      catch (final Exception ex) {
         logger.severe("ERROR" + ex.getCause());
      }
   }


   public void deleteElementByIndex(final int index) {
      try {
         _listElements.remove(index);
         _coleccion.remove(index);
      }
      catch (final Exception ex) {
         logger.severe("ERROR" + ex.getCause());
      }

   }


   public List getListElements() {
      return _listElements;
   }


   public void setListElements(final List listElements) {
      this._listElements = listElements;
   }


   public Object getObjectByIndex(final int index) {
      if (index >= 0) {
         try {
            return _listElements.get(index);
         }
         catch (final Exception ex) {
            logger.severe("ERROR: " + ex.getCause());
            return null;
         }
      }
      logger.info("INFO: INDEX < 0)");
      return null;

   }


   public Vector getRowDataObject(final Object obj) {
      for (int i = 0; i < _listElements.size(); i++) {
         final Object aux = _listElements.get(i);
         if (aux.equals(obj)) {
            return _coleccion.get(i);
         }
      }

      return null;
   }


   @SuppressWarnings("unchecked")
   public void updateObject(final Object newObj) {
      if ((newObj != null) && (_listElements != null)) {
         for (int i = 0; i < _listElements.size(); i++) {
            final Object oldObj = _listElements.get(i);
            if (oldObj.equals(newObj)) {
               _listElements.set(i, newObj);
               i = _listElements.size();
               //_coleccion = new GDatosForTablaFactory().getDatosForTabla(_listElements, newObj).getColeccion();
               _coleccion = GDatosForTablaFactory.getWMSLayerData(_listElements).getColeccion();
            }
         }
      }
      else {
         logger.info("(public void updateObject(Object newObj))El objeto que se quiere actualizar no est� el la lista de datos ");
      }
   }


   @SuppressWarnings("unchecked")
   public void addObject(final Object newObj) {
      if (newObj != null) {
         _listElements.add(newObj);
         //_coleccion = new GDatosForTablaFactory().getDatosForTabla(_listElements, newObj).getColeccion();
         _coleccion = GDatosForTablaFactory.getWMSLayerData(_listElements).getColeccion();
      }
   }


   @SuppressWarnings("unchecked")
   public void addElementToList(final Object obj) {
      _listElements.add(obj);
   }


   public void removeAllObjects() {
      _listElements = new ArrayList();
   }


   public Vector<Vector<?>> getColeccion() {
      return _coleccion;
   }


   public void setColeccion(final Vector<Vector<?>> coleccion) {
      this._coleccion = coleccion;
   }


   public Vector<String> getNomColumnas() {
      return _nomColumnas;
   }


   public void setNomColumnas(final Vector<String> nomColumnas) {
      this._nomColumnas = nomColumnas;
   }


   public int[] getTamColumnas() {
      return _tamColumnas;
   }


   public void setTamColumnas(final int[] tamColumnas) {
      this._tamColumnas = tamColumnas;
   }


}
