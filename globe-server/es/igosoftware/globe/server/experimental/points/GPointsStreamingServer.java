

package es.igosoftware.globe.server.experimental.points;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GLoggerObject;


public class GPointsStreamingServer
         extends
            GLoggerObject {

   private final boolean _verbose;
   final File            _pointsCloudsDirectory;


   public GPointsStreamingServer(final int port,
                                 final String pointsCloudsDirectoryName,
                                 final boolean verbose) {
      GAssert.isPositive(port, "port");
      GAssert.notNull(pointsCloudsDirectoryName, "pointsCloudsDirectoryName");

      //      _port = port;
      _verbose = verbose;

      _pointsCloudsDirectory = initializePointsCloudsDirectory(pointsCloudsDirectoryName);

      start(port);
   }


   @Override
   public String logName() {
      return "GPointsServer";
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   private File initializePointsCloudsDirectory(final String pointsCloudsDirectoryName) {
      final File pointsCloudsDirectory = new File(pointsCloudsDirectoryName);

      if (!pointsCloudsDirectory.exists()) {
         throw new RuntimeException("Invalid pointsCloudsDirectoryName (" + pointsCloudsDirectory.getAbsolutePath() + ")");
      }

      return pointsCloudsDirectory;
   }


   private void start(final int port) {
      logInfo("Starting server at port " + port);

      // Configure the server. 
      final NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
               Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors());

      final ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

      final ChannelPipeline pipeline = bootstrap.getPipeline();

      //      // Decoders
      //      pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(256, Delimiters.lineDelimiter()));
      //      pipeline.addLast("stringDecoder", new StringDecoder("UTF-8"));
      //
      //      // Encoder
      //      pipeline.addLast("stringEncoder", new StringEncoder("UTF-8"));


      pipeline.addLast("encoder", new GPointsProtocolEncoder(512));
      pipeline.addLast("decoder", new GPointsProtocolDecoder(512 * 1024));

      // Set up the handler
      final GPointsStreamingServerHandler handler = new GPointsStreamingServerHandler(this);
      pipeline.addLast("handler", handler);


      // Bind and start to accept incoming connections.
      bootstrap.bind(new InetSocketAddress(port));
   }


   void channelConnected(final Channel channel,
                         final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " connected.");
   }


   void channelClosed(final Channel channel,
                      final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " closed.");
   }


   String[] getPointsCloudsNames() {
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


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GPointsServer 0.1");
      System.out.println("-----------------\n");


      if ((args.length < 1) || (args.length > 2)) {
         System.err.println("Usage: " + GPointsStreamingServer.class.getSimpleName() + " pointsCloudDirectoryName [<port>]");
         return;
      }

      final String pointsCloudDirectoryName = args[0];
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      new GPointsStreamingServer(port, pointsCloudDirectoryName, true);
   }
}
