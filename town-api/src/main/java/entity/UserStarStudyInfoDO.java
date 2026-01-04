package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStarStudyInfoDO implements Serializable {
    private Integer id;
    private Integer userTel;
    private Integer studyId;
}

