package demo.transfer;


import demo.manager.ServiceManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import po.*;

@Component
public class RequestTransfer {

    @Resource
    private ServiceManager serviceManager;

    public ResponseMsg login(LoginReq msg) {
        return serviceManager.userService.login(msg);
    }

    public ResponseMsg register(RegisterReq msg) {
        return serviceManager.userService.register(msg);
    }

    public ResponseMsg updateUserInfo(String token, UpdateUserInfoReq msg) {
        return serviceManager.userService.updateUserInfo(token, msg);
    }
}
