package townInterface;

import po.*;

public interface INoticeService {
    ResponseMsg createNotice(String token, CreateNoticeReq msg);

    ResponseMsg updateNotice(String token, UpdateNoticeReq msg);

    ResponseMsg listNotice(ListNoticeReq msg);

    ResponseMsg setNoticeRead(String token, SetNoticeReadReq msg);

    ResponseMsg listNoticeRead(String token, ListNoticeReadReq msg);
}
