package com.town.convert;

import com.google.protobuf.ByteString;
import entity.PeopleUpdateApplyDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PeopleUpdateApplyConvert {

    @Mapping(target = "newPeople", expression = "java(proto.hasNewPeople() ? proto.getNewPeople().toByteArray() : null)")
    PeopleUpdateApplyDO toDO(po.PeopleUpdateApply proto);

    @Mapping(target = "newPeople", ignore = true)
    po.PeopleUpdateApply toProto(PeopleUpdateApplyDO entity);

    /* =======================
     * AfterMapping：安全处理 ByteString
     * ======================= */

    @AfterMapping
    default void fillNewPeople(
            PeopleUpdateApplyDO entity,
            @MappingTarget po.PeopleUpdateApply.Builder builder
    ) {
        if (entity.getNewPeople() != null) {
            builder.setNewPeople(ByteString.copyFrom(entity.getNewPeople()));
        }
    }
}
