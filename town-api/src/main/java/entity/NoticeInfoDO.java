package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class NoticeInfoDO implements Serializable {
    private Integer noticeId;
    private Integer noticeType;
    private Long noticeCreateTime;
    private String noticeTitle;
    private String noticeContext;
    private Integer writerTel;
    private String writerName;
    private Boolean isTop;
    private Boolean isAcceptRead;
    private byte[] noticeAtt;
}

