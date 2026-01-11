package util;


import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import po.MsgType;
import po.RespCode;
import po.ResponseMsg;
import serviceEntity.BizResult;

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

    public static ResponseMsg buildOk(MsgType type, Message msg) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(RespCode.TRC_OK)
                .setMsg(msg == null ? ByteString.EMPTY : msg.toByteString())
                .build();
    }

    public static ResponseMsg buildOk(MsgType msgType, BizResult result) {
        return ResponseMsg.newBuilder()
                .setMsgType(msgType)
                .setErrCode(result.getCode())
                .setMsg(result.getMsg() == null
                        ? ByteString.EMPTY
                        : result.getMsg().toByteString())
                .build();
    }

    public static ResponseMsg buildError(MsgType type, RespCode code) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(code)
                .setMsg(ByteString.EMPTY)
                .build();
    }

    public static ResponseMsg buildError(MsgType type, RespCode code, Message msg) {
        return ResponseMsg.newBuilder()
                .setMsgType(type)
                .setErrCode(code)
                .setMsg(msg == null ? ByteString.EMPTY : msg.toByteString())
                .build();
    }

    public static ResponseMsg buildError(MsgType msgType) {
        return ResponseMsg.newBuilder()
                .setMsgType(msgType)
                .setErrCode(RespCode.TRC_ERR)
                .setMsg(ByteString.EMPTY)
                .build();
    }
}
