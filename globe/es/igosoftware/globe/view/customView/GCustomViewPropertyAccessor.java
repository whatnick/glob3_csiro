package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.view.orbit.OrbitView;


public class GCustomViewPropertyAccessor
         extends
            ViewPropertyAccessor {

   private GCustomViewPropertyAccessor() {}


   public static PropertyAccessor.PositionAccessor createCenterPositionAccessor(final OrbitView view) {
      return new CenterPositionAccessor(view);
   }


   public static PropertyAccessor.DoubleAccessor createZoomAccessor(final OrbitView view) {
      return new ZoomAccessor(view);
   }

   private static class CenterPositionAccessor
            implements
               PropertyAccessor.PositionAccessor {
      private final OrbitView _customView;


      public CenterPositionAccessor(final OrbitView view) {
         this._customView = view;
      }


      @Override
      public Position getPosition() {
         if (this._customView == null) {
            return null;
         }

         return _customView.getCenterPosition();

      }


      @Override
      public boolean setPosition(final Position value) {
         //noinspection SimplifiableIfStatement
         if ((this._customView == null) || (value == null)) {
            return false;
         }


         try {

            this._customView.setCenterPosition(value);
            return true;
         }
         catch (final Exception e) {
            return false;
         }
      }
   }

   private static class ZoomAccessor
            implements
               PropertyAccessor.DoubleAccessor {
      OrbitView _customView;


      public ZoomAccessor(final OrbitView customView) {
         this._customView = customView;
      }


      @Override
      public final Double getDouble() {
         if (this._customView == null) {
            return null;
         }

         return this._customView.getZoom();

      }


      @Override
      public final boolean setDouble(final Double value) {
         //noinspection SimplifiableIfStatement
         if ((this._customView == null) || (value == null)) {
            return false;
         }

         try {
            this._customView.setZoom(value);
            return true;

         }
         catch (final Exception e) {
            return false;
         }
      }
   }

}
