package convert;

import entity.UserReadNoticeInfoDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserReadNoticeInfoConvert {

    UserReadNoticeInfoDO toDO(po.UserReadNoticeInfo proto);

    po.UserReadNoticeInfo toProto(UserReadNoticeInfoDO entity);
}

