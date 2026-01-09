package serviceEntity;

import com.google.protobuf.Message;
import entity.UpdateInfoDO;
import lombok.Getter;
import po.RespCode;

/**
 * @author Administrator
 * @Package : serviceEntity
 * @Create on : 2026/1/8 15:26
 **/

public class BizResult {

    @Getter
    private final RespCode code;
    @Getter
    private final Message msg;
    @Getter
    private final UpdateInfoDO updateInfoDO;

    private BizResult(RespCode code, Message msg) {
        this.code = code;
        this.msg = msg;
        this.updateInfoDO = null;
    }

    private BizResult(RespCode code, Message msg, UpdateInfoDO updateInfoDO) {
        this.code = code;
        this.msg = msg;
        this.updateInfoDO = updateInfoDO;
    }

    public static BizResult ok(Message msg) {
        return new BizResult(RespCode.TRC_OK, msg);
    }

    public static BizResult ok(Message msg, UpdateInfoDO updateInfoDO) {
        return new BizResult(RespCode.TRC_OK, msg, updateInfoDO);
    }

    public static BizResult error(RespCode code) {
        return new BizResult(code, null);
    }
}

