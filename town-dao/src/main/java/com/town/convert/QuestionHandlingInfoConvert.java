package com.town.convert;

import entity.QuestionHandlingInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionHandlingInfoConvert {

    @Mapping(target = "handlingType", expression = "java(proto.getHandlingTypeValue())")
    QuestionHandlingInfoDO toDO(po.QuestionHandlingInfo proto);

    @Mapping(target = "handlingType", expression = "java(po.QuestionHandlingType.forNumber(entity.getHandlingType()))")
    po.QuestionHandlingInfo toProto(QuestionHandlingInfoDO entity);
}

