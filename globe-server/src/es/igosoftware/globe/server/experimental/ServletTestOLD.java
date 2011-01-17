

package es.igosoftware.globe.server.experimental;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServletTestOLD
         extends
            GHttpServlet {

   private static final long serialVersionUID = 1L;


   public ServletTestOLD() {
      super();
   }


   @Override
   protected void doRequest(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final GRequestType requestType) throws IOException {
      final ServletOutputStream os = response.getOutputStream();

      os.println("Goodmorning Vietnam!");
   }


}
