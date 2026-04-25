-- ============================================================
-- 后台管理系统数据库表结构
-- 数据库名: tradesys
-- 字符集: utf8mb4
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS tradesys DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tradesys;

-- ============================================================
-- 1. 系统管理模块
-- ============================================================

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) COMMENT '权限编码',
    menu_type TINYINT DEFAULT 1 COMMENT '类型: 1-菜单, 2-按钮',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(50) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 2. 用户管理模块 - 代理商表
-- ============================================================

CREATE TABLE IF NOT EXISTS agent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    agent_no VARCHAR(50) NOT NULL UNIQUE COMMENT '代理商编号',
    agent_name VARCHAR(100) NOT NULL COMMENT '代理商名称',
    parent_id BIGINT DEFAULT 0 COMMENT '上级代理商ID(0表示顶级)',
    level INT DEFAULT 1 COMMENT '代理商级别: 1-一级, 2-二级, 3-三级...',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    id_card VARCHAR(30) COMMENT '身份证号',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    direct_merchant_count INT DEFAULT 0 COMMENT '直属商户数量',
    total_merchant_count INT DEFAULT 0 COMMENT '总商户数量(含下级)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_agent_no (agent_no),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代理商表';

-- ============================================================
-- 3. 用户管理模块 - 商户表
-- ============================================================

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    merchant_no VARCHAR(50) NOT NULL UNIQUE COMMENT '商户编号',
    merchant_name VARCHAR(100) NOT NULL COMMENT '商户名称',
    parent_id BIGINT NOT NULL COMMENT '所属代理商ID',
    parent_path VARCHAR(500) COMMENT '层级路径(如: /1/5/10/)',
    contact_name VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '经营地址',
    business_license VARCHAR(100) COMMENT '营业执照号',
    id_card VARCHAR(30) COMMENT '法人身份证号',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_merchant_no (merchant_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- ============================================================
-- 4. 产品管理模块 - 产品信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    product_no VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编号',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_type TINYINT COMMENT '产品类型: 1-POS机, 2-聚合支付, 3-其他',
    description VARCHAR(500) COMMENT '产品描述',
    unit_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    cost_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价',
    default_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '默认费率',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品信息表';

-- ============================================================
-- 5. 产品管理模块 - 机器信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS machine (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    machine_no VARCHAR(50) NOT NULL UNIQUE COMMENT '机器编号',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    sn VARCHAR(100) COMMENT 'SN码',
    mac_address VARCHAR(50) COMMENT 'MAC地址',
    merchant_id BIGINT DEFAULT NULL COMMENT '绑定商户ID(空表示未绑定)',
    agent_id BIGINT DEFAULT NULL COMMENT '所属代理商ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-库存, 1-已出库, 2-已绑定, 3-故障',
    purchase_date DATE COMMENT '采购日期',
    activate_time DATETIME COMMENT '激活时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_machine_no (machine_no),
    INDEX idx_product_id (product_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_sn (sn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机器信息表';

-- ============================================================
-- 6. 代理产品关联表 (代理商可代理不同产品)
-- ============================================================

CREATE TABLE IF NOT EXISTS agent_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    agent_id BIGINT NOT NULL COMMENT '代理商ID',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    profit_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '分润费率(如0.0012表示万12)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_agent_product (agent_id, product_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代理产品关联表';

-- ============================================================
-- 7. 账户管理模块 - 代理账户表
-- ============================================================

CREATE TABLE IF NOT EXISTS agent_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    agent_id BIGINT NOT NULL UNIQUE COMMENT '代理商ID',
    balance DECIMAL(15,2) DEFAULT 0.00 COMMENT '账户余额',
    frozen_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '冻结金额',
    total_income DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计收益',
    total_withdraw DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计提现',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-冻结, 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_agent_id (agent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代理账户表';

-- ============================================================
-- 8. 交易管理模块 - 通道管理表
-- ============================================================

CREATE TABLE IF NOT EXISTS channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    channel_no VARCHAR(50) NOT NULL UNIQUE COMMENT '通道编号',
    channel_name VARCHAR(100) NOT NULL COMMENT '通道名称',
    channel_type TINYINT COMMENT '通道类型: 1-微信, 2-支付宝, 3-银联, 4-其他',
    provider VARCHAR(100) COMMENT '服务商',
    rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '通道费率',
    daily_limit DECIMAL(15,2) DEFAULT 0.00 COMMENT '日交易额上限(0表示不限制)',
    single_limit DECIMAL(12,2) DEFAULT 0.00 COMMENT '单笔限额(0表示不限制)',
    merchant_limit DECIMAL(12,2) DEFAULT 0.00 COMMENT '商户单笔限额',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-关闭, 1-开启',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通道管理表';

-- 通道日交易统计表 (用于日限额控制)
CREATE TABLE IF NOT EXISTS channel_daily_stat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    channel_id BIGINT NOT NULL COMMENT '通道ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    total_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '当日交易总额',
    total_count INT DEFAULT 0 COMMENT '当日交易笔数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_channel_date (channel_id, stat_date),
    INDEX idx_channel_id (channel_id),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通道日交易统计表';

-- ============================================================
-- 9. 交易管理模块 - 交易明细表
-- ============================================================

CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    trans_no VARCHAR(50) NOT NULL UNIQUE COMMENT '交易流水号',
    out_trans_no VARCHAR(100) COMMENT '外部交易号',
    merchant_id BIGINT NOT NULL COMMENT '商户ID',
    agent_id BIGINT NOT NULL COMMENT '所属代理商ID',
    product_id BIGINT COMMENT '产品ID',
    machine_id BIGINT COMMENT '机器ID',
    channel_id BIGINT COMMENT '通道ID',
    trans_type TINYINT COMMENT '交易类型: 1-消费, 2-撤销, 3-退款',
    trans_amount DECIMAL(12,2) NOT NULL COMMENT '交易金额',
    fee_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费金额',
    rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '交易费率',
    profit_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '分润金额',
    status TINYINT DEFAULT 0 COMMENT '交易状态: 0-处理中, 1-成功, 2-失败, 3-已撤销',
    trans_time DATETIME COMMENT '交易时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_trans_no (trans_no),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_trans_time (trans_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易明细表';

-- ============================================================
-- 10. 分润信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS profit_share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    profit_no VARCHAR(50) NOT NULL UNIQUE COMMENT '分润流水号',
    transaction_id BIGINT NOT NULL COMMENT '交易ID',
    trans_no VARCHAR(50) COMMENT '交易流水号',
    agent_id BIGINT NOT NULL COMMENT '获得分润的代理商ID',
    product_id BIGINT COMMENT '产品ID',
    trans_amount DECIMAL(12,2) COMMENT '交易金额',
    profit_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '分润费率',
    profit_amount DECIMAL(10,2) NOT NULL COMMENT '分润金额',
    level TINYINT DEFAULT 1 COMMENT '分润层级(1表示直接代理)',
    parent_agent_id BIGINT COMMENT '上级代理商ID(用于层级展示)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-待结算, 1-已结算',
    settle_time DATETIME COMMENT '结算时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_profit_no (profit_no),
    INDEX idx_agent_id (agent_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分润信息表';

-- ============================================================
-- 11. 账户明细表
-- ============================================================

CREATE TABLE IF NOT EXISTS account_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    detail_no VARCHAR(50) NOT NULL UNIQUE COMMENT '明细单号',
    agent_id BIGINT NOT NULL COMMENT '代理商ID',
    account_type TINYINT COMMENT '账户类型: 1-分润账户, 2-提现账户',
    trans_type TINYINT COMMENT '交易类型: 1-分润收入, 2-提现, 3-提现退款, 4-调账',
    amount DECIMAL(12,2) NOT NULL COMMENT '变动金额(正为增加,负为减少)',
    before_balance DECIMAL(15,2) COMMENT '变动前余额',
    after_balance DECIMAL(15,2) COMMENT '变动后余额',
    relate_no VARCHAR(50) COMMENT '关联单号(分润号/提现单号)',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_detail_no (detail_no),
    INDEX idx_agent_id (agent_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户明细表';

-- ============================================================
-- 12. 对账单
-- ============================================================

CREATE TABLE IF NOT EXISTS statement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    statement_no VARCHAR(50) NOT NULL UNIQUE COMMENT '对账单号',
    agent_id BIGINT NOT NULL COMMENT '代理商ID',
    stat_date DATE NOT NULL COMMENT '对账日期',
    total_trans_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '交易总额',
    total_trans_count INT DEFAULT 0 COMMENT '交易笔数',
    total_fee_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '手续费总额',
    total_profit_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '分润总额',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待确认, 1-已确认, 2-有异议',
    confirm_time DATETIME COMMENT '确认时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_agent_date (agent_id, stat_date),
    INDEX idx_statement_no (statement_no),
    INDEX idx_agent_id (agent_id),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账单';

-- ============================================================
-- 13. 提现记录表
-- ============================================================

CREATE TABLE IF NOT EXISTS withdraw (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    withdraw_no VARCHAR(50) NOT NULL UNIQUE COMMENT '提现单号',
    agent_id BIGINT NOT NULL COMMENT '代理商ID',
    amount DECIMAL(12,2) NOT NULL COMMENT '提现金额',
    fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '提现手续费',
    actual_amount DECIMAL(12,2) COMMENT '实际到账金额',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    account_name VARCHAR(50) COMMENT '账户名',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-申请中, 1-处理中, 2-已完成, 3-已拒绝',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    process_time DATETIME COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_withdraw_no (withdraw_no),
    INDEX idx_agent_id (agent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现记录表';
