package provider.impl;

import entity.NotifyUserInfoDO;
import entity.TokenInfoDO;
import entity.UpdateInfoDO;
import entity.UserInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.IUpdateService;
import townInterface.IUserService;
import util.ConstValue;
import util.JwtUtil;
import util.RandomUtil;
import util.TimeUtil;

import java.util.Random;

@DubboService(timeout = 10000, retries = 0)
public class UserServiceImpl extends AbstractRpcService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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
    public String getUserName() {
        Random random = new Random();
        StringBuilder userName = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            userName.append(random.nextInt(10));
        }
        return userName.toString();
    }

    @Override
    public ResponseMsg login(LoginReq msg) {
        return execute(MsgType.TMT_LoginRsp, null, () -> {

            UserInfoDO user = daoService.user_selectById(msg.getUserTel());
            if (user == null) {
                return BizResult.error(RespCode.TRC_USER_NOT_EXIST);
            }

            if (!user.getUserPwd().equals(msg.getUserPwd())) {
                return BizResult.error(RespCode.TRC_PASSWORD_ERR);
            }

            UserInfo proto = daoService.toProto(user);

            if (proto.getFlagType() == TUserFlagType.TUFT_BAN){
                return BizResult.error(RespCode.TRC_USER_IS_BAN);
            }

            TokenInfoDO tokenInfo = new TokenInfoDO(
                    proto.getUserTel(),
                    RandomUtil.RandomCode(6),
                    System.currentTimeMillis(),
                    proto.getUserPower(),
                    proto.getFlagType(),
                    proto.getUserName()
            );

            String token = JwtUtil.generateToken("login", tokenInfo);
            daoService.redis_set(
                    ConstValue.Redis_Prefix_Token + proto.getUserTel(),
                    tokenInfo,
                    ConstValue.Redis_Token_Expire_Sec
            );

            LoginRsp rsp = LoginRsp.newBuilder()
                    .setUserInfo(proto)
                    .setToken(token)
                    .build();

            return BizResult.ok(rsp);
        });
    }


    @Override
    public ResponseMsg register(RegisterReq msg) {
        return execute(MsgType.TMT_RegisterRsp, null, () -> {

            UserInfoDO userInfoDO = daoService.toDO(msg.getUserInfo());

            if (userInfoDO.getUserTel() == 0
                    || userInfoDO.getUserPwd().isEmpty()
                    || userInfoDO.getUserName().isEmpty()) {
                return BizResult.error(RespCode.TRC_ERR);
            }

            if (daoService.user_selectById(userInfoDO.getUserTel()) != null) {
                return BizResult.error(RespCode.TRC_USER_EXIST);
            }

            userInfoDO.setUserCreateTime(TimeUtil.nowMillis());
            int insert = daoService.user_insert(userInfoDO);
            if (insert <= 0) {
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            RegisterRsp resp = RegisterRsp.newBuilder().build();
            return BizResult.ok(resp);
        });
    }

    @Override
    public ResponseMsg updateUserInfo(String token, UpdateUserInfoReq msg) {
        return execute(MsgType.TMT_UpdateUserInfoRsp, token, () -> {
            UserInfoDO userInfoDO = daoService.toDO(msg.getUserInfo());
            if (userInfoDO.isEmpty()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            // 判断存再不存在
            UserInfoDO old = daoService.user_selectById(userInfoDO.getUserTel());
            if (old == null) {
                return BizResult.error(RespCode.TRC_USER_NOT_EXIST);
            }

            UserInfoDO newInfo = null;
            if (msg.getIsDel()){
                int delete = daoService.user_delete(userInfoDO.getUserTel());
                if (delete <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
            }
            else {
                if (userInfoDO.otherNull()) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                // 更新DB
                int update = daoService.user_update(userInfoDO);
                if (update <= 0) {
                    return BizResult.error(RespCode.TRC_DB_ERROR);
                }
                newInfo = daoService.user_selectById(userInfoDO.getUserTel());
            }

            // 添加修改记录
            UpdateInfoDO update = new UpdateInfoDO();
            update.setInfoId(userInfoDO.getUserTel());
            update.setInfoType(TUpdateInfoType.TUIT_USER_VALUE);
            update.setBeforeMsg(daoService.toProto(old).toByteArray());
            update.setAfterMsg(newInfo !=null ? daoService.toProto(newInfo).toByteArray() : null);
            update.setUpdateTime(TimeUtil.nowMillis());
            update.setUpdateUserTel(UserContext.getUserTel());
            update.setUpdateName(UserContext.getUserName());

            // 如果修改了就让重登，不管是啥暴力一点
            Object o = daoService.redis_get(ConstValue.Redis_Prefix_Token + userInfoDO.getUserTel());
            if (o != null) {
                boolean b = daoService.redis_delete(ConstValue.Redis_Prefix_Token + userInfoDO.getUserTel());
                if (!b) {
                    return BizResult.error(RespCode.TRC_REDIS_ERROR);
                }
            }

            UpdateUserInfoRsp resp = UpdateUserInfoRsp.newBuilder().build();
            return BizResult.ok(resp, update);
        });
    }

    @Override
    public ResponseMsg listUserInfo(String token, ListUserInfoReq msg) {
        return execute(MsgType.TMT_ListUserInfoRsp, token, ()->{
            ListUserInfoRsp.Builder builder = ListUserInfoRsp.newBuilder();

            TUserPower userPower = UserContext.getUserPower();
            if (userPower.equals(TUserPower.TUP_CM)){
                UserInfoDO userInfoDO = daoService.user_selectById(UserContext.getUserTel());
                builder.addUserInfos(daoService.toProto(userInfoDO));
            }
            else {
                if (msg.hasUserTel()){
                    UserInfoDO userInfoDO = daoService.user_selectById(UserContext.getUserTel());
                    builder.addUserInfos(daoService.toProto(userInfoDO));
                }
                else if (msg.getPage() > 0 && msg.getSize() > 0) {
                    for (UserInfoDO userInfoDO : daoService.user_selectAll(msg.getPage(), msg.getSize())) {
                        builder.addUserInfos(daoService.toProto(userInfoDO));
                    }
                } else {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
            }

            return BizResult.ok(builder.build());
        });
    }

    @Override
    public RespCode AddNotifyUserInfo(NotifyUserInfoDO notifyUserInfoDO) {
        int insert = daoService.notify_insert(notifyUserInfoDO);
        if (insert <= 0){
            return RespCode.TRC_DB_ERROR;
        }
        return RespCode.TRC_OK;
    }

    @Override
    public ResponseMsg listNotifyUserInfo(String token, ListNotifyUserInfoReq msg) {
        return execute(MsgType.TMT_ListNotifyUserInfoRsp, token, () ->{
            ListNotifyUserInfoRsp.Builder builder = ListNotifyUserInfoRsp.newBuilder();
            for (NotifyUserInfoDO notifyUserInfoDO : daoService.notify_selectByUserId(UserContext.getUserTel())) {
                builder.addInfos(daoService.toProto(notifyUserInfoDO));
            }

            int deleteAll = daoService.notify_deleteAll();
            if (deleteAll <= 0){
                log.error("delete notify info all");
            }

            return BizResult.ok(builder.build());
        });
    }
}
