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
        (#{questType}, #{questContext}, #{questPhoto},
         #{questWriterTel}, #{nodeType}, #{choiceUser}, #{questTime})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(QuestionInfoDO entity);

    @Update("""
        UPDATE question_info SET
        question_type = #{questType},
        question_ctx = #{questContext},
        quest_photo = #{questPhoto},
        question_write_tel = #{questWriterTel},
        node_type = #{nodeType},
        choice_user = #{choiceUser},
        question_time = #{questTime}
        WHERE question_id = #{questId}
        """)
    int update(QuestionInfoDO entity);

    @Delete("DELETE FROM question_info WHERE question_id = #{id}")
    int delete(Integer id);
}


