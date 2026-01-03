package com.town.convert;

import entity.PeopleUpdateApplyDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PeopleUpdateApplyConvert {

    @Mapping(target = "newPeople", expression = "java(proto.hasNewPeople() ? proto.getNewPeople().toByteArray() : null)")
    PeopleUpdateApplyDO toDO(po.PeopleUpdateApply proto);

    @Mapping(target = "newPeople", expression = "java(entity.getNewPeople() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getNewPeople()))")
    po.PeopleUpdateApply toProto(PeopleUpdateApplyDO entity);
}
