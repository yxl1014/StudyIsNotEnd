package provider.impl;

import entity.TokenInfoDO;
import entity.UserInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.*;
import townInterface.IDaoService;
import townInterface.IUserService;
import util.ConstValue;
import util.JwtUtil;
import util.RandomUtil;

import java.util.Date;
import java.util.Random;

@DubboService(timeout = 300000, retries = 0)
public class UserServiceImpl implements IUserService {

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
        ResponseMsg.Builder builder = ResponseMsg.newBuilder().setMsgType(MsgType.TMT_LoginRsp);
        LoginRsp.Builder resp = LoginRsp.newBuilder();
        UserInfoDO userInfoDO = daoService.selectById(msg.getUserTel());
        if (userInfoDO == null) {
            builder.setErrCode(RespCode.TRC_USER_NOT_EXIST);
        } else {
            if (!userInfoDO.getUserPwd().equals(msg.getUserPwd())) {
                builder.setErrCode(RespCode.TRC_PASSWORD_ERR);
            } else {
                // 登陆成功
                UserInfo proto = daoService.toProto(userInfoDO);
                // 1、生成token
                TokenInfoDO tokenInfo = new TokenInfoDO(proto.getUserTel(), RandomUtil.RandomCode(6), new Date().getTime());
                String token = JwtUtil.generateToken("login", tokenInfo);

                // 2、token存redis
                daoService.redis_set(ConstValue.Redis_Prefix_Token + proto.getUserTel(), token, ConstValue.Redis_Token_Expire_Sec);

                // 3、设置返回值
                builder.setErrCode(RespCode.TRC_OK);
                resp.setUserInfo(proto).setToken(token);
            }
        }
        builder.setMsg(resp.build().toByteString());
        return builder.build();
    }


    @Override
    public ResponseMsg register(RegisterReq msg) {
        ResponseMsg.Builder builder = ResponseMsg.newBuilder().setMsgType(MsgType.TMT_RegisterRsp);
        LoginRsp.Builder resp = LoginRsp.newBuilder();

        UserInfoDO userInfoDO = daoService.toDO(msg.getUserInfo());
        // 1、判断有没有存在的
        UserInfoDO existUser = daoService.selectById(userInfoDO.getUserTel());
        if (userInfoDO.getUserTel() == 0 || userInfoDO.getUserPwd().isEmpty() || userInfoDO.getUserName().isEmpty()) {
            //TODO TRC_PARAM_NULL
            builder.setErrCode(RespCode.TRC_ERR);
        } else if (existUser != null) {
            builder.setErrCode(RespCode.TRC_USER_EXIST);
        } else {
            // 2、设置创建时间
            userInfoDO.setUserCreateTime(new Date().getTime());
            int insert = daoService.insert(userInfoDO);
            if (insert <= 0) {
                builder.setErrCode(RespCode.TRC_DB_ERROR);
            } else {
                builder.setErrCode(RespCode.TRC_OK);
            }
        }

        builder.setMsg(resp.build().toByteString());
        return builder.build();
    }

    @Override
    public ResponseMsg updateUserInfo(UpdateUserInfoReq msg) {
        ResponseMsg.Builder builder = ResponseMsg.newBuilder().setMsgType(MsgType.TMT_UpdateUserInfoRsp);
        UpdateUserInfoRsp.Builder resp = UpdateUserInfoRsp.newBuilder();




        builder.setMsg(resp.build().toByteString());
        return builder.build();
    }
}
