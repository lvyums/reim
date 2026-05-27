CREATE DATABASE vimanage_reim DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE vimanage_reim;

-- 删除业务表（如果存在）
DROP TABLE IF EXISTS reim_status_log;
DROP TABLE IF EXISTS reim_subsidy_calendar;
DROP TABLE IF EXISTS reim_subsidy;
DROP TABLE IF EXISTS reim_itinerary;
DROP TABLE IF EXISTS reim_form;

-- 删除基础数据表
DROP TABLE IF EXISTS base_business_type;
DROP TABLE IF EXISTS base_city;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS reim_department;
DROP TABLE IF EXISTS reim_company;

-- =============================================
-- 基础数据表（全部添加自增主键，原主键改为唯一业务键）
-- =============================================

-- 费用归属公司表
CREATE TABLE reim_company (
                              id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                              reim_company_id   VARCHAR(50) NOT NULL COMMENT '公司业务主键',
                              reim_company_no   VARCHAR(20) NOT NULL COMMENT '公司编号',
                              reim_company_name VARCHAR(100) NOT NULL COMMENT '公司名称',
                              status            TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
                              remark            VARCHAR(200) DEFAULT NULL COMMENT '备注',
                              create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              update_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              UNIQUE KEY uk_company_id (reim_company_id),
                              UNIQUE KEY uk_company_no (reim_company_no)
) COMMENT '费用归属公司';

-- 部门表
CREATE TABLE reim_department (
                                 id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                                 reim_department_id  VARCHAR(50) NOT NULL COMMENT '部门业务主键',
                                 reim_department_no  VARCHAR(20) NOT NULL COMMENT '部门编号',
                                 reim_department_name VARCHAR(100) NOT NULL COMMENT '部门名称',
                                 status              TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
                                 remark              VARCHAR(200) DEFAULT NULL COMMENT '备注',
                                 create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 update_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 UNIQUE KEY uk_dept_id (reim_department_id),
                                 UNIQUE KEY uk_dept_no (reim_department_no)
) COMMENT '报销部门';

-- 员工表（部门关联通过 department_id 字段，此处为 VARCHAR，指向 reim_department.reim_department_id）
CREATE TABLE reim_employee (
                               id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                               reimburser_id    VARCHAR(50) NOT NULL COMMENT '员工业务主键',
                               reimburser_no    VARCHAR(20) NOT NULL COMMENT '员工工号',
                               reimburser_name  VARCHAR(50) NOT NULL COMMENT '员工姓名',
                               department_id    VARCHAR(50) NOT NULL COMMENT '默认所属部门ID（关联 reim_department.reim_department_id）',
                               status           TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
                               remark           VARCHAR(200) DEFAULT NULL COMMENT '备注',
                               create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               UNIQUE KEY uk_emp_id (reimburser_id),
                               UNIQUE KEY uk_emp_no (reimburser_no)
) COMMENT '员工';

-- 城市表（新增自增主键，city_no 保留为业务唯一键）
CREATE TABLE base_city (
                           id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                           city_no    VARCHAR(20) NOT NULL COMMENT '城市编码',
                           city_name  VARCHAR(50) NOT NULL COMMENT '城市名称',
                           city_type  TINYINT NOT NULL COMMENT '城市类型 1:一线 2:二线 3:三线',
                           status     TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
                           remark     VARCHAR(200) DEFAULT NULL COMMENT '备注',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           UNIQUE KEY uk_city_no (city_no),
                           UNIQUE KEY uk_city_name (city_name)
) COMMENT '城市';

-- 业务类型表（树形结构）
CREATE TABLE base_business_type (
                                    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                                    business_type_id      VARCHAR(50) NOT NULL COMMENT '业务类型业务主键',
                                    business_type_no      VARCHAR(30) NOT NULL COMMENT '业务类型编号',
                                    business_type_name    VARCHAR(100) NOT NULL COMMENT '业务类型名称',
                                    there_subordinate_node TINYINT NOT NULL DEFAULT 0 COMMENT '是否有下级节点 0:无 1:有',
                                    superior_id           VARCHAR(50) DEFAULT NULL COMMENT '上级业务类型业务主键',
                                    level                 TINYINT NOT NULL DEFAULT 1 COMMENT '层级 1-一级 2-二级 3-三级',
                                    status                TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
                                    remark                VARCHAR(200) DEFAULT NULL COMMENT '备注',
                                    create_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    update_time           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    UNIQUE KEY uk_biz_type_id (business_type_id),
                                    UNIQUE KEY uk_biz_type_no (business_type_no)
) COMMENT '业务类型';

