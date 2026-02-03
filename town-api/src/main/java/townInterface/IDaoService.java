package townInterface;

import entity.*;

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

    /// question
    QuestionInfoDO toDO(po.QuestionInfo proto);
    po.QuestionInfo toProto(QuestionInfoDO entity);
    QuestionInfoDO quest_selectById(Integer id);
    List<QuestionInfoDO> quest_selectByWriterTel(int page, int size, Integer userTel);
    List<QuestionInfoDO> quest_selectByChoiceUser(int page, int size, Integer choiceUser);
    int quest_insert(QuestionInfoDO entity);
    int quest_update(QuestionInfoDO entity);

    /// notify
    NotifyUserInfoDO toDO(po.NotifyUserInfo proto);
    po.NotifyUserInfo toProto(NotifyUserInfoDO entity);
    NotifyUserInfoDO notify_selectById(Integer id);
    List<NotifyUserInfoDO> notify_selectByUserId(Integer userTel);
    int notify_insert(NotifyUserInfoDO entity);
    int notify_update(NotifyUserInfoDO entity);
    int notify_delete(Integer id);
    int notify_deleteAll();

    /// study
    StudyInfoDO toDO(po.StudyInfo proto);
    po.StudyInfo toProto(StudyInfoDO entity);
    StudyInfoDO study_selectById(Integer id);
    StudyInfoDO study_selectByCreateTime(Long createTime);
    List<StudyInfoDO> study_selectAll(int page, int size);
    int study_insert(StudyInfoDO entity);
    int study_update(StudyInfoDO entity);
    int study_delete(Integer id);

    /// star
    UserStarStudyInfoDO toDO(po.UserStarStudyInfo proto);
    po.UserStarStudyInfo toProto(UserStarStudyInfoDO entity);
    UserStarStudyInfoDO star_selectById(Integer id);
    UserStarStudyInfoDO star_selectByIdAndTel(Integer id, Integer userTel);
    List<UserStarStudyInfoDO> star_selectByUserTel(Integer userTel, int page, int size);
    int star_insert(UserStarStudyInfoDO entity);
    int star_update(UserStarStudyInfoDO entity);
    int star_delete(Integer id);

    /// people
    PeopleInfoDO toDO(po.PeopleInfo proto);
    po.PeopleInfo toProto(PeopleInfoDO entity);
    PeopleInfoDO people_selectById(String id);
    List<PeopleInfoDO> selectAll(int page, int size);
    int people_insert(PeopleInfoDO entity);
    int people_update(PeopleInfoDO entity);
    int people_delete(String id);

    /// apply
    PeopleUpdateApplyDO toDO(po.PeopleUpdateApply proto);
    po.PeopleUpdateApply toProto(PeopleUpdateApplyDO entity);
    PeopleUpdateApplyDO apply_selectById(Integer id);
    List<PeopleUpdateApplyDO> apply_selectAll(int page, int size);
    List<PeopleUpdateApplyDO> apply_selectAllByUserTel(int page, int size, Integer userTel);
    int apply_insert(PeopleUpdateApplyDO entity);
    int apply_update(PeopleUpdateApplyDO entity);
    int apply_delete(Integer id);
}
