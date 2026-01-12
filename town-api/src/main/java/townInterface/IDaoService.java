package townInterface;

import entity.NoticeInfoDO;
import entity.UpdateInfoDO;
import entity.UserInfoDO;
import entity.UserReadNoticeInfoDO;

import java.util.List;

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
    UserInfoDO user_selectById(Integer userTel);

    int user_insert(UserInfoDO entity);

    int user_update(UserInfoDO entity);

    int user_delete(Integer userTel);

    /* Proto → Entity */
    UserInfoDO toDO(po.UserInfo proto);

    /* Entity → Proto */
    po.UserInfo toProto(UserInfoDO entity);


    /// notice
    NoticeInfoDO notice_selectById(Integer id);
    List<NoticeInfoDO> notice_selectAll(int page, int size);
    NoticeInfoDO notice_selectByWriterAndCreateTime(Integer writerTel, Long createTime);
    int notice_insert(NoticeInfoDO entity);
    int notice_update(NoticeInfoDO entity);
    int notice_delete(Integer id);
    NoticeInfoDO toDO(po.NoticeInfo proto);
    po.NoticeInfo toProto(NoticeInfoDO entity);

    /// update
    int update_insert(UpdateInfoDO entity);
    UpdateInfoDO update_selectById(Integer updateId);
    List<UpdateInfoDO> update_selectAll(int page, int size);
    UpdateInfoDO toDO(po.UpdateInfo proto);
    po.UpdateInfo toProto(UpdateInfoDO entity);

    /// serReadNoticeInfo
    UserReadNoticeInfoDO read_selectById(Integer id);
    List<UserReadNoticeInfoDO> read_selectByUserTel(Integer userTel);
    int read_insert(UserReadNoticeInfoDO entity);
    List<UserReadNoticeInfoDO> read_selectByUserTelAndNoticeId(Integer userTel, Integer noticeId);

}
