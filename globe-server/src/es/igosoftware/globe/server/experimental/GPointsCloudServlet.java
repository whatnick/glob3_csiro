

package es.igosoftware.globe.server.experimental;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


public class GPointsCloudServlet
         extends
            GHttpServlet {

   private static final long serialVersionUID = 1L;


   public GPointsCloudServlet() {
      super();
   }


   @Override
   protected void doRequest(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final GRequestType requestType) throws IOException {
      final ServletOutputStream os = response.getOutputStream();

      final Gson gson = new Gson();

      final String[] result = new String[] { "Goodmorning Vietnam!", "another string" };
      os.println(gson.toJson(result));

      //      os.println("Hello");
   }

}
