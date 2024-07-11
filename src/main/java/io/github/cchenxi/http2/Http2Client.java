package io.github.cchenxi.http2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PingFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.util.Promise;

import org.eclipse.jetty.http.HttpURI;

/**
 * http2 client
 * Date: 2024-06-30
 *
 * @author chenxi
 */
public class Http2Client {
    public static void main(String[] args) throws Exception {
        HTTP2Client http2Client = new HTTP2Client();
        http2Client.start();

        // Address of the server's clear-text port.
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8089);

        Session.Listener sessionListener = new Session.Listener.Adapter(){
            @Override
            public Map<Integer, Integer> onPreface(Session session) {
                System.out.println(LocalDateTime.now() + "::on per face");
                return null;
            }

            @Override
            public Stream.Listener onNewStream(Stream stream, HeadersFrame frame) {
                System.out.println(LocalDateTime.now() + "::on new stream");
                return new Stream.Listener.Adapter() {};
            }

            @Override
            public void onSettings(Session session, SettingsFrame frame) {
                System.out.println(LocalDateTime.now() + "::on settings");
                System.out.println(LocalDateTime.now() + "::settings frame data::" + frame.getSettings());
                super.onSettings(session, frame);
            }

            @Override
            public void onPing(Session session, PingFrame frame) {
                System.out.println(LocalDateTime.now() + "::on ping");
                super.onPing(session, frame);
            }

            @Override
            public void onReset(Session session, ResetFrame frame) {
                System.out.println(LocalDateTime.now() + "::on reset");
                super.onReset(session, frame);
            }

            @Override
            public void onClose(Session session, GoAwayFrame frame) {
                System.out.println(LocalDateTime.now() + "::on close");
                super.onClose(session, frame);
            }

            @Override
            public boolean onIdleTimeout(Session session) {
                System.out.println(LocalDateTime.now() + "::on idle timeout");
                return super.onIdleTimeout(session);
            }

            @Override
            public void onFailure(Session session, Throwable failure) {
                System.out.println(LocalDateTime.now() + "::on failure");
                super.onFailure(session, failure);
            }
        };

        Promise<Session> sessionPromise = new Promise.Adapter<Session>() {
            @Override
            public void failed(Throwable x) {
                System.out.println(LocalDateTime.now() + "::connect failed::" + Arrays.toString(x.getStackTrace()));
            }

            @Override
            public void succeeded(Session session) {
                System.out.println(LocalDateTime.now() + "::connect succeed::" + session);
                startSendDataTask(session);
            }
        };

        http2Client.connect(serverAddress, sessionListener, sessionPromise);
    }

    private static void startSendDataTask(Session session) {
        // Configure the request headers.
        HttpFields requestHeaders = new HttpFields();
        requestHeaders.add(HttpHeader.USER_AGENT, "Jetty HTTP2Client 10.0.21-SNAPSHOT");

        // The request metadata with method, URI and headers.
        MetaData.Request request = new MetaData.Request("GET", new HttpURI("http://localhost:8080/path"), HttpVersion.HTTP_2, requestHeaders);

        // The HTTP/2 HEADERS frame, with endStream=true
        // to signal that this request has no content.
        HeadersFrame headersFrame = new HeadersFrame(request, null, true);

        // Open a Stream by sending the HEADERS frame.
        session.newStream(headersFrame, new Promise.Adapter<Stream>(){
            @Override
            public void failed(Throwable x) {
                super.failed(x);
            }

            @Override
            public void succeeded(Stream result) {
                super.succeeded(result);
            }
        }, new Stream.Listener.Adapter(){

        });

        /**
        // Configure the request headers.
        HttpFields requestHeaders2 = new HttpFields();
        requestHeaders2.put(HttpHeader.CONTENT_TYPE, "application/json");

        // The request metadata with method, URI and headers.
        MetaData.Request request2 = new MetaData.Request("POST", new HttpURI("http://localhost:8080/path"), HttpVersion.HTTP_2, requestHeaders2);

        // The HTTP/2 HEADERS frame, with endStream=false to
        // signal that there will be more frames in this stream.
        HeadersFrame headersFrame2 = new HeadersFrame(request2, null, false);

        // Open a Stream by sending the HEADERS frame.
        session.newStream(headersFrame2, new Promise.Adapter<Stream>(){
            @Override
            public void failed(Throwable x) {
                super.failed(x);
            }

            @Override
            public void succeeded(Stream result) {
                super.succeeded(result);
            }
        }, new Stream.Listener.Adapter(){

        });

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
