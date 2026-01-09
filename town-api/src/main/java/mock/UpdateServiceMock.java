package mock;

import entity.UpdateInfoDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.RespCode;
import townInterface.IUpdateService;

/**
 * @author Administrator
 * @Package : provider.mock
 * @Create on : 2026/1/9 14:49
 **/

/// 如果Update未启动则执行这里的
public class UpdateServiceMock implements IUpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateServiceMock.class);
    @Override
    public RespCode addUpdateInfo(UpdateInfoDO updateInfoDO) {
        log.warn("update micro not enable, updateInfoDO insert failed, updateInfoDO : {}", updateInfoDO.toString());
        // 什么都不做，直接返回成功
        return RespCode.TRC_OK;
    }
}
