package util;


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
}
