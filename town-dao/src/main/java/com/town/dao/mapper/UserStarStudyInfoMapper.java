package com.town.dao.mapper;

import entity.UserStarStudyInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserStarStudyInfoMapper {

    @Select("SELECT * FROM user_star_study_info WHERE id = #{id}")
    UserStarStudyInfoDO selectById(Integer id);

    @Insert("""
            INSERT INTO user_star_study_info
            (user_tel, study_id)
            VALUES
            (#{userTel}, #{studyId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserStarStudyInfoDO entity);

    @Update("""
            UPDATE user_star_study_info SET
            user_tel = #{userTel},
            study_id = #{studyId}
            WHERE id = #{id}
            """)
    int update(UserStarStudyInfoDO entity);

    @Delete("DELETE FROM user_star_study_info WHERE id = #{id}")
    int delete(Integer id);
}

