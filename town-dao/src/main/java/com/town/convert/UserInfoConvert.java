package com.town.convert;

import com.town.convert.commonUtil.ProtoCommonMapper;
import entity.UserInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = ProtoCommonMapper.class
)
public interface UserInfoConvert {

    /* Proto → Entity */

    @Mapping(
            target = "userPower",
            expression = "java(proto.hasUserPower() ? proto.getUserPowerValue() : null)"
    )
    @Mapping(
            target = "flagType",
            expression = "java(proto.hasFlagType() ? proto.getFlagTypeValue() : null)"
    )
    UserInfoDO toDO(po.UserInfo proto);

    @Mapping(
            target = "userPower",
            expression = "java(entity.getUserPower() == null ? null : po.TUserPower.forNumber(entity.getUserPower()))"
    )
    @Mapping(
            target = "flagType",
            expression = "java(entity.getFlagType() == null ? null : po.TUserFlagType.forNumber(entity.getFlagType()))"
    )
    po.UserInfo toProto(UserInfoDO entity);
}

