package provider.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.CreateQuestionReq;
import po.CreateQuestionRsp;
import po.MsgType;
import po.ResponseMsg;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import townInterface.IDaoService;
import townInterface.IQuestionService;
import townInterface.IUpdateService;

/**
 * @author Administrator
 * @Package : provider.impl
 * @Create on : 2026/1/12 20:18
 **/

@DubboService(timeout = 10000, retries = 0)
public class QuestionServiceImpl extends AbstractRpcService implements IQuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    @DubboReference
    public IDaoService daoService;

    @DubboReference(
            check = false,     // Update 服务不启动，Notice 服务也能启动
            mock = "mock.UpdateServiceMock",     // 不可用时自动走 mock
            timeout = 1000,    // 防止阻塞
            retries = 0        // 不要重试
    )
    public IUpdateService updateService;

    @Override
    public IUpdateService updateService() {
        return updateService;
    }

    @Override
    public ResponseMsg createQuestion(String token, CreateQuestionReq msg) {
        return execute(MsgType.TMT_CreateQuestionRsp, token, () -> {
            // TODO
            CreateQuestionRsp rsp = CreateQuestionRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }
}
