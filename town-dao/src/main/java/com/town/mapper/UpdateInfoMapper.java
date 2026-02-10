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
            <script>
            UPDATE update_info
            <set>
                <if test="infoId != null">
                    info_id = #{infoId},
                </if>
                <if test="infoType != null">
                    info_type = #{infoType},
                </if>
                <if test="beforeMsg != null">
                    before_msg = #{beforeMsg},
                </if>
                <if test="afterMsg != null">
                    after_msg = #{afterMsg},
                </if>
                <if test="updateTime != null">
                    update_time = #{updateTime},
                </if>
                <if test="updateUserTel != null">
                    update_user_tel = #{updateUserTel},
                </if>
                <if test="updateName != null">
                    update_name = #{updateName},
                </if>
            </set>
            WHERE update_id = #{updateId}
            </script>
            """)
    int update(UpdateInfoDO entity);

    @Delete("DELETE FROM update_info WHERE update_id = #{id}")
    int delete(Integer id);
}


