package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PeopleInfoDO implements Serializable {
    private String peopleCardId;
    private String peopleName;
    private String peopleHouseId;
    private String peopleCtx;
}

