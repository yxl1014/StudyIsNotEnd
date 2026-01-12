package com.town.mapper;

import entity.UpdateInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UpdateInfoMapper {

    @Select("SELECT * FROM update_info WHERE update_id = #{id}")
    UpdateInfoDO selectById(Integer id);

    @Select("SELECT * FROM update_info ORDER BY update_id DESC")
    List<UpdateInfoDO> selectAll();

    @Insert("""
        INSERT INTO update_info
        (info_id, info_type, before_msg, after_msg,
         update_time, update_user_tel, update_name)
        VALUES
        (#{infoId}, #{infoType}, #{beforeMsg}, #{afterMsg},
         #{updateTime}, #{updateUserTel}, #{updateName})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "updateId")
    int insert(UpdateInfoDO entity);

    @Update("""
        UPDATE update_info SET
        info_id = #{infoId},
        info_type = #{infoType},
        before_msg = #{beforeMsg},
        after_msg = #{afterMsg},
        update_time = #{updateTime},
        update_user_tel = #{updateUserTel},
        update_name = #{updateName}
        WHERE update_id = #{updateId}
        """)
    int update(UpdateInfoDO entity);

    @Delete("DELETE FROM update_info WHERE update_id = #{id}")
    int delete(Integer id);
}


