package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import po.TUserFlagType;
import po.TUserPower;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenInfoDO implements Serializable {
    private Long userTel; // 用户电话
    private String randomCode; // 随机编号
    private Long createTime; // token创建时间
    private TUserPower userPower; // 用户角色
    private TUserFlagType userFlagType; // 用户标记
    private String userName; // 用户名
}
