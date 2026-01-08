package townInterface;

import po.CreateNoticeReq;
import po.ResponseMsg;

public interface INoticeService {
    ResponseMsg createNotice(String token, CreateNoticeReq msg);
}
