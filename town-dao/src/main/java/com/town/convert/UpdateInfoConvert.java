package com.town.convert;

import entity.UpdateInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdateInfoConvert {

    @Mapping(target = "infoType", expression = "java(proto.getInfoTypeValue())")
    @Mapping(
            target = "beforeMsg",
            expression = "java(proto.hasBeforeMsg() ? proto.getBeforeMsg().toByteArray() : null)"
    )
    @Mapping(
            target = "afterMsg",
            expression = "java(proto.hasAfterMsg() ? proto.getAfterMsg().toByteArray() : null)"
    )
    UpdateInfoDO toDO(po.UpdateInfo proto);

    @Mapping(target = "infoType", expression = "java(po.TUpdateInfoType.forNumber(entity.getInfoType()))")
    @Mapping(
            target = "beforeMsg",
            expression = "java(entity.getBeforeMsg() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getBeforeMsg()))"
    )
    @Mapping(
            target = "afterMsg",
            expression = "java(entity.getAfterMsg() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getAfterMsg()))"
    )
    po.UpdateInfo toProto(UpdateInfoDO entity);
}

