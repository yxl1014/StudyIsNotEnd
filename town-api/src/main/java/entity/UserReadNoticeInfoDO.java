package entity;

import lombok.Data;

@Data
public class UserReadNoticeInfoDO {
    private Integer id;
    private Integer userTel;
    private Integer noticeId;
    private Long readTime;
}

