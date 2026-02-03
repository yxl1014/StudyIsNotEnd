package provider.impl;

import entity.PeopleInfoDO;
import entity.PeopleUpdateApplyDO;
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
import townInterface.IPeopleService;
import townInterface.IUpdateService;
import util.TimeUtil;

/**
 * @author Administrator
 * @Package : provider.impl
 * @Create on : 2026/1/28 14:34
 **/


@DubboService(timeout = 10000, retries = 0)
public class PeopleServiceImpl extends AbstractRpcService implements IPeopleService {
    private static final Logger log = LoggerFactory.getLogger(PeopleServiceImpl.class);
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
    public ResponseMsg createPeople(String token, CreatePeopleReq msg) {
        return execute(MsgType.TMT_CreatePeopleRsp, token, () -> {
            CreatePeopleRsp.Builder builder = CreatePeopleRsp.newBuilder();
            int index = 0;
            for (PeopleInfo peopleInfo : msg.getInfosList()) {
                PeopleInfoDO infoDO = daoService.toDO(peopleInfo);
                if (infoDO.getPeopleCardId().isEmpty()) {
                    builder.addErr(
                            ErrInfo.newBuilder()
                                    .setIndex(index++)
                                    .setECode(RespCode.TRC_PEOPLE_CARD_IS_NULL)
                    );
                    continue;
                }

                PeopleInfoDO isExist = daoService.people_selectById(infoDO.getPeopleCardId());
                if (isExist != null) {
                    builder.addErr(
                            ErrInfo.newBuilder()
                                    .setIndex(index++)
                                    .setECode(RespCode.TRC_PEOPLE_CARD_IS_EXIST)
                    );
                    continue;
                }

                int insert = daoService.people_insert(infoDO);
                if (insert <= 0) {
                    builder.addErr(
                            ErrInfo.newBuilder()
                                    .setIndex(index++)
                                    .setECode(RespCode.TRC_DB_ERROR)
                    );
                    continue;
                }

                index++;
            }

            CreatePeopleRsp rsp = builder.build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg updatePeople(String token, UpdatePeopleReq msg) {
        return execute(MsgType.TMT_UpdatePeopleRsp, token, () -> {
            PeopleInfoDO infoDO = daoService.toDO(msg.getInfos());
            if (infoDO.getPeopleCardId().isEmpty()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            PeopleInfoDO isExist = daoService.people_selectById(infoDO.getPeopleCardId());
            if (isExist == null) {
                return BizResult.error(RespCode.TRC_PEOPLE_CARD_IS_NOT_EXIST);
            }

            if (msg.getIsDel()) {
                int del = daoService.people_delete(infoDO.getPeopleCardId());
                if (del <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
            } else {
                int update = daoService.people_update(infoDO);
                if (update <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
            }

            UpdatePeopleRsp rsp = UpdatePeopleRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg listPeopleInfo(String token, ListPeopleInfoReq msg) {
        return execute(MsgType.TMT_ListPeopleInfoRsp, token, () -> {
            ListPeopleInfoRsp.Builder builder = ListPeopleInfoRsp.newBuilder();

            TUserPower userPower = UserContext.getUserPower();
            if (userPower == TUserPower.TUP_CM) {
                if (msg.getCardId().isEmpty()) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                PeopleInfoDO peopleInfoDO = daoService.people_selectById(msg.getCardId());
                if (peopleInfoDO == null) {
                    return BizResult.error(RespCode.TRC_PEOPLE_CARD_IS_NOT_EXIST);
                }
                builder.addInfos(daoService.toProto(peopleInfoDO));
            } else {
                if (msg.getCardId().isEmpty()) {
                    if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                        return BizResult.error(RespCode.TRC_PARAM_NULL);
                    }
                    for (PeopleInfoDO peopleInfoDO : daoService.selectAll(msg.getPage(), msg.getSize())) {
                        builder.addInfos(daoService.toProto(peopleInfoDO));
                    }
                } else {
                    if (msg.getCardId().isEmpty()) {
                        return BizResult.error(RespCode.TRC_PARAM_NULL);
                    }
                    PeopleInfoDO peopleInfoDO = daoService.people_selectById(msg.getCardId());
                    if (peopleInfoDO == null) {
                        return BizResult.error(RespCode.TRC_PEOPLE_CARD_IS_NOT_EXIST);
                    }
                    builder.addInfos(daoService.toProto(peopleInfoDO));
                }
            }

            return BizResult.ok(builder.build());
        });
    }

    @Override
    public ResponseMsg createPeopleUpdateApply(String token, CreatePeopleUpdateApplyReq msg) {
        return execute(MsgType.TMT_CreatePeopleUpdateApplyRsp, token, ()->{
            if (!msg.hasApply()){
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }
            PeopleUpdateApplyDO applyDO = daoService.toDO(msg.getApply());
            if (applyDO.getNewPeople() == null){
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            int userTel = UserContext.getUserTel();
            long nowTime = TimeUtil.nowMillis();
            applyDO.setApplyUserId(userTel);
            applyDO.setApplyCreateTime(nowTime);

            int insert = daoService.apply_insert(applyDO);
            if (insert <= 0){
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            CreatePeopleUpdateApplyRsp rsp = CreatePeopleUpdateApplyRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg listPeopleUpdateApply(String token, ListPeopleUpdateApplyReq msg) {
        return execute(MsgType.TMT_ListPeopleUpdateApplyRsp, token, ()->{
            ListPeopleUpdateApplyRsp.Builder builder = ListPeopleUpdateApplyRsp.newBuilder();
            TUserPower userPower = UserContext.getUserPower();
            if (msg.hasApplyId()){
                PeopleUpdateApplyDO updateApplyDO = daoService.apply_selectById(msg.getApplyId());
                if (updateApplyDO == null){
                    return BizResult.error(RespCode.TRC_APPLY_NOT_FOUND);
                }
                if (userPower.equals(TUserPower.TUP_CM)){
                    if (updateApplyDO.getApplyUserId() != UserContext.getUserTel()){
                        return BizResult.error(RespCode.TRC_USER_POWER_NOT_ENOUGH);
                    }
                }
                builder.addApply(daoService.toProto(updateApplyDO));
            }
            else {
                if (!msg.hasPage() || !msg.hasSize() || msg.getPage() == 0 || msg.getSize() == 0){
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                if (userPower.equals(TUserPower.TUP_CM)) {
                    for (PeopleUpdateApplyDO applyDO : daoService.apply_selectAllByUserTel(msg.getPage(), msg.getSize(), UserContext.getUserTel())) {
                        builder.addApply(daoService.toProto(applyDO));
                    }
                }
                else {
                    for (PeopleUpdateApplyDO applyDO : daoService.apply_selectAll(msg.getPage(), msg.getSize())) {
                        builder.addApply(daoService.toProto(applyDO));
                    }
                }
            }

            return BizResult.ok(builder.build());
        });
    }

    @Override
    public ResponseMsg delPeopleUpdateApply(String token, DelPeopleUpdateApplyReq msg) {
        return execute(MsgType.TMT_DelPeopleUpdateApplyRsp, token, ()->{
            if (!msg.hasApplyId()){
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }
            PeopleUpdateApplyDO updateApplyDO = daoService.apply_selectById(msg.getApplyId());
            if (updateApplyDO == null){
                return BizResult.error(RespCode.TRC_APPLY_NOT_FOUND);
            }
            int del = daoService.apply_delete(msg.getApplyId());
            if (del <= 0){
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            int userTel = UserContext.getUserTel();
            long time = TimeUtil.nowMillis();
            String userName = UserContext.getUserName();
            UpdateInfoDO updateInfoDO = new UpdateInfoDO();
            updateInfoDO.setInfoId(msg.getApplyId());
            updateInfoDO.setBeforeMsg(daoService.toProto(updateInfoDO).toByteArray());
            updateInfoDO.setUpdateTime(time);
            updateInfoDO.setUpdateUserTel(userTel);
            updateInfoDO.setUpdateName(userName);

            DelPeopleUpdateApplyRsp rsp = DelPeopleUpdateApplyRsp.newBuilder().build();
            return BizResult.ok(rsp, updateInfoDO);
        });
    }
}
