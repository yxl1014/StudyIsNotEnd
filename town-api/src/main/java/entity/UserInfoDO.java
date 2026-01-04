package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDO implements Serializable {
    private Integer userTel;
    private String userName;
    private String userPwd;
    private String userTown;
    private Integer userPower;
    private Long userCreateTime;
    private Integer flagType;
}

