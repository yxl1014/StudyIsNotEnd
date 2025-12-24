package com.town.dao.mapper;

import entity.UserInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserInfoMapper {

    @Select("SELECT * FROM user_info WHERE user_tel = #{userTel}")
    UserInfoDO selectById(Integer userTel);

    @Insert("""
        INSERT INTO user_info
        (user_tel, user_name, user_pwd, user_town, user_power, user_create_time, flag_type)
        VALUES
        (#{userTel}, #{userName}, #{userPwd}, #{userTown},
         #{userPower}, #{userCreateTime}, #{flagType})
        """)
    int insert(UserInfoDO entity);

    @Update("""
        UPDATE user_info SET
        user_name=#{userName},
        user_pwd=#{userPwd},
        user_town=#{userTown},
        user_power=#{userPower},
        flag_type=#{flagType}
        WHERE user_tel=#{userTel}
        """)
    int update(UserInfoDO entity);

    @Delete("DELETE FROM user_info WHERE user_tel=#{userTel}")
    int delete(Integer userTel);
}
