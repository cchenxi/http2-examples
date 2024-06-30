package io.github.cchenxi.http2;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;

/**
 * http2 client
 * Date: 2024-06-30
 *
 * @author chenxi
 */
public class Http2Client {
    public static void main(String[] args) throws Exception {

        HTTP2Client http2Client = new HTTP2Client();
        http2Client.setConnectTimeout(60 * 1_000L);
        http2Client.start();

        // Address of the server's clear-text port.
        SocketAddress serverAddress = new InetSocketAddress("localhost", 8089);

        // Connect to the server, the CompletableFuture will be
        // notified when the connection is succeeded (or failed).
        CompletableFuture<Session> sessionCF = http2Client.connect(serverAddress, new Session.Listener.Adapter() {
            @Override
            public boolean onIdleTimeout(Session session) {
                System.out.println("on server idle timeout.");
                return super.onIdleTimeout(session);
            }
        });

        // Block to obtain the Session.
        // Alternatively you can use the CompletableFuture APIs to avoid blocking.
        Session session = sessionCF.get();

        System.out.println(session);

        /**

        // Configure the request headers.
        HttpFields requestHeaders = HttpFields.build()
                .put(HttpHeader.USER_AGENT, "Jetty HTTP2Client 10.0.21-SNAPSHOT");

        // The request metadata with method, URI and headers.
        MetaData.Request request = new MetaData.Request("GET", HttpURI.from("http://localhost:8080/path"), HttpVersion.HTTP_2, requestHeaders);

        // The HTTP/2 HEADERS frame, with endStream=true
        // to signal that this request has no content.
        HeadersFrame headersFrame = new HeadersFrame(request, null, true);

        // Open a Stream by sending the HEADERS frame.
        session.newStream(headersFrame, new Stream.Listener.Adapter() {

        });

        // Configure the request headers.
        HttpFields requestHeaders2 = HttpFields.build()
                .put(HttpHeader.CONTENT_TYPE, "application/json");

        // The request metadata with method, URI and headers.
        MetaData.Request request2 = new MetaData.Request("POST", HttpURI.from("http://localhost:8080/path"), HttpVersion.HTTP_2, requestHeaders2);

        // The HTTP/2 HEADERS frame, with endStream=false to
        // signal that there will be more frames in this stream.
        HeadersFrame headersFrame2 = new HeadersFrame(request2, null, false);

        // Open a Stream by sending the HEADERS frame.
        CompletableFuture<Stream> streamCF = session.newStream(headersFrame2, new Stream.Listener.Adapter());

        // Block to obtain the Stream.
        // Alternatively you can use the CompletableFuture APIs to avoid blocking.
        Stream stream = streamCF.get();

        // The request content, in two chunks.
        String content1 = "{\"greet\": \"hello world\"}";
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(content1);
        String content2 = "{\"user\": \"jetty\"}";
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode(content2);

        // Send the first DATA frame on the stream, with endStream=false
        // to signal that there are more frames in this stream.
        CompletableFuture<Stream> dataCF1 = stream.data(new DataFrame(stream.getId(), buffer1, false));

        // Only when the first chunk has been sent we can send the second,
        // with endStream=true to signal that there are no more frames.
        dataCF1.thenCompose(s -> s.data(new DataFrame(s.getId(), buffer2, true)));
         **/
    }
}
