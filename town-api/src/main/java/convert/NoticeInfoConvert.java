package convert;

import convert.commonUtil.ProtoCommonMapper;
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
    @Mapping(target = "noticeAtt", expression = "java(proto.getNoticeAttCount() > 0 ? proto.getNoticeAtt(0).toByteArray() : null)")
    NoticeInfoDO toDO(po.NoticeInfo proto);

    /* Entity → Proto */

    @Mapping(target = "noticeType", expression = "java(po.TNoticeType.forNumber(entity.getNoticeType()))")
    @Mapping(target = "noticeAttList", expression = "java(entity.getNoticeAtt() == null ? java.util.Collections.emptyList() : java.util.List.of(com.google.protobuf.ByteString.copyFrom(entity.getNoticeAtt())))")
    po.NoticeInfo toProto(NoticeInfoDO entity);
}

