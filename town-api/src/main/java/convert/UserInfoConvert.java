package convert;

import convert.commonUtil.ProtoCommonMapper;
import entity.UserInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = ProtoCommonMapper.class
)
public interface UserInfoConvert {

    /* Proto → Entity */

    @Mapping(target = "userPower", expression = "java(proto.getUserPowerValue())")
    @Mapping(target = "flagType", expression = "java(proto.getFlagTypeValue())")
    UserInfoDO toDO(po.UserInfo proto);

    /* Entity → Proto */

    @Mapping(target = "userPower", expression = "java(po.TUserPower.forNumber(entity.getUserPower()))")
    @Mapping(target = "flagType", expression = "java(po.TUserFlagType.forNumber(entity.getFlagType()))")
    po.UserInfo toProto(UserInfoDO entity);
}

