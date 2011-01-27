

package es.igosoftware.experimental.pointscloud.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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

import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.experimental.pointscloud.GJsonConverter;
import es.igosoftware.experimental.pointscloud.GPointsClouseServerCommands;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GUtils;


@ChannelPipelineCoverage("all")
public class GPointsStreamingServerHandler
         extends
            SimpleChannelUpstreamHandler {

   private static final boolean LOG_REQUESTS         = true;
   private static final long    NANO_SECONDS_TO_POLL = GUtils.isDevelopment() ? 10000 : 1000;

   private static final Gson    GSON                 = new Gson();

   //      private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


   private class ResponsePusher
            extends
               Thread {

      private ResponsePusher() {
         setDaemon(true);
         setPriority(MAX_PRIORITY);
      }


      @Override
      public void run() {
         try {
            //            final ArrayList<GPair<Channel, String>> queuedResponses = new ArrayList<GPair<Channel, String>>();
            //            final ArrayList<String> responsesInChannel = new ArrayList<String>();

            while (true) {
               final GPair<Channel, String> firstResponse = _responsesQueue.poll(NANO_SECONDS_TO_POLL, TimeUnit.NANOSECONDS);
               if (firstResponse == null) {
                  tryToSendAsynchronousResponses();
                  continue;
               }

               //               queuedResponses.clear();
               //               queuedResponses.add(firstResponse);
               //
               //               Thread.sleep(2); // give some time to accumulate responses
               //
               //               GPair<Channel, String> nextResponse;
               //               while ((nextResponse = _responsesQueue.poll()) != null) {
               //                  queuedResponses.add(nextResponse);
               //               }
               //
               //               if (queuedResponses.size() == 1) {

               // queued 1 response, just send it
               sendResponseNow(firstResponse._first, firstResponse._second);

               //               }
               //               else {
               //                  // queued more than 1 response, group then by channel
               //                  final LinkedList<GPair<Channel, String>> toProcess = new LinkedList<GPair<Channel, String>>(queuedResponses);
               //
               //                  while (!toProcess.isEmpty()) {
               //                     responsesInChannel.clear();
               //
               //                     final GPair<Channel, String> firstResponseInChannel = toProcess.removeFirst();
               //                     responsesInChannel.add(firstResponseInChannel._second);
               //
               //                     final Iterator<GPair<Channel, String>> toProcessIterator = toProcess.iterator();
               //                     while (toProcessIterator.hasNext()) {
               //                        final GPair<Channel, String> current = toProcessIterator.next();
               //                        if (current._first == firstResponseInChannel._first) {
               //                           responsesInChannel.add(current._second);
               //                           toProcessIterator.remove();
               //                        }
               //                     }
               //
               //                     if (responsesInChannel.size() == 1) {
               //                        sendResponseNow(firstResponseInChannel._first, firstResponseInChannel._second);
               //                     }
               //                     else {
               //                        final String composite = firstResponseInChannel._second.createComposite(responsesInChannel,
               //                                 getMultiplexor());
               //                        sendResponseNow(firstResponseInChannel._first, composite);
               //                     }
               //                  }
               //               }
            }
         }
         catch (final InterruptedException e) {
            _server.logSevere(e.getLocalizedMessage(), e);
         }
      }


      private void tryToSendAsynchronousResponses() {
         final GPair<Channel, String> response = _asynchronousResponsesQueue.poll();
         if (response != null) {
            sendResponseNow(response._first, response._second);
         }
      }


      private void sendResponseNow(final Channel chanel,
                                   final String response) {
         //         if (LOG_COMMANDS) {
         //            final int sizeInBytes = getSerializedSize(response);
         //            logInfo("Sending: " + response + " (" + sizeInBytes + "b) to " + chanel.getRemoteAddress());
         //         } 
         logSendingResponse(response, chanel.getRemoteAddress());
         chanel.write(response);
      }
   }


   private final GPointsStreamingServer                _server;

   private final BlockingQueue<GPair<Channel, String>> _responsesQueue             = new LinkedBlockingQueue<GPair<Channel, String>>();
   private final BlockingQueue<GPair<Channel, String>> _asynchronousResponsesQueue = new LinkedBlockingQueue<GPair<Channel, String>>();


   GPointsStreamingServerHandler(final GPointsStreamingServer server) {
      _server = server;

      new ResponsePusher().start();
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

      final Object message = e.getMessage();
      if (message instanceof String) {
         final String request = (String) message;

         processRequest(e, request);
      }
      else {
         requestError(e, "Invalid Request Class" + message.getClass());
      }
   }


   private void processRequest(final MessageEvent e,
                               final String request) {
      logReceivedRequest(request, e.getRemoteAddress());

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
      final int __Diego_at_work;

   }


   private void sendPointsCloud(final MessageEvent e,
                                final String pointsCloudName) {
      ObjectInputStream input = null;
      try {
         final File treeObjectGZFile = new File(new File(_server._pointsCloudsDirectory, pointsCloudName), "/tree.object.gz");
         input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeObjectGZFile), 2048));

         final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();

         final Map<String, Object> pointsCloudJSON = GJsonConverter.convertToJSON(pointsCloud);

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


   private void sendResponse(final MessageEvent e,
                             final Object response) {


      final String jsonResponse = GSON.toJson(response);

      _responsesQueue.add(new GPair<Channel, String>(e.getChannel(), jsonResponse));
   }


   private void requestError(final MessageEvent e,
                             final String request) {
      _server.logSevere("Invalid request (" + request + "), closing connection from " + e.getRemoteAddress());

      closeChannel(e.getChannel());
   }


   private void requestError(final MessageEvent e,
                             final Exception ex) {
      _server.logSevere("Error handling request, closing connection from " + e.getRemoteAddress(), ex);

      closeChannel(e.getChannel());
   }


   private void logReceivedRequest(final String request,
                                   final SocketAddress socketAddress) {
      if (LOG_REQUESTS) {
         _server.logInfo("Received: \"" + request + "\" from " + socketAddress);
      }
   }


   private void logSendingResponse(final String response,
                                   final SocketAddress socketAddress) {
      if (LOG_REQUESTS) {
         _server.logInfo("Sending: \"" + response + "\" to " + socketAddress);
      }
   }


   //   @Override
   //   public final void channelOpen(final ChannelHandlerContext ctx,
   //                                 final ChannelStateEvent e) throws Exception {
   //      super.channelOpen(ctx, e);
   //
   //      final ChannelPipeline pipeline = e.getChannel().getPipeline();
   //   }


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
