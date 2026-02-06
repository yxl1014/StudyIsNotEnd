package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateInfoDO implements Serializable {
    private Integer updateId;
    private Long infoId;
    private Integer infoType;
    private byte[] beforeMsg;
    private byte[] afterMsg;
    private Long updateTime;
    private Long updateUserTel;
    private String updateName;
}

