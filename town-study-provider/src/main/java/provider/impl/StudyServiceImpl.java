package provider.impl;

import entity.StudyInfoDO;
import entity.UpdateInfoDO;
import entity.UserStarStudyInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.IStudyService;
import townInterface.IUpdateService;
import util.TimeUtil;

/**
 * @author Administrator
 * @Package : provider.impl
 * @Create on : 2026/1/14 16:01
 **/


@DubboService(timeout = 10000, retries = 0)
public class StudyServiceImpl extends AbstractRpcService implements IStudyService {

    private static final Logger log = LoggerFactory.getLogger(StudyServiceImpl.class);
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
    public ResponseMsg createStudy(String token, CreateStudyReq msg) {
        return execute(MsgType.TMT_CreateStudyRsp, token, () -> {
            if (!msg.hasStudyInfo()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            StudyInfoDO studyInfoDO = daoService.toDO(msg.getStudyInfo());
            if (studyInfoDO.getStudyTitle().isEmpty()
                    || studyInfoDO.getStudyTip().isEmpty()
                    || studyInfoDO.getStudyContent().isEmpty()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            long nowMillis = TimeUtil.nowMillis();
            studyInfoDO.setStudyId(null);
            studyInfoDO.setStudyCreateTime(nowMillis);

            int insert = daoService.study_insert(studyInfoDO);
            if (insert <= 0){
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            StudyInfoDO insertStudy = daoService.study_selectByCreateTime(nowMillis);

            UpdateInfoDO update = new UpdateInfoDO();
            update.setInfoId(insertStudy.getStudyId());
            update.setInfoType(TUpdateInfoType.TUIT_STUDY_VALUE);
            update.setAfterMsg(daoService.toProto(insertStudy).toByteArray());
            update.setUpdateTime(nowMillis);
            update.setUpdateUserTel(UserContext.getUserTel());
            update.setUpdateName(UserContext.getUserName());

            CreateStudyRsp rsp = CreateStudyRsp.newBuilder().build();
            return BizResult.ok(rsp, update);
        });
    }

    @Override
    public ResponseMsg updateStudy(String token, UpdateStudyReq msg) {
        return execute(MsgType.TMT_UpdateStudyRsp, token, ()->{
            StudyInfoDO studyInfoDO = daoService.toDO(msg.getStudyInfo());
            if (studyInfoDO.getStudyId() == null || studyInfoDO.getStudyId() <= 0) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            StudyInfoDO oldStudyInfo = daoService.study_selectById(studyInfoDO.getStudyId());
            if (oldStudyInfo == null) {
                return BizResult.error(RespCode.TRC_STUDY_NOT_EXIST);
            }

            StudyInfoDO newStudy = null;
            if (msg.getIsDel()) {
                int delete = daoService.study_delete(studyInfoDO.getStudyId());
                if (delete <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
            } else {
                studyInfoDO.setStudyCreateTime(null);
                studyInfoDO.setReadCount(null);

                // 更新DB
                int update = daoService.study_update(studyInfoDO);
                if (update <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
                newStudy = daoService.study_selectById(studyInfoDO.getStudyId());
            }


            long nowMillis = TimeUtil.nowMillis();
            int writerTel = UserContext.getUserTel();
            String writerName = UserContext.getUserName();

            UpdateInfoDO update = new UpdateInfoDO();
            update.setInfoId(studyInfoDO.getStudyId());
            update.setInfoType(TUpdateInfoType.TUIT_STUDY_VALUE);
            update.setBeforeMsg(daoService.toProto(oldStudyInfo).toByteArray());
            update.setAfterMsg(daoService.toProto(newStudy).toByteArray());
            update.setUpdateTime(nowMillis);
            update.setUpdateUserTel(writerTel);
            update.setUpdateName(writerName);

            UpdateStudyRsp rsp = UpdateStudyRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg listStudy(String token, ListStudyReq msg) {
        return execute(MsgType.TMT_ListStudyRsp, token, ()->{
            int studyId = msg.getStudyId();
            ListStudyRsp.Builder builder = ListStudyRsp.newBuilder();
            /// 代表查询所有
            if (studyId == 0) {
                if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                for (StudyInfoDO studyInfoDO : daoService.study_selectAll(msg.getPage(), msg.getSize())) {
                    builder.addInfos(daoService.toProto(studyInfoDO));
                }
            } else {
                StudyInfoDO studyInfoDO = daoService.study_selectById(studyId);
                if (studyInfoDO == null) {
                    return BizResult.error(RespCode.TRC_STUDY_NOT_EXIST);
                }
                StudyInfoDO addCountInfo = new StudyInfoDO();
                addCountInfo.setStudyId(studyId);
                addCountInfo.setReadCount(studyInfoDO.getReadCount() + 1);
                int update = daoService.study_update(addCountInfo);
                if (update <= 0){
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
                builder.addInfos(daoService.toProto(studyInfoDO));
            }

            ListStudyRsp rsp = builder.build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg starStudy(String token, StarStudyReq msg) {
        return execute(MsgType.TMT_StarStudyRsp, token, ()->{
            int studyId = msg.getStudyId();

            int userTel = UserContext.getUserTel();

            UserStarStudyInfoDO userStarStudyInfoDO = daoService.star_selectByIdAndTel(studyId, userTel);

            StarStudyRsp rsp = StarStudyRsp.newBuilder().build();
            if (msg.getIsCancel())
            {
                if (userStarStudyInfoDO != null) {
                    int del = daoService.star_delete(userStarStudyInfoDO.getId());
                    if (del <= 0){
                        return BizResult.error(RespCode.TRC_DB_ERROR);
                    }
                }
                return BizResult.ok(rsp);
            }

            StudyInfoDO studyInfoDO = daoService.study_selectById(studyId);
            if (studyInfoDO == null) {
                return BizResult.error(RespCode.TRC_STUDY_NOT_EXIST);
            }

            if (userStarStudyInfoDO != null){
                return BizResult.error(RespCode.TRC_STUDY_IS_STAR);
            }

            UserStarStudyInfoDO starInfo = new UserStarStudyInfoDO();
            starInfo.setStudyId(studyId);
            starInfo.setUserTel(UserContext.getUserTel());
            int insert = daoService.star_insert(starInfo);
            if (insert <= 0){
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg listUserStarStudy(String token, ListUserStarStudyReq msg) {
        return execute(MsgType.TMT_ListUserStarStudyRsp, token, ()->{
            ListUserStarStudyRsp.Builder builder = ListUserStarStudyRsp.newBuilder();

            if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }
            for (UserStarStudyInfoDO userStarStudyInfoDO : daoService.star_selectByUserTel(UserContext.getUserTel(), msg.getPage(), msg.getSize())) {
                StudyInfoDO studyInfoDO = daoService.study_selectById(userStarStudyInfoDO.getStudyId());
                if (studyInfoDO == null){
                    continue;
                }
                builder.addInfos(daoService.toProto(studyInfoDO));
            }

            return BizResult.ok(builder.build());
        });
    }
}
