package com.town.convert;

import com.google.protobuf.ByteString;
import entity.NotifyUserInfoDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotifyUserInfoConvert {

    @Mapping(target = "msgCtx", expression = "java(proto.hasMsgCtx() ? proto.getMsgCtx().toByteArray() : null)")
    NotifyUserInfoDO toDO(po.NotifyUserInfo proto);

    @Mapping(target = "msgCtx", ignore = true)
    po.NotifyUserInfo toProto(NotifyUserInfoDO entity);

    @AfterMapping
    default void fillMsgCtx(
            NotifyUserInfoDO entity,
            @MappingTarget po.NotifyUserInfo.Builder builder
    ) {
        if (entity.getMsgCtx() != null) {
            builder.setMsgCtx(ByteString.copyFrom(entity.getMsgCtx()));
        }
    }
}

