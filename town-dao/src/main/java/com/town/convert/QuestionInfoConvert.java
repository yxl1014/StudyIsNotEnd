package com.town.convert;

import com.town.convert.commonUtil.ProtoCommonMapper;
import entity.QuestionInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = ProtoCommonMapper.class
)
public interface QuestionInfoConvert {

    @Mapping(target = "questionType", expression = "java(proto.getQuestTypeValue())")
    @Mapping(target = "nodeType", expression = "java(proto.getNodeTypeValue())")
    @Mapping(target = "questPhoto", expression = "java(proto.hasQuestPhoto() ? proto.getQuestPhoto().toByteArray() : null)")
    QuestionInfoDO toDO(po.QuestionInfo proto);

    @Mapping(target = "questType", expression = "java(po.QuestionType.forNumber(entity.getQuestionType()))")
    @Mapping(target = "nodeType", expression = "java(po.QuestionNodeType.forNumber(entity.getNodeType()))")
    @Mapping(target = "questPhoto", expression = "java(entity.getQuestPhoto() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getQuestPhoto()))")
    po.QuestionInfo toProto(QuestionInfoDO entity);
}

