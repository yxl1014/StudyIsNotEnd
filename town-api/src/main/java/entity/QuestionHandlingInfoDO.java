package entity;

import lombok.Data;

@Data
public class QuestionHandlingInfoDO {
    private Integer handleId;
    private Integer questionId;
    private Integer handlingType;
    private Integer handleUserTel;
    private String handleCtx;
    private Long handleTime;
}
