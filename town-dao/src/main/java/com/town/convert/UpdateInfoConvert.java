package com.town.convert;

import com.google.protobuf.ByteString;
import entity.UpdateInfoDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UpdateInfoConvert {

    @Mapping(
            target = "infoType",
            expression = "java(proto.hasInfoType() ? proto.getInfoTypeValue() : null)"
    )
    @Mapping(
            target = "beforeMsg",
            expression = "java(proto.hasBeforeMsg() ? proto.getBeforeMsg().toByteArray() : null)"
    )
    @Mapping(
            target = "afterMsg",
            expression = "java(proto.hasAfterMsg() ? proto.getAfterMsg().toByteArray() : null)"
    )
    UpdateInfoDO toDO(po.UpdateInfo proto);

    @Mapping(
            target = "infoType",
            expression = "java(entity.getInfoType() == null ? null : po.TUpdateInfoType.forNumber(entity.getInfoType()))"
    )
    @Mapping(target = "beforeMsg", ignore = true)
    @Mapping(target = "afterMsg", ignore = true)
    po.UpdateInfo toProto(UpdateInfoDO entity);

    /* =======================
     * AfterMapping：安全处理 bytes → ByteString
     * ======================= */

    @AfterMapping
    default void fillBytes(
            UpdateInfoDO entity,
            @MappingTarget po.UpdateInfo.Builder builder
    ) {
        if (entity.getBeforeMsg() != null) {
            builder.setBeforeMsg(ByteString.copyFrom(entity.getBeforeMsg()));
        }
        if (entity.getAfterMsg() != null) {
            builder.setAfterMsg(ByteString.copyFrom(entity.getAfterMsg()));
        }
    }
}

