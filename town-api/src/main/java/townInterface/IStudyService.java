package townInterface;

import po.*;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/14 16:01
 **/


public interface IStudyService {
    ResponseMsg createStudy(String token, CreateStudyReq msg);

    ResponseMsg updateStudy(String token, UpdateStudyReq msg);

    ResponseMsg listStudy(String token, ListStudyReq msg);

    ResponseMsg starStudy(String token, StarStudyReq msg);

    ResponseMsg listUserStarStudy(String token, ListUserStarStudyReq msg);
}
