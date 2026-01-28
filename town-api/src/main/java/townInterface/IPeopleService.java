package townInterface;

import po.CreatePeopleReq;
import po.ListPeopleInfoReq;
import po.ResponseMsg;
import po.UpdatePeopleReq;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/28 14:32
 **/


public interface IPeopleService {
    ResponseMsg createPeople(String token, CreatePeopleReq msg);

    ResponseMsg updatePeople(String token, UpdatePeopleReq msg);

    ResponseMsg listPeopleInfo(String token, ListPeopleInfoReq msg);
}
