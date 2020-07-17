package cc.ifnot.java.libs.socket;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/17 3:15 PM
 * description:
 */
class Server {

    private static final int BUFSIZE = 1024;
    private static final String ServerThreadFactoryPrefix = "s_";
    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new ServerThreadFactory());

    private static Map<SelectionKey, List<ByteBuffer>> data = new HashMap<>();

    public static void main(String[] args) throws IOException {

        final ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(8888));
        channel.configureBlocking(false);

        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

        final Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            final int ret = selector.select();
            Lg.d("selected: %s", ret);
            final Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey k : keys) {
                Lg.d("keys: %s", k);
                if (k.isAcceptable()) {
                    final ServerSocketChannel ss = (ServerSocketChannel) k.channel();
                    final SocketChannel s = ss.accept();
                    s.configureBlocking(false);
                    k.cancel();
                    s.register(selector, SelectionKey.OP_READ);
                } else if (k.isReadable()) {
                    pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doRead(k);
                            } catch (Exception e) {
                                k.cancel();
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (k.isWritable()) {
                    pool.execute(() -> {
                        try {
                            doWrite(k);
                        } catch (Exception e) {
                            k.cancel();
                            e.printStackTrace();
                        }
                    });
                } else {
                    Lg.d("event no card: %s", k);
                    k.channel().close();
                    k.cancel();
                }
            }
        }
    }

    private static void doWrite(SelectionKey k) throws Exception {
        final List<ByteBuffer> byteBuffers = data.get(k);
        final SocketChannel channel = (SocketChannel) k.channel();
        if (byteBuffers != null) {
            for (ByteBuffer b : byteBuffers) {
                Lg.d("write: %s", Arrays.toString(b.array()));
                channel.write(b);
                k.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private static void doRead(SelectionKey k) throws Exception {
        final SocketChannel channel = (SocketChannel) k.channel();
        final ByteBuffer buf = ByteBuffer.allocate(BUFSIZE);
        List<ByteBuffer> rl = new ArrayList<>();
        int read;
        do {
            read = channel.read(buf);
            if (read == -1) throw new NotYetConnectedException();
            Lg.d("read: %s", Arrays.toString(buf.array()));
            rl.add(buf);
        } while (read > 0);

        data.put(k, rl);
        k.interestOps(SelectionKey.OP_WRITE);
    }

    private static class ServerThreadFactory extends AtomicLong implements ThreadFactory {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, ServerThreadFactoryPrefix + getAndIncrement());
        }
    }
}
