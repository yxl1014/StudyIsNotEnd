package demo.netty.handler.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import po.*;

import java.util.EnumMap;

/**
 * @author Administrator
 * @Package : demo.netty.handler.util
 * @Create on : 2026/2/9 13:52
 **/


public final class RespBodyParsers {

    private static final EnumMap<MsgType, Parser<? extends Message>> PARSER_MAP =
            new EnumMap<>(MsgType.class);

    static {
        PARSER_MAP.put(MsgType.TMT_LoginRsp, LoginRsp.parser());
        PARSER_MAP.put(MsgType.TMT_RegisterRsp, RegisterRsp.parser());
        PARSER_MAP.put(MsgType.TMT_UpdateUserInfoRsp, UpdateUserInfoRsp.parser());
        PARSER_MAP.put(MsgType.TMT_CreateNoticeRsp, CreateNoticeRsp.parser());
        PARSER_MAP.put(MsgType.TMT_UpdateNoticeRsp, UpdateNoticeRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListNoticeRsp, ListNoticeRsp.parser());
        PARSER_MAP.put(MsgType.TMT_SetNoticeReadRsp, SetNoticeReadRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListUpdateInfoRsp, ListUpdateInfoRsp.parser());
        PARSER_MAP.put(MsgType.TMT_CreateQuestionRsp, CreateQuestionRsp.parser());
        PARSER_MAP.put(MsgType.TMT_UpdateQuestionRsp, UpdateQuestionRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListQuestionHandlingRsp, ListQuestionHandlingRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListQuestionRsp, ListQuestionRsp.parser());
        PARSER_MAP.put(MsgType.TMT_CreateStudyRsp, CreateStudyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_UpdateStudyRsp, UpdateStudyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListStudyRsp, ListStudyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_StarStudyRsp, StarStudyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListUserStarStudyRsp, ListUserStarStudyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_CreatePeopleRsp, CreatePeopleRsp.parser());
        PARSER_MAP.put(MsgType.TMT_UpdatePeopleRsp, UpdatePeopleRsp.parser());
        PARSER_MAP.put(MsgType.TMT_CreatePeopleUpdateApplyRsp, CreatePeopleUpdateApplyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_DelPeopleUpdateApplyRsp, DelPeopleUpdateApplyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListNoticeReadRsp, ListNoticeReadRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListPeopleInfoRsp, ListPeopleInfoRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListPeopleUpdateApplyRsp, ListPeopleUpdateApplyRsp.parser());
        PARSER_MAP.put(MsgType.TMT_ListUserInfoRsp, ListUserInfoRsp.parser());
    }

    private RespBodyParsers() {}

    public static Message parse(ResponseMsg resp) throws InvalidProtocolBufferException {
        Parser<? extends Message> parser = PARSER_MAP.get(resp.getMsgType());
        if (parser == null || resp.getMsg().isEmpty()) {
            return null;
        }
        return parser.parseFrom(resp.getMsg());
    }
}
