package townInterface;

import po.LoginReq;
import po.LoginRsp;
import po.ResponseMsg;
import po.UserInfo;

public interface IUserService {
    String getUserName();

    ResponseMsg login(LoginReq msg);
}
