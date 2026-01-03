package provider;

import entity.UserInfoDO;
import exception.BizException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.*;
import townInterface.IDaoService;
import townInterface.IRedisService;
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
            if (!msg.getUserPwd().equals(userInfoDO.getUserPwd())) {
                builder.setErrCode(RespCode.TRC_PASSWORD_ERR);
            } else {
                // 登陆成功
                UserInfo proto = daoService.toProto(userInfoDO);
                // 1、生成token
                TokenInfo tokenInfo = TokenInfo.newBuilder()
                        .setUserTel(proto.getUserTel())
                        .setRandomCode(RandomUtil.RandomCode(6))
                        .setCreateTime(new Date().getTime())
                        .build();
                String token = JwtUtil.generateToken("login", tokenInfo);

                // 2、token存redis
                IRedisService redisService = daoService.redisService();
                redisService.set("token", token, ConstValue.Redis_Token_Expire_Sec);

                // 3、设置返回值
                builder.setErrCode(RespCode.TRC_OK);
                resp.setUserInfo(proto).setToken(token);
            }
        }
        builder.setMsg(resp.build().toByteString());
        return builder.build();
    }
}
