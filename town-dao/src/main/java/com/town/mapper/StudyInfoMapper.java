package com.town.mapper;

import entity.StudyInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyInfoMapper {

    @Select("SELECT * FROM study_info WHERE study_id = #{id}")
    StudyInfoDO selectById(Integer id);

    @Select("SELECT * FROM study_info WHERE study_create_time = #{createTime}")
    StudyInfoDO selectByCreateTime(Long createTime);

    @Select("SELECT * FROM study_info WHERE is_open = true ORDER BY is_top DESC")
    List<StudyInfoDO> selectAll();

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
            <script>
            UPDATE study_info
            <set>
                <if test="studyType != null">
                    study_type = #{studyType},
                </if>
                <if test="studyCreateTime != null">
                    study_create_time = #{studyCreateTime},
                </if>
                <if test="studyTitle != null">
                    study_title = #{studyTitle},
                </if>
                <if test="studyTip != null">
                    study_tip = #{studyTip},
                </if>
                <if test="studyContent != null">
                    study_content = #{studyContent},
                </if>
                <if test="isOpen != null">
                    is_open = #{isOpen},
                </if>
                <if test="isTop != null">
                    is_top = #{isTop},
                </if>
                <if test="readCount != null">
                    read_count = #{readCount},
                </if>
            </set>
            WHERE study_id = #{studyId}
            </script>
            """)
    int update(StudyInfoDO entity);

    @Delete("DELETE FROM study_info WHERE study_id = #{id}")
    int delete(Integer id);
}

