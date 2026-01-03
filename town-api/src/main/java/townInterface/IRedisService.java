package townInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRedisService {

    /* ======================= Key ======================= */

    boolean exists(String key);

    boolean delete(String key);

    boolean expire(String key, long seconds);

    long getExpire(String key);

    /* ======================= String ======================= */

    void set(String key, Object value);

    void set(String key, Object value, long seconds);

    Object get(String key);

    /* ======================= Hash ======================= */

    void hSet(String key, String field, Object value);

    Object hGet(String key, String field);

    void hDel(String key, String... fields);

    Map<Object, Object> hGetAll(String key);

    /* ======================= List ======================= */

    void lPush(String key, Object value);

    Object rPop(String key);

    List<Object> lRange(String key, long start, long end);

    /* ======================= Set ======================= */

    void sAdd(String key, Object... values);

    Set<Object> sMembers(String key);

    boolean sIsMember(String key, Object value);

    /* ======================= ZSet ======================= */

    void zAdd(String key, Object value, double score);

    Set<Object> zRange(String key, long start, long end);

    Long zRemove(String key, Object... values);
}
