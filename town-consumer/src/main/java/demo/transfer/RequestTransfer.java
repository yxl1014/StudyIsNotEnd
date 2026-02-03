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

    public ResponseMsg listNotifyUserInfo(String token, ListNotifyUserInfoReq msg) {
        return serviceManager.userService.listNotifyUserInfo(token, msg);
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

    public ResponseMsg createStudyReq(String token, CreateStudyReq msg) {
        return serviceManager.studyService.createStudy(token, msg);
    }

    public ResponseMsg updateStudy(String token, UpdateStudyReq msg) {
        return serviceManager.studyService.updateStudy(token, msg);
    }

    public ResponseMsg listStudy(String token, ListStudyReq msg) {
        return serviceManager.studyService.listStudy(token, msg);
    }

    public ResponseMsg starStudy(String token, StarStudyReq msg) {
        return serviceManager.studyService.starStudy(token, msg);
    }

    public ResponseMsg listUserStarStudy(String token, ListUserStarStudyReq msg) {
        return serviceManager.studyService.listUserStarStudy(token, msg);
    }

    public ResponseMsg createPeople(String token, CreatePeopleReq msg) {
        return serviceManager.peopleService.createPeople(token, msg);
    }

    public ResponseMsg updatePeople(String token, UpdatePeopleReq msg) {
        return serviceManager.peopleService.updatePeople(token, msg);
    }

    public ResponseMsg listPeopleInfo(String token, ListPeopleInfoReq msg) {
        return serviceManager.peopleService.listPeopleInfo(token, msg);
    }

    public ResponseMsg createPeopleUpdateApply(String token, CreatePeopleUpdateApplyReq msg) {
        return serviceManager.peopleService.createPeopleUpdateApply(token, msg);
    }

    public ResponseMsg listPeopleUpdateApply(String token, ListPeopleUpdateApplyReq msg) {
        return serviceManager.peopleService.listPeopleUpdateApply(token, msg);
    }

    public ResponseMsg delPeopleUpdateApply(String token, DelPeopleUpdateApplyReq msg) {
        return serviceManager.peopleService.delPeopleUpdateApply(token, msg);
    }
}
