/**
 * 
 */
package es.igosoftware.globe;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import es.igosoftware.util.GAssert;

public class GLayerInfo
         implements
            ILayerInfo {


   public static List<ILayerInfo> createFromNames(final String... names) {
      if (names == null) {
         return null;
      }

      final ArrayList<ILayerInfo> result = new ArrayList<ILayerInfo>(names.length);
      for (final String name : names) {
         result.add(new GLayerInfo(name));
      }

      return result;
   }


   private final String _name;
   private final Icon   _icon;


   public GLayerInfo(final String name) {
      this(name, null);
   }


   public GLayerInfo(final String name,
                     final Icon icon) {
      GAssert.notNull(name, "_name");

      _name = name;
      _icon = icon;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public Icon getIcon() {
      return _icon;
   }


}
