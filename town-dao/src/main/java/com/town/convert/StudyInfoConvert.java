package com.town.convert;

import entity.StudyInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyInfoConvert {

    @Mapping(target = "studyType", expression = "java(proto.getStudyTypeValue())")
    StudyInfoDO toDO(po.StudyInfo proto);

    @Mapping(target = "studyType", expression = "java(po.StudyType.forNumber(entity.getStudyType()))")
    po.StudyInfo toProto(StudyInfoDO entity);
}

