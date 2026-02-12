package client;

import com.google.protobuf.InvalidProtocolBufferException;
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

public class NettyTest {

    public static void main(String[] args) throws InterruptedException {
        new NettyTest().connect(18023, "127.0.0.1");
    }

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

            // 5. 业务
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
                        ctx.writeAndFlush(buildListQuestMsg());
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ResponseMsg responseMsg = (ResponseMsg) msg;
            try {
                if (responseMsg.getMsgType() == MsgType.TMT_LoginRsp) {
                    LoginRsp loginRsp = LoginRsp.parseFrom(responseMsg.getMsg());
                    System.out.println("token:" + loginRsp.getToken());
                }
                if (responseMsg.getMsgType() == MsgType.TMT_ListNoticeRsp) {
                    ListNoticeRsp listNoticeRsp = ListNoticeRsp.parseFrom(responseMsg.getMsg());
                    for (NoticeInfo noticeInfo : listNoticeRsp.getInfosList()) {
                        System.out.println(noticeInfo.toString());
                    }
                }
                if (responseMsg.getMsgType() == MsgType.TMT_ListUpdateInfoRsp) {
                    ListUpdateInfoRsp listUpdateInfoRsp = ListUpdateInfoRsp.parseFrom(responseMsg.getMsg());
                    for (UpdateInfo updateInfo : listUpdateInfoRsp.getInfosList()) {
                        System.out.println(updateInfo.toString());
                    }
                }
                if (responseMsg.getMsgType() == MsgType.TMT_ListQuestionRsp) {
                    ListQuestionRsp listQuestionRsp = ListQuestionRsp.parseFrom(responseMsg.getMsg());
                    for (QuestionInfo questionInfo : listQuestionRsp.getInfosList()) {
                        System.out.println(questionInfo.toString());
                    }

                }
            } catch (InvalidProtocolBufferException e) {
                System.out.println(e);
            }
            System.out.println("ChatReqHandler channelRead 收到信息：" + responseMsg.toString());
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        private RequestMsg buildCreateQuestMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_CreateQuestionReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMTMwMDEyXCIsXCJjcmVhdGVUaW1lXCI6MTc3MDg2NTUyMzQ2MSxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIixcInVzZXJOYW1lXCI6XCJ4eHgxMjNcIn0iLCJpYXQiOjE3NzA4NjU1MjMsImV4cCI6MTc3MDk1MTkyM30.AD32gaVQH-XL0AsHTjPyD0994gIbB8Nn4oXfYFXaihQ");
            msgBuilder.setMsg(
                    CreateQuestionReq.newBuilder()
                            .setQuestion(
                                    QuestionInfo.newBuilder()
                                            .setQuestionType(QuestionType.TQT_WSHJ)
                                            .setQuestionCtx("xxxxxxxxx")
                                            .setNodeType(QuestionNodeType.TQNT_TAIL)
                            )
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildUpdateQuestMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_UpdateQuestionReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMTMwMDEyXCIsXCJjcmVhdGVUaW1lXCI6MTc3MDg2NTUyMzQ2MSxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIixcInVzZXJOYW1lXCI6XCJ4eHgxMjNcIn0iLCJpYXQiOjE3NzA4NjU1MjMsImV4cCI6MTc3MDk1MTkyM30.AD32gaVQH-XL0AsHTjPyD0994gIbB8Nn4oXfYFXaihQ");
            msgBuilder.setMsg(
                    UpdateQuestionReq.newBuilder()
                            .setQuestion(
                                    QuestionInfo.newBuilder()
                                            .setQuestionId(1)
                                            .setQuestionType(QuestionType.TQT_JCSS)
                                            .setQuestionCtx("yyyyyyyyy")
                            )
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildListQuestMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_ListQuestionReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxMjM0NTYsXCJyYW5kb21Db2RlXCI6XCI0MTAwNTBcIixcImNyZWF0ZVRpbWVcIjoxNzcwODY2NzEwNTU3LFwidXNlclBvd2VyXCI6XCJUVVBfQ0dNXCIsXCJ1c2VyRmxhZ1R5cGVcIjpcIlRVRlRfREVGQVVMVFwiLFwidXNlck5hbWVcIjpcInh4eFwifSIsImlhdCI6MTc3MDg2NjcxMCwiZXhwIjoxNzcwOTUzMTEwfQ.b0dYatowwKLfal1TxpQqC7F3QeV1VXegzMXqm9iM9GE");
            msgBuilder.setMsg(
                    ListQuestionReq.newBuilder()
                            .setPage(1)
                            .setSize(10)
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildListUpdateMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_ListUpdateInfoReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMDQxMDUzXCIsXCJjcmVhdGVUaW1lXCI6MTc2ODIwOTM0ODk1NSxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIixcInVzZXJOYW1lXCI6XCJhZG1pblwifSIsImlhdCI6MTc2ODIwOTM0OCwiZXhwIjoxNzY4Mjk1NzQ4fQ.PEJzhPOAZPwLpaVPfiOtVgG82VTtM1beVZJ2rADu3QA");
            msgBuilder.setMsg(
                    ListUpdateInfoReq.newBuilder()
                            .setPage(1)
                            .setSize(10)
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildListNoticeMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_ListNoticeReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMDQxMDUzXCIsXCJjcmVhdGVUaW1lXCI6MTc2ODIwOTM0ODk1NSxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIixcInVzZXJOYW1lXCI6XCJhZG1pblwifSIsImlhdCI6MTc2ODIwOTM0OCwiZXhwIjoxNzY4Mjk1NzQ4fQ.PEJzhPOAZPwLpaVPfiOtVgG82VTtM1beVZJ2rADu3QA");
            msgBuilder.setMsg(
                    ListNoticeReq.newBuilder()
                            .setPage(1)
                            .setSize(10)
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildCreateNoticeMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_CreateNoticeReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMDQxMDUzXCIsXCJjcmVhdGVUaW1lXCI6MTc2ODIwOTM0ODk1NSxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIixcInVzZXJOYW1lXCI6XCJhZG1pblwifSIsImlhdCI6MTc2ODIwOTM0OCwiZXhwIjoxNzY4Mjk1NzQ4fQ.PEJzhPOAZPwLpaVPfiOtVgG82VTtM1beVZJ2rADu3QA");
            msgBuilder.setMsg(
                    CreateNoticeReq.newBuilder()
                            .setNoticeInfo(
                                    NoticeInfo.newBuilder()
                                            .setNoticeType(TNoticeType.TNT_TZ)
                                            .setNoticeTitle("测试通知标题")
                                            .setNoticeContext("测试通知正文正文正文正文正文正文")
                                            .setIsAcceptRead(true)
                                            .setIsTop(true)
                            )
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildUpdateUserInfoMsg() {
            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_UpdateUserInfoReq);
            msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxLFwicmFuZG9tQ29kZVwiOlwiMDQwMDMzXCIsXCJjcmVhdGVUaW1lXCI6MTc2Nzg4NDczODQxNCxcInVzZXJQb3dlclwiOlwiVFVQX0NHTVwiLFwidXNlckZsYWdUeXBlXCI6XCJUVUZUX0RFRkFVTFRcIn0iLCJpYXQiOjE3Njc4ODQ3MzgsImV4cCI6MTc2Nzk3MTEzOH0.1WYcMAg_g6oL7MFN2L-xbP_FjQl_oHMiNE5YQCDsEEY");
            msgBuilder.setMsg(
                    UpdateUserInfoReq.newBuilder()
                            .setIsDel(false)
                            .setUserInfo(
                                    UserInfo.newBuilder()
                                            .setUserTel(10001)
                                            .setUserName("zzz")
                            )
                            .build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildLoginRequestMsg() {

            LoginReq.Builder builder = LoginReq.newBuilder();
//            builder.setUserTel(1);
//            builder.setUserPwd("admin");
            builder.setUserTel(123456);
            builder.setUserPwd("123456");

            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_LoginReq);
            msgBuilder.setMsg(builder.build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }

        private RequestMsg buildRegisterRequestMsg() {

            RegisterReq.Builder register = RegisterReq.newBuilder();
            UserInfo.Builder userInfo = UserInfo.newBuilder();
            userInfo.setUserTel(10001);
            userInfo.setUserPwd("123456");
            userInfo.setUserName("xxx");
            userInfo.setUserPower(TUserPower.TUP_CM);
            userInfo.setUserTown("yyy");
            userInfo.setFlagType(TUserFlagType.TUFT_DEFAULT);

            register.setUserInfo(userInfo);

            RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
            msgBuilder.setMsgType(MsgType.TMT_RegisterReq);
            msgBuilder.setMsg(register.build().toByteString());
            RequestMsg requestMsg = msgBuilder.build();
            System.out.println("发送消息：" + requestMsg.toString());
            return requestMsg;
        }
    }

}
