package townInterface;

import entity.UserInfoDO;

public interface IDaoService {
    IRedisService  redisService();

    /// user
    UserInfoDO selectById(Integer userTel);

    int insert(UserInfoDO entity);

    int update(UserInfoDO entity);

    int delete(Integer userTel);

    /* Proto → Entity */
    UserInfoDO toDO(po.UserInfo proto);

    /* Entity → Proto */
    po.UserInfo toProto(UserInfoDO entity);

}
