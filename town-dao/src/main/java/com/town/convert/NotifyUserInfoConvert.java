package com.town.convert;

import entity.NotifyUserInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotifyUserInfoConvert {

    @Mapping(target = "msgCtx", expression = "java(proto.hasMsgCtx() ? proto.getMsgCtx().toByteArray() : null)")
    NotifyUserInfoDO toDO(po.NotifyUserInfo proto);

    @Mapping(target = "msgCtx", expression = "java(entity.getMsgCtx() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getMsgCtx()))")
    po.NotifyUserInfo toProto(NotifyUserInfoDO entity);
}

