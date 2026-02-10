package com.town.mapper;

import entity.NotifyUserInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotifyUserInfoMapper {

    @Select("SELECT * FROM notify_user_info WHERE id = #{id}")
    NotifyUserInfoDO selectById(Integer id);


    @Select("SELECT * FROM notify_user_info WHERE user_tel = #{userTel}")
    List<NotifyUserInfoDO> selectByUserId(@Param("userTel") Long userTel);

    @Insert("""
            INSERT INTO notify_user_info
            (user_tel, msg_type, msg_ctx)
            VALUES
            (#{userTel}, #{msgMype}, #{msgCtx})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotifyUserInfoDO entity);

    @Update("""
            <script>
            UPDATE notify_user_info
            <set>
                <if test="userTel != null">
                    user_tel = #{userTel},
                </if>
                <if test="msgType != null">
                    msg_type = #{msgType},
                </if>
                <if test="msgCtx != null">
                    msg_ctx = #{msgCtx},
                </if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int update(NotifyUserInfoDO entity);

    @Delete("DELETE FROM notify_user_info WHERE id = #{id}")
    int delete(Integer id);

    @Delete("DELETE FROM notify_user_info")
    int deleteAll();
}

