package com.town.dao.mapper;

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
        UPDATE question_handling_info SET
        question_id = #{questionId},
        handling_type = #{handlingType},
        handle_user_tel = #{handleUserTel},
        handle_ctx = #{handleCtx},
        handle_time = #{handleTime}
        WHERE handle_id = #{handleId}
        """)
    int update(QuestionHandlingInfoDO entity);

    @Delete("DELETE FROM question_handling_info WHERE handle_id = #{id}")
    int delete(Integer id);
}


