package provider.impl;

import entity.UpdateInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
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
        if (insert <= 0) {
            return RespCode.TRC_ERR;
        }
        return RespCode.TRC_OK;
    }

    @Override
    public ResponseMsg listUpdateInfo(String token, ListUpdateInfoReq msg) {
        return execute(MsgType.TMT_ListUpdateInfoRsp, token, () -> {

            int updateId = msg.getUpdateId();
            ListUpdateInfoRsp.Builder builder = ListUpdateInfoRsp.newBuilder();

            /// 代表查询所有
            if (updateId == 0) {
                if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                    return BizResult.error(RespCode.TRC_PARAM_NULL);
                }
                for (UpdateInfoDO updateInfoDO : daoService.update_selectAll(msg.getPage(), msg.getSize())) {
                    builder.addInfos(daoService.toProto(updateInfoDO));
                }
            } else {
                UpdateInfoDO updateInfoDO = daoService.update_selectById(updateId);
                if (updateInfoDO == null) {
                    return BizResult.error(RespCode.TRC_NOTICE_NOT_EXIST);
                }
                builder.addInfos(daoService.toProto(updateInfoDO));
            }

            return BizResult.ok(builder.build());
        });
    }
}
