package com.town.dao.mapper;

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
            (#{noticeType}, #{noticeCreateTime}, #{noticeTitle}, #{noticeCtx},
             #{writeTel}, #{writerName}, #{isTop}, #{isAcceptRead}, #{noticeAtt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "noticeId")
    int insert(NoticeInfoDO entity);

    @Update("""
            UPDATE notice_info SET
            notice_type = #{noticeType},
            notice_create_time = #{noticeCreateTime},
            notice_title = #{noticeTitle},
            notice_ctx = #{noticeCtx},
            write_tel = #{writeTel},
            writer_name = #{writerName},
            is_top = #{isTop},
            is_accept_read = #{isAcceptRead},
            notice_att = #{noticeAtt}
            WHERE notice_id = #{noticeId}
            """)
    int update(NoticeInfoDO entity);

    @Delete("DELETE FROM notice_info WHERE notice_id = #{id}")
    int delete(Integer id);
}