-- =============================================
-- 业务表（同样添加自增主键，原主键降级为业务唯一键）
-- =============================================

-- 报销单主表（原 id 重命名为 form_uid，新增 self_id 自增主键，外键引用仍用 form_uid）
CREATE TABLE reim_form (
                           form_uid                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键 报销单业务主键',
                           order_no                        VARCHAR(50) NOT NULL COMMENT '报销单号',
                           title                           VARCHAR(200) NOT NULL COMMENT '报销标题',
                           reason                          VARCHAR(500) DEFAULT NULL COMMENT '出差事由',
                           reimburser_id                   VARCHAR(50) NOT NULL COMMENT '报销人ID（关联 reim_employee.reimburser_id）',
                           department_id                   VARCHAR(50) NOT NULL COMMENT '报销部门ID（关联 reim_department.reim_department_id）',
                           company_id                      VARCHAR(50) NOT NULL COMMENT '费用归属公司ID（关联 reim_company.reim_company_id）',
                           business_type_id                VARCHAR(50) NOT NULL COMMENT '业务类型ID（关联 base_business_type.business_type_id）',
                           status                          TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-未提交 2-审批中 3-审批通过 4-已完成 5-已作废',
                           meal_allowance_total            INT NOT NULL DEFAULT 0 COMMENT '餐费补助合计(分)',
                           transport_allowance_total       INT NOT NULL DEFAULT 0 COMMENT '交通补助合计(分)',
                           communication_allowance_total   INT NOT NULL DEFAULT 0 COMMENT '通讯补助合计(分)',
                           allowance_total                 INT NOT NULL DEFAULT 0 COMMENT '补助总金额(分)',
                           remark                          TEXT DEFAULT NULL COMMENT '备注',
                           version                         INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                           deleted                         TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
                           create_time                     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time                     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           UNIQUE KEY uk_form_uid (form_uid),
                           UNIQUE KEY uk_order_no (order_no),
                           INDEX idx_reimburser_id (reimburser_id),
                           INDEX idx_status (status),
                           INDEX idx_create_time (create_time),
                           CONSTRAINT chk_form_meal CHECK (meal_allowance_total >= 0),
                           CONSTRAINT chk_form_trans CHECK (transport_allowance_total >= 0),
                           CONSTRAINT chk_form_comm CHECK (communication_allowance_total >= 0),
                           CONSTRAINT chk_form_total CHECK (allowance_total >= 0),
                           CONSTRAINT chk_form_status CHECK (status IN (1,2,3,4,5))
) COMMENT '报销单主表（金额单位：分）';

-- 补录行程表（原 id 改为 itinerary_uid，新增自增 id，form_id 仍引用 reim_form.form_uid）
CREATE TABLE reim_itinerary (
                                itinerary_uid     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键,行程业务主键（原 id）',
                                form_id           BIGINT UNSIGNED NOT NULL COMMENT '报销单业务主键（关联 reim_form.form_uid）',
                                traveler_id       VARCHAR(50) NOT NULL COMMENT '出行人ID（关联 reim_employee.reimburser_id）',
                                departure_city_no VARCHAR(20) NOT NULL COMMENT '出发城市编码',
                                arrival_city_no   VARCHAR(20) NOT NULL COMMENT '到达城市编码',
                                departure_date    DATE NOT NULL COMMENT '出发日期',
                                arrival_date      DATE NOT NULL COMMENT '到达日期',
                                description       VARCHAR(500) DEFAULT NULL COMMENT '行程说明',
                                deleted           TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-未删 1-已删',
                                create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                UNIQUE KEY uk_itinerary_uid (itinerary_uid),
                                UNIQUE INDEX uk_form_traveler_date (form_id, traveler_id, departure_date, arrival_date, deleted),
                                INDEX idx_form_id (form_id),
                                CONSTRAINT chk_itinerary_date1 CHECK (arrival_date >= departure_date)
) COMMENT '补录行程';

