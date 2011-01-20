

package es.igosoftware.globe.server.experimental.points;

import java.net.SocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.gson.Gson;


@ChannelPipelineCoverage("all")
public class GPointsServerHandler
         extends
            SimpleChannelUpstreamHandler {

   private static final boolean LOG_COMMANDS = true;


   private final GPointsServer  _server;


   GPointsServerHandler(final GPointsServer server) {
      _server = server;
   }


   @Override
   public void channelConnected(final ChannelHandlerContext ctx,
                                final ChannelStateEvent e) throws Exception {
      super.channelConnected(ctx, e);

      _server.logInfo("Connection from " + e.getChannel().getRemoteAddress());

      final Channel channel = e.getChannel();
      final int sessionID = channel.getId().intValue();


      _server.channelConnected(channel, sessionID);

      channel.getCloseFuture().addListener(new ChannelFutureListener() {
         @Override
         public void operationComplete(final ChannelFuture future) throws Exception {
            //            remove(future.getChannel());
            _server.channelClosed(channel, sessionID);
         }
      });

      //      final IDCommand initializeClientCommand = new GDInitializeClientCommand(sessionID, _server);
      //      sendCommand(channel, initializeClientCommand);
   }


   @Override
   public void messageReceived(final ChannelHandlerContext ctx,
                               final MessageEvent e) throws Exception {
      super.messageReceived(ctx, e);

      final String command = (String) e.getMessage();

      processCommand(e, command);
   }


   private void processCommand(final MessageEvent e,
                               final String command) {
      logReceivedCommand(command, e.getRemoteAddress());

      if (command == null) {
         commandError(e, command);
      }
      else if (command.equalsIgnoreCase("dir")) {
         sendResponse(e, _server.getPointsCloudsNames());
      }
      else if (command.equalsIgnoreCase("close")) {
         e.getChannel().close();
      }
      else {
         commandError(e, command);
      }
   }


   //   @Override
   //   public final void channelOpen(final ChannelHandlerContext ctx,
   //                                 final ChannelStateEvent e) throws Exception {
   //      super.channelOpen(ctx, e);
   //
   //      final ChannelPipeline pipeline = e.getChannel().getPipeline();
   //   }


   private void sendResponse(final MessageEvent e,
                             final Object response) {

      final Gson gson = new Gson();

      final String jsonResponse = gson.toJson(response);

      logSendingCommand(jsonResponse, e.getRemoteAddress());

      final Channel channel = e.getChannel();
      channel.write(jsonResponse);
      channel.write("\n");
   }


   private void commandError(final MessageEvent e,
                             final String command) {
      _server.logSevere("invalid command (" + command + "), closing connection");

      e.getChannel().close();
   }


   private void logReceivedCommand(final String command,
                                   final SocketAddress socketAddress) {
      if (LOG_COMMANDS) {
         //         if (!command.isFragmentCommand() || LOG_FRAGMENT_COMMANDS) {
         final String sizeInBytes = getSerializedSize(command);
         _server.logInfo("Received: " + command + " (" + sizeInBytes + ") from " + socketAddress);
         //         }
      }
   }


   private String getSerializedSize(final String command) {
      return command.length() + "b";
   }


   private void logSendingCommand(final String result,
                                  final SocketAddress socketAddress) {
      if (LOG_COMMANDS) {
         //         if (!command.isFragmentCommand() || LOG_FRAGMENT_COMMANDS) {
         final String sizeInBytes = getSerializedSize(result);
         _server.logInfo("Sending: " + result + " (" + sizeInBytes + ") from " + socketAddress);
         //         }
      }
   }


}
