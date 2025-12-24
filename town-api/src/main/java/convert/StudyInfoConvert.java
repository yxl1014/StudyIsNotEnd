package convert;

import entity.StudyInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyInfoConvert {

    @Mapping(target = "studyType", expression = "java(proto.getStudyTypeValue())")
    @Mapping(
            target = "studyContent",
            expression = "java(proto.getStudyContentCount() > 0 ? proto.getStudyContent(0).toByteArray() : null)"
    )
    StudyInfoDO toDO(po.StudyInfo proto);

    @Mapping(target = "studyType", expression = "java(po.StudyType.forNumber(entity.getStudyType()))")
    @Mapping(
            target = "studyContentList",
            expression = "java(entity.getStudyContent() == null ? java.util.Collections.emptyList() : java.util.List.of(com.google.protobuf.ByteString.copyFrom(entity.getStudyContent())))"
    )
    po.StudyInfo toProto(StudyInfoDO entity);
}

