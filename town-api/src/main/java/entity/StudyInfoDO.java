package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudyInfoDO implements Serializable {
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

