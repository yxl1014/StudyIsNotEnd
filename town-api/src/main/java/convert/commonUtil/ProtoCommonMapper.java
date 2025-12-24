package convert.commonUtil;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProtoCommonMapper {

    /* ========= enum ========= */

    default Integer enumToInt(Enum<?> e) {
        return e == null ? null : e.ordinal();
    }

    /* ========= bytes ========= */

    default byte[] byteStringToBytes(com.google.protobuf.ByteString bs) {
        return bs == null ? null : bs.toByteArray();
    }

    default com.google.protobuf.ByteString bytesToByteString(byte[] bytes) {
        return bytes == null ? null : com.google.protobuf.ByteString.copyFrom(bytes);
    }
}

