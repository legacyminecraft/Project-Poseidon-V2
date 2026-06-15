package com.legacyminecraft.poseidon.network.netty;

import com.legacyminecraft.poseidon.network.connection.ConnectionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

public final class NettyConnectionManager implements ConnectionManager<NettyPlayerConnection> {

    private static final Logger log = LoggerFactory.getLogger(NettyConnectionManager.class);

    private final List<NettyPlayerConnection> connections = new CopyOnWriteArrayList<>();

    static {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    public NettyConnectionManager(
            MinecraftServer server,
            InetAddress host,
            int port,
            int threads,
            boolean useNativeTransport) {
        NettyEnvironment environment = createEnvironment(useNativeTransport);

        EventLoopGroup group = new MultiThreadIoEventLoopGroup(
                threads,
                environment.threadFactory(),
                environment.ioHandlerFactory());

        new ServerBootstrap()
                .group(group)
                .channel(environment.channelType())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.setOption(ChannelOption.TCP_NODELAY, true);
                        NetLoginHandler netLoginHandler = new NetLoginHandler(server, channel);
                        addConnection((NettyPlayerConnection) netLoginHandler.networkManager);
                    }
                })
                .bind(host, port)
                .syncUninterruptibly()
                .channel().closeFuture().addListener(_ -> group.shutdownGracefully());
    }

    public void addConnection(NettyPlayerConnection connection) {
        this.connections.add(connection);
    }

    @Override
    public Iterable<NettyPlayerConnection> getConnections() {
        return this.connections::iterator;
    }

    public void tickConnections() {
        for (NettyPlayerConnection connection : this.connections) {
            try {
                connection.getNetHandler().tick();
                connection.flush();
            } catch (Exception e) {
                log.warn("Failed to tick connection", e);
                connection.disconnect("Internal server error");
            }

            if (!connection.isConnected()) {
                this.connections.remove(connection);
            }
        }
    }

    private static NettyEnvironment createEnvironment(boolean useNativeTransport) {
        ThreadFactory threadFactory;
        IoHandlerFactory ioHandlerFactory;
        Class<? extends ServerSocketChannel> channelType;

        if (useNativeTransport && Epoll.isAvailable()) {
            threadFactory = Thread.ofPlatform().name("Netty Epoll I/O #", 1).factory();
            ioHandlerFactory = EpollIoHandler.newFactory();
            channelType = EpollServerSocketChannel.class;
        } else if (useNativeTransport && KQueue.isAvailable()) {
            threadFactory = Thread.ofPlatform().name("Netty KQueue I/O #", 1).factory();
            ioHandlerFactory = KQueueIoHandler.newFactory();
            channelType = KQueueServerSocketChannel.class;
        } else {
            threadFactory = Thread.ofPlatform().name("Netty NIO I/O #", 1).factory();
            ioHandlerFactory = NioIoHandler.newFactory();
            channelType = NioServerSocketChannel.class;
        }

        return new NettyEnvironment(threadFactory, ioHandlerFactory, channelType);
    }

    private record NettyEnvironment(
            ThreadFactory threadFactory,
            IoHandlerFactory ioHandlerFactory,
            Class<? extends ServerSocketChannel> channelType
    ) {
    }
}
