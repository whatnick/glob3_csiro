package es.unex.meigas.extGIS;

import es.unex.meigas.core.Meigas;

public class PlotsLayersSelectionPanel extends LayersSelectionPanel {

	@Override
	protected LayerAndIDField[] getLayers() {

		return Meigas.getGISConnection().getPlotLayersAndIDFields();
	}

	@Override
	protected void setLayers(LayerAndIDField[] layers) {

		Meigas.getGISConnection().setPlotLayersAndIDFields(layers);

	}

}
