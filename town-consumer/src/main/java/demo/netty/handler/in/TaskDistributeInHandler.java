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
import java.util.function.BiFunction;
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

    /// 这里初始化调度器
    public void init() {
        // User
        handlerMap.put(MsgType.TMT_LoginReq,
                (msg, ctx) -> noTokenHandle(
                        msg.getMsg().toByteArray(),
                        LoginReq.parser(),
                        requestTransfer::login,
                        ctx
                ));

        handlerMap.put(MsgType.TMT_RegisterReq,
                (msg, ctx) -> noTokenHandle(
                        msg.getMsg().toByteArray(),
                        RegisterReq.parser(),
                        requestTransfer::register,
                        ctx
                ));

        handlerMap.put(MsgType.TMT_UpdateUserInfoReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        UpdateUserInfoReq.parser(),
                        requestTransfer::updateUserInfo,
                        ctx
                ));

        // Notice
        handlerMap.put(MsgType.TMT_CreateNoticeReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        CreateNoticeReq.parser(),
                        requestTransfer::createNotice,
                        ctx
                ));
        handlerMap.put(MsgType.TMT_UpdateNoticeReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        UpdateNoticeReq.parser(),
                        requestTransfer::updateNotice,
                        ctx
                ));
        handlerMap.put(MsgType.TMT_ListNoticeReq,
                (msg, ctx) -> noTokenHandle(
                        msg.getMsg().toByteArray(),
                        ListNoticeReq.parser(),
                        requestTransfer::listNotice,
                        ctx
                ));
        handlerMap.put(MsgType.TMT_SetNoticeReadReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        SetNoticeReadReq.parser(),
                        requestTransfer::setNoticeRead,
                        ctx
                ));
        handlerMap.put(MsgType.TMT_ListNoticeReadReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        ListNoticeReadReq.parser(),
                        requestTransfer::listNoticeRead,
                        ctx
                ));
        handlerMap.put(MsgType.TMT_ListUpdateInfoReq,
                (msg, ctx) -> handle(
                        msg.getToken(),
                        msg.getMsg().toByteArray(),
                        ListUpdateInfoReq.parser(),
                        requestTransfer::listUpdateInfo,
                        ctx
                ));
    }

    private <T extends com.google.protobuf.Message> ResponseMsg noTokenHandle(
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

    private <T extends com.google.protobuf.Message> ResponseMsg handle(
            String token,
            byte[] body,
            Parser<T> parser,
            BiFunction<String, T, ResponseMsg> bizFunc,
            ChannelHandlerContext ctx
    ) throws InvalidProtocolBufferException {
        T req = parser.parseFrom(body);
        if (req == null) {
            ctx.writeAndFlush(
                    CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_REQUEST_BODY_NULL)
            );
            return null;
        }
        return bizFunc.apply(token, req);
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
