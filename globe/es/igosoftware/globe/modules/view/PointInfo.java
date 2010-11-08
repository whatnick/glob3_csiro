package es.igosoftware.globe.modules.view;

import es.igosoftware.util.GPair;


public class PointInfo {

   public GPair<String, Object>[] _info;
   public String                  _layer;


   public PointInfo(final String layer,
                    final GPair<String, Object>[] info) {

      _info = info;
      _layer = layer;
   }


   @Override
   public String toString() {
      return _layer;
   }

}
