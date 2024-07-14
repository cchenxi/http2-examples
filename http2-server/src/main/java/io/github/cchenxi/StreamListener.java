package io.github.cchenxi;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.ResetFrame;
import org.eclipse.jetty.util.Callback;

/**
 * custom Stream listener
 * Date: 2024-07-13
 *
 * @author chenxi
 */
public class StreamListener extends Stream.Listener.Adapter {
    @Override
    public void onData(Stream stream, DataFrame frame, Callback callback) {
        // Get the content buffer.
        ByteBuffer buffer = frame.getData();

        // Consume the buffer, here - as an example - just log it.
        System.out.println(LocalDateTime.now() + "::Consuming buffer " + buffer);
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));

        // 测试
        System.out.println(stream.getIdleTimeout());

        stream.setIdleTimeout(20 * 1_000L);

//        stream.reset(new ResetFrame(stream.getId(), -20), Callback.NOOP);

        System.out.println(stream.getIdleTimeout());

        // Tell the implementation that the buffer has been consumed.
        callback.succeeded();

        // By returning from the method, implicitly tell the implementation
        // to deliver to this method more DATA frames when they are available.
    }

    @Override
    public boolean onIdleTimeout(Stream stream, Throwable x) {
        System.out.println(LocalDateTime.now());
        // 可以设置服务端主动断开连接
        stream.getSession().close(200, "idle timeout", Callback.NOOP);
        return true;
    }
}
