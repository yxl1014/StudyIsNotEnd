package convert;

import entity.UpdateInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UpdateInfoConvert {

    @Mapping(target = "infoType", expression = "java(proto.getInfoTypeValue())")
    UpdateInfoDO toDO(po.UpdateInfo proto);

    @Mapping(target = "infoType", expression = "java(po.TUpdateInfoType.forNumber(entity.getInfoType()))")
    po.UpdateInfo toProto(UpdateInfoDO entity);
}

