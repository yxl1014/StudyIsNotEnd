package provider.impl;


import entity.NoticeInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.INoticeService;
import util.TimeUtil;

import java.util.Date;

@DubboService(timeout = 300000, retries = 0)
public class NoticeServiceImpl extends AbstractRpcService implements INoticeService {

    @DubboReference
    public IDaoService daoService;

    @Override
    public ResponseMsg createNotice(String token, CreateNoticeReq msg) {
        return execute(MsgType.TMT_CreateNoticeRsp, token, () -> {
            TUserPower userPower = UserContext.getUserPower();
            if (userPower != TUserPower.TUP_CGM) {
                return BizResult.error(RespCode.TRC_USER_POWER_NOT_ENOUGH);
            }

            if (!msg.hasNoticeInfo()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            NoticeInfo proto = msg.getNoticeInfo();
            if (!proto.hasNoticeTitle() || !proto.hasNoticeContext()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);

            }

            NoticeInfoDO noticeInfoDO = daoService.toDO(proto);

            // 自增ID，把自己输入的ID置0
            noticeInfoDO.setNoticeId(null);
            noticeInfoDO.setNoticeCreateTime(TimeUtil.nowMillis());
            noticeInfoDO.setWriterTel(UserContext.getUserTel());
            noticeInfoDO.setWriterName(UserContext.getUserName());

            int insert = daoService.notice_insert(noticeInfoDO);
            if (insert <= 0) {
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            CreateNoticeRsp rsp = CreateNoticeRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }
}
