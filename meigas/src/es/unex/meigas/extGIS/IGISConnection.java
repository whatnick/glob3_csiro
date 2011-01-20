package es.unex.meigas.extGIS;

import java.awt.geom.Rectangle2D;

import es.unex.meigas.core.DasocraticElement;
import es.unex.meigas.dataObjects.IFeature;
import es.unex.meigas.dataObjects.IVectorLayer;

public interface IGISConnection {

	public LayerAndIDField[] getTreeLayersAndIDFields();

	public LayerAndIDField[] getPlotLayersAndIDFields();

	public void addTreeLayerAndIDField(LayerAndIDField layer);

	public void addPlotLayerAndIDField(LayerAndIDField layer);

	public void removeTreeLayerAndIDField(LayerAndIDField layer);

	public void removePlotLayerAndIDField(LayerAndIDField layer);

	public void setPlotLayersAndIDFields(LayerAndIDField[] layers);

	public void setTreeLayersAndIDFields(LayerAndIDField[] layers);

	public void sync(DasocraticElement element);

	public void setSync(boolean b);

	public void setZoom(boolean bZoom);

	public void setMinScale(boolean bMinScale);

	public void setMinScale(int minScale);

	public boolean isSync();

	public boolean isZoom();

	public boolean isMinScale();

	public int getMinScale();

	public void clearSelection(IVectorLayer layer);

	public void setScale(IVectorLayer layer, int minScale);

	public void zoomTo(IVectorLayer layer, Rectangle2D rect);

	public void select(IVectorLayer layer, int iField);

}