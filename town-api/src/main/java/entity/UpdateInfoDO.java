package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateInfoDO implements Serializable {
    private Integer updateId;
    private Integer infoId;
    private Integer infoType;
    private byte[] beforeMsg;
    private byte[] afterMsg;
    private Long updateTime;
    private Integer updateUserTel;
    private String updateName;
}

