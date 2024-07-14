package io.github.cchenxi;

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
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PingFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.http2.server.RawHTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
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
        ThreadPool threadPool = new QueuedThreadPool(12, 3);
        Server server = new Server(threadPool);

        HttpConfiguration httpConfiguration = new HttpConfiguration();

        ServerSessionListener sessionListener = new SessionListener();

        // Create a ServerConnector with RawHTTP2ServerConnectionFactory.
        RawHTTP2ServerConnectionFactory http2 = new RawHTTP2ServerConnectionFactory(httpConfiguration, sessionListener);

        // Configure RawHTTP2ServerConnectionFactory, for example:

        // Configure the max number of concurrent requests.
        http2.setMaxConcurrentStreams(128);

        // Create the ServerConnector.
        ServerConnector connector = new ServerConnector(server, http2);
        connector.setPort(8089);
        // 10分钟
        connector.setIdleTimeout(10 * 60 * 1_000L);

        // Add the Connector to the Server
        server.addConnector(connector);

        // Start the Server so it starts accepting connections from clients.
        server.start();
    }
}
