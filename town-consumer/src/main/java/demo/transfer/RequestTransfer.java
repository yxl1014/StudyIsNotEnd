package demo.transfer;


import demo.manager.ServiceManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import po.LoginReq;
import po.LoginRsp;
import po.ResponseMsg;
import po.UserInfo;

@Component
public class RequestTransfer {

    @Resource
    private ServiceManager serviceManager;

    public ResponseMsg login(LoginReq msg) {
        return serviceManager.userService.login(msg);
    }
}
