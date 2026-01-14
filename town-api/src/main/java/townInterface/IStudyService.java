package townInterface;

import po.CreateStudyReq;
import po.ResponseMsg;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/14 16:01
 **/


public interface IStudyService {
    ResponseMsg createStudy(String token, CreateStudyReq msg);
}
