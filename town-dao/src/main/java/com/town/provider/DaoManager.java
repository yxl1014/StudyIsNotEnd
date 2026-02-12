package com.town.provider;


import com.github.pagehelper.PageHelper;
import com.town.convert.*;
import com.town.mapper.*;
import com.town.redis.RedisManager;
import entity.*;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import po.*;
import townInterface.IDaoService;

import java.util.List;

@DubboService(timeout = 10000, retries = 0)
public class DaoManager implements IDaoService {
    private static final Logger log = LoggerFactory.getLogger(DaoManager.class);

    @Resource
    private NoticeInfoMapper noticeInfoMapper;

    @Resource
    private NotifyUserInfoMapper notifyUserInfoMapper;

    @Resource
    private PeopleInfoMapper peopleInfoMapper;

    @Resource
    private PeopleUpdateApplyMapper peopleUpdateApplyMapper;

    @Resource
    private QuestionHandlingInfoMapper questionHandlingInfoMapper;

    @Resource
    private QuestionInfoMapper questionInfoMapper;

    @Resource
    private StudyInfoMapper studyInfoMapper;

    @Resource
    private UpdateInfoMapper updateInfoMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserReadNoticeInfoMapper userReadNoticeInfoMapper;

    @Resource
    private UserStarStudyInfoMapper userStarStudyInfoMapper;

    @Autowired
    private NoticeInfoConvert noticeInfoConvert;
    @Autowired
    private NotifyUserInfoConvert notifyUserInfoConvert;
    @Autowired
    private PeopleInfoConvert peopleInfoConvert;
    @Autowired
    private PeopleUpdateApplyConvert peopleUpdateApplyConvert;
    @Autowired
    private QuestionInfoConvert questionInfoConvert;
    @Autowired
    private QuestionHandlingInfoConvert questionHandlingInfoConvert;
    @Autowired
    private StudyInfoConvert studyInfoConvert;
    @Autowired
    private UpdateInfoConvert updateInfoConvert;
    @Autowired
    private UserInfoConvert userInfoConvert;
    @Autowired
    private UserReadNoticeInfoConvert userReadNoticeInfoConvert;
    @Autowired
    private UserStarStudyInfoConvert userStarStudyInfoConvert;


    /* ======================= User ======================= */
    @Override
    public UserInfoDO user_selectById(Long userTel) {
        return userInfoMapper.selectById(userTel);
    }

