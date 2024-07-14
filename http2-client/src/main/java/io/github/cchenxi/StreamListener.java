package io.github.cchenxi;

import java.time.LocalDateTime;

import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.ResetFrame;

/**
 * custom Stream listener
 * Date: 2024-07-13
 *
 * @author chenxi
 */
public class StreamListener extends Stream.Listener.Adapter {
    @Override
    public boolean onIdleTimeout(Stream stream, Throwable x) {
        System.out.println(LocalDateTime.now() + " on idle timeout.");
        return super.onIdleTimeout(stream, x);
    }

    @Override
    public void onReset(Stream stream, ResetFrame frame) {
        System.out.println(LocalDateTime.now() + " on reset.");
        super.onReset(stream, frame);
    }
}
