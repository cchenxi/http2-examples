package io.github.cchenxi.http2;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.HTTP2Session;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.http2.server.RawHTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 * http2 server
 * Date: 2024-06-30
 *
 * @author chenxi
 */
public class Http2Server {
    public static void main(String[] args) throws Exception {
        // Create a Server instance.
        ThreadPool threadPool = new ExecutorThreadPool(12, 3);
        Server server = new Server(threadPool);

        HttpConfiguration httpConfiguration = new HttpConfiguration();

        ServerSessionListener sessionListener = new ServerSessionListener.Adapter() {
            @Override
            public void onAccept(Session session) {
                System.out.println(LocalDateTime.now() + "::on accept.");
                if (session instanceof HTTP2Session) {
                    SocketAddress clientAddress = ((HTTP2Session) session).getEndPoint().getRemoteAddress();
                    SocketAddress serverAddress = ((HTTP2Session) session).getEndPoint().getLocalAddress();
                    System.out.println(LocalDateTime.now() + "::Connection from client::" + clientAddress + " to server::" + serverAddress);
                }
            }

            @Override
            public Map<Integer, Integer> onPreface(Session session) {
                System.out.println(LocalDateTime.now() + "::on perface");
                return Collections.singletonMap(SettingsFrame.ENABLE_PUSH, 0);
            }

            @Override
            public boolean onIdleTimeout(Session session) {
                System.out.println(LocalDateTime.now() + "::on client idle timeout.");
                return super.onIdleTimeout(session);
            }

            @Override
            public Stream.Listener onNewStream(Stream stream, HeadersFrame frame) {
                System.out.println(LocalDateTime.now() + "::on new stream");
                // This is the "new stream" event, so it's guaranteed to be a request.
                MetaData.Request request = (MetaData.Request)frame.getMetaData();

                // Return a Stream.Listener to handle the request events,
                // for example request content events or a request reset.
                return new Stream.Listener.Adapter() {
                    @Override
                    public void onData(Stream stream, DataFrame frame, Callback callback) {
                        // Get the content buffer.
                        ByteBuffer buffer = frame.getData();

                        // Consume the buffer, here - as an example - just log it.
                        System.out.println(LocalDateTime.now() + "::Consuming buffer " + buffer);
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        System.out.println(new String(bytes, StandardCharsets.UTF_8));

                        // Tell the implementation that the buffer has been consumed.
                        callback.succeeded();

                        // By returning from the method, implicitly tell the implementation
                        // to deliver to this method more DATA frames when they are available.
                    }
                };
            }
        };

        // Create a ServerConnector with RawHTTP2ServerConnectionFactory.
        RawHTTP2ServerConnectionFactory http2 = new RawHTTP2ServerConnectionFactory(httpConfiguration, sessionListener);

        // Configure RawHTTP2ServerConnectionFactory, for example:

        // Configure the max number of concurrent requests.
        http2.setMaxConcurrentStreams(128);

        // Create the ServerConnector.
        ServerConnector connector = new ServerConnector(server, http2);
        connector.setPort(8089);
        //
        connector.setIdleTimeout(15 * 1_000L);

        // Add the Connector to the Server
        server.addConnector(connector);

        // Start the Server so it starts accepting connections from clients.
        server.start();
    }
}
