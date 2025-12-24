package entity;

import lombok.Data;

@Data
public class UserInfoDO {
    private Integer userTel;
    private String userName;
    private String userPwd;
    private String userTown;
    private Integer userPower;
    private Long userCreateTime;
    private Integer flagType;
}

