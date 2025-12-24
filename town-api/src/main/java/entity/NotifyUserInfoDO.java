package entity;

import lombok.Data;

@Data
public class NotifyUserInfoDO {
    private Integer id;
    private Integer msgMype;
    private byte[] msgCtx;
}

