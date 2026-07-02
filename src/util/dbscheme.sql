-- ============================================
--   HOSTEL MANAGEMENT SYSTEM - SCHEMA
-- ============================================

CREATE DATABASE IF NOT EXISTS hostel_management;
USE hostel_management;

-- ── Person (base table) ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS person (
    id         INT          PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    age        INT,
    email      VARCHAR(100),
    phone      VARCHAR(20),
    address    TEXT,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ── Admin ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admin (
    id         INT          PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  DEFAULT 'Admin',
    last_login TIMESTAMP    NULL,
    FOREIGN KEY (id) REFERENCES person(id)
);

-- ── Room ──────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS room (
    roomId   VARCHAR(20)    PRIMARY KEY,
    capacity INT            NOT NULL DEFAULT 2,
    occupied INT            DEFAULT 0,
    type     VARCHAR(50)    DEFAULT 'Double',
    price    DECIMAL(10,2)  DEFAULT 4000.00
);

-- ── Student ───────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS student (
    id              INT          PRIMARY KEY,
    course          VARCHAR(100),
    roomId          VARCHAR(20),
    enrollment_date DATE,
    status          VARCHAR(20)  DEFAULT 'Active',
    password        VARCHAR(255),
    username        VARCHAR(50)  UNIQUE,
    regNumber       VARCHAR(50)  UNIQUE,
    FOREIGN KEY (id)     REFERENCES person(id),
    FOREIGN KEY (roomId) REFERENCES room(roomId)
);

-- ── Payment ───────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payment (
    paymentId     VARCHAR(50)   PRIMARY KEY,
    studentId     VARCHAR(50)   NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    paymentDate   DATE          NOT NULL,
    dueDate       DATE,
    status        VARCHAR(20)   DEFAULT 'Pending',
    paymentMethod VARCHAR(50),
    FOREIGN KEY (studentId) REFERENCES student(id) ON DELETE CASCADE
);

-- ── Seed: Insert all 200 rooms (101-300), Double, capacity 2, KSh 4,000 ──────
INSERT IGNORE INTO room (roomId, capacity, occupied, type, price) VALUES
('101',2,0,'Double',4000.00),('102',2,0,'Double',4000.00),('103',2,0,'Double',4000.00),
('104',2,0,'Double',4000.00),('105',2,0,'Double',4000.00),('106',2,0,'Double',4000.00),
('107',2,0,'Double',4000.00),('108',2,0,'Double',4000.00),('109',2,0,'Double',4000.00),
('110',2,0,'Double',4000.00),('111',2,0,'Double',4000.00),('112',2,0,'Double',4000.00),
('113',2,0,'Double',4000.00),('114',2,0,'Double',4000.00),('115',2,0,'Double',4000.00),
('116',2,0,'Double',4000.00),('117',2,0,'Double',4000.00),('118',2,0,'Double',4000.00),
('119',2,0,'Double',4000.00),('120',2,0,'Double',4000.00),('121',2,0,'Double',4000.00),
('122',2,0,'Double',4000.00),('123',2,0,'Double',4000.00),('124',2,0,'Double',4000.00),
('125',2,0,'Double',4000.00),('126',2,0,'Double',4000.00),('127',2,0,'Double',4000.00),
('128',2,0,'Double',4000.00),('129',2,0,'Double',4000.00),('130',2,0,'Double',4000.00),
('131',2,0,'Double',4000.00),('132',2,0,'Double',4000.00),('133',2,0,'Double',4000.00),
('134',2,0,'Double',4000.00),('135',2,0,'Double',4000.00),('136',2,0,'Double',4000.00),
('137',2,0,'Double',4000.00),('138',2,0,'Double',4000.00),('139',2,0,'Double',4000.00),
('140',2,0,'Double',4000.00),('141',2,0,'Double',4000.00),('142',2,0,'Double',4000.00),
('143',2,0,'Double',4000.00),('144',2,0,'Double',4000.00),('145',2,0,'Double',4000.00),
('146',2,0,'Double',4000.00),('147',2,0,'Double',4000.00),('148',2,0,'Double',4000.00),
('149',2,0,'Double',4000.00),('150',2,0,'Double',4000.00),('151',2,0,'Double',4000.00),
('152',2,0,'Double',4000.00),('153',2,0,'Double',4000.00),('154',2,0,'Double',4000.00),
('155',2,0,'Double',4000.00),('156',2,0,'Double',4000.00),('157',2,0,'Double',4000.00),
('158',2,0,'Double',4000.00),('159',2,0,'Double',4000.00),('160',2,0,'Double',4000.00),
('161',2,0,'Double',4000.00),('162',2,0,'Double',4000.00),('163',2,0,'Double',4000.00),
('164',2,0,'Double',4000.00),('165',2,0,'Double',4000.00),('166',2,0,'Double',4000.00),
('167',2,0,'Double',4000.00),('168',2,0,'Double',4000.00),('169',2,0,'Double',4000.00),
('170',2,0,'Double',4000.00),('171',2,0,'Double',4000.00),('172',2,0,'Double',4000.00),
('173',2,0,'Double',4000.00),('174',2,0,'Double',4000.00),('175',2,0,'Double',4000.00),
('176',2,0,'Double',4000.00),('177',2,0,'Double',4000.00),('178',2,0,'Double',4000.00),
('179',2,0,'Double',4000.00),('180',2,0,'Double',4000.00),('181',2,0,'Double',4000.00),
('182',2,0,'Double',4000.00),('183',2,0,'Double',4000.00),('184',2,0,'Double',4000.00),
('185',2,0,'Double',4000.00),('186',2,0,'Double',4000.00),('187',2,0,'Double',4000.00),
('188',2,0,'Double',4000.00),('189',2,0,'Double',4000.00),('190',2,0,'Double',4000.00),
('191',2,0,'Double',4000.00),('192',2,0,'Double',4000.00),('193',2,0,'Double',4000.00),
('194',2,0,'Double',4000.00),('195',2,0,'Double',4000.00),('196',2,0,'Double',4000.00),
('197',2,0,'Double',4000.00),('198',2,0,'Double',4000.00),('199',2,0,'Double',4000.00),
('200',2,0,'Double',4000.00),('201',2,0,'Double',4000.00),('202',2,0,'Double',4000.00),
('203',2,0,'Double',4000.00),('204',2,0,'Double',4000.00),('205',2,0,'Double',4000.00),
('206',2,0,'Double',4000.00),('207',2,0,'Double',4000.00),('208',2,0,'Double',4000.00),
('209',2,0,'Double',4000.00),('210',2,0,'Double',4000.00),('211',2,0,'Double',4000.00),
('212',2,0,'Double',4000.00),('213',2,0,'Double',4000.00),('214',2,0,'Double',4000.00),
('215',2,0,'Double',4000.00),('216',2,0,'Double',4000.00),('217',2,0,'Double',4000.00),
('218',2,0,'Double',4000.00),('219',2,0,'Double',4000.00),('220',2,0,'Double',4000.00),
('221',2,0,'Double',4000.00),('222',2,0,'Double',4000.00),('223',2,0,'Double',4000.00),
('224',2,0,'Double',4000.00),('225',2,0,'Double',4000.00),('226',2,0,'Double',4000.00),
('227',2,0,'Double',4000.00),('228',2,0,'Double',4000.00),('229',2,0,'Double',4000.00),
('230',2,0,'Double',4000.00),('231',2,0,'Double',4000.00),('232',2,0,'Double',4000.00),
('233',2,0,'Double',4000.00),('234',2,0,'Double',4000.00),('235',2,0,'Double',4000.00),
('236',2,0,'Double',4000.00),('237',2,0,'Double',4000.00),('238',2,0,'Double',4000.00),
('239',2,0,'Double',4000.00),('240',2,0,'Double',4000.00),('241',2,0,'Double',4000.00),
('242',2,0,'Double',4000.00),('243',2,0,'Double',4000.00),('244',2,0,'Double',4000.00),
('245',2,0,'Double',4000.00),('246',2,0,'Double',4000.00),('247',2,0,'Double',4000.00),
('248',2,0,'Double',4000.00),('249',2,0,'Double',4000.00),('250',2,0,'Double',4000.00),
('251',2,0,'Double',4000.00),('252',2,0,'Double',4000.00),('253',2,0,'Double',4000.00),
('254',2,0,'Double',4000.00),('255',2,0,'Double',4000.00),('256',2,0,'Double',4000.00),
('257',2,0,'Double',4000.00),('258',2,0,'Double',4000.00),('259',2,0,'Double',4000.00),
('260',2,0,'Double',4000.00),('261',2,0,'Double',4000.00),('262',2,0,'Double',4000.00),
('263',2,0,'Double',4000.00),('264',2,0,'Double',4000.00),('265',2,0,'Double',4000.00),
('266',2,0,'Double',4000.00),('267',2,0,'Double',4000.00),('268',2,0,'Double',4000.00),
('269',2,0,'Double',4000.00),('270',2,0,'Double',4000.00),('271',2,0,'Double',4000.00),
('272',2,0,'Double',4000.00),('273',2,0,'Double',4000.00),('274',2,0,'Double',4000.00),
('275',2,0,'Double',4000.00),('276',2,0,'Double',4000.00),('277',2,0,'Double',4000.00),
('278',2,0,'Double',4000.00),('279',2,0,'Double',4000.00),('280',2,0,'Double',4000.00),
('281',2,0,'Double',4000.00),('282',2,0,'Double',4000.00),('283',2,0,'Double',4000.00),
('284',2,0,'Double',4000.00),('285',2,0,'Double',4000.00),('286',2,0,'Double',4000.00),
('287',2,0,'Double',4000.00),('288',2,0,'Double',4000.00),('289',2,0,'Double',4000.00),
('290',2,0,'Double',4000.00),('291',2,0,'Double',4000.00),('292',2,0,'Double',4000.00),
('293',2,0,'Double',4000.00),('294',2,0,'Double',4000.00),('295',2,0,'Double',4000.00),
('296',2,0,'Double',4000.00),('297',2,0,'Double',4000.00),('298',2,0,'Double',4000.00),
('299',2,0,'Double',4000.00),('300',2,0,'Double',4000.00);

-- ── Seed: Default admin account ───────────────────────────────────────────────
-- Password is 'admin123' — change after first login
INSERT IGNORE INTO person (name, age, email, phone)
VALUES ('Administrator', 30, 'admin@hostel.com', '0700000000');

INSERT IGNORE INTO admin (id, username, password, role)
SELECT id, 'admin', 'admin123', 'Super Admin'
FROM person WHERE name = 'Administrator' LIMIT 1;