package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PeopleUpdateApplyDO implements Serializable {
    private Integer applyId;
    private Long applyUserId;
    private Long applyCreateTime;
    private byte[] newPeople;
}

