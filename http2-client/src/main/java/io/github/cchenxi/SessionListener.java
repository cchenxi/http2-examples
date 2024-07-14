package io.github.cchenxi;

import java.time.LocalDateTime;
import java.util.Map;

import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PingFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;

/**
 * custom session listener
 * Date: 2024-07-13
 *
 * @author chenxi
 */
public class SessionListener extends Session.Listener.Adapter {
    @Override
    public Map<Integer, Integer> onPreface(Session session) {
        System.out.println(LocalDateTime.now() + "::on per face");
        return null;
    }

    @Override
    public Stream.Listener onNewStream(Stream stream, HeadersFrame frame) {
        System.out.println(LocalDateTime.now() + "::on new stream");
        return new Stream.Listener.Adapter() {

        };
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
//        super.onClose(session, frame);
    }

    @Override
    public boolean onIdleTimeout(Session session) {
        System.out.println(LocalDateTime.now() + "::on idle timeout");
        return true;
//        return super.onIdleTimeout(session);
    }

    @Override
    public void onFailure(Session session, Throwable failure) {
        System.out.println(LocalDateTime.now() + "::on failure");
        super.onFailure(session, failure);
    }
}
