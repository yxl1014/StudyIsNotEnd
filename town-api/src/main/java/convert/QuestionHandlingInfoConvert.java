package convert;

import entity.QuestionHandlingInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionHandlingInfoConvert {

    @Mapping(
            target = "handlingCtx",
            expression = "java(proto.getHandlingCtxCount() > 0 ? proto.getHandlingCtx(0).toByteArray() : null)"
    )
    @Mapping(target = "handlingType", expression = "java(proto.getHandlingTypeValue())")
    QuestionHandlingInfoDO toDO(po.QuestionHandlingInfo proto);

    @Mapping(target = "handlingType", expression = "java(po.QuestionHandlingType.forNumber(entity.getHandlingType()))")
    @Mapping(
            target = "handlingCtxList",
            expression = "java(entity.getHandlingCtx() == null ? java.util.Collections.emptyList() : java.util.List.of(com.google.protobuf.ByteString.copyFrom(entity.getHandlingCtx())))"
    )
    po.QuestionHandlingInfo toProto(QuestionHandlingInfoDO entity);
}

