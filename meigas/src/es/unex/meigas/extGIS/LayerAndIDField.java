package es.unex.meigas.extGIS;

import es.unex.meigas.dataObjects.IVectorLayer;

public class LayerAndIDField {

	private String IDField;
	private IVectorLayer layer;

	public LayerAndIDField(IVectorLayer layer, String IDField){

		this.layer = layer;
		this.IDField = IDField;

	}

	public String getIDField() {
		return IDField;
	}

	public void setIDField(String field) {
		IDField = field;
	}

	public IVectorLayer getLayer() {
		return layer;
	}

	public void setLayer(IVectorLayer layer) {
		this.layer = layer;
	}

}
