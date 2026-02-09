package com.town.mapper;

import entity.UserInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    @Select("SELECT * FROM user_info WHERE user_tel = #{userTel}")
    UserInfoDO selectById(Long userTel);

    @Select("SELECT * FROM user_info")
    List<UserInfoDO> selectAll();

    @Insert("""
        INSERT INTO user_info
        (user_tel, user_name, user_pwd, user_town, user_power, user_create_time, flag_type)
        VALUES
        (#{userTel}, #{userName}, #{userPwd}, #{userTown},
         #{userPower}, #{userCreateTime}, #{flagType})
        """)
    int insert(UserInfoDO entity);

    @Update({
            "<script>",
            "UPDATE user_info",
            "<set>",
            "<if test='userName != null'>user_name = #{userName},</if>",
            "<if test='userPwd != null'>user_pwd = #{userPwd},</if>",
            "<if test='userTown != null'>user_town = #{userTown},</if>",
            "<if test='userPower != null'>user_power = #{userPower},</if>",
            "<if test='flagType != null'>flag_type = #{flagType},</if>",
            "</set>",
            "WHERE user_tel = #{userTel}",
            "</script>"
    })
    int update(UserInfoDO entity);

    @Delete("DELETE FROM user_info WHERE user_tel=#{userTel}")
    int delete(Long userTel);
}
