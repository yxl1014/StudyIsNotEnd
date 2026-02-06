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
    private Long writerTel;
    private String writerName;
    private Boolean isTop;
    private Boolean isAcceptRead;
    private byte[] noticeAtt;

    public boolean isEmpty() {
        return noticeId == null || noticeId <= 0;
    }

    public boolean otherNull() {
        return noticeType == null
                && noticeCreateTime == null
                && isBlank(noticeTitle)
                && isBlank(noticeContext)
                && writerTel == null
                && isBlank(writerName)
                && isTop == null
                && isAcceptRead == null
                && noticeAtt == null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}

