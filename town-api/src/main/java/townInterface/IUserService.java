package townInterface;

import entity.NotifyUserInfoDO;
import po.*;

public interface IUserService {
    String getUserName();

    ResponseMsg login(LoginReq msg);

    ResponseMsg register(RegisterReq msg);

    ResponseMsg updateUserInfo(String token, UpdateUserInfoReq msg);

    RespCode AddNotifyUserInfo(NotifyUserInfoDO notifyUserInfoDO);

    ResponseMsg listNotifyUserInfo(String token, ListNotifyUserInfoReq msg);

    ResponseMsg listUserInfo(String token, ListUserInfoReq msg);
}
