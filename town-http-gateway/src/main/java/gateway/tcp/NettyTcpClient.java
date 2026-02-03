package gateway.tcp;

import gateway.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @Package : gateway.tcp
 * @Create on : 2026/2/3 15:23
 **/


@Component
public class NettyTcpClient {

    private final Bootstrap bootstrap;
    private final NettyClientConfig config;

    public NettyTcpClient(NettyClientConfig config) {
        this.config = config;

        EventLoopGroup group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(4));
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                                1024 * 1024, 0, 4, 0, 4));

                        ch.pipeline().addLast(new NettyTcpClientHandler());
                    }
                });
    }

    public byte[] sendAndReceive(byte[] data) {
        try {
            Channel channel = bootstrap
                    .connect(config.getHost(), config.getPort())
                    .sync()
                    .channel();

            NettyTcpClientHandler handler = channel.pipeline().get(NettyTcpClientHandler.class);

            channel.writeAndFlush(Unpooled.wrappedBuffer(data)).sync();

            byte[] resp = handler.getResponse(config.getReadTimeout());

            channel.close();
            return resp;

        } catch (Exception e) {
            throw new RuntimeException("TCP forward failed", e);
        }
    }
}

