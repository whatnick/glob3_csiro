/**
 * 
 */
package es.igosoftware.ootemplate;

public class OOSyntaxException
         extends
            Exception {
   private static final long serialVersionUID = 1L;


   public OOSyntaxException(final String msg) {
      super(msg);
   }


   public OOSyntaxException(final Throwable t) {
      super(t);
   }

}
