package com.town.mapper;

import entity.QuestionInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionInfoMapper {

    @Select("SELECT * FROM question_info WHERE question_id = #{id}")
    QuestionInfoDO selectById(Integer id);

    @Select("SELECT * FROM question_info WHERE question_write_tel = #{userTel}")
    List<QuestionInfoDO> selectByWriterTel(Integer userTel);

    @Select("SELECT * FROM question_info WHERE choice_user = #{choiceUser} OR choice_user = null OR choice_user = 0")
    List<QuestionInfoDO> selectByChoiceUser(Integer choiceUser);

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
    <script>
    UPDATE question_info
    <set>
        <if test="questType != null">
            question_type = #{questType},
        </if>
        <if test="questContext != null">
            question_ctx = #{questContext},
        </if>
        <if test="questPhoto != null">
            quest_photo = #{questPhoto},
        </if>
        <if test="questWriterTel != null">
            question_write_tel = #{questWriterTel},
        </if>
        <if test="nodeType != null">
            node_type = #{nodeType},
        </if>
        <if test="choiceUser != null">
            choice_user = #{choiceUser},
        </if>
        <if test="questTime != null">
            question_time = #{questTime},
        </if>
    </set>
    WHERE question_id = #{questId}
    </script>
    """)
    int update(QuestionInfoDO entity);

    @Delete("DELETE FROM question_info WHERE question_id = #{id}")
    int delete(Integer id);
}


