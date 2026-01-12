package serviceEntity;


import com.google.protobuf.ByteString;
import entity.TokenInfoDO;
import entity.UpdateInfoDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import townInterface.IUpdateService;
import util.CommonEntityBuilder;
import util.TokenResolver;

import java.util.ArrayList;
import java.util.function.Supplier;


/**
 * @author Administrator
 * @Package : serviceEntity
 * @Create on : 2026/1/8 15:27
 **/
public abstract class AbstractRpcService {
    private static final Logger log = LoggerFactory.getLogger(AbstractRpcService.class);

    public abstract IUpdateService updateService();

    private ArrayList<MsgType> checkTokenMsg;

    public AbstractRpcService() {
        initCheckTokenMsg();
    }

    private void initCheckTokenMsg() {
        checkTokenMsg = new ArrayList<>();
        checkTokenMsg.add(MsgType.TMT_UpdateUserInfoRsp);
        checkTokenMsg.add(MsgType.TMT_CreateNoticeRsp);
        checkTokenMsg.add(MsgType.TMT_UpdateNoticeRsp);
        checkTokenMsg.add(MsgType.TMT_ListUpdateInfoRsp);
    }


    protected ResponseMsg execute(
            MsgType msgType,
            String token,
            Supplier<BizResult> supplier
    ) {
        try {
            TokenInfoDO tokenInfo = resolveAndBindContext(token);

            ResponseMsg permissionFail = checkPermission(msgType, tokenInfo);
            if (permissionFail != null) {
                return permissionFail;
            }

            BizResult result = doBusiness(supplier);
            handleUpdateInfo(tokenInfo, result);

            return CommonEntityBuilder.buildOk(msgType, result);

        } catch (Exception e) {
            log.error("rpc error, type={}", msgType, e);
            return CommonEntityBuilder.buildError(msgType);
        } finally {
            UserContext.clear();
        }
    }


    /// 将token设置到threadLocal当中
    private TokenInfoDO resolveAndBindContext(String token) {
        TokenInfoDO tokenInfo = TokenResolver.resolve(token);
        if (tokenInfo != null) {
            UserContext.set(tokenInfo);
        }
        return tokenInfo;
    }

    /// 校验权限
    private ResponseMsg checkPermission(MsgType msgType, TokenInfoDO tokenInfo) {
        if (!checkTokenMsg.contains(msgType)) {
            return null;
        }

        if (tokenInfo == null || tokenInfo.getUserPower() != TUserPower.TUP_CGM) {
            return ResponseMsg.newBuilder()
                    .setMsgType(msgType)
                    .setErrCode(RespCode.TRC_USER_POWER_NOT_ENOUGH)
                    .build();
        }
        return null;
    }

    /// 执行逻辑
    private BizResult doBusiness(Supplier<BizResult> supplier) {
        BizResult result = supplier.get();
        if (result == null) {
            throw new IllegalStateException("BizResult must not be null");
        }
        return result;
    }

    /// 记录操作历史
    private void handleUpdateInfo(TokenInfoDO tokenInfo, BizResult result) {
        if (tokenInfo == null || tokenInfo.getUserPower() != TUserPower.TUP_CGM) {
            return;
        }

        UpdateInfoDO updateInfo = result.getUpdateInfoDO();
        if (updateInfo == null) {
            return;
        }

        IUpdateService service = updateService();
        if (service == null) {
            return;
        }

        RespCode respCode = service.addUpdateInfo(updateInfo);
        if (respCode != RespCode.TRC_OK) {
            log.error("add update failed updateInfo={}", updateInfo);
        }
    }
}

