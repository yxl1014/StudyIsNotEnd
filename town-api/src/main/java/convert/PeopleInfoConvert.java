package convert;

import entity.PeopleInfoDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeopleInfoConvert {

    PeopleInfoDO toDO(po.PeopleInfo proto);

    po.PeopleInfo toProto(PeopleInfoDO entity);
}

