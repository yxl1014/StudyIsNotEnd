package serviceEntity;

import com.google.protobuf.Message;
import po.RespCode;

/**
 * @author Administrator
 * @Package : serviceEntity
 * @Create on : 2026/1/8 15:26
 **/

public class BizResult {

    private final RespCode code;
    private final Message msg;

    private BizResult(RespCode code, Message msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BizResult ok(Message msg) {
        return new BizResult(RespCode.TRC_OK, msg);
    }

    public static BizResult error(RespCode code) {
        return new BizResult(code, null);
    }

    public RespCode getCode() {
        return code;
    }

    public Message getMsg() {
        return msg;
    }
}