    @Override
    public List<UserInfoDO> user_selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return userInfoMapper.selectAll();
    }

    @Override
    public int user_insert(UserInfoDO entity) {
        return userInfoMapper.insert(entity);
    }

    @Override
    public int user_update(UserInfoDO entity) {
        return userInfoMapper.update(entity);
    }

    @Override
    public int user_delete(Long userTel) {
        return userInfoMapper.delete(userTel);
    }

    @Override
    public UserInfoDO toDO(UserInfo proto) {
        return userInfoConvert.toDO(proto);
    }

    @Override
    public UserInfo toProto(UserInfoDO entity) {
        return userInfoConvert.toProto(entity);
    }

    /* ======================= User ======================= */

    /* ======================= Notice ======================= */

    @Override
    public NoticeInfoDO notice_selectById(Integer id) {
        return noticeInfoMapper.selectById(id);
    }

    @Override
    public List<NoticeInfoDO> notice_selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return noticeInfoMapper.selectAll();
    }

    @Override
    public NoticeInfoDO notice_selectByWriterAndCreateTime(Long writerTel, Long createTime) {
        return noticeInfoMapper.selectByWriterAndCreateTime(writerTel, createTime);
    }

    @Override
    public int notice_insert(NoticeInfoDO entity) {
        return noticeInfoMapper.insert(entity);
    }

    @Override
    public int notice_update(NoticeInfoDO entity) {
        return noticeInfoMapper.update(entity);
    }

    @Override
    public int notice_delete(Integer id) {
        return noticeInfoMapper.delete(id);
    }

    @Override
    public NoticeInfoDO toDO(NoticeInfo proto) {
        return noticeInfoConvert.toDO(proto);
    }

    @Override
    public NoticeInfo toProto(NoticeInfoDO entity) {
        return noticeInfoConvert.toProto(entity);
    }

    /* ======================= Notice ======================= */


    /* ======================= Update ======================= */

    @Override
    public int update_insert(UpdateInfoDO entity) {
        return updateInfoMapper.insert(entity);
    }

    @Override
    public UpdateInfoDO update_selectById(Integer updateId) {
        return updateInfoMapper.selectById(updateId);
    }

    @Override
    public List<UpdateInfoDO> update_selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return updateInfoMapper.selectAll();
    }

    @Override
    public UpdateInfoDO toDO(UpdateInfo proto) {
        return updateInfoConvert.toDO(proto);
    }

    @Override
    public UpdateInfo toProto(UpdateInfoDO entity) {
        return updateInfoConvert.toProto(entity);
    }

    /* ======================= Update ======================= */

    /* ======================= UserReadNotice ======================= */

    @Override
    public UserReadNoticeInfoDO read_selectById(Integer id) {
        return userReadNoticeInfoMapper.selectById(id);
    }

    @Override
    public List<UserReadNoticeInfoDO> read_selectByUserTel(Long userTel) {
        return userReadNoticeInfoMapper.selectByUserTel(userTel);
    }

    @Override
    public List<UserReadNoticeInfoDO> read_selectByUserTelAndNoticeId(Long userTel, Integer noticeId) {
        return userReadNoticeInfoMapper.selectByUserTelAndNoticeId(userTel, noticeId);
    }

    @Override
    public int read_insert(UserReadNoticeInfoDO entity) {
        return userReadNoticeInfoMapper.insert(entity);
    }

    /* ======================= UserReadNotice ======================= */


    /* ======================= Question ======================= */

    @Override
    public QuestionInfoDO toDO(QuestionInfo proto) {
        return questionInfoConvert.toDO(proto);
    }

    @Override
    public QuestionInfo toProto(QuestionInfoDO entity) {
        return questionInfoConvert.toProto(entity);
    }

    @Override
    public QuestionInfoDO quest_selectById(Integer id) {
        return questionInfoMapper.selectById(id);
    }

    @Override
    public List<QuestionInfoDO> quest_selectByWriterTel(int page, int size, Long userTel) {
        PageHelper.startPage(page, size);
        return questionInfoMapper.selectByWriterTel(userTel);
    }

    @Override
    public List<QuestionInfoDO> quest_selectByChoiceUser(int page, int size, Long choiceUser) {
        PageHelper.startPage(page, size);
        return questionInfoMapper.selectByChoiceUser(choiceUser);
    }

    @Override
    public List<QuestionInfoDO> quest_selectByWriterTelAndType(int page, int size, Long userTel, Integer nodeType) {
        PageHelper.startPage(page, size);
        return questionInfoMapper.selectByWriterTelAndType(userTel, nodeType);
    }

    @Override
    public List<QuestionInfoDO> quest_selectByChoiceUserAndType(int page, int size, Long choiceUser, Integer nodeType) {
        PageHelper.startPage(page, size);
        return questionInfoMapper.selectByChoiceUserAndType(choiceUser, nodeType);
    }

    @Override
    public int quest_insert(QuestionInfoDO entity) {
        return questionInfoMapper.insert(entity);
    }

    @Override
    public int quest_update(QuestionInfoDO entity) {
        return questionInfoMapper.update(entity);
    }

    /* ======================= Question ======================= */



    /* ======================= Notify ======================= */

    @Override
    public NotifyUserInfoDO toDO(NotifyUserInfo proto) {
        return notifyUserInfoConvert.toDO(proto);
    }

    @Override
    public NotifyUserInfo toProto(NotifyUserInfoDO entity) {
        return notifyUserInfoConvert.toProto(entity);
    }

    @Override
    public NotifyUserInfoDO notify_selectById(Integer id) {
        return notifyUserInfoMapper.selectById(id);
    }

    @Override
    public List<NotifyUserInfoDO> notify_selectByUserId(Long userTel) {
        return notifyUserInfoMapper.selectByUserId(userTel);
    }

    @Override
    public int notify_insert(NotifyUserInfoDO entity) {
        return notifyUserInfoMapper.insert(entity);
    }

    @Override
    public int notify_update(NotifyUserInfoDO entity) {
        return notifyUserInfoMapper.update(entity);
    }

    @Override
    public int notify_delete(Integer id) {
        return notifyUserInfoMapper.delete(id);
    }

    @Override
    public int notify_deleteAll() {
        return notifyUserInfoMapper.deleteAll();
    }

    /* ======================= Notify ======================= */

    /* ======================= Study ======================= */

    @Override
    public StudyInfoDO toDO(StudyInfo proto) {
        return studyInfoConvert.toDO(proto);
    }

    @Override
    public StudyInfo toProto(StudyInfoDO entity) {
        return studyInfoConvert.toProto(entity);
    }

    @Override
    public StudyInfoDO study_selectById(Integer id) {
        return studyInfoMapper.selectById(id);
    }

    @Override
    public StudyInfoDO study_selectByCreateTime(Long createTime) {
        return studyInfoMapper.selectByCreateTime(createTime);
    }

    @Override
    public List<StudyInfoDO> study_selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return studyInfoMapper.selectAll();
    }

    @Override
    public int study_insert(StudyInfoDO entity) {
        return studyInfoMapper.insert(entity);
    }

    @Override
    public int study_update(StudyInfoDO entity) {
        return studyInfoMapper.update(entity);
    }

    @Override
    public int study_delete(Integer id) {
        return studyInfoMapper.delete(id);
    }

    /* ======================= Study ======================= */



    /* ======================= Star ======================= */

    @Override
    public UserStarStudyInfoDO toDO(UserStarStudyInfo proto) {
        return userStarStudyInfoConvert.toDO(proto);
    }

    @Override
    public UserStarStudyInfo toProto(UserStarStudyInfoDO entity) {
        return userStarStudyInfoConvert.toProto(entity);
    }

    @Override
    public UserStarStudyInfoDO star_selectById(Integer id) {
        return userStarStudyInfoMapper.selectById(id);
    }

    @Override
    public UserStarStudyInfoDO star_selectByIdAndTel(Integer id, Long userTel) {
        return userStarStudyInfoMapper.selectByIdAndTel(id, userTel);
    }


    @Override
    public List<UserStarStudyInfoDO> star_selectByUserTel(Long userTel, int page, int size) {
        PageHelper.startPage(page, size);
        return userStarStudyInfoMapper.selectByUserTel(userTel);
    }

    @Override
    public int star_insert(UserStarStudyInfoDO entity) {
        return userStarStudyInfoMapper.insert(entity);
    }

    @Override
    public int star_update(UserStarStudyInfoDO entity) {
        return userStarStudyInfoMapper.update(entity);
    }

    @Override
    public int star_delete(Integer id) {
        return userStarStudyInfoMapper.delete(id);
    }

    /* ======================= Star ======================= */




    /* ======================= People ======================= */

    @Override
    public PeopleInfoDO toDO(PeopleInfo proto) {
        return peopleInfoConvert.toDO(proto);
    }

    @Override
    public PeopleInfo toProto(PeopleInfoDO entity) {
        return peopleInfoConvert.toProto(entity);
    }

    @Override
    public PeopleInfoDO people_selectById(String id) {
        return peopleInfoMapper.selectById(id);
    }

    @Override
    public List<PeopleInfoDO> selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return peopleInfoMapper.selectAll();
    }

    @Override
    public int people_insert(PeopleInfoDO entity) {
        return peopleInfoMapper.insert(entity);
    }

    @Override
    public int people_update(PeopleInfoDO entity) {
        return peopleInfoMapper.update(entity);
    }

    @Override
    public int people_delete(String id) {
        return peopleInfoMapper.delete(id);
    }

    /* ======================= People ======================= */



    /* ======================= Apply ======================= */

    @Override
    public PeopleUpdateApplyDO toDO(PeopleUpdateApply proto) {
        return peopleUpdateApplyConvert.toDO(proto);
    }

    @Override
    public PeopleUpdateApply toProto(PeopleUpdateApplyDO entity) {
        return peopleUpdateApplyConvert.toProto(entity);
    }

    @Override
    public PeopleUpdateApplyDO apply_selectById(Integer id) {
        return peopleUpdateApplyMapper.selectById(id);
    }

    @Override
    public List<PeopleUpdateApplyDO> apply_selectAll(int page, int size) {
        PageHelper.startPage(page, size);
        return peopleUpdateApplyMapper.selectAll();
    }

    @Override
    public List<PeopleUpdateApplyDO> apply_selectAllByUserTel(int page, int size, Long userTel) {
        PageHelper.startPage(page, size);
        return peopleUpdateApplyMapper.selectAllByUserTel(userTel);
    }

    @Override
    public int apply_insert(PeopleUpdateApplyDO entity) {
        return peopleUpdateApplyMapper.insert(entity);
    }

    @Override
    public int apply_update(PeopleUpdateApplyDO entity) {
        return peopleUpdateApplyMapper.update(entity);
    }

    @Override
    public int apply_delete(Integer id) {
        return peopleUpdateApplyMapper.delete(id);
    }

    /* ======================= Apply ======================= */


    @Resource
    private RedisManager redisManager;

    /* ======================= Key ======================= */

    @Override
    public boolean redis_exists(String key){
        return  redisManager.exists(key);
    }

    @Override
    public boolean redis_delete(String key){
        return redisManager.delete(key);
    }

    @Override
    public boolean redis_expire(String key, long seconds){
        return redisManager.expire(key, seconds);
    }

    @Override
    public long redis_getExpire(String key){
        return redisManager.getExpire(key);
    }

    /* ======================= String ======================= */

    @Override
    public void redis_set(String key, Object value){
        redisManager.set(key, value);
    }

    @Override
    public void redis_set(String key, Object value, long seconds){
        redisManager.set(key,value,seconds);
    }

    @Override
    public Object redis_get(String key){
        return redisManager.get(key);
    }
}
