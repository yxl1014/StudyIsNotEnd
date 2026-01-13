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

    public ResponseMsg createNotice(String token, CreateNoticeReq msg) {
        return serviceManager.noticeService.createNotice(token, msg);
    }

    public ResponseMsg updateNotice(String token, UpdateNoticeReq msg) {
        return serviceManager.noticeService.updateNotice(token, msg);
    }

    public ResponseMsg listNotice(ListNoticeReq msg) {
        return serviceManager.noticeService.listNotice(msg);
    }

    public ResponseMsg setNoticeRead(String token, SetNoticeReadReq msg) {
        return serviceManager.noticeService.setNoticeRead(token, msg);
    }

    public ResponseMsg listNoticeRead(String token, ListNoticeReadReq msg) {
        return serviceManager.noticeService.listNoticeRead(token, msg);
    }

    public ResponseMsg listUpdateInfo(String token, ListUpdateInfoReq msg) {
        return serviceManager.updateService.listUpdateInfo(token, msg);
    }

    public ResponseMsg createQuestion(String token, CreateQuestionReq msg) {
        return serviceManager.questionService.createQuestion(token, msg);
    }

    public ResponseMsg updateQuestion(String token, UpdateQuestionReq msg) {
        return serviceManager.questionService.updateQuestion(token, msg);
    }

    public ResponseMsg listQuestion(String token, ListQuestionReq msg) {
        return serviceManager.questionService.listQuestion(token, msg);
    }
}
