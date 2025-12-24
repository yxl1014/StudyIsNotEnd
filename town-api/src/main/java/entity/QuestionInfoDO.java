package entity;

import lombok.Data;

@Data
public class QuestionInfoDO {
    private Integer questionId;
    private Integer questionType;
    private String questionCtx;
    private byte[] questPhoto;
    private Integer questionWriteTel;
    private Integer nodeType;
    private Integer choiceUser;
    private Long questionTime;
}

