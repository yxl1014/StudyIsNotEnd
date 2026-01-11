package com.town.mapper;

import entity.UserReadNoticeInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserReadNoticeInfoMapper {

    @Select("SELECT * FROM user_read_notice_info WHERE id = #{id}")
    UserReadNoticeInfoDO selectById(Integer id);

    @Select("SELECT * FROM user_read_notice_info WHERE user_tel = #{userTel}")
    List<UserReadNoticeInfoDO> selectByUserTel(Integer userTel);

    @Insert("""
            INSERT INTO user_read_notice_info
            (user_tel, notice_id, read_time)
            VALUES
            (#{userTel}, #{noticeId}, #{readTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserReadNoticeInfoDO entity);

    @Update("""
        UPDATE user_read_notice_info SET
        user_tel = #{userTel},
        notice_id = #{noticeId},
        read_time = #{readTime}
        WHERE id = #{id}
        """)
    int update(UserReadNoticeInfoDO entity);

    @Delete("DELETE FROM user_read_notice_info WHERE id = #{id}")
    int delete(Integer id);
}

