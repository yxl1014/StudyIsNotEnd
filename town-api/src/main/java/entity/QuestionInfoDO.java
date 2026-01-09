package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionInfoDO implements Serializable {
    private Integer questId;
    private Integer questType;
    private String questContext;
    private byte[] questPhoto;
    private Integer questWriterTel;
    private Integer nodeType;
    private Integer choiceUser;
    private Long questTime;
}

