package com.town.convert;

import entity.UserStarStudyInfoDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserStarStudyInfoConvert {

    UserStarStudyInfoDO toDO(po.UserStarStudyInfo proto);

    po.UserStarStudyInfo toProto(UserStarStudyInfoDO entity);
}