-- 补助信息表（原 id 改为 subsidy_uid，新增自增 id，itinerary_id 引用 reim_itinerary.itinerary_uid）
CREATE TABLE reim_subsidy (
                              subsidy_uid        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键,补助信息业务主键（原 id）',
                              form_id           BIGINT UNSIGNED NOT NULL COMMENT '报销单业务主键（关联 reim_form.form_uid）',
                              itinerary_id      BIGINT UNSIGNED NOT NULL COMMENT '行程业务主键（关联 reim_itinerary.itinerary_uid，一对一）',
                              traveler_id       VARCHAR(50) NOT NULL COMMENT '出行人ID',
                              start_date        DATE NOT NULL COMMENT '补助开始日期',
                              end_date          DATE NOT NULL COMMENT '补助结束日期',
                              subsidy_city_no   VARCHAR(20) NOT NULL COMMENT '补助城市编码',
                              apply_amount      INT NOT NULL DEFAULT 0 COMMENT '申请金额(分)',
                              subsidy_amount    INT NOT NULL DEFAULT 0 COMMENT '实际补助(分)',
                              deleted           TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                              create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              update_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              UNIQUE KEY uk_subsidy_uid (subsidy_uid),
                              UNIQUE INDEX uk_itinerary_id (itinerary_id, deleted),
                              INDEX idx_form_id (form_id),
                              CONSTRAINT chk_subsidy_apply CHECK (apply_amount >= 0),
                              CONSTRAINT chk_subsidy_actual CHECK (subsidy_amount >= 0),
                              CONSTRAINT chk_subsidy_date CHECK (end_date >= start_date)
) COMMENT '补助信息（金额单位：分）';
-- 删除旧唯一索引
ALTER TABLE reim_subsidy DROP INDEX uk_itinerary_id;

-- 补助日历明细表（原 id 改为 calendar_uid，新增自增 id，subsidy_id 引用 reim_subsidy.subsidy_uid）
CREATE TABLE reim_subsidy_calendar (
                                       calendar_uid               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键,日历明细业务主键（原 id）',
                                       subsidy_id                 BIGINT UNSIGNED NOT NULL COMMENT '补助信息业务主键（关联 reim_subsidy.subsidy_uid）',
                                       date                       DATE NOT NULL COMMENT '具体日期',
                                       city_no                    VARCHAR(20) NOT NULL COMMENT '补助城市编码',
                                       meal_selected              TINYINT(1) NOT NULL DEFAULT 0 COMMENT '餐补是否勾选',
                                       meal_standard_amount       INT NOT NULL DEFAULT 0 COMMENT '餐补标准(分)',
                                       meal_actual_amount         INT NOT NULL DEFAULT 0 COMMENT '餐补实际(分)',
                                       transport_selected         TINYINT(1) NOT NULL DEFAULT 0 COMMENT '交补是否勾选',
                                       transport_standard_amount  INT NOT NULL DEFAULT 0 COMMENT '交补标准(分)',
                                       transport_actual_amount    INT NOT NULL DEFAULT 0 COMMENT '交补实际(分)',
                                       comm_selected              TINYINT(1) NOT NULL DEFAULT 0 COMMENT '通补是否勾选',
                                       comm_standard_amount       INT NOT NULL DEFAULT 0 COMMENT '通补标准(分)',
                                       comm_actual_amount         INT NOT NULL DEFAULT 0 COMMENT '通补实际(分)',
                                       deleted                    TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                                       create_time                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       update_time                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       UNIQUE KEY uk_calendar_uid (calendar_uid),
                                       UNIQUE INDEX uk_subsidy_date (subsidy_id, date, deleted),
                                       INDEX idx_subsidy_id (subsidy_id),
                                       CONSTRAINT chk_cal_meal_std CHECK (meal_standard_amount >= 0),
                                       CONSTRAINT chk_cal_meal_act CHECK (meal_actual_amount >= 0),
                                       CONSTRAINT chk_cal_trans_std CHECK (transport_standard_amount >= 0),
                                       CONSTRAINT chk_cal_trans_act CHECK (transport_actual_amount >= 0),
                                       CONSTRAINT chk_cal_comm_std CHECK (comm_standard_amount >= 0),
                                       CONSTRAINT chk_cal_comm_act CHECK (comm_actual_amount >= 0)
) COMMENT '补助日历明细（金额单位：分）';



-- 方案A
-- 1. 改三个表的 deleted 列类型为 BIGINT
--ALTER TABLE reim_itinerary MODIFY deleted BIGINT NOT NULL DEFAULT 0;
--ALTER TABLE reim_subsidy MODIFY deleted BIGINT NOT NULL DEFAULT 0;
--ALTER TABLE reim_subsidy_calendar MODIFY deleted BIGINT NOT NULL DEFAULT 0;

