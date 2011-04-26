

package es.igosoftware.euclid.scripting;


public class GIllegalScriptException
         extends
            Exception {

   public GIllegalScriptException(final String msg) {
      super(msg);
   }


   public GIllegalScriptException(final String msg,
                                  final Exception cause) {
      super(msg, cause);
   }

}
