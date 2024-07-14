package io.github.cchenxi;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;

/**
 * http2 client
 * Date: 2024-06-30
 *
 * @author chenxi
 */
public class Http2Client {
    public static void main(String[] args) throws Exception {
        HTTP2Client http2Client = new HTTP2Client();
        // 10分钟
        http2Client.setIdleTimeout(10 * 60 * 1_000L);
        http2Client.start();

        // Address of the server's clear-text port.
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8089);

        Session.Listener sessionListener = new SessionListener();

        Promise<Session> sessionPromise = new Promise.Adapter<Session>() {
            @Override
            public void succeeded(Session session) {
                System.out.println(LocalDateTime.now() + "::session::" + session + "::connection has been established to the server.");
                runSendDataTask(session);
            }

            @Override
            public void failed(Throwable x) {

            }
        };

        http2Client.connect(serverAddress, sessionListener, sessionPromise);
    }

    private static void runSendDataTask(Session session) {
        // 验证new Stream发送数据
        // 设置服务端 stream的idle，new stream的速率减慢之后，服务端的影响
        sendData(session, true);
    }


    private static void sendData(Session session, boolean end) {
        session.newStream(
                genHeadersFrame(),
                new Promise.Adapter<Stream>() {
                    @Override
                    public void succeeded(Stream stream) {
                        String content1 = stream.getId() + "{\"greet\": \"hello world\"}";
                        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(content1);
                        stream.data(new DataFrame(stream.getId(), buffer1, false), null);

                        String content2 = stream.getId() + "{\"user\": \"jetty\"}";
                        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode(content2);
                        stream.data(new DataFrame(stream.getId(), buffer2, end), null);

                    }

                    @Override
                    public void failed(Throwable x) {

                    }
                },
                new StreamListener());
    }

    private static HeadersFrame genHeadersFrame() {
        // Configure the request headers.
        HttpFields requestHeaders = new HttpFields();
        requestHeaders.add(HttpHeader.USER_AGENT, "Jetty HTTP2Client 10.0.21-SNAPSHOT");

        // The request metadata with method, URI and headers.
        MetaData.Request request = new MetaData.Request("GET", new HttpURI("http://localhost:8080/path"), HttpVersion.HTTP_2, requestHeaders);

        // The HTTP/2 HEADERS frame, with endStream=true
        // to signal that this request has no content.
        return new HeadersFrame(request, null, false);
    }
}
