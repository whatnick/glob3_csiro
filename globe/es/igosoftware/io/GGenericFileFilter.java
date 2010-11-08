package es.igosoftware.io;

import java.io.File;
import java.util.Arrays;

import javax.swing.filechooser.FileFilter;

public class GGenericFileFilter
         extends
            FileFilter {

   private final String[] _extensions;
   private final String   _description;
   private final boolean  _acceptDirectories = true;


   public GGenericFileFilter(final String[] extensions,
                             final String description) {
      _extensions = Arrays.copyOf(extensions, extensions.length);
      _description = description;
   }


   public GGenericFileFilter(final String extensions,
                             final String description) {
      this(new String[] { extensions }, description);
   }


   @Override
   public boolean accept(final File f) {

      if (f.isDirectory()) {
         if (_acceptDirectories) {
            return true;
         }
      }

      if (_extensions[0] == null) {
         return true;
      }

      boolean endsWith = false;
      for (final String element : _extensions) {
         if (f.getName().toUpperCase().endsWith(element.toUpperCase())) {
            endsWith = true;
         }
      }

      return endsWith;
   }


   /**
    * @see javax.swing.filechooser.FileFilter#getDescription()
    */
   @Override
   public String getDescription() {
      return _description;
   }
}
