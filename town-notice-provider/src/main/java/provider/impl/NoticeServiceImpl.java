package provider.impl;


import entity.NoticeInfoDO;
import entity.UpdateInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.INoticeService;
import townInterface.IUpdateService;
import util.TimeUtil;

import java.util.Date;

@DubboService(timeout = 10000, retries = 0)
public class NoticeServiceImpl extends AbstractRpcService implements INoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeServiceImpl.class);
    @DubboReference
    public IDaoService daoService;

    @DubboReference(
            check = false,     // Update 服务不启动，Notice 服务也能启动
            mock = "mock.UpdateServiceMock",     // 不可用时自动走 mock
            timeout = 1000,    // 防止阻塞
            retries = 0        // 不要重试
    )
    public IUpdateService updateService;

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

            long nowMillis = TimeUtil.nowMillis();
            int writerTel = UserContext.getUserTel();
            String writerName = UserContext.getUserName();

            // 自增ID，把自己输入的ID置0
            noticeInfoDO.setNoticeId(null);
            noticeInfoDO.setNoticeCreateTime(nowMillis);
            noticeInfoDO.setWriterTel(writerTel);
            noticeInfoDO.setWriterName(writerName);

            int insert = daoService.notice_insert(noticeInfoDO);
            if (insert <= 0) {
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            // 获取刚刚创建的
            NoticeInfoDO insertNotice = daoService.notice_selectByWriterAndCreateTime(writerTel, nowMillis);

            UpdateInfoDO update = new UpdateInfoDO();
            update.setInfoId(insertNotice.getNoticeId());
            update.setInfoType(TUpdateInfoType.TUIT_NOTICE_VALUE);
            update.setAfterMsg(daoService.toProto(insertNotice).toByteArray());
            update.setUpdateTime(nowMillis);
            update.setUpdateUserTel(writerTel);
            update.setUpdateName(writerName);

            RespCode respCode = updateService.addUpdateInfo(update);
            if (respCode != RespCode.TRC_OK) {
                log.error("add update failed");
            }

            CreateNoticeRsp rsp = CreateNoticeRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }
}