-- 2. 后端删除 SQL 改成这三条
-- 原来：SET deleted = 1 WHERE ...
--UPDATE reim_itinerary SET deleted = itinerary_uid WHERE itinerary_uid = ?;
--UPDATE reim_subsidy SET deleted = subsidy_uid WHERE subsidy_uid = ?;
--UPDATE reim_subsidy_calendar SET deleted = calendar_uid WHERE calendar_uid = ?;


-- 方案B
-- 1. deleted 改为可为 NULL，默认 0
ALTER TABLE reim_itinerary MODIFY deleted BIGINT DEFAULT 0;
ALTER TABLE reim_subsidy MODIFY deleted BIGINT DEFAULT 0;
ALTER TABLE reim_subsidy_calendar MODIFY deleted BIGINT DEFAULT 0;

-- 2. 已删除的数据清掉旧值
UPDATE reim_itinerary SET deleted = NULL WHERE deleted != 0;
UPDATE reim_subsidy SET deleted = NULL WHERE deleted != 0;
UPDATE reim_subsidy_calendar SET deleted = NULL WHERE deleted != 0;

-- 3. 后端删除 SQL 改一行
UPDATE reim_itinerary SET deleted = NULL WHERE itinerary_uid = ?;
UPDATE reim_subsidy SET deleted = NULL WHERE subsidy_uid = ?;
UPDATE reim_subsidy_calendar SET deleted = NULL WHERE calendar_uid = ?;

-- 4. 查询存活数据不变
-- SELECT ... WHERE deleted = 0

-- 3. 查询存活数据不变
-- SELECT ... WHERE deleted = 0

-- 报销单状态变更日志表（原 id 改为 log_uid，新增自增 id）
CREATE TABLE reim_status_log (
                                 log_uid         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键,日志业务主键（原 id）',
                                 form_id         BIGINT UNSIGNED NOT NULL COMMENT '报销单业务主键（关联 reim_form.form_uid）',
                                 from_status     TINYINT DEFAULT NULL COMMENT '原状态',
                                 to_status       TINYINT NOT NULL COMMENT '新状态',
                                 operator_id     VARCHAR(50) NOT NULL COMMENT '操作人ID',
                                 operate_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                 remark          VARCHAR(200) DEFAULT NULL COMMENT '备注',
                                 create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 UNIQUE KEY uk_log_uid (log_uid),
                                 INDEX idx_form_id (form_id),
                                 INDEX idx_operate_time (operate_time)
) COMMENT '报销单状态变更日志';


-- =============================================
-- 初始化数据（无需指定自增 id，原业务主键仍插入）
-- =============================================

INSERT INTO reim_company (reim_company_id, reim_company_no, reim_company_name, status) VALUES
                                                                                           ('1C54557F1782E000', '0407', '胜意科技北京分公司', 1),
                                                                                           ('19218A262C976000', '0408', '胜意科技上海分公司', 1),
                                                                                           ('1C61686865DA8000', '0409', '胜意科技武汉分公司', 1),
                                                                                           ('1717271D1DA15000', '0410', '胜意科技杭州分公司', 1),
                                                                                           ('16AE93CC7EF92002', '0411', '胜意科技荆州分公司', 1);

INSERT INTO reim_department (reim_department_id, reim_department_no, reim_department_name, status) VALUES
                                                                                                       ('13AB8D7B52A9B002', '072001', '客户成功事业部', 1),
                                                                                                       ('13BFD31C6029A002', '072002', '企业消费事业部', 1),
                                                                                                       ('14515BB4BFB92003', '072003', '企业费控事业部', 1),
                                                                                                       ('19206611C47A6000', '072004', '集采事业部', 1),
                                                                                                       ('19D32F9FE9647000', '072005', '航旅事业部', 1),
                                                                                                       ('13C7E2BAE0393001', '072006', '运营事业部', 1),
                                                                                                       ('14055D22BB808001', '072007', '营销事业部', 1);

