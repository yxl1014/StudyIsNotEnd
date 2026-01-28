package com.town.mapper;

import entity.PeopleInfoDO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PeopleInfoMapper {
    @Select("SELECT * FROM people_info WHERE people_card_id = #{id}")
    PeopleInfoDO selectById(String id);

    @Select("SELECT * FROM people_info")
    List<PeopleInfoDO> selectAll();


    @Insert("""
        INSERT INTO people_info
        (people_card_id, people_name, people_house_id, people_ctx)
        VALUES
        (#{peopleCardId}, #{peopleName}, #{peopleHouseId}, #{peopleCtx})
        """)
    int insert(PeopleInfoDO entity);

    @Update("""
        UPDATE people_info SET
        people_name = #{peopleName},
        people_house_id = #{peopleHouseId},
        people_ctx = #{peopleCtx}
        WHERE people_card_id = #{peopleCardId}
        """)
    int update(PeopleInfoDO entity);

    @Delete("DELETE FROM people_info WHERE people_card_id = #{id}")
    int delete(String id);
}


