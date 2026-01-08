package com.town.provider;


import com.town.convert.*;
import com.town.mapper.*;
import com.town.redis.RedisManager;
import entity.NoticeInfoDO;
import entity.UserInfoDO;
import exception.BizException;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import po.NoticeInfo;
import po.RespCode;
import po.UserInfo;
import townInterface.IDaoService;
import townInterface.IRedisService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DubboService(timeout = 300000, retries = 0)
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
    public UserInfoDO user_selectById(Integer userTel) {
        return userInfoMapper.selectById(userTel);
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
    public int user_delete(Integer userTel) {
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
