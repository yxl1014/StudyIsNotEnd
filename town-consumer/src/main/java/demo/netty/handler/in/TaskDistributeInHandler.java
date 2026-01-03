package demo.netty.handler.in;

import demo.manager.ServiceManager;
import demo.transfer.RequestTransfer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import util.CommonEntityBuilder;

public class TaskDistributeInHandler extends SimpleChannelInboundHandler<RequestMsg> {
    private static final Logger log = LoggerFactory.getLogger(TaskDistributeInHandler.class);

    private ServiceManager serviceManager;

    private RequestTransfer requestTransfer;

    public TaskDistributeInHandler(ServiceManager serviceManager, RequestTransfer requestTransfer) {
        this.serviceManager = serviceManager;
        this.requestTransfer = requestTransfer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMsg msg) throws Exception {
        log.info("TaskDistributeInHandler Message class: {}, toString: {}", msg.getClass().getName(), msg);

        ResponseMsg responseMsg = null;
        try {
            switch (msg.getMsgType()) {
                case TMT_TEST: {
                    RequestTest requestTest = RequestTest.parseFrom(msg.getMsg());
                    log.info("hello {}", requestTest.getHello());
                    responseMsg = ResponseMsg
                            .newBuilder()
                            .setErrCode(RespCode.TRC_OK)
                            .setMsg(
                                    ResponseTest.newBuilder()
                                            .setWorld("world")
                                            .build()
                                            .toByteString()
                            )
                            .setMsgType(MsgType.TMT_TEST_RESP)
                            .build();
                    break;
                }
                case TMT_LoginReq: {
                    LoginReq loginReq = LoginReq.parseFrom((msg.getMsg()));
                    if (loginReq == null) {
                        ctx.writeAndFlush(CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_REQUEST_BODY_NULL));
                        return;
                    }
                    responseMsg = requestTransfer.login(loginReq);
                }
                default: {
                    log.info("xxx");
                    break;
                }
            }
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            log.error("parse protocol failed");
            ctx.writeAndFlush(CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_PARSE_PROTOCOL_ERR));
            return;
        }

        if (responseMsg == null) {
            log.error("responseMsg is null, RequestMsg: {}", msg);
        } else {
            ctx.writeAndFlush(responseMsg);
        }
    }
}
