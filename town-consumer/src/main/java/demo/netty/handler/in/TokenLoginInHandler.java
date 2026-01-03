package demo.netty.handler.in;

import demo.manager.ServiceManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import townInterface.IRedisService;
import util.CommonEntityBuilder;
import util.ConstValue;
import util.JwtUtil;

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
        if (msg.getToken().isEmpty()) {
            if (msg.getMsgType() != MsgType.TMT_LoginReq && msg.getMsgType() != MsgType.TMT_RegisterReq) {
                log.info("token expired");
                ctx.writeAndFlush(CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_TOKEN_NOT_EXIST))
                        .addListener(future ->
                        {
                            if (future.isSuccess()) {
                                log.info("响应发送成功");
                            } else {
                                log.error("响应发送失败", future.cause());
                            }
                        });
                return;
            }
        } else {
            // 验证token
            TokenInfo tokenInfo = JwtUtil.parseTokenInfo(msg.getToken());

            // 解析失败则返回
            if (tokenInfo == null) {
                ctx.writeAndFlush(CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_TOKEN_INVALID));
                return;
            }

            // 看看本地有没有
            IRedisService redisService = serviceManager.daoService.redisService();

            // 看看redis
            Object redisValue = redisService.get(ConstValue.Redis_Prefix_Token + tokenInfo.getUserTel());
            TokenInfo redisCache = redisValue == null ? null : (TokenInfo) redisValue;

            // 说明失效了
            if (redisCache == null || !redisCache.equals(tokenInfo)) {
                ctx.writeAndFlush(CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_TOKEN_INVALID));
                return;
            }
        }

        // 进行下一步
        ctx.fireChannelRead(msg);
    }
}
