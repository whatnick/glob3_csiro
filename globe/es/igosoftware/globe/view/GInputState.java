package es.igosoftware.globe.view;

public enum GInputState {

   ORBIT("Standard Orbit View", true, false),
   PANORAMICS("Panoramic", false, true);

   private final String  _stateName;
   private final boolean _isMoving;
   private final boolean _isPanoramicZoom;


   private GInputState(final String stateName,
                       final boolean isMoving,
                       final boolean isPanoramicZoom) {
      _stateName = stateName;
      _isMoving = isMoving;
      _isPanoramicZoom = isPanoramicZoom;
   }


   public String getName() {
      return _stateName;
   }


   public boolean isMoving() {
      return _isMoving;
   }


   public boolean isPanoramicZoom() {
      return _isPanoramicZoom;
   }


   @Override
   public String toString() {
      return _stateName;
   }

}
