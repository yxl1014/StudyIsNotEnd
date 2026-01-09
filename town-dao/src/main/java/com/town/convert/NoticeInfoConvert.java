package com.town.convert;

import com.google.protobuf.ByteString;
import com.town.convert.commonUtil.ProtoCommonMapper;
import entity.NoticeInfoDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = ProtoCommonMapper.class
)
public interface NoticeInfoConvert {

    /* Proto → Entity */

    @Mapping(target = "noticeType", expression = "java(proto.getNoticeTypeValue())")
    @Mapping(target = "noticeAtt", expression = "java(proto.hasNoticeAtt() ? proto.getNoticeAtt().toByteArray() : null)")
    NoticeInfoDO toDO(po.NoticeInfo proto);

    /* Entity → Proto */

    @Mapping(target = "noticeType", expression = "java(po.TNoticeType.forNumber(entity.getNoticeType()))")
    @Mapping(target = "noticeAtt", ignore = true)
    po.NoticeInfo toProto(NoticeInfoDO entity);

    @AfterMapping
    default void fillNoticeAtt(
            NoticeInfoDO entity,
            @MappingTarget po.NoticeInfo.Builder builder
    ) {
        if (entity.getNoticeAtt() != null) {
            builder.setNoticeAtt(ByteString.copyFrom(entity.getNoticeAtt()));
        }
    }
}

