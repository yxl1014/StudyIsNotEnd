package townInterface;

import entity.UserInfoDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDaoService {
    /// redis
    /* ======================= Key ======================= */

    boolean redis_exists(String key);

    boolean redis_delete(String key);

    boolean redis_expire(String key, long seconds);

    long redis_getExpire(String key);

    /* ======================= String ======================= */

    void redis_set(String key, Object value);

    void redis_set(String key, Object value, long seconds);

    Object redis_get(String key);

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
