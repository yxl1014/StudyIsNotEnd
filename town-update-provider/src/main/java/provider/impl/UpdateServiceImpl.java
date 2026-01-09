package provider.impl;

import entity.UpdateInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.RespCode;
import serviceEntity.AbstractRpcService;
import townInterface.IDaoService;
import townInterface.IUpdateService;

/**
 * @author Administrator
 * @Package : provider
 * @Create on : 2026/1/9 10:39
 **/


@DubboService(timeout = 10000, retries = 0)
public class UpdateServiceImpl extends AbstractRpcService implements IUpdateService {

    @DubboReference
    public IDaoService daoService;

    @Override
    public IUpdateService updateService() {
        return null;
    }

    @Override
    public RespCode addUpdateInfo(UpdateInfoDO updateInfoDO) {
        int insert = daoService.update_insert(updateInfoDO);
        if (insert <= 0){
            return RespCode.TRC_ERR;
        }
        return RespCode.TRC_OK;
    }
}
