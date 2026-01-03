package com.town.mapper;

import entity.PeopleUpdateApplyDO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PeopleUpdateApplyMapper {

    @Select("SELECT * FROM people_update_apply WHERE apply_id = #{id}")
    PeopleUpdateApplyDO selectById(Integer id);

    @Insert("""
            INSERT INTO people_update_apply
            (apply_user_id, apply_create_time, new_people)
            VALUES
            (#{applyUserId}, #{applyCreateTime}, #{newPeople})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "applyId")
    int insert(PeopleUpdateApplyDO entity);

    @Update("""
            UPDATE people_update_apply SET
            apply_user_id = #{applyUserId},
            apply_create_time = #{applyCreateTime},
            new_people = #{newPeople}
            WHERE apply_id = #{applyId}
            """)
    int update(PeopleUpdateApplyDO entity);

    @Delete("DELETE FROM people_update_apply WHERE apply_id = #{id}")
    int delete(Integer id);
}
