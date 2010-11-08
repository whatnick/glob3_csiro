package es.igosoftware.io;

import java.io.File;

public interface ILoader {

   public static class AbortLoading
            extends
               Exception {
      private static final long serialVersionUID = 1L;

   }

   public static interface IHandler {
      public void loadError(final File file,
                            final ILoader.ErrorType error);


      public void loaded(final File file,
                         final int bytesLoaded) throws ILoader.AbortLoading;


      public void stop();
   }


   public static enum ErrorType {
      NOT_FOUND,
      CANT_READ
   }


   public void load(final String fileName,
                    final int bytesToLoad,
                    final int priority,
                    final ILoader.IHandler handler);


   public void cancelLoading(final String fileName);


   //   public void cancelLoad(final String fileName);

}
