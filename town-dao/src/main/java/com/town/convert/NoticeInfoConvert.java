package com.town.convert;

import com.town.convert.commonUtil.ProtoCommonMapper;
import entity.NoticeInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "noticeAtt", expression = "java(entity.getNoticeAtt() == null ? null : com.google.protobuf.ByteString.copyFrom(entity.getNoticeAtt()))")
    po.NoticeInfo toProto(NoticeInfoDO entity);
}

