package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionHandlingInfoDO implements Serializable {
    private Integer handleId;
    private Integer questionId;
    private Integer handlingType;
    private Long handleUserTel;
    private String handleCtx;
    private Long handleTime;
}
