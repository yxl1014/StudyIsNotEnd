package provider.impl;


import entity.NoticeInfoDO;
import entity.UpdateInfoDO;
import entity.UserReadNoticeInfoDO;
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

import java.util.List;

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
    public IUpdateService updateService() {
        return updateService;
    }

    @Override
    public ResponseMsg createNotice(String token, CreateNoticeReq msg) {
        return execute(MsgType.TMT_CreateNoticeRsp, token, () -> {
            if (!msg.hasNoticeInfo()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            NoticeInfo proto = msg.getNoticeInfo();
            if (!proto.hasNoticeTitle() || !proto.hasNoticeContext()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);

            }

            NoticeInfoDO noticeInfoDO = daoService.toDO(proto);

            long nowMillis = TimeUtil.nowMillis();
            long writerTel = UserContext.getUserTel();
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
            update.setInfoId((long)insertNotice.getNoticeId());
            update.setInfoType(TUpdateInfoType.TUIT_NOTICE_VALUE);
            update.setAfterMsg(daoService.toProto(insertNotice).toByteArray());
            update.setUpdateTime(nowMillis);
            update.setUpdateUserTel(writerTel);
            update.setUpdateName(writerName);

            CreateNoticeRsp rsp = CreateNoticeRsp.newBuilder().build();
            return BizResult.ok(rsp, update);
        });
    }

    @Override
    public ResponseMsg updateNotice(String token, UpdateNoticeReq msg) {
        return execute(MsgType.TMT_UpdateNoticeRsp, token, () -> {
            NoticeInfoDO noticeInfoDO = daoService.toDO(msg.getNoticeInfo());
            if (noticeInfoDO.isEmpty()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            NoticeInfoDO oldNotice = daoService.notice_selectById(noticeInfoDO.getNoticeId());
            if (oldNotice == null) {
                return BizResult.error(RespCode.TRC_NOTICE_NOT_EXIST);
            }

            NoticeInfoDO newNotice = null;
            if (msg.getIsDel()) {
                int delete = daoService.notice_delete(noticeInfoDO.getNoticeId());
                if (delete <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
            } else {
                if (noticeInfoDO.otherNull()) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }

                // 不让改发布时间、创建者
                noticeInfoDO.setNoticeCreateTime(null);
                noticeInfoDO.setWriterTel(null);
                noticeInfoDO.setWriterName(null);

                // 更新DB
                int update = daoService.notice_update(noticeInfoDO);
                if (update <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
                newNotice = daoService.notice_selectById(noticeInfoDO.getNoticeId());
            }


            long nowMillis = TimeUtil.nowMillis();
            long writerTel = UserContext.getUserTel();
            String writerName = UserContext.getUserName();
            UpdateInfoDO update = new UpdateInfoDO();
            update.setInfoId((long)msg.getNoticeInfo().getNoticeId());
            update.setInfoType(TUpdateInfoType.TUIT_NOTICE_VALUE);
            update.setBeforeMsg(msg.getNoticeInfo().toByteArray());
            update.setAfterMsg(newNotice != null ? daoService.toProto(newNotice).toByteArray() : null);
            update.setUpdateTime(nowMillis);
            update.setUpdateUserTel(writerTel);
            update.setUpdateName(writerName);

            UpdateNoticeRsp rsp = UpdateNoticeRsp.newBuilder().build();
            return BizResult.ok(rsp, update);
        });
    }


    @Override
    public ResponseMsg listNotice(ListNoticeReq msg) {
        return execute(MsgType.TMT_ListNoticeRsp, null, () -> {
            int noticeId = msg.getNoticeId();

            ListNoticeRsp.Builder builder = ListNoticeRsp.newBuilder();
            /// 代表查询所有
            if (noticeId == 0) {
                if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                for (NoticeInfoDO noticeInfoDO : daoService.notice_selectAll(msg.getPage(), msg.getSize())) {
                    builder.addInfos(daoService.toProto(noticeInfoDO));
                }
            } else {
                NoticeInfoDO noticeInfoDO = daoService.notice_selectById(noticeId);
                if (noticeInfoDO == null) {
                    return BizResult.error(RespCode.TRC_NOTICE_NOT_EXIST);
                }
                builder.addInfos(daoService.toProto(noticeInfoDO));
            }

            ListNoticeRsp rsp = builder.build();
            return BizResult.ok(rsp);
        });
    }


    @Override
    public ResponseMsg setNoticeRead(String token, SetNoticeReadReq msg) {
        return execute(MsgType.TMT_SetNoticeReadRsp, token, () ->{
            int noticeId = msg.getNoticeId();
            if (noticeId <= 0){
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            NoticeInfoDO noticeInfoDO = daoService.notice_selectById(noticeId);
            if (noticeInfoDO == null) {
                return BizResult.error(RespCode.TRC_NOTICE_NOT_EXIST);
            }

            if (!noticeInfoDO.getIsAcceptRead()) {
                return BizResult.error(RespCode.TRC_NOTICE_CAN_NOT_ACCEPT);
            }

            long userTel = UserContext.getUserTel();
            List<UserReadNoticeInfoDO> readList = daoService.read_selectByUserTelAndNoticeId(userTel, noticeId);
            if (!readList.isEmpty()) {
                return BizResult.error(RespCode.TRC_NOTICE_IS_ACCEPT);
            }

            UserReadNoticeInfoDO readDO = new UserReadNoticeInfoDO(userTel, noticeId, TimeUtil.nowMillis());
            int insert = daoService.read_insert(readDO);
            if (insert <= 0){
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            SetNoticeReadRsp rsp = SetNoticeReadRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg listNoticeRead(String token, ListNoticeReadReq msg) {
        return execute(MsgType.TMT_ListNoticeReadRsp, token, () ->{
            ListNoticeReadRsp.Builder builder = ListNoticeReadRsp.newBuilder();

            long userTel = UserContext.getUserTel();
            List<UserReadNoticeInfoDO> readList = daoService.read_selectByUserTel(userTel);
            for (UserReadNoticeInfoDO infoDO : readList)
            {
                builder.addNoticeList(infoDO.getNoticeId());
            }

            return BizResult.ok(builder.build());
        });
    }
}
