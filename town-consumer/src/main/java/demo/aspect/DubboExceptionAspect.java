package demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import po.RespCode;
import util.CommonEntityBuilder;

/**
 * @author Administrator
 * @Package : demo.aspect
 * @Create on : 2026/1/12 20:28
 **/


@Aspect
@Component
public class DubboExceptionAspect {

    private static final Logger log = LoggerFactory.getLogger(DubboExceptionAspect.class);

    @Around("execution(public po.ResponseMsg demo.transfer.RequestTransfer.*(..))")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (org.apache.dubbo.rpc.RpcException e) {
            log.error("service not found err:{}", e.toString());
            return CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_SERVICE_UNAVAILABLE);
        } catch (Throwable t) {
            log.error("server err:{}", t.toString());
            return CommonEntityBuilder.buildNoBodyResp(RespCode.TRC_ERR);
        }
    }
}

