package demo.netty.handler.in;

import demo.controller.ServiceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;

/// token验证处理器
public class TokenLoginInHandler extends SimpleChannelInboundHandler<RequestMsg> {
    private static final Logger log = LoggerFactory.getLogger(TokenLoginInHandler.class);

    private ServiceManager serviceManager;

    public TokenLoginInHandler(ServiceManager serviceManager) {

        this.serviceManager = serviceManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMsg msg) throws Exception {
        log.info("ServerListenerHandler Message class: {}, toString: {}", msg.getClass().getName(), msg);

        // 如果没有token，并且不是登录或者注册，直接返回
        if (msg.getToken().isEmpty()
                && msg.getMsgType() != MsgType.TMT_LoginReq
                && msg.getMsgType() != MsgType.TMT_RegisterReq) {
            log.info("token expired");
            ctx.writeAndFlush(ResponseMsg.newBuilder().setErrCode(RespCode.TRC_ERR).setMsgType(MsgType.TMT_TEST_RESP).build()).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("响应发送成功");
                } else {
                    log.error("响应发送失败", future.cause());
                }
            });
        } else {
            // 验证token

            // 进行下一步
            ctx.fireChannelRead(msg);
        }
    }
}
