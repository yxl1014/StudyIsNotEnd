package client;

/**
 * @author Administrator
 * @Package : client
 * @Create on : 2026/2/3 15:44
 **/

import gateway.GatewayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import po.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BinaryGatewayControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testBinaryForward() throws Exception {

        // ===== 构造二进制请求 =====
        byte[] requestBytes = buildUpdateUserInfoMsg().toByteArray();

        // ===== 发送 HTTP 二进制请求 =====
        HttpURLConnection conn =
                (HttpURLConnection) new URL("http://localhost:48080/gateway/forward")
                        .openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBytes);
        }

        byte[] resp = conn.getInputStream().readAllBytes();

        ResponseMsg msg = ResponseMsg.parseFrom(resp);
        System.out.println(msg.toString());
//        LoginRsp loginRsp = LoginRsp.parseFrom(registerRsp.getMsg());
//        System.out.println(loginRsp.getToken());
        // ===== 打印返回结果 =====
//        System.out.println("loginRsp:" + registerRsp.toString());
        System.out.println();
    }

    private RequestMsg buildUpdateUserInfoMsg() {
        RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
        msgBuilder.setMsgType(MsgType.TMT_UpdateUserInfoReq);
        msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxMjM0NTYsXCJyYW5kb21Db2RlXCI6XCIzMTQ0MTVcIixcImNyZWF0ZVRpbWVcIjoxNzcwNjE4MDkzOTM1LFwidXNlclBvd2VyXCI6XCJUVVBfQ0dNXCIsXCJ1c2VyRmxhZ1R5cGVcIjpcIlRVRlRfREVGQVVMVFwiLFwidXNlck5hbWVcIjpcInh4eFwifSIsImlhdCI6MTc3MDYxODA5MywiZXhwIjoxNzcwNzA0NDkzfQ.vr1dJERaX2BFsW0haHSURS8DKeK-CzDUSqmWkBFqbfc");
        msgBuilder.setMsg(
                UpdateUserInfoReq.newBuilder()
                        .setIsDel(false)
                        .setUserInfo(
                                UserInfo.newBuilder()
                                        .setUserTel(1)
                                        .setUserName("xxx123")
                        )
                        .build().toByteString());
        RequestMsg requestMsg = msgBuilder.build();
        System.out.println("发送消息：" + requestMsg.toString());
        return requestMsg;
    }

    private RequestMsg buildUpdateNoticeMsg()
    {
        RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
        msgBuilder.setMsgType(MsgType.TMT_UpdateNoticeReq);
        msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxMjM0NTYsXCJyYW5kb21Db2RlXCI6XCIzMTQ0MTVcIixcImNyZWF0ZVRpbWVcIjoxNzcwNjE4MDkzOTM1LFwidXNlclBvd2VyXCI6XCJUVVBfQ0dNXCIsXCJ1c2VyRmxhZ1R5cGVcIjpcIlRVRlRfREVGQVVMVFwiLFwidXNlck5hbWVcIjpcInh4eFwifSIsImlhdCI6MTc3MDYxODA5MywiZXhwIjoxNzcwNzA0NDkzfQ.vr1dJERaX2BFsW0haHSURS8DKeK-CzDUSqmWkBFqbfc");
        msgBuilder.setMsg(
                UpdateNoticeReq.newBuilder()
                        .setNoticeInfo(
                                NoticeInfo.newBuilder()
                                        .setNoticeId(1)
                                        .setNoticeType(TNoticeType.TNT_TZ)
                                        .setNoticeTitle("带大家看大111")
                                        .setNoticeContext("更多空间广阔的交流过得快")
                                        .setIsAcceptRead(true)
                                        .setIsTop(true)
                        )
                        .build().toByteString());
        RequestMsg requestMsg = msgBuilder.build();
        System.out.println("发送消息：" + requestMsg.toString());
        return requestMsg;
    }

    private RequestMsg buildCreateNoticeMsg() {
        RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
        msgBuilder.setMsgType(MsgType.TMT_CreateNoticeReq);
        msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxMjM0NTYsXCJyYW5kb21Db2RlXCI6XCIzMTQ0MTVcIixcImNyZWF0ZVRpbWVcIjoxNzcwNjE4MDkzOTM1LFwidXNlclBvd2VyXCI6XCJUVVBfQ0dNXCIsXCJ1c2VyRmxhZ1R5cGVcIjpcIlRVRlRfREVGQVVMVFwiLFwidXNlck5hbWVcIjpcInh4eFwifSIsImlhdCI6MTc3MDYxODA5MywiZXhwIjoxNzcwNzA0NDkzfQ.vr1dJERaX2BFsW0haHSURS8DKeK-CzDUSqmWkBFqbfc");
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

    private RequestMsg buildListUserMsg(){
        ListUserInfoReq.Builder builder = ListUserInfoReq.newBuilder();
        builder.setPage(1);
        builder.setSize(10);

        RequestMsg.Builder msgBuilder = RequestMsg.newBuilder();
        msgBuilder.setMsgType(MsgType.TMT_ListUserInfoReq);
        msgBuilder.setMsg(builder.build().toByteString());
        msgBuilder.setToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsb2dpbiIsInRva2VuSW5mbyI6IntcInVzZXJUZWxcIjoxMjM0NTYsXCJyYW5kb21Db2RlXCI6XCIzMTQ0MTVcIixcImNyZWF0ZVRpbWVcIjoxNzcwNjE4MDkzOTM1LFwidXNlclBvd2VyXCI6XCJUVVBfQ0dNXCIsXCJ1c2VyRmxhZ1R5cGVcIjpcIlRVRlRfREVGQVVMVFwiLFwidXNlck5hbWVcIjpcInh4eFwifSIsImlhdCI6MTc3MDYxODA5MywiZXhwIjoxNzcwNzA0NDkzfQ.vr1dJERaX2BFsW0haHSURS8DKeK-CzDUSqmWkBFqbfc");
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
        userInfo.setUserTel(123456);
        userInfo.setUserPwd("123456");
        userInfo.setUserName("xxx");
        userInfo.setUserPower(TUserPower.TUP_CGM);
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

