package demo.netty.handler.in;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import demo.manager.ServiceManager;
import demo.netty.handler.util.MsgHandler;
import demo.transfer.RequestTransfer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import util.CommonEntityBuilder;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class TaskDistributeInHandler extends SimpleChannelInboundHandler<RequestMsg> {
    private static final Logger log = LoggerFactory.getLogger(TaskDistributeInHandler.class);

    private ServiceManager serviceManager;

    private RequestTransfer requestTransfer;

    public TaskDistributeInHandler(ServiceManager serviceManager, RequestTransfer requestTransfer) {
        this.serviceManager = serviceManager;
        this.requestTransfer = requestTransfer;
        init();
    }

    private final Map<MsgType, MsgHandler> handlerMap = new EnumMap<>(MsgType.class);

    /// 这是一个调度器
    public void init() {
        handlerMap.put(MsgType.TMT_LoginReq,
                (msg, ctx) -> handle(
                        msg.getMsg().toByteArray(),
                        LoginReq.parser(),
                        requestTransfer::login,
                        ctx
                ));

        handlerMap.put(MsgType.TMT_RegisterReq,
                (msg, ctx) -> handle(
                        msg.getMsg().toByteArray(),
                        RegisterReq.parser(),
                        requestTransfer::register,
                        ctx
                ));

        handlerMap.put(MsgType.TMT_UpdateUserInfoReq,
                (msg, ctx) -> handle(
                        msg.getMsg().toByteArray(),
                        UpdateUserInfoReq.parser(),
                        requestTransfer::updateUserInfo,
                        ctx
                ));
    }

    private <T extends com.google.protobuf.Message> ResponseMsg handle(
            byte[] body,
            Parser<T> parser,
            Function<T, ResponseMsg> bizFunc,
            ChannelHandlerContext ctx
    ) throws InvalidProtocolBufferException {

        T req = parser.parseFrom(body);
        if (req == null) {
            ctx.writeAndFlush(
                    CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_REQUEST_BODY_NULL)
            );
            return null;
        }
        return bizFunc.apply(req);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMsg msg) throws Exception {
        log.info("TaskDistributeInHandler Message class: {}, toString: {}", msg.getClass().getName(), msg);

        MsgHandler handler = handlerMap.get(msg.getMsgType());

        if (handler == null) {
            log.warn("No handler for MsgType: {}", msg.getMsgType());
            return;
        }

        try {
            ResponseMsg resp = handler.handle(msg, ctx);
            if (resp != null) {
                ctx.writeAndFlush(resp);
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("parse protocol failed", e);
            ctx.writeAndFlush(
                    CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_PARSE_PROTOCOL_ERR)
            );
        } catch (Exception e) {
            log.error("handle failed", e);
        }
    }
}