INSERT INTO reim_employee (reimburser_id, reimburser_no, reimburser_name, department_id, status) VALUES
                                                                                                     ('13AB3A3F72409002', '74541', '徐年年', '13AB8D7B52A9B002', 1),
                                                                                                     ('13AB498CC6409002', '74008', '郑雨雪', '13BFD31C6029A002', 1),
                                                                                                     ('13AB4A56BB009002', '21552', '邹薇', '14515BB4BFB92003', 1),
                                                                                                     ('13AB591FE8009002', '80681', '王成军', '19206611C47A6000', 1),
                                                                                                     ('13AB77281A408001', '89899', '潘展飞', '19D32F9FE9647000', 1),
                                                                                                     ('13AB7925EB808001', '10503', '姜林', '13C7E2BAE0393001', 1);

INSERT INTO base_city (city_no, city_name, city_type, status) VALUES
                                                                  ('10119', '北京', 1, 1),
                                                                  ('10621', '上海', 1, 1),
                                                                  ('10458', '武汉', 2, 1),
                                                                  ('10216', '杭州', 2, 1),
                                                                  ('10455', '荆州', 3, 1);

INSERT INTO base_business_type (business_type_id, business_type_no, business_type_name, there_subordinate_node, superior_id, level, status) VALUES
                                                                                                                                                ('18F0916A8C2C4000', '1001001', '员工差旅活动', 1, NULL, 1, 1),
                                                                                                                                                ('18F091913EEC4000', '100100101', '境内出差', 1, '18F0916A8C2C4000', 2, 1),
                                                                                                                                                ('1B5FEB7DD4396000', '10010010101', '项目出差', 0, '18F091913EEC4000', 3, 1),
                                                                                                                                                ('1A92E43082EFC000', '10010010102', '市场拓展出差', 0, '18F091913EEC4000', 3, 1),
                                                                                                                                                ('13AB3A4138008001', '100100102', '境外出差', 1, '18F0916A8C2C4000', 2, 1),
                                                                                                                                                ('13AB3A4248008002', '10010010201', '国外考察', 0, '13AB3A4138008001', 3, 1),
                                                                                                                                                ('13AB3A4154008001', '10010010202', '售后维护出差', 0, '13AB3A4138008001', 3, 1),
                                                                                                                                                ('13AB3A4172008001', '1001002', '人力资源', 1, NULL, 1, 1),
                                                                                                                                                ('13AB3A418F808001', '100100201', '个人团队培训', 0, '13AB3A4172008001', 2, 1),
                                                                                                                                                ('13AB3A41AC408001', '100100202', '招聘会', 0, '13AB3A4172008001', 2, 1),
                                                                                                                                                ('13AB3A41CD808002', '1001003', '员工福利', 1, NULL, 1, 1),
                                                                                                                                                ('13AB3A41ED408002', '100100301', '员工旅游', 0, '13AB3A41CD808002', 2, 1),
                                                                                                                                                ('13AB3A420CC08002', '100100302', '员工团建', 0, '13AB3A41CD808002', 2, 1),
                                                                                                                                                ('13AB3A422A808001', '100100303', '员工体检', 0, '13AB3A41CD808002', 2, 1);













-- =============================================
-- 业务表测试数据插入（基于已有基础数据）
-- =============================================

-- 1. 报销单主表 reim_form
-- 注意：form_uid 为自增主键，此处手动指定值以便后续关联，实际生产可不指定
INSERT INTO reim_form (
    form_uid, order_no, title, reason, reimburser_id, department_id, company_id,
    business_type_id, status, meal_allowance_total, transport_allowance_total,
    communication_allowance_total, allowance_total, remark, version
) VALUES
-- 报销单1：徐年年（北京分公司/客户成功事业部），境内项目出差，状态：审批通过
(1001, 'RE20240001', '2024年3月北京-上海项目出差', '参与客户现场需求调研', '13AB3A3F72409002', '13AB8D7B52A9B002', '1C54557F1782E000',
 '1B5FEB7DD4396000', 3, 2000, 1000, 0, 3000, '住宿费另由公司承担', 0),
-- 报销单2：郑雨雪（上海分公司/企业消费事业部），市场拓展出差，状态：审批中
(1002, 'RE20240002', '2024年4月杭州市场推广活动', '参加行业展会并拓展客户', '13AB498CC6409002', '13BFD31C6029A002', '19218A262C976000',
 '1A92E43082EFC000', 2, 1500, 800, 0, 2300, '展会门票另行报销', 0),
