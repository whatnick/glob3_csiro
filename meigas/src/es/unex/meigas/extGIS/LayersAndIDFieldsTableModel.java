package es.unex.meigas.extGIS;

import javax.swing.table.AbstractTableModel;

import es.unex.meigas.core.Meigas;
import es.unex.meigas.dataObjects.AbstractInputFactory;
import es.unex.meigas.dataObjects.IVectorLayer;

public class LayersAndIDFieldsTableModel extends AbstractTableModel {

    private static final String columnNames[] = {"Utilizar", "Capa", "Campo ID"};

    private Object[][] rowData;

    public LayersAndIDFieldsTableModel(LayerAndIDField[] layersAndID){

        IVectorLayer[] layers = Meigas.getInputFactory().getVectorLayers(AbstractInputFactory.SHAPE_TYPE_ANY);
        rowData = new Object[layers.length][3];
        for (int i = 0; i < layers.length; i++) {
            rowData[i][0] = isLayerSelected(layers[i], layersAndID);
            rowData[i][1] = layers[i];
            rowData[i][2] = getIDField(layers[i], layersAndID);
        }

    }

    private Object getIDField(IVectorLayer layer,
                            LayerAndIDField[] layersAndID) {

        for (int i = 0; i < layersAndID.length; i++) {
            if (layersAndID[i].getLayer().getName().equals(layer.getName())){
                return layersAndID[i].getIDField();
            }
        }

        try{
        	return layer.getFieldName(0);
        }
        catch(Exception e){
        	return "";
        }

    }

    private Boolean isLayerSelected(IVectorLayer layer,
                                    LayerAndIDField[] layersAndID) {

        for (int i = 0; i < layersAndID.length; i++) {
            if (layersAndID[i].getLayer().getName().equals(layer.getName())){
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;

    }

    public String getColumnName(int col) {

        return columnNames[col].toString();

    }

    public int getRowCount() {

        return rowData.length;

    }

    public int getColumnCount() {

        return columnNames.length;

    }

    public Object getValueAt(int row, int col) {

        return rowData[row][col];

    }

    public void setValueAt(Object obj, int row, int col){

    	rowData[row][col] = obj;
    	this.fireTableCellUpdated(row, col);

    }

    public Class getColumnClass(int c) {

        return getValueAt(0, c).getClass();

    }

    public boolean isCellEditable(int row, int col) {

        return col != 1;

    }



}
