package com.town.mapper;

import entity.QuestionInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface QuestionInfoMapper {

    @Select("SELECT * FROM question_info WHERE question_id = #{id}")
    QuestionInfoDO selectById(Integer id);

    @Insert("""
        INSERT INTO question_info
        (question_type, question_ctx, quest_photo,
         question_write_tel, node_type, choice_user, question_time)
        VALUES
        (#{questionType}, #{questionCtx}, #{questPhoto},
         #{questionWriteTel}, #{nodeType}, #{choiceUser}, #{questionTime})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(QuestionInfoDO entity);

    @Update("""
        UPDATE question_info SET
        question_type = #{questionType},
        question_ctx = #{questionCtx},
        quest_photo = #{questPhoto},
        question_write_tel = #{questionWriteTel},
        node_type = #{nodeType},
        choice_user = #{choiceUser},
        question_time = #{questionTime}
        WHERE question_id = #{questionId}
        """)
    int update(QuestionInfoDO entity);

    @Delete("DELETE FROM question_info WHERE question_id = #{id}")
    int delete(Integer id);
}


