package demo.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import townInterface.IUserService;


// 服务集合中心
@Component
public class ServiceManager {
    @DubboReference
    public IUserService userService;
}
