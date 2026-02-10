package com.town.mapper;

import entity.PeopleUpdateApplyDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PeopleUpdateApplyMapper {

    @Select("SELECT * FROM people_update_apply WHERE apply_id = #{id}")
    PeopleUpdateApplyDO selectById(Integer id);

    @Select("SELECT * FROM people_update_apply")
    List<PeopleUpdateApplyDO> selectAll();

    @Select("SELECT * FROM people_update_apply WHERE apply_user_id = #{userTel}")
    List<PeopleUpdateApplyDO> selectAllByUserTel(@Param("userTel") Long userTel);

    @Insert("""
            INSERT INTO people_update_apply
            (apply_user_id, apply_create_time, new_people)
            VALUES
            (#{applyUserId}, #{applyCreateTime}, #{newPeople})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "applyId")
    int insert(PeopleUpdateApplyDO entity);

    @Update("""
            <script>
            UPDATE people_update_apply
            <set>
                <if test="applyUserId != null">
                    apply_user_id = #{applyUserId},
                </if>
                <if test="applyCreateTime != null">
                    apply_create_time = #{applyCreateTime},
                </if>
                <if test="newPeople != null">
                    new_people = #{newPeople},
                </if>
            </set>
            WHERE apply_id = #{applyId}
            </script>
            """)
    int update(PeopleUpdateApplyDO entity);


    @Delete("DELETE FROM people_update_apply WHERE apply_id = #{id}")
    int delete(Integer id);
}
