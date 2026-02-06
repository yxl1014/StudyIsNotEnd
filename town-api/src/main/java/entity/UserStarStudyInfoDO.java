package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStarStudyInfoDO implements Serializable {
    private Integer id;
    private Long userTel;
    private Integer studyId;
}

