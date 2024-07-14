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
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PingFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.util.Callback;

/**
 * custom Session listener
 * Date: 2024-07-13
 *
 * @author chenxi
 */
public class SessionListener extends ServerSessionListener.Adapter {
    @Override
    public void onAccept(Session session) {
        System.out.println(LocalDateTime.now() + "::session::" + session + "::connection has been accepted by the server.");
        if (session instanceof HTTP2Session) {
            SocketAddress clientAddress = ((HTTP2Session) session).getEndPoint().getRemoteAddress();
            SocketAddress serverAddress = ((HTTP2Session) session).getEndPoint().getLocalAddress();
            System.out.println(LocalDateTime.now() + "::Connection from client::" + clientAddress + " to server::" + serverAddress);

            System.out.println(LocalDateTime.now() + "::stream idle timeout is::" + ((HTTP2Session) session).getStreamIdleTimeout());
        }
    }

    @Override
    public Map<Integer, Integer> onPreface(Session session) {
        System.out.println(LocalDateTime.now()  + "::on per face");
        return Collections.singletonMap(SettingsFrame.ENABLE_PUSH, 0);
    }

    @Override
    public boolean onIdleTimeout(Session session) {
        System.out.println(LocalDateTime.now()  + "::on client idle timeout.");
        return super.onIdleTimeout(session);
    }

    @Override
    public Stream.Listener onNewStream(Stream stream, HeadersFrame frame) {
        System.out.println(LocalDateTime.now() + "::session::" + stream.getSession() + "::on new stream");
        // This is the "new stream" event, so it's guaranteed to be a request.
        MetaData.Request request = (MetaData.Request)frame.getMetaData();
        System.out.println(request);
        stream.setIdleTimeout(35 * 1_000L);

        // Return a Stream.Listener to handle the request events,
        // for example request content events or a request reset.
        return new StreamListener();
    }

    @Override
    public void onSettings(Session session, SettingsFrame frame) {
        System.out.println(LocalDateTime.now()  + "::settings frame::" + frame);
        super.onSettings(session, frame);
    }

    @Override
    public void onPing(Session session, PingFrame frame) {
        System.out.println(LocalDateTime.now()  + "::ping frame::" + frame);
        super.onPing(session, frame);
    }

    @Override
    public void onReset(Session session, ResetFrame frame) {
        System.out.println(LocalDateTime.now()  + "::reset frame::" + frame);
        super.onReset(session, frame);
    }

    @Override
    public void onClose(Session session, GoAwayFrame frame) {
        System.out.println(LocalDateTime.now()  + "::close go away frame::" + frame);
        super.onClose(session, frame);
    }

    @Override
    public void onFailure(Session session, Throwable failure) {
        System.out.println(LocalDateTime.now()  + "::failure ex::" + failure);
        super.onFailure(session, failure);
    }

    @Override
    public void onGoAway(Session session, GoAwayFrame frame) {
        System.out.println(LocalDateTime.now()  + "::goaway go away frame::" + frame);
        super.onGoAway(session, frame);
    }

    @Override
    public void onClose(Session session, GoAwayFrame frame, Callback callback) {
        System.out.println(LocalDateTime.now()  + "::close2 go away frame::" + frame);
        super.onClose(session, frame, callback);
    }

    @Override
    public void onFailure(Session session, Throwable failure, Callback callback) {
        System.out.println(LocalDateTime.now()  + "::failure2 ex::" + failure);
        super.onFailure(session, failure, callback);
    }
}
