package serviceEntity;


import com.google.protobuf.ByteString;
import entity.TokenInfoDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import townInterface.IUpdateService;
import util.TokenResolver;

import java.util.function.Supplier;


/**
 * @author Administrator
 * @Package : serviceEntity
 * @Create on : 2026/1/8 15:27
 **/
public abstract class AbstractRpcService {
    private static final Logger log = LoggerFactory.getLogger(AbstractRpcService.class);

    public abstract IUpdateService updateService();

    protected ResponseMsg execute(
            MsgType msgType,
            String token,                 // 入口 token
            Supplier<BizResult> supplier
    ) {
        try {
            // 解析 token → TokenInfo
            TokenInfoDO tokenInfo = TokenResolver.resolve(token);
            if (tokenInfo != null) {
                UserContext.set(tokenInfo);
            }

            // 执行业务逻辑
            BizResult result = supplier.get();

            // 添加更新信息
            if (TUserPower.TUP_CGM.equals(tokenInfo.getUserPower())
                    && result.getUpdateInfoDO() != null
                    && updateService() != null) {
                RespCode respCode = updateService().addUpdateInfo(result.getUpdateInfoDO());
                if (respCode != RespCode.TRC_OK) {
                    log.error("add update failed updateInfo{}", result.getUpdateInfoDO().toString());
                }
            }

            // 统一构建 ResponseMsg
            return ResponseMsg.newBuilder()
                    .setMsgType(msgType)
                    .setErrCode(result.getCode())
                    .setMsg(result.getMsg() == null
                            ? ByteString.EMPTY
                            : result.getMsg().toByteString())
                    .build();

        } catch (Exception e) {
            log.error("rpc error, type={}, err={}", msgType, e);
            return ResponseMsg.newBuilder()
                    .setMsgType(msgType)
                    .setErrCode(RespCode.TRC_ERR)
                    .setMsg(ByteString.EMPTY)
                    .build();

        } finally {
            // 清理 ThreadLocal
            UserContext.clear();
        }
    }
}

