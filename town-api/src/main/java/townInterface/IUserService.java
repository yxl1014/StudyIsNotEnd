package townInterface;

import po.*;

public interface IUserService {
    String getUserName();

    ResponseMsg login(LoginReq msg);

    ResponseMsg register(RegisterReq msg);

    ResponseMsg updateUserInfo(String token, UpdateUserInfoReq msg);

    ResponseMsg listNotifyUserInfo(String token, ListNotifyUserInfoReq msg);
}
