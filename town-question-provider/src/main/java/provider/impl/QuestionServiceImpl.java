package provider.impl;

import entity.QuestionInfoDO;
import entity.UpdateInfoDO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.*;
import serviceEntity.AbstractRpcService;
import serviceEntity.BizResult;
import serviceEntity.UserContext;
import townInterface.IDaoService;
import townInterface.IQuestionService;
import townInterface.IUpdateService;
import util.TimeUtil;

import java.util.List;

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
            if (!msg.hasQuestion()) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            QuestionInfoDO questInfo = daoService.toDO(msg.getQuestion());
            questInfo.setQuestionId(null);
            questInfo.setQuestionWriterTel(UserContext.getUserTel());
            questInfo.setNodeType(QuestionNodeType.TQNT_PRE_VALUE);
            questInfo.setChoiceUser(null);
            questInfo.setQuestionTime(TimeUtil.nowMillis());

            int insert = daoService.quest_insert(questInfo);
            if (insert <= 0) {
                return BizResult.error(RespCode.TRC_DB_ERROR);
            }

            CreateQuestionRsp rsp = CreateQuestionRsp.newBuilder().build();
            return BizResult.ok(rsp);
        });
    }

    @Override
    public ResponseMsg updateQuestion(String token, UpdateQuestionReq msg) {
        return execute(MsgType.TMT_UpdateQuestionRsp, token, () -> {
            QuestionInfo question = msg.getQuestion();
            int questId = question.getQuestionId();
            if (questId <= 0) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            QuestionInfoDO oldQuestion = daoService.quest_selectById(questId);
            if (oldQuestion == null) {
                return BizResult.error(RespCode.TRC_QUESTION_NOT_EXIST);
            }

            RespCode respCode;
            UpdateInfoDO updateInfoDO = null;
            if (TUserPower.TUP_CM.equals(UserContext.getUserPower())) {
                respCode = updateByCm(question, oldQuestion);
            } else {
                respCode = updateByCgb(question, oldQuestion);
                if (respCode == RespCode.TRC_OK) {
                    QuestionInfoDO newQuestion = daoService.quest_selectById(questId);
                    updateInfoDO = new UpdateInfoDO();
                    updateInfoDO.setInfoId((long) questId);
                    updateInfoDO.setInfoType(TUpdateInfoType.TUIT_QUESTION_VALUE);
                    updateInfoDO.setBeforeMsg(daoService.toProto(oldQuestion).toByteArray());
                    updateInfoDO.setAfterMsg(daoService.toProto(newQuestion).toByteArray());
                    updateInfoDO.setUpdateTime(TimeUtil.nowMillis());
                    updateInfoDO.setUpdateUserTel(UserContext.getUserTel());
                    updateInfoDO.setUpdateName(UserContext.getUserName());
                }
            }
            if (respCode != RespCode.TRC_OK) {
                return BizResult.error(respCode);
            }
            UpdateQuestionRsp rsp = UpdateQuestionRsp.newBuilder().build();
            if (updateInfoDO != null) {
                return BizResult.ok(rsp, updateInfoDO);
            }
            return BizResult.ok(rsp);
        });
    }

    /// 村民修改
    private RespCode updateByCm(QuestionInfo question, QuestionInfoDO oldQuestion) {
        if (oldQuestion.getNodeType() != QuestionNodeType.TQNT_PRE_VALUE) {
            return RespCode.TRC_QUESTION_IS_IN_OPT;
        }
        if (!question.hasQuestionType() && !question.hasQuestionCtx() && !question.hasQuestPhoto()) {
            return RespCode.TRC_PARAM_NULL;
        }

        QuestionInfoDO updateDO = daoService.toDO(question);
        updateDO.setQuestionWriterTel(null);
        updateDO.setNodeType(null);
        updateDO.setChoiceUser(null);
        updateDO.setQuestionTime(null);

        int update = daoService.quest_update(updateDO);
        if (update <= 0) {
            return RespCode.TRC_DB_ERROR;
        }

        return RespCode.TRC_OK;
    }

    /// 村官干部修改
    private RespCode updateByCgb(QuestionInfo question, QuestionInfoDO oldQuestion) {
        QuestionInfoDO updateDO = daoService.toDO(question);
        if (oldQuestion.equals(updateDO)) {
            return RespCode.TRC_QUESTION_NOT_UPDATE;
        }
        int update = daoService.quest_update(updateDO);
        if (update <= 0) {
            return RespCode.TRC_DB_ERROR;
        }
        return RespCode.TRC_OK;
    }

    @Override
    public ResponseMsg listQuestion(String token, ListQuestionReq msg) {
        return execute(MsgType.TMT_ListQuestionRsp, token, () -> {
            if (msg.getPage() <= 0 || msg.getSize() <= 0) {
                return BizResult.error(RespCode.TRC_PARAM_NULL);
            }

            ListQuestionRsp.Builder builder = ListQuestionRsp.newBuilder();
            List<QuestionInfoDO> questList;
            long userTel = UserContext.getUserTel();
            if (msg.hasNodeType()) {
                if (UserContext.getUserPower() == TUserPower.TUP_CM) {
                    questList = daoService.quest_selectByWriterTelAndType(msg.getPage(), msg.getSize(), userTel, msg.getNodeTypeValue());
                } else {
                    questList = daoService.quest_selectByChoiceUserAndType(msg.getPage(), msg.getSize(), userTel, msg.getNodeTypeValue());
                }
            } else {
                if (UserContext.getUserPower() == TUserPower.TUP_CM) {
                    questList = daoService.quest_selectByWriterTel(msg.getPage(), msg.getSize(), userTel);
                } else {
                    questList = daoService.quest_selectByChoiceUser(msg.getPage(), msg.getSize(), userTel);
                }
            }

            for (QuestionInfoDO questionInfoDO : questList) {
                builder.addInfos(daoService.toProto(questionInfoDO));
            }

            return BizResult.ok(builder.build());
        });
    }
}
