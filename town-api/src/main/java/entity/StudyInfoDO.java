package entity;

import lombok.Data;

@Data
public class StudyInfoDO {
    private Integer studyId;
    private Integer studyType;
    private Long studyCreateTime;
    private String studyTitle;
    private String studyTip;
    private String studyContent;
    private Boolean isOpen;
    private Boolean isTop;
    private Integer readCount;
}

