package entity;

import lombok.Data;

@Data
public class PeopleUpdateApplyDO {
    private Integer applyId;
    private Integer applyUserId;
    private Long applyCreateTime;
    private byte[] newPeople;
}

