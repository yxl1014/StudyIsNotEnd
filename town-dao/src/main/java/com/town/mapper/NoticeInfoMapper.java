package com.town.mapper;

import entity.NoticeInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NoticeInfoMapper {

    @Select("SELECT * FROM notice_info WHERE notice_id = #{id}")
    NoticeInfoDO selectById(Integer id);

    @Insert("""
            INSERT INTO notice_info
            (notice_type, notice_create_time, notice_title, notice_ctx,
             write_tel, writer_name, is_top, is_accept_read, notice_att)
            VALUES
            (#{noticeType}, #{noticeCreateTime}, #{noticeTitle}, #{noticeContext},
             #{writerTel}, #{writerName}, #{isTop}, #{isAcceptRead}, #{noticeAtt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "noticeId")
    int insert(NoticeInfoDO entity);

    @Update({
            "<script>",
            "UPDATE notice_info",
            "<set>",
            "<if test='noticeType != null'>notice_type = #{noticeType},</if>",
            "<if test='noticeCreateTime != null'>notice_create_time = #{noticeCreateTime},</if>",
            "<if test='noticeTitle != null'>notice_title = #{noticeTitle},</if>",
            "<if test='noticeCtx != null'>notice_ctx = #{noticeContext},</if>",
            "<if test='writeTel != null'>write_tel = #{writerTel},</if>",
            "<if test='writerName != null'>writer_name = #{writerName},</if>",
            "<if test='isTop != null'>is_top = #{isTop},</if>",
            "<if test='isAcceptRead != null'>is_accept_read = #{isAcceptRead},</if>",
            "<if test='noticeAtt != null'>notice_att = #{noticeAtt},</if>",
            "</set>",
            "WHERE notice_id = #{noticeId}",
            "</script>"
    })
    int update(NoticeInfoDO entity);

    @Delete("DELETE FROM notice_info WHERE notice_id = #{id}")
    int delete(Integer id);
}


