package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.assertj.core.util.DateUtil;
import po.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyTest {

    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws InterruptedException {
        ChannelFuture future = null;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ClientChannelInitializer());

            future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("服务端连接失败，msg:{" + e.getMessage() + "}");
        } finally {
            if (null != future) {
                if (future.channel() != null && future.channel().isOpen()) {
                    future.channel().close();
                }
            }
            System.out.println(DateUtil.now() + "准备重连...");
            Thread.sleep(3000);
            connect(port, host);
            System.out.println("【" + DateUtil.now() + "】重连成功...");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyTest().connect(18023, "127.0.0.1");
    }

    public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            // ========== 出站处理器（发送消息给服务器）==========

            // 1. 长度编码器（必须！）
            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

            // 2. Protobuf 编码器（编码客户端请求）
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());

            // ========== 入站处理器（接收服务器响应）==========

            // 3. 帧解码器（必须！）
            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                    1024 * 1024, 0, 4, 0, 4));

            // 4. Protobuf 解码器（解码服务器响应）
            pipeline.addLast("protobufDecoder",
                    new ProtobufDecoder(ResponseMsg.getDefaultInstance()));

            // 5. 业务处理器
            pipeline.addLast("chatHandler", new ChatReqHandler());

            // 6. 心跳（可选）
            pipeline.addFirst("idle", new IdleStateHandler(30, 30, 60, TimeUnit.SECONDS));

            // 7. 添加调试日志（可选）
            pipeline.addFirst("logging", new LoggingHandler(LogLevel.DEBUG));

            System.out.println("Client pipeline initialized");
        }
    }

    public class ChatReqHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            Random random = new Random();
            System.out.println("ChatReqHandler channelActive,IM系统准备就绪，请发送消息: ");
            new Thread(() -> {
                while (true) {
                    try {
                        RequestTest.Builder builder = RequestTest.newBuilder();
                        StringBuilder msg = new StringBuilder("world");
                        for (int i = 0; i < 5; i++) {
                            msg.append(random.nextInt(10));
                        }
                        builder.setHello(msg.toString());
                        RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();

                        msgBuilder.setMsgType(MsgType.TMT_TEST);
                        msgBuilder.setMsg(builder.build().toByteString());
                        RequestMsg requestMsg = msgBuilder.build();
                        System.out.println("发送消息：" + requestMsg.toString());
                        //发送数据并刷新
                        ctx.writeAndFlush(requestMsg);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ResponseMsg responseMsg = (ResponseMsg) msg;
            System.out.println("ChatReqHandler channelRead 收到信息：" + responseMsg.toString());
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
