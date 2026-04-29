-- ============================================================
-- 后台管理系统数据库表结构 (H2 Database)
-- ============================================================

-- ============================================================
-- 1. 系统管理模块
-- ============================================================

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT DEFAULT 1,
    agent_id BIGINT DEFAULT NULL,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sys_user_username ON sys_user(username);
CREATE INDEX idx_sys_user_agent_id ON sys_user(agent_id);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    permission_name VARCHAR(100) NOT NULL,
    permission_code VARCHAR(100),
    menu_type TINYINT DEFAULT 1,
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(50),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sys_permission_parent_id ON sys_permission(parent_id);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_sys_user_role ON sys_user_role(user_id, role_id);
CREATE INDEX idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_sys_user_role_role_id ON sys_user_role(role_id);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_sys_role_permission ON sys_role_permission(role_id, permission_id);
CREATE INDEX idx_sys_role_permission_role_id ON sys_role_permission(role_id);
CREATE INDEX idx_sys_role_permission_permission_id ON sys_role_permission(permission_id);

-- ============================================================
-- 2. 用户管理模块 - 代理商表
-- ============================================================

CREATE TABLE IF NOT EXISTS agent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_no VARCHAR(50) NOT NULL UNIQUE,
    agent_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    level INT DEFAULT 1,
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    id_card VARCHAR(30),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    status TINYINT DEFAULT 1,
    direct_merchant_count INT DEFAULT 0,
    total_merchant_count INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agent_parent_id ON agent(parent_id);
CREATE INDEX idx_agent_agent_no ON agent(agent_no);
CREATE INDEX idx_agent_level ON agent(level);

-- ============================================================
-- 3. 用户管理模块 - 商户表
-- ============================================================

CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_no VARCHAR(50) NOT NULL UNIQUE,
    merchant_name VARCHAR(100) NOT NULL,
    parent_id BIGINT NOT NULL,
    parent_path VARCHAR(500),
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    business_license VARCHAR(100),
    id_card VARCHAR(30),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchant_parent_id ON merchant(parent_id);
CREATE INDEX idx_merchant_merchant_no ON merchant(merchant_no);

-- ============================================================
-- 4. 产品管理模块 - 产品信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_no VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    product_type TINYINT,
    description VARCHAR(500),
    unit_price DECIMAL(10,2) DEFAULT 0.00,
    cost_price DECIMAL(10,2) DEFAULT 0.00,
    default_rate DECIMAL(5,4) DEFAULT 0.0000,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 5. 产品管理模块 - 机器信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    machine_no VARCHAR(50) NOT NULL UNIQUE,
    product_id BIGINT NOT NULL,
    sn VARCHAR(100),
    mac_address VARCHAR(50),
    merchant_id BIGINT DEFAULT NULL,
    agent_id BIGINT DEFAULT NULL,
    status TINYINT DEFAULT 0,
    purchase_date DATE,
    activate_time TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_machine_machine_no ON machine(machine_no);
CREATE INDEX idx_machine_product_id ON machine(product_id);
CREATE INDEX idx_machine_merchant_id ON machine(merchant_id);
CREATE INDEX idx_machine_agent_id ON machine(agent_id);
CREATE INDEX idx_machine_sn ON machine(sn);

-- ============================================================
-- 6. 代理产品关联表 (代理商可代理不同产品)
-- ============================================================

CREATE TABLE IF NOT EXISTS agent_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    profit_rate DECIMAL(5,4) DEFAULT 0.0000,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_agent_product ON agent_product(agent_id, product_id);
CREATE INDEX idx_agent_product_agent_id ON agent_product(agent_id);
CREATE INDEX idx_agent_product_product_id ON agent_product(product_id);

-- ============================================================
-- 7. 账户管理模块 - 代理账户表
-- ============================================================

CREATE TABLE IF NOT EXISTS agent_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    agent_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(15,2) DEFAULT 0.00,
    frozen_amount DECIMAL(15,2) DEFAULT 0.00,
    total_income DECIMAL(15,2) DEFAULT 0.00,
    total_withdraw DECIMAL(15,2) DEFAULT 0.00,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agent_account_agent_id ON agent_account(agent_id);

-- ============================================================
-- 8. 交易管理模块 - 通道管理表
-- ============================================================

CREATE TABLE IF NOT EXISTS channel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_no VARCHAR(50) NOT NULL UNIQUE,
    channel_name VARCHAR(100) NOT NULL,
    threshold_percent VARCHAR(4) NOT NULL,
    channel_type TINYINT,
    provider VARCHAR(100),
    rate DECIMAL(5,4) DEFAULT 0.0000,
    daily_limit DECIMAL(15,2) DEFAULT 0.00,
    single_limit DECIMAL(12,2) DEFAULT 0.00,
    merchant_limit DECIMAL(12,2) DEFAULT 0.00,
    status TINYINT DEFAULT 1,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 通道日交易统计表 (用于日限额控制)
CREATE TABLE IF NOT EXISTS channel_daily_stat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    total_amount DECIMAL(15,2) DEFAULT 0.00,
    total_count INT DEFAULT 0,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_channel_daily_stat ON channel_daily_stat(channel_id, stat_date);
CREATE INDEX idx_channel_daily_stat_channel_id ON channel_daily_stat(channel_id);
CREATE INDEX idx_channel_daily_stat_stat_date ON channel_daily_stat(stat_date);

-- ============================================================
-- 9. 交易管理模块 - 交易明细表
-- ============================================================

CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trans_no VARCHAR(50) NOT NULL UNIQUE,
    out_trans_no VARCHAR(100),
    merchant_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    product_id BIGINT,
    machine_id BIGINT,
    channel_id BIGINT,
    trans_type TINYINT,
    trans_amount DECIMAL(12,2) NOT NULL,
    fee_amount DECIMAL(10,2) DEFAULT 0.00,
    rate DECIMAL(5,4) DEFAULT 0.0000,
    profit_amount DECIMAL(10,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0,
    trans_time TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_trans_no ON transaction(trans_no);
CREATE INDEX idx_transaction_merchant_id ON transaction(merchant_id);
CREATE INDEX idx_transaction_agent_id ON transaction(agent_id);
CREATE INDEX idx_transaction_trans_time ON transaction(trans_time);
CREATE INDEX idx_transaction_status ON transaction(status);

-- ============================================================
-- 10. 分润信息表
-- ============================================================

CREATE TABLE IF NOT EXISTS profit_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profit_no VARCHAR(50) NOT NULL UNIQUE,
    transaction_id BIGINT NOT NULL,
    trans_no VARCHAR(50),
    agent_id BIGINT NOT NULL,
    product_id BIGINT,
    trans_amount DECIMAL(12,2),
    profit_rate DECIMAL(5,4) DEFAULT 0.0000,
    profit_amount DECIMAL(10,2) NOT NULL,
    level TINYINT DEFAULT 1,
    parent_agent_id BIGINT,
    status TINYINT DEFAULT 1,
    settle_time TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profit_share_profit_no ON profit_share(profit_no);
CREATE INDEX idx_profit_share_agent_id ON profit_share(agent_id);
CREATE INDEX idx_profit_share_transaction_id ON profit_share(transaction_id);
CREATE INDEX idx_profit_share_create_time ON profit_share(create_time);

-- ============================================================
-- 11. 账户明细表
-- ============================================================

CREATE TABLE IF NOT EXISTS account_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    detail_no VARCHAR(50) NOT NULL UNIQUE,
    agent_id BIGINT NOT NULL,
    account_type TINYINT,
    trans_type TINYINT,
    amount DECIMAL(12,2) NOT NULL,
    before_balance DECIMAL(15,2),
    after_balance DECIMAL(15,2),
    relate_no VARCHAR(50),
    remark VARCHAR(255),
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_detail_detail_no ON account_detail(detail_no);
CREATE INDEX idx_account_detail_agent_id ON account_detail(agent_id);
CREATE INDEX idx_account_detail_create_time ON account_detail(create_time);

-- ============================================================
-- 12. 对账单
-- ============================================================

CREATE TABLE IF NOT EXISTS statement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    statement_no VARCHAR(50) NOT NULL UNIQUE,
    agent_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    total_trans_amount DECIMAL(15,2) DEFAULT 0.00,
    total_trans_count INT DEFAULT 0,
    total_fee_amount DECIMAL(12,2) DEFAULT 0.00,
    total_profit_amount DECIMAL(12,2) DEFAULT 0.00,
    status TINYINT DEFAULT 0,
    confirm_time TIMESTAMP,
    remark VARCHAR(500),
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_statement ON statement(agent_id, stat_date);
CREATE INDEX idx_statement_statement_no ON statement(statement_no);
CREATE INDEX idx_statement_agent_id ON statement(agent_id);
CREATE INDEX idx_statement_stat_date ON statement(stat_date);

-- ============================================================
-- 13. 提现记录表
-- ============================================================

CREATE TABLE IF NOT EXISTS withdraw (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    withdraw_no VARCHAR(50) NOT NULL UNIQUE,
    agent_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    fee DECIMAL(10,2) DEFAULT 0.00,
    actual_amount DECIMAL(12,2),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    account_name VARCHAR(50),
    status TINYINT DEFAULT 0,
    reject_reason VARCHAR(255),
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    process_time TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_withdraw_withdraw_no ON withdraw(withdraw_no);
CREATE INDEX idx_withdraw_agent_id ON withdraw(agent_id);
CREATE INDEX idx_withdraw_status ON withdraw(status);