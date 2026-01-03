package com.town.mapper;

import entity.NotifyUserInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NotifyUserInfoMapper {

    @Select("SELECT * FROM notify_user_info WHERE id = #{id}")
    NotifyUserInfoDO selectById(Integer id);

    @Insert("""
        INSERT INTO notify_user_info
        (msg_mype, msg_ctx)
        VALUES
        (#{msgMype}, #{msgCtx})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotifyUserInfoDO entity);

    @Update("""
        UPDATE notify_user_info SET
        msg_mype = #{msgMype},
        msg_ctx = #{msgCtx}
        WHERE id = #{id}
        """)
    int update(NotifyUserInfoDO entity);

    @Delete("DELETE FROM notify_user_info WHERE id = #{id}")
    int delete(Integer id);
}

