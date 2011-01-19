

package es.igosoftware.globe.server.experimental.points;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GLoggerObject;


public class GPointsServer
         extends
            GLoggerObject {

   private final boolean              _verbose;
   private final File                 _pointsCloudsDirectory;
   private final int                  _port;
   private final GPointsServerHandler _handler;


   public GPointsServer(final int port,
                        final String pointsCloudsDirectoryName,
                        final boolean verbose) {
      GAssert.isPositive(port, "port");
      GAssert.notNull(pointsCloudsDirectoryName, "pointsCloudsDirectoryName");

      _port = port;
      _verbose = verbose;

      _pointsCloudsDirectory = initializePointsCloudsDirectory(pointsCloudsDirectoryName);

      _handler = start();
   }


   private GPointsServerHandler start() {
      logInfo("Starting server at port " + _port);

      // Configure the server. 
      final NioServerSocketChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
               Executors.newCachedThreadPool(), Runtime.getRuntime().availableProcessors());
      final ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

      final ChannelPipeline pipeline = bootstrap.getPipeline();

      // Decoders
      pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(80, Delimiters.lineDelimiter()));
      pipeline.addLast("stringDecoder", new StringDecoder("UTF-8"));

      // Encoder
      pipeline.addLast("stringEncoder", new StringEncoder("UTF-8"));

      // Set up the default event pipeline.
      final GPointsServerHandler handler = new GPointsServerHandler(this);
      pipeline.addLast("handler", handler);


      //      // Configure the pipeline factory.
      //      bootstrap.setPipelineFactory(new GPointsServerPipelineFactory());

      // Bind and start to accept incoming connections.
      bootstrap.bind(new InetSocketAddress(_port));

      return handler;
   }


   private File initializePointsCloudsDirectory(final String pointsCloudsDirectoryName) {
      final File pointsCloudsDirectory = new File(pointsCloudsDirectoryName);

      if (!pointsCloudsDirectory.exists()) {
         throw new RuntimeException("Invalid pointsCloudsDirectoryName (" + pointsCloudsDirectory.getAbsolutePath() + ")");
      }

      return pointsCloudsDirectory;
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


   @Override
   public String logName() {
      return "GPointsServer";
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   void channelConnected(final Channel channel,
                         final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " connected.");
   }


   void channelClosed(final Channel channel,
                      final int sessionID) {
      logInfo("RemoteAddress=" + channel.getRemoteAddress() + ", Session=" + sessionID + " closed.");

      //      synchronized (_propertyChangeListeners) {
      //         final Iterator<Entry<PropertyChangeListenerKey, PropertyChangeListenerData>> iterator = _propertyChangeListeners.entrySet().iterator();
      //         while (iterator.hasNext()) {
      //            final Entry<PropertyChangeListenerKey, PropertyChangeListenerData> entry = iterator.next();
      //            final PropertyChangeListenerKey listenerKey = entry.getKey();
      //
      //            final int listenerSessionID = listenerKey._sessionID;
      //            if (listenerSessionID == sessionID) {
      //               final PropertyChangeListenerData listenerData = entry.getValue();
      //
      //               logInfo("  Removing listener for session=" + sessionID + ", listenerID=" + listenerKey._subscriptionID
      //                       + ", propertyName=\"" + listenerData._propertyName + "\"");
      //
      //               listenerData._model.removePropertyChangeListener(listenerData._propertyName, listenerData._listener);
      //
      //               iterator.remove();
      //            }
      //         }
      //      }
   }


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GPointsServer 0.1");
      System.out.println("-----------------\n");


      if ((args.length < 1) || (args.length > 2)) {
         System.err.println("Usage: " + GPointsServer.class.getSimpleName() + " pointsCloudDirectoryName [<port>]");
         return;
      }

      final String pointsCloudDirectoryName = args[0];
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;


      new GPointsServer(port, pointsCloudDirectoryName, true);
   }


}
