package com.town.convert;

import entity.StudyInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyInfoConvert {

    /* Proto → Entity */
    @Mapping(
            target = "studyType",
            expression = "java(proto.hasStudyType() ? proto.getStudyTypeValue() : null)"
    )
    StudyInfoDO toDO(po.StudyInfo proto);

    /* Entity → Proto */
    @Mapping(
            target = "studyType",
            expression = "java(entity.getStudyType() == null ? null : po.StudyType.forNumber(entity.getStudyType()))"
    )
    po.StudyInfo toProto(StudyInfoDO entity);

}

