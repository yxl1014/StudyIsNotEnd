package provider.impl;

import entity.StudyInfoDO;
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
}
