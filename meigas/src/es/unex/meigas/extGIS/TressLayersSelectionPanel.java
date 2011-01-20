package es.unex.meigas.extGIS;

import es.unex.meigas.core.Meigas;

public class TressLayersSelectionPanel extends LayersSelectionPanel {

	@Override
	protected LayerAndIDField[] getLayers() {

		return Meigas.getGISConnection().getTreeLayersAndIDFields();
	}

	@Override
	protected void setLayers(LayerAndIDField[] layers) {

		Meigas.getGISConnection().setTreeLayersAndIDFields(layers);

	}

}
