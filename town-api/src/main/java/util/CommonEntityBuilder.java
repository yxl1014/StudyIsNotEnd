package util;


import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import po.MsgType;
import po.RespCode;
import po.ResponseMsg;

/// 通用Entity构造类
public class CommonEntityBuilder {
    /// 构造无消息体返回
    public static ResponseMsg buildNoBodyResp(RespCode respCode) {
        return ResponseMsg
                .newBuilder()
                .setErrCode(respCode)
                .setMsgType(MsgType.TMT_NoBodyRsp)
                .build();
    }

    public static ResponseMsg ok(MsgType type, Message msg) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(RespCode.TRC_OK)
                .setMsg(msg == null ? ByteString.EMPTY : msg.toByteString())
                .build();
    }

    public static ResponseMsg error(MsgType type, RespCode code) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(code)
                .setMsg(ByteString.EMPTY)
                .build();
    }

    public static ResponseMsg error(MsgType type, RespCode code, Message msg) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(code)
                .setMsg(msg == null ? ByteString.EMPTY : msg.toByteString())
                .build();
    }
}
