package demo.netty;

import demo.manager.ServiceManager;
import demo.netty.handler.in.ServerListenerHandler;
import demo.netty.handler.in.TaskDistributeInHandler;
import demo.netty.handler.in.TokenLoginInHandler;
import demo.transfer.RequestTransfer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.RequestMsg;

public class NettyServerHandler extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
    private ServiceManager serviceManager;
    private RequestTransfer requestTransfer;
    public NettyServerHandler(ServiceManager serviceManager, RequestTransfer requestTransfer) {
        this.serviceManager = serviceManager;
        this.requestTransfer = requestTransfer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        // ========== 入站处理器（从客户端到服务器）==========

        // 1. 帧解码器（处理TCP粘包拆包 - 必须添加！）
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                1024 * 1024,  // 最大帧长度 1MB
                0,            // 长度字段偏移量
                4,            // 长度字段长度（4字节）
                0,            // 长度调整值
                4             // 需要跳过的字节数（跳过长度字段本身）
        ));

        // 2. Protobuf 解码器（解码客户端请求）
        pipeline.addLast("protobufDecoder",
                new ProtobufDecoder(RequestMsg.getDefaultInstance()));


        // 5. 帧编码器
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

        // 4. Protobuf 编码器（必须放在这里！）
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());


        // 3. 业务处理器
        pipeline.addLast("serverHandler", new ServerListenerHandler(serviceManager));
        pipeline.addLast("tokenHandler", new TokenLoginInHandler(serviceManager));
        pipeline.addLast("distributeHandler", new TaskDistributeInHandler(serviceManager, requestTransfer));

        // ========== 出站处理器（从服务器到客户端）==========

        // 6. 添加调试日志（可选）
        pipeline.addFirst("logging", new LoggingHandler(LogLevel.DEBUG));

        log.info("Server pipeline initialized");
    }
}
