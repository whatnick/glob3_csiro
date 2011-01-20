/**
 * 
 */


package es.igosoftware.dmvc.transferring;

import java.io.Serializable;


public class GDFileRequest
         implements
            Serializable {

   private final String _fileName;


   public GDFileRequest(final String fileName) {
      _fileName = fileName;
   }

   private static final long serialVersionUID = 1L;


   public String getFileName() {
      return _fileName;
   }


   @Override
   public String toString() {
      return "GDFileRequest [fileName=" + _fileName + "]";
   }


}
