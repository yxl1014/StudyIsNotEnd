package entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotifyUserInfoDO implements Serializable {
    private Integer id;
    private Integer userTel;
    private Integer msgType;
    private byte[] msgCtx;
}

