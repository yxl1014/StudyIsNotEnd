package provider;

import org.apache.dubbo.config.annotation.DubboService;
import townInterface.IUserService;

import java.util.Random;

@DubboService
public class UserServiceImpl implements IUserService {
    @Override
    public String getUserName() {
        Random random = new Random();
        StringBuilder userName = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            userName.append(random.nextInt(10));
        }
        return userName.toString();
    }
}
