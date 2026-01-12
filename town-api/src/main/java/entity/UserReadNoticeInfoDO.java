package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReadNoticeInfoDO implements Serializable {
    private Integer id;
    private Integer userTel;
    private Integer noticeId;
    private Long readTime;

    public UserReadNoticeInfoDO(Integer userTel, Integer noticeId, Long readTime) {
        this.userTel = userTel;
        this.noticeId = noticeId;
        this.readTime = readTime;
    }
}

