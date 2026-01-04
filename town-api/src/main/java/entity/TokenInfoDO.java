package entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TokenInfoDO implements Serializable {
    private Integer userTel; // 用户电话
    private String randomCode; // 随机编号
    private Long createTime; // token创建时间
}
