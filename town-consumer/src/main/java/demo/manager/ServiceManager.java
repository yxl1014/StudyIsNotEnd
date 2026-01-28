package demo.manager;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import townInterface.*;


// 服务集合中心
@Component
public class ServiceManager {
    @DubboReference
    public IUserService userService;

    @DubboReference
    public IDaoService daoService;

    @DubboReference
    public INoticeService noticeService;

    @DubboReference
    public IUpdateService updateService;

    @DubboReference
    public IQuestionService questionService;

    @DubboReference
    public IStudyService studyService;

    @DubboReference
    public IPeopleService peopleService;
}
