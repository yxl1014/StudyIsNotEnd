package townInterface;

import entity.UpdateInfoDO;
import po.RespCode;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/9 10:41
 **/

public interface IUpdateService {
    RespCode addUpdateInfo(UpdateInfoDO updateInfoDO);
}
