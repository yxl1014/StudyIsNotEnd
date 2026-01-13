package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionInfoDO implements Serializable {
    private Integer questionId;
    private Integer questionType;
    private String questionCtx;
    private byte[] questPhoto;
    private Integer questionWriterTel;
    private Integer nodeType;
    private Integer choiceUser;
    private Long questionTime;
}

