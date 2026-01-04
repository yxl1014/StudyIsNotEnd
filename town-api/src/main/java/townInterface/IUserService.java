package townInterface;

import po.*;

public interface IUserService {
    String getUserName();

    ResponseMsg login(LoginReq msg);

    ResponseMsg register(RegisterReq msg);
}
