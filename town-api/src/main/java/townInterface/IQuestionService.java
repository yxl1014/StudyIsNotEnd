package townInterface;

import po.CreateQuestionReq;
import po.ResponseMsg;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/12 20:19
 **/


public interface IQuestionService {
    ResponseMsg createQuestion(String token, CreateQuestionReq msg);
}
