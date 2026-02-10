package com.town.mapper;

import entity.UserStarStudyInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserStarStudyInfoMapper {

    @Select("SELECT * FROM user_star_study_info WHERE id = #{id}")
    UserStarStudyInfoDO selectById(@Param("id") Integer id);

    @Select("SELECT * FROM user_star_study_info WHERE study_id = #{id} AND user_tel = #{userTel}")
    UserStarStudyInfoDO selectByIdAndTel(@Param("id") Integer id, @Param("userTel") Long userTel);

    @Select("SELECT * FROM user_star_study_info WHERE user_tel = #{userTel}")
    List<UserStarStudyInfoDO> selectByUserTel(@Param("userTel") Long userTel);

    @Insert("""
            INSERT INTO user_star_study_info
            (user_tel, study_id)
            VALUES
            (#{userTel}, #{studyId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserStarStudyInfoDO entity);

    @Update("""
            <script>
            UPDATE user_star_study_info
            <set>
                <if test="userTel != null">
                    user_tel = #{userTel},
                </if>
                <if test="studyId != null">
                    study_id = #{studyId},
                </if>
            </set>
            WHERE id = #{id}
            </script>
            """)
    int update(UserStarStudyInfoDO entity);

    @Delete("DELETE FROM user_star_study_info WHERE id = #{id}")
    int delete(Integer id);
}

