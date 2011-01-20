package es.unex.meigas.extGIS;

import java.util.ArrayList;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.core.Plot;
import es.unex.meigas.core.Tree;
import es.unex.meigas.dataObjects.IFeature;
import es.unex.meigas.dataObjects.IFeatureIterator;
import es.unex.meigas.dataObjects.IVectorLayer;

public abstract class AbstractGISConnection implements IGISConnection{

	private boolean isZoomSet;
	private boolean isMinScaleSet;
	private int minScale;
	private boolean isSyncSet;
	private ArrayList<LayerAndIDField> m_plotsLayers = new ArrayList<LayerAndIDField>();
	private ArrayList<LayerAndIDField> m_treesLayers = new ArrayList<LayerAndIDField>();

	public void addPlotLayerAndIDField(LayerAndIDField layer){

		m_plotsLayers.add(layer);

	}

	public void addTreeLayerAndIDField(LayerAndIDField layer){

		m_treesLayers.add(layer);

	}

	public void removePlotLayerAndIDField(LayerAndIDField layer){

		m_plotsLayers.remove(layer);

	}

	public void removeTreeLayerAndIDField(LayerAndIDField layer){

		m_treesLayers.remove(layer);

	}

	public LayerAndIDField[] getTreeLayersAndIDFields(){

		return (LayerAndIDField[]) m_treesLayers.toArray(new LayerAndIDField[0]);
	}

	public LayerAndIDField[] getPlotLayersAndIDFields(){

		return (LayerAndIDField[]) m_plotsLayers.toArray(new LayerAndIDField[0]);

	}

	public void setPlotLayersAndIDFields(LayerAndIDField[] layers){

		m_plotsLayers.clear();

		for (int i = 0; i < layers.length; i++) {
			m_plotsLayers.add(layers[i]);
		}

	}

	public void setTreeLayersAndIDFields(LayerAndIDField[] layers){

		m_treesLayers.clear();

		for (int i = 0; i < layers.length; i++) {
			m_treesLayers.add(layers[i]);
		}

	}

	public void setSync(boolean b){
		isSyncSet = b;
	}

	public void setZoom(boolean setZoom){
		isZoomSet = setZoom;
		if (!setZoom){
			isMinScaleSet = false;
		}
	}

	public void setMinScale(boolean isMinScale) {

		this.isMinScaleSet = isMinScale;

	}

	public void setMinScale(int minScale) {

		this.minScale = minScale;

	}

	public boolean isSync(){

		return isSyncSet;

	}

	public boolean isZoom(){

		return isZoomSet;

	}

	public boolean isMinScale(){

		return isMinScaleSet;

	}

	public int getMinScale(){

		return minScale;

	}

	public void sync(DasocraticElement element){

		try {

			String sName = null;
			if (!isSyncSet){
				return;
			}

			if (element == null){
				return;
			}

			LayerAndIDField[] layers = null;

			if (element instanceof Tree){
				layers = getTreeLayersAndIDFields();
			}
			else{
				layers = getPlotLayersAndIDFields();
			}

			if (element instanceof Tree || element instanceof Plot){
				sName = element.getName();
			}
			else{
				ArrayList plots = element.getPlots();
				if (plots == null){
					return;
				}
				else{
					sName = ((Plot)plots.get(0)).getName();
				}
			}

			if (layers != null){
				boolean bFound = false;
				for (int i = 0; i < layers.length && !bFound; i++) {
					IVectorLayer layer = layers[i].getLayer();
					layer.open();
					clearSelection(layer);
					int iFieldIndex = layer.getFieldIndexByName(layers[i].getIDField());
					IFeatureIterator iter = layer.iterator();
					int iIndex = 0;
					while(iter.hasNext() && !bFound){
						IFeature feature = iter.next();
						String sNameInTable = feature.getRecord().getValue(iFieldIndex).toString();
						if (sNameInTable.equals(sName)){
							bFound = true;
							select(layer, iIndex);
							if (isZoomSet){
								zoomTo(layer, element.getBoundingBox());
								if (isMinScaleSet){
									setScale(layer, minScale);
								}
							}
						}
						iIndex++;
					}
					layer.close();
				}
			}
		}
		catch(Exception e){}

	}

}
