package es.igosoftware.globe;

import java.util.List;


public interface ILayerFactoryModule
         extends
            IGlobeModule {


   public List<ILayerInfo> getAvailableLayers(final IGlobeApplication application);


   public IGlobeLayer addNewLayer(final IGlobeApplication application,
                                  final ILayerInfo layerInfo);


}
