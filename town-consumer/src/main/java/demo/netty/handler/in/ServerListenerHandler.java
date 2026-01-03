package demo.netty.handler.in;

import demo.manager.ServiceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.RequestMsg;

public class ServerListenerHandler extends SimpleChannelInboundHandler<RequestMsg> {
    private static final Logger log = LoggerFactory.getLogger(ServerListenerHandler.class);

    private ServiceManager serviceManager;
    public ServerListenerHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * 设备接入连接时处理
     *
     * @param ctx
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("有新的连接：[{}]", ctx.channel().id().asLongText());
    }

    /**
     * 数据处理
     *
     * @param ctx
     * @param msg
     */
    @SneakyThrows
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMsg msg) {
        // 进行下一步
        ctx.fireChannelRead(msg);
    }


    /**
     * 设备下线处理
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("设备下线了:{}", ctx.channel().id().asLongText());
    }

    /**
     * 设备连接异常处理
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 打印异常
        log.info("异常：{}", cause.getMessage());
        // 关闭连接
        ctx.close();
    }
}
