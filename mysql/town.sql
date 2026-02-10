CREATE DATABASE IF NOT EXISTS town
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE town;

-- 用户信息
CREATE TABLE user_info
(
    user_tel         BIGINT PRIMARY KEY,
    user_name        VARCHAR(255) NULL,
    user_pwd         VARCHAR(255) NULL,
    user_town        VARCHAR(255) NULL,
    user_power       INT          NULL,
    user_create_time BIGINT       NULL,
    flag_type        INT          NULL
) ENGINE = InnoDB;

-- 公告信息
CREATE TABLE notice_info
(
    notice_id          INT PRIMARY KEY AUTO_INCREMENT,
    notice_type        INT          NULL,
    notice_create_time BIGINT       NULL,
    notice_title       VARCHAR(255) NULL,
    notice_context     TEXT         NULL,
    writer_tel         BIGINT       NULL,
    writer_name        VARCHAR(32)  NULL,
    is_top             BOOLEAN      NULL,
    is_accept_read     BOOLEAN      NULL,
    notice_att         MEDIUMBLOB   NULL
) ENGINE = InnoDB;

-- 用户已读公告
CREATE TABLE user_read_notice_info
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    user_tel  BIGINT NULL,
    notice_id INT    NULL,
    read_time BIGINT NULL
) ENGINE = InnoDB;

-- 更新信息
CREATE TABLE update_info
(
    update_id       INT PRIMARY KEY AUTO_INCREMENT,
    info_id         BIGINT      NULL,
    info_type       INT         NULL,
    before_msg      MEDIUMBLOB  NULL,
    after_msg       MEDIUMBLOB  NULL,
    update_time     BIGINT      NULL,
    update_user_tel BIGINT      NULL,
    update_name     VARCHAR(32) NULL
) ENGINE = InnoDB;

-- 问题信息
CREATE TABLE question_info
(
    question_id        INT PRIMARY KEY AUTO_INCREMENT,
    question_type      INT        NULL,
    question_ctx       TEXT       NULL,
    quest_photo        MEDIUMBLOB NULL,
    question_write_tel BIGINT     NULL,
    node_type          INT        NULL,
    choice_user        BIGINT     NULL,
    question_time      BIGINT     NULL
) ENGINE = InnoDB;

-- 问题处理信息
CREATE TABLE question_handling_info
(
    handle_id       INT PRIMARY KEY AUTO_INCREMENT,
    question_id     INT    NULL,
    handling_type   INT    NULL,
    handle_user_tel BIGINT NULL,
    handle_ctx      TEXT   NULL,
    handle_time     BIGINT NULL
) ENGINE = InnoDB;

-- 通知用户信息
CREATE TABLE notify_user_info
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    user_tel BIGINT NULL,
    msg_type INT    NULL,
    msg_ctx  BLOB   NULL
) ENGINE = InnoDB;

-- 学习内容
CREATE TABLE study_info
(
    study_id          INT PRIMARY KEY AUTO_INCREMENT,
    study_type        INT     NULL,
    study_create_time BIGINT  NULL,
    study_title       TEXT    NULL,
    study_tip         TEXT    NULL,
    study_content     TEXT    NULL,
    is_open           BOOLEAN NULL,
    is_top            BOOLEAN NULL,
    read_count        INT     NULL
) ENGINE = InnoDB;

-- 用户收藏学习内容
CREATE TABLE user_star_study_info
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    user_tel BIGINT NULL,
    study_id INT    NULL
) ENGINE = InnoDB;

-- 家庭人员信
CREATE TABLE people_info
(
    people_card_id  VARCHAR(32) PRIMARY KEY,
    people_name     VARCHAR(255) NULL,
    people_house_id VARCHAR(64)  NULL,
    people_ctx      TEXT         NULL
) ENGINE = InnoDB;

-- 家庭信息修改申请
CREATE TABLE people_update_apply
(
    apply_id          INT PRIMARY KEY AUTO_INCREMENT,
    apply_user_id     BIGINT NULL,
    apply_create_time BIGINT NULL,
    new_people        BLOB   NULL
) ENGINE = InnoDB;
