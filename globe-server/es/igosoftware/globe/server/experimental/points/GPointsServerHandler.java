

package es.igosoftware.globe.server.experimental.points;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.gson.Gson;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.pointscloud.octree.GPCInnerNode;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCNode;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.pointscloud.GPointsClouseServerCommands;


@ChannelPipelineCoverage("all")
public class GPointsServerHandler
         extends
            SimpleChannelUpstreamHandler {


   private static final boolean LOG_REQUESTS = true;


   private final GPointsServer  _server;


   GPointsServerHandler(final GPointsServer server) {
      _server = server;
   }


   @Override
   public void channelConnected(final ChannelHandlerContext ctx,
                                final ChannelStateEvent e) throws Exception {
      super.channelConnected(ctx, e);

      final Channel channel = e.getChannel();

      _server.logInfo("Connection from " + channel.getRemoteAddress());

      final int sessionID = channel.getId().intValue();

      _server.channelConnected(channel, sessionID);

      channel.getCloseFuture().addListener(new ChannelFutureListener() {
         @Override
         public void operationComplete(final ChannelFuture future) throws Exception {
            _server.channelClosed(channel, sessionID);
         }
      });

      //      final IDrequest initializeClientrequest = new GDInitializeClientrequest(sessionID, _server);
      //      sendrequest(channel, initializeClientrequest);
   }


   @Override
   public void messageReceived(final ChannelHandlerContext ctx,
                               final MessageEvent e) throws Exception {
      super.messageReceived(ctx, e);

      final String request = (String) e.getMessage();

      processrequest(e, request);
   }


   private void processrequest(final MessageEvent e,
                               final String request) {
      logReceivedrequest(request, e.getRemoteAddress());

      if (request == null) {
         requestError(e, request);
      }
      else if (request.equalsIgnoreCase(GPointsClouseServerCommands.DIR_COMMAND)) {
         sendResponse(e, _server.getPointsCloudsNames());
      }
      else if (request.equalsIgnoreCase(GPointsClouseServerCommands.CLOSE_COMMAND)) {
         closeChannel(e.getChannel());
      }
      else if (request.startsWith(GPointsClouseServerCommands.GET_COMMAND)) {
         if (request.length() <= GPointsClouseServerCommands.GET_COMMAND_LENGTH) {
            requestError(e, request);
         }
         else {
            final String pointsCloudName = request.substring(GPointsClouseServerCommands.GET_COMMAND_LENGTH);
            sendPointsCloud(e, pointsCloudName);
         }
      }
      else {
         requestError(e, request);
      }
      final int TODO;

   }


   private void sendPointsCloud(final MessageEvent e,
                                final String pointsCloudName) {
      ObjectInputStream input = null;
      try {
         final File treeObjectGZFile = new File(new File(_server._pointsCloudsDirectory, pointsCloudName), "/tree.object.gz");
         input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeObjectGZFile), 2048));

         final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();

         final Map<String, Object> pointsCloudJSON = convertToJSON(pointsCloud);

         sendResponse(e, pointsCloudJSON);
      }
      catch (final IOException ex) {
         requestError(e, ex);
      }
      catch (final ClassNotFoundException ex) {
         requestError(e, ex);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


   private Map<String, Object> convertToJSON(final GPCPointsCloud pointsCloud) {
      final Map<String, Object> result = new HashMap<String, Object>();

      result.put("root", convertToJSON(pointsCloud.getRoot()));

      result.put("projection", pointsCloud.getProjection().name());

      result.put("verticesCount", pointsCloud.getVerticesCount());

      result.put("hasColors", pointsCloud.hasColors());
      result.put("hasNormals", pointsCloud.hasNormals());
      result.put("hasIntensities", pointsCloud.hasIntensities());

      result.put("minIntensity", pointsCloud.getMinIntensity());
      result.put("maxIntensity", pointsCloud.getMaxIntensity());

      result.put("minElevation", pointsCloud.getMinElevation());
      result.put("maxElevation", pointsCloud.getMaxElevation());

      return result;
   }


   private Map<String, Object> convertToJSON(final GPCInnerNode innerNode) {
      final Map<String, Object> result = new HashMap<String, Object>();

      final GPCNode[] children = innerNode.getChildren();

      final List<Map<String, Object>> childrenJSON = new ArrayList<Map<String, Object>>(children.length);

      for (final GPCNode child : children) {
         childrenJSON.add(convertToJSON(child));
      }

      result.put("bounds", convertToJSON(innerNode.getBounds()));
      result.put("children", childrenJSON);

      return result;
   }


   private Map<String, Object> convertToJSON(final GPCNode node) {
      if (node instanceof GPCInnerNode) {
         return convertToJSON((GPCInnerNode) node);
      }
      else if (node instanceof GPCLeafNode) {
         return convertToJSON((GPCLeafNode) node);
      }
      else {
         throw new RuntimeException("class " + node.getClass() + " not supported");
      }
   }


   private Map<String, Object> convertToJSON(final GPCLeafNode node) {
      final Map<String, Object> result = new HashMap<String, Object>();

      result.put("bounds", convertToJSON(node.getBounds()));

      result.put("id", node.getId());
      result.put("pointsCount", node.getPointsCount());
      result.put("lodIndices", node.getLodIndices());
      result.put("referencePoint", convertToJSON(node.getReferencePoint()));
      result.put("minimumBounds", convertToJSON(node.getMinimumBounds()));

      return result;
   }


   //   @Override
   //   public final void channelOpen(final ChannelHandlerContext ctx,
   //                                 final ChannelStateEvent e) throws Exception {
   //      super.channelOpen(ctx, e);
   //
   //      final ChannelPipeline pipeline = e.getChannel().getPipeline();
   //   }


   private Map<String, Object> convertToJSON(final GAxisAlignedOrthotope<IVector3<?>, ?> box) {
      final Map<String, Object> result = new HashMap<String, Object>();
      result.put("lower", convertToJSON(box._lower));
      result.put("upper", convertToJSON(box._upper));
      return result;
   }


   private Object convertToJSON(final IVector3<?> vector) {
      final Map<String, Object> result = new HashMap<String, Object>();
      result.put("x", vector.x());
      result.put("y", vector.y());
      result.put("z", vector.z());
      return result;
   }


   private void sendResponse(final MessageEvent e,
                             final Object response) {

      final Gson gson = new Gson();
      //      final Gson gson = new GsonBuilder().setPrettyPrinting().create();

      final String jsonResponse = gson.toJson(response);

      logSendingrequest(jsonResponse, e.getRemoteAddress());

      final Channel channel = e.getChannel();
      channel.write(jsonResponse);
      channel.write("\n");
   }


   private void requestError(final MessageEvent e,
                             final String request) {
      _server.logSevere("invalid request (" + request + "), closing connection");

      closeChannel(e.getChannel());
   }


   private void requestError(final MessageEvent e,
                             final Exception ex) {
      _server.logSevere("error handling request, closing connection", ex);

      closeChannel(e.getChannel());
   }


   private void logReceivedrequest(final String request,
                                   final SocketAddress socketAddress) {
      if (LOG_REQUESTS) {
         _server.logInfo("Received: " + request + " (" + request.length() + "b) from " + socketAddress);
      }
   }


   private void logSendingrequest(final String result,
                                  final SocketAddress socketAddress) {
      if (LOG_REQUESTS) {
         _server.logInfo("Sending: " + result + " (" + result.length() + "b) from " + socketAddress);
      }
   }


   @Override
   public final void exceptionCaught(final ChannelHandlerContext ctx,
                                     final ExceptionEvent e) {
      _server.logSevere("Unexpected exception from downstream.", e.getCause());
      closeChannel(e.getChannel());
   }


   private final void closeChannel(final Channel channel) {
      _server.logInfo("Closing " + channel);
      channel.close();
   }

}
