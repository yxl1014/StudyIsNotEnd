package convert;

import entity.NotifyUserInfoDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotifyUserInfoConvert {

    NotifyUserInfoDO toDO(po.NotifyUserInfo proto);

    po.NotifyUserInfo toProto(NotifyUserInfoDO entity);
}