-- 报销单3：邹薇（武汉分公司/企业费控事业部），个人团队培训，状态：未提交
(1003, 'RE20240003', '2024年5月武汉敏捷开发培训', '参加Scrum Master认证培训', '13AB4A56BB009002', '14515BB4BFB92003', '1C61686865DA8000',
 '13AB3A418F808001', 1, 0, 0, 0, 0, '培训费发票已上传', 0);

-- 2. 补录行程表 reim_itinerary
INSERT INTO reim_itinerary (
    itinerary_uid, form_id, traveler_id, departure_city_no, arrival_city_no,
    departure_date, arrival_date, description
) VALUES
-- 行程属于报销单1001：北京->上海，出差2天
(2001, 1001, '13AB3A3F72409002', '10119', '10621', '2024-03-10', '2024-03-11', '参与需求调研与方案设计'),
-- 行程属于报销单1002：上海->杭州，出差1天
(2002, 1002, '13AB498CC6409002', '10621', '10216', '2024-04-05', '2024-04-05', '展会现场支持'),
-- 行程属于报销单1003：武汉->荆州，出差1天（培训地点在荆州）
(2003, 1003, '13AB4A56BB009002', '10458', '10455', '2024-05-20', '2024-05-20', '前往荆州参加培训');

-- 3. 补助信息表 reim_subsidy
INSERT INTO reim_subsidy (
    subsidy_uid, form_id, itinerary_id, traveler_id, start_date, end_date,
    subsidy_city_no, apply_amount, subsidy_amount
) VALUES
-- 补助对应行程2001，补助城市为上海（到达城市）
(3001, 1001, 2001, '13AB3A3F72409002', '2024-03-10', '2024-03-11', '10621', 3000, 3000),
-- 补助对应行程2002，补助城市为杭州
(3002, 1002, 2002, '13AB498CC6409002', '2024-04-05', '2024-04-05', '10216', 800, 0),   -- 实际补助尚未审批
-- 补助对应行程2003，补助城市为荆州
(3003, 1003, 2003, '13AB4A56BB009002', '2024-05-20', '2024-05-20', '10455', 0, 0);   -- 未提交，暂未申请补助

-- 4. 补助日历明细表 reim_subsidy_calendar
-- 为每个补助生成具体的每天补助明细（餐补、交补、通补的标准和实际金额）
INSERT INTO reim_subsidy_calendar (
    calendar_uid, subsidy_id, date, city_no,
    meal_selected, meal_standard_amount, meal_actual_amount,
    transport_selected, transport_standard_amount, transport_actual_amount,
    comm_selected, comm_standard_amount, comm_actual_amount
) VALUES
-- 补助3001的第一天（2024-03-10，上海）
(4001, 3001, '2024-03-10', '10621',
 1, 1000, 1000,   -- 餐补选，标准1000分=10元，实发1000
 1, 500, 500,     -- 交通补贴选，标准500分=5元，实发500
 0, 0, 0),
-- 补助3001的第二天（2024-03-11，上海）
(4002, 3001, '2024-03-11', '10621',
 1, 1000, 1000,
 1, 500, 500,
 0, 0, 0),
-- 补助3002（2024-04-05，杭州）
(4003, 3002, '2024-04-05', '10216',
 1, 800, 0,   -- 餐补选但未实发（审批中）
 1, 400, 0,
 0, 0, 0),
-- 补助3003（2024-05-20，荆州）
(4004, 3003, '2024-05-20', '10455',
 0, 600, 0,
 0, 300, 0,
 0, 0, 0);

-- 5. 报销单状态变更日志表 reim_status_log
INSERT INTO reim_status_log (
    log_uid, form_id, from_status, to_status, operator_id, operate_time, remark
) VALUES
-- 报销单1001 状态变更记录：未提交→审批中（2024-03-12），审批中→审批通过（2024-03-15）
(5001, 1001, 1, 2, '13AB3A3F72409002', '2024-03-12 09:23:00', '员工提交报销单'),
(5002, 1001, 2, 3, '19206611C47A6000', '2024-03-15 14:17:00', '部门经理审批通过'),
-- 报销单1002 状态变更记录：未提交→审批中（2024-04-06）
(5003, 1002, 1, 2, '13AB498CC6409002', '2024-04-06 10:05:00', '员工提交，待经理审批'),
-- 报销单1003 暂无状态变更（仍未提交），故无记录
(5004, 1003, 1, 1, '13AB4A56BB009002', '2024-05-10 08:30:00', '草稿保存');