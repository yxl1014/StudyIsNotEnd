package convert;

import entity.PeopleUpdateApplyDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeopleUpdateApplyConvert {

    PeopleUpdateApplyDO toDO(po.PeopleUpdateApply proto);

    po.PeopleUpdateApply toProto(PeopleUpdateApplyDO entity);
}
