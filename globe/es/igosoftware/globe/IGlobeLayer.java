package es.igosoftware.globe;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;

import java.util.List;

import javax.swing.Icon;

public interface IGlobeLayer
         extends
            Layer {


   @Override
   public String getName();


   public Icon getIcon(final IGlobeApplication application);


   public Sector getExtent();


   public GProjection getProjection();


   public void setProjection(GProjection proj);


   public void redraw();


   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application);


   public void doDefaultAction(final IGlobeApplication application);


   public List<ILayerAction> getLayerActions(final IGlobeApplication application);

}
