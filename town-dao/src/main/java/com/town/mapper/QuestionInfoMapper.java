package com.town.mapper;

import entity.QuestionInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionInfoMapper {

    @Select("SELECT * FROM question_info WHERE question_id = #{id}")
    QuestionInfoDO selectById(Integer id);

    @Select("SELECT * FROM question_info WHERE question_write_tel = #{userTel}")
    List<QuestionInfoDO> selectByWriterTel(@Param("userTel") Long userTel);

    @Select("SELECT * FROM question_info WHERE choice_user = #{choiceUser} OR choice_user IS null OR choice_user = 0")
    List<QuestionInfoDO> selectByChoiceUser(@Param("userTel") Long choiceUser);

    @Insert("""
        INSERT INTO question_info
        (question_type, question_ctx, quest_photo,
         question_write_tel, node_type, choice_user, question_time)
        VALUES
        (#{questionType}, #{questionCtx}, #{questPhoto},
         #{questionWriterTel}, #{nodeType}, #{choiceUser}, #{questionTime})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(QuestionInfoDO entity);


    @Update("""
    <script>
    UPDATE question_info
    <set>
        <if test="questionType != null">
            question_type = #{questionType},
        </if>
        <if test="questionCtx != null">
            question_ctx = #{questionCtx},
        </if>
        <if test="questPhoto != null">
            quest_photo = #{questPhoto},
        </if>
        <if test="questionWriterTel != null">
            question_write_tel = #{questionWriterTel},
        </if>
        <if test="nodeType != null">
            node_type = #{nodeType},
        </if>
        <if test="choiceUser != null">
            choice_user = #{choiceUser},
        </if>
        <if test="questionTime != null">
            question_time = #{questionTime},
        </if>
    </set>
    WHERE question_id = #{questionId}
    </script>
    """)
    int update(QuestionInfoDO entity);

    @Delete("DELETE FROM question_info WHERE question_id = #{id}")
    int delete(Integer id);
}


