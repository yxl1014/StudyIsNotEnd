package townInterface;

import po.CreateQuestionReq;
import po.ListQuestionReq;
import po.ResponseMsg;
import po.UpdateQuestionReq;

/**
 * @author Administrator
 * @Package : townInterface
 * @Create on : 2026/1/12 20:19
 **/


public interface IQuestionService {
    ResponseMsg createQuestion(String token, CreateQuestionReq msg);

    ResponseMsg updateQuestion(String token, UpdateQuestionReq msg);

    ResponseMsg listQuestion(String token, ListQuestionReq msg);
}
