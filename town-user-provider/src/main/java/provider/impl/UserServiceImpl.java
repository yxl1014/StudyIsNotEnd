package provider.impl;

import entity.TokenInfoDO;
import entity.UserInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.IUserService;
import util.*;

import java.util.Date;
import java.util.Random;

@DubboService(timeout = 300000, retries = 0)
public class UserServiceImpl extends AbstractRpcService implements IUserService {

    @DubboReference
    public IDaoService daoService;

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

            UserInfoDO user = daoService.selectById(msg.getUserTel());
            if (user == null) {
                return BizResult.error(RespCode.TRC_USER_NOT_EXIST);
            }

            if (!user.getUserPwd().equals(msg.getUserPwd())) {
                return BizResult.error(RespCode.TRC_PASSWORD_ERR);
            }

            UserInfo proto = daoService.toProto(user);

            if (proto.getFlagType() == TUserFlagType.TUFT_BAN){
                // TODO TRC_USER_IS_BAN
                return BizResult.error(RespCode.TRC_ERR);
            }

            TokenInfoDO tokenInfo = new TokenInfoDO(
                    proto.getUserTel(),
                    RandomUtil.RandomCode(6),
                    System.currentTimeMillis(),
                    proto.getUserPower(),
                    proto.getFlagType()
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

            if (daoService.selectById(userInfoDO.getUserTel()) != null) {
                return BizResult.error(RespCode.TRC_USER_EXIST);
            }

            userInfoDO.setUserCreateTime(System.currentTimeMillis());
            int insert = daoService.insert(userInfoDO);
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
            TUserPower userPower = UserContext.getUserPower();
            if (userPower != TUserPower.TUP_CGM)
            {
                // TODO TRC_USER_POWER_NOT_ENOUGH
                return BizResult.error(RespCode.TRC_ERR);
            }

            UserInfoDO userInfoDO = daoService.toDO(msg.getUserInfo());
            if (userInfoDO.isEmpty())
            {
                // TODO TRC_PARAM_NULL
                return BizResult.error(RespCode.TRC_ERR);
            }

            // 判断存再不存在
            UserInfoDO old = daoService.selectById(userInfoDO.getUserTel());
            if (old == null)
            {
                return BizResult.error(RespCode.TRC_USER_NOT_EXIST);
            }

            // 更新DB
            int update = daoService.update(userInfoDO);
            if (update <= 0)
            {
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            // 如果修改了就让重登，不管是啥暴力一点
            boolean b = daoService.redis_delete(ConstValue.Redis_Prefix_Token + userInfoDO.getUserTel());
            if (!b)
            {
                // TODO TRC_REDIS_ERROR
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            UpdateUserInfoRsp resp = UpdateUserInfoRsp.newBuilder().build();
            return BizResult.ok(resp);
        });
    }
}
