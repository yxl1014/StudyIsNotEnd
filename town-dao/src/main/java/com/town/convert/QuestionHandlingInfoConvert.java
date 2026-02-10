package com.town.convert;

import entity.QuestionHandlingInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionHandlingInfoConvert {

    /* Proto → Entity */
    @Mapping(
            target = "handlingType",
            expression = "java(proto.hasHandlingType() ? proto.getHandlingTypeValue() : null)"
    )
    QuestionHandlingInfoDO toDO(po.QuestionHandlingInfo proto);

    /* Entity → Proto */

    @Mapping(
            target = "handlingType",
            expression = "java(entity.getHandlingType() == null ? null : po.QuestionHandlingType.forNumber(entity.getHandlingType()))"
    )
    po.QuestionHandlingInfo toProto(QuestionHandlingInfoDO entity);

}

