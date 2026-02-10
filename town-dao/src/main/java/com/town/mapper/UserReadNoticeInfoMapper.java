package com.town.mapper;

import entity.UserReadNoticeInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserReadNoticeInfoMapper {

    @Select("SELECT * FROM user_read_notice_info WHERE id = #{id}")
    UserReadNoticeInfoDO selectById(Integer id);

    @Select("SELECT * FROM user_read_notice_info WHERE user_tel = #{userTel}")
    List<UserReadNoticeInfoDO> selectByUserTel(@Param("userTel") Long userTel);

    @Select("SELECT * FROM user_read_notice_info WHERE user_tel = #{userTel} AND notice_id = #{noticeId}")
    List<UserReadNoticeInfoDO> selectByUserTelAndNoticeId(@Param("userTel") Long userTel, @Param("noticeId") Integer noticeId);

    @Insert("""
            INSERT INTO user_read_notice_info
            (user_tel, notice_id, read_time)
            VALUES
            (#{userTel}, #{noticeId}, #{readTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserReadNoticeInfoDO entity);

    @Update("""
            <script>
            UPDATE user_read_notice_info
            <set>
                <if test="userTel != null">
                    user_tel = #{userTel},
                </if>
                <if test="noticeId != null">
                    notice_id = #{noticeId},
                </if>
                <if test="readTime != null">
                    read_time = #{readTime},
                </if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int update(UserReadNoticeInfoDO entity);


    @Delete("DELETE FROM user_read_notice_info WHERE id = #{id}")
    int delete(Integer id);
}

