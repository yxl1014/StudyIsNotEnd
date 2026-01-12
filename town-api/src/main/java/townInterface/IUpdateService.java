package townInterface;

import entity.UpdateInfoDO;
import po.ListUpdateInfoReq;
import po.RespCode;
import po.ResponseMsg;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/9 10:41
 **/

public interface IUpdateService {
    RespCode addUpdateInfo(UpdateInfoDO updateInfoDO);

    ResponseMsg listUpdateInfo(String token, ListUpdateInfoReq msg);
}
