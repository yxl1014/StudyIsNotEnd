package com.town.provider;


import com.town.convert.*;
import com.town.mapper.*;
import com.town.redis.RedisManager;
import entity.UserInfoDO;
import exception.BizException;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import po.RespCode;
import po.UserInfo;
import townInterface.IDaoService;
import townInterface.IRedisService;

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

    @Resource
    private RedisManager redisManager;

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

    @Override
    public IRedisService redisService() {
        return redisManager;
    }

    @Override
    public UserInfoDO selectById(Integer userTel) {
        return userInfoMapper.selectById(userTel);
    }

    @Override
    public int insert(UserInfoDO entity) {
        return userInfoMapper.insert(entity);
    }

    @Override
    public int update(UserInfoDO entity) {
        return userInfoMapper.update(entity);
    }

    @Override
    public int delete(Integer userTel) {
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
}
