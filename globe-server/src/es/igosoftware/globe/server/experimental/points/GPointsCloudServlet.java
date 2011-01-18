

package es.igosoftware.globe.server.experimental.points;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.globe.server.experimental.GHttpServlet;
import es.igosoftware.globe.server.experimental.GParameterException;
import es.igosoftware.globe.server.experimental.GRequestType;
import es.igosoftware.io.GIOUtils;


public class GPointsCloudServlet
         extends
            GHttpServlet {

   private static final long serialVersionUID = 1L;
   private File              _pointsCloudsDirectory;


   public GPointsCloudServlet() {
      super();
   }


   @Override
   public void init() throws ServletException {
      //      log("************* getinit init ****************");

      final ServletConfig servletConfig = getServletConfig();

      final ServletContext servletContext = servletConfig.getServletContext();

      //      final Enumeration attributeNames = servletContext.getAttributeNames();
      //      while (attributeNames.hasMoreElements()) {
      //         final Object attributeName = attributeNames.nextElement();
      //         System.out.println(">>>>>>>> " + attributeName + "=" + servletContext.getAttribute(attributeName.toString()));
      //      }

      //      System.out.println("###########" + servletContext.getContextPath());
      //      System.out.println("###########" + servletContext.getRealPath("."));

      final File projectDirectory = new File(servletContext.getRealPath(".")).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();

      final String pointsCloudsDirectoryName = servletConfig.getInitParameter("points_cloud_directory");

      if (pointsCloudsDirectoryName == null) {
         throw new ServletException("Missing mandatory init parameter \"points_cloud_directory\"");
      }


      final File pointsCloudsDirectory = new File(projectDirectory, pointsCloudsDirectoryName);
      if (!pointsCloudsDirectory.exists()) {
         throw new ServletException("Invalid directory in parameter \"points_cloud_directory\" ("
                                    + pointsCloudsDirectory.getAbsolutePath() + ")");
      }

      _pointsCloudsDirectory = pointsCloudsDirectory;
   }


   private String[] getPointsCloudsNames() {
      if (_pointsCloudsDirectory == null) {
         return new String[] {};
      }

      final String[] directoriesNames = _pointsCloudsDirectory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            final File file = new File(dir, name);
            if (!file.isDirectory()) {
               return false;
            }

            final File treeFile = new File(file, "tree.object.gz");
            return treeFile.exists();
         }
      });

      return directoriesNames;
   }


   private void sendResult(final HttpServletResponse response,
                           final Object result) throws IOException {
      final Gson gson = new Gson();

      final String responseString = gson.toJson(result);

      final ServletOutputStream os = response.getOutputStream();
      os.println(responseString);
   }


   @Override
   protected void doRequest(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final GRequestType requestType) throws IOException, GParameterException {

      final String command = getMandatoryParameter(request, "command").trim();

      if (command.equalsIgnoreCase("dir")) {
         final String[] result = getPointsCloudsNames();
         sendResult(response, result);
      }
      else if (command.equalsIgnoreCase("get")) {
         final String name = getMandatoryParameter(request, "name");

         final File treeFile = new File(new File(_pointsCloudsDirectory, name), "tree.object.gz");

         ObjectInputStream input = null;
         try {
            input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeFile), 2048));

            final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();

            sendResult(response, pointsCloud);
         }
         catch (final ClassNotFoundException e) {
            sendResult(response, e);
         }
         finally {
            GIOUtils.gentlyClose(input);
         }
      }
      else {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.getOutputStream().println("Invalid command: " + command);
      }
   }


}
