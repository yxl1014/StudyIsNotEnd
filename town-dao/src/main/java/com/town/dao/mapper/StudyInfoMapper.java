package com.town.dao.mapper;

import entity.StudyInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StudyInfoMapper {

    @Select("SELECT * FROM study_info WHERE study_id = #{id}")
    StudyInfoDO selectById(Integer id);

    @Insert("""
        INSERT INTO study_info
        (study_type, study_create_time, study_title, study_tip,
         study_content, is_open, is_top, read_count)
        VALUES
        (#{studyType}, #{studyCreateTime}, #{studyTitle}, #{studyTip},
         #{studyContent}, #{isOpen}, #{isTop}, #{readCount})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "studyId")
    int insert(StudyInfoDO entity);

    @Update("""
        UPDATE study_info SET
        study_type = #{studyType},
        study_create_time = #{studyCreateTime},
        study_title = #{studyTitle},
        study_tip = #{studyTip},
        study_content = #{studyContent},
        is_open = #{isOpen},
        is_top = #{isTop},
        read_count = #{readCount}
        WHERE study_id = #{studyId}
        """)
    int update(StudyInfoDO entity);

    @Delete("DELETE FROM study_info WHERE study_id = #{id}")
    int delete(Integer id);
}

