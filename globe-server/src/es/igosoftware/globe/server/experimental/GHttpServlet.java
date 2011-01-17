

package es.igosoftware.globe.server.experimental;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class GHttpServlet
         extends
            HttpServlet {


   private static final long serialVersionUID = 1L;


   protected GHttpServlet() {
      super();
   }


   @Override
   protected final void doGet(final HttpServletRequest request,
                              final HttpServletResponse response) throws ServletException, IOException {
      try {
         doRequest(request, response, GRequestType.GET);
      }
      catch (final GParameterException e) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.getOutputStream().println(e.getMessage());
      }
   }


   @Override
   protected final void doPost(final HttpServletRequest request,
                               final HttpServletResponse response) throws ServletException, IOException {
      try {
         doRequest(request, response, GRequestType.POST);
      }
      catch (final GParameterException e) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.getOutputStream().println(e.getMessage());
      }
   }


   protected abstract void doRequest(final HttpServletRequest request,
                                     final HttpServletResponse response,
                                     final GRequestType requestType) throws ServletException, IOException, GParameterException;


   protected final String[] getMandatoryParameters(final HttpServletRequest request,
                                                   final String name) throws GParameterException {
      final String[] value = request.getParameterValues(name);

      if (value == null) {
         throw new GParameterException("Mandatory parameter \"" + name + "\" not present in request");
      }

      return value;
   }


   protected final String getMandatoryParameter(final HttpServletRequest request,
                                                final String name) throws GParameterException {
      final String[] values = getMandatoryParameters(request, name);

      if (values.length != 1) {
         throw new GParameterException("The mandatory parameter \"" + name + "\" is " + values.length + " times in request");
      }

      return values[0];
   }

}
