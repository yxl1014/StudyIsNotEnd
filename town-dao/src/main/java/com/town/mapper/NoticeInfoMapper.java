package com.town.mapper;

import entity.NoticeInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeInfoMapper {

    @Select("SELECT * FROM notice_info WHERE notice_id = #{id}")
    NoticeInfoDO selectById(Integer id);

    @Select("""
        SELECT *
        FROM notice_info
        ORDER BY is_top DESC, notice_id DESC
    """)
    List<NoticeInfoDO> selectAll();


    @Select("SELECT * FROM notice_info WHERE writer_tel = #{writerTel} and notice_create_time = #{createTime}")
    NoticeInfoDO selectByWriterAndCreateTime(@Param("writerTel") Long writerTel,@Param("createTime")  Long createTime);

    @Insert("""
            INSERT INTO notice_info
            (notice_type, notice_create_time, notice_title, notice_context,
             writer_tel, writer_name, is_top, is_accept_read, notice_att)
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
            "<if test='noticeContext != null'>notice_context = #{noticeContext},</if>",
            "<if test='writerTel != null'>writer_tel = #{writerTel},</if>",
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


