package townInterface;

import po.*;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/28 14:32
 **/


public interface IPeopleService {
    ResponseMsg createPeople(String token, CreatePeopleReq msg);

    ResponseMsg updatePeople(String token, UpdatePeopleReq msg);

    ResponseMsg listPeopleInfo(String token, ListPeopleInfoReq msg);

    ResponseMsg createPeopleUpdateApply(String token, CreatePeopleUpdateApplyReq msg);

    ResponseMsg listPeopleUpdateApply(String token, ListPeopleUpdateApplyReq msg);

    ResponseMsg delPeopleUpdateApply(String token, DelPeopleUpdateApplyReq msg);
}
