package provider.impl;

import entity.PeopleInfoDO;
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
}
