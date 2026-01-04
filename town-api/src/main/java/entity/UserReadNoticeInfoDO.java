package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserReadNoticeInfoDO implements Serializable {
    private Integer id;
    private Integer userTel;
    private Integer noticeId;
    private Long readTime;
}

