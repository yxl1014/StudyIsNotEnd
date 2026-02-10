package com.town.mapper;

import entity.QuestionHandlingInfoDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface QuestionHandlingInfoMapper {

    @Select("SELECT * FROM question_handling_info WHERE handle_id = #{id}")
    QuestionHandlingInfoDO selectById(Integer id);

    @Insert("""
            INSERT INTO question_handling_info
            (question_id, handling_type, handle_user_tel, handle_ctx, handle_time)
            VALUES
            (#{questionId}, #{handlingType}, #{handleUserTel},
             #{handleCtx}, #{handleTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "handleId")
    int insert(QuestionHandlingInfoDO entity);

    @Update("""
            <script>
            UPDATE question_handling_info
            <set>
                <if test="questionId != null">
                    question_id = #{questionId},
                </if>
                <if test="handlingType != null">
                    handling_type = #{handlingType},
                </if>
                <if test="handleUserTel != null">
                    handle_user_tel = #{handleUserTel},
                </if>
                <if test="handleCtx != null">
                    handle_ctx = #{handleCtx},
                </if>
                <if test="handleTime != null">
                    handle_time = #{handleTime},
                </if>
            </set>
            WHERE handle_id = #{handleId}
            </script>
            """)
    int update(QuestionHandlingInfoDO entity);


    @Delete("DELETE FROM question_handling_info WHERE handle_id = #{id}")
    int delete(Integer id);
}


