package io.github.cchenxi;

import org.eclipse.jetty.http2.api.Stream;

/**
 * custom Stream listener
 * Date: 2024-07-13
 *
 * @author chenxi
 */
public class StreamListener extends Stream.Listener.Adapter {
    @Override
    public boolean onIdleTimeout(Stream stream, Throwable x) {
        return super.onIdleTimeout(stream, x);
    }
}
