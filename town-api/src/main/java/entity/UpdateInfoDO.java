package entity;

import lombok.Data;

@Data
public class UpdateInfoDO {
    private Integer updateId;
    private Integer infoId;
    private Integer infoType;
    private byte[] beforeMsg;
    private byte[] afterMsg;
    private Long updateTime;
    private Integer updateUserTel;
    private String updateName;
}

