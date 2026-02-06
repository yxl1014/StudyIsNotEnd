package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDO implements Serializable {
    private Long userTel;
    private String userName;
    private String userPwd;
    private String userTown;
    private Integer userPower;
    private Long userCreateTime;
    private Integer flagType;

    public boolean isEmpty() {
        return userTel == null || userTel <= 0;
    }

    public boolean otherNull() {
        return isBlank(userName)
                && isBlank(userPwd)
                && isBlank(userTown)
                && userPower == null
                && userCreateTime == null
                && flagType == null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

