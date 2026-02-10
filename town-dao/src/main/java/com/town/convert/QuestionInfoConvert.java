package com.town.convert;

import com.google.protobuf.ByteString;
import com.town.convert.commonUtil.ProtoCommonMapper;
import entity.QuestionInfoDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = ProtoCommonMapper.class
)
public interface QuestionInfoConvert {

    /* =======================
     * Proto → Entity
     * ======================= */

    @Mapping(
            target = "questionType",
            expression = "java(proto.hasQuestionType() ? proto.getQuestionTypeValue() : null)"
    )
    @Mapping(
            target = "nodeType",
            expression = "java(proto.hasNodeType() ? proto.getNodeTypeValue() : null)"
    )
    @Mapping(
            target = "questPhoto",
            expression = "java(proto.hasQuestPhoto() ? proto.getQuestPhoto().toByteArray() : null)"
    )
    QuestionInfoDO toDO(po.QuestionInfo proto);

    /* =======================
     * Entity → Proto
     * ======================= */

    @Mapping(
            target = "questionType",
            expression = "java(entity.getQuestionType() == null ? null : po.QuestionType.forNumber(entity.getQuestionType()))"
    )
    @Mapping(
            target = "nodeType",
            expression = "java(entity.getNodeType() == null ? null : po.QuestionNodeType.forNumber(entity.getNodeType()))"
    )
    @Mapping(target = "questPhoto", ignore = true)
    po.QuestionInfo toProto(QuestionInfoDO entity);

    /* =======================
     * AfterMapping：安全处理 bytes → ByteString
     * ======================= */

    @AfterMapping
    default void fillQuestPhoto(
            QuestionInfoDO entity,
            @MappingTarget po.QuestionInfo.Builder builder
    ) {
        if (entity.getQuestPhoto() != null) {
            builder.setQuestPhoto(ByteString.copyFrom(entity.getQuestPhoto()));
        }
    }
}

