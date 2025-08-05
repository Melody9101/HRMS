CREATE TABLE if not exists `annual_holiday` (
  `date` date NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `holiday` tinyint NOT NULL,
  `create_by` int DEFAULT '0',
  `create_at` datetime DEFAULT NULL,
  PRIMARY KEY (`date`)
);


CREATE TABLE if not exists `attendance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int NOT NULL DEFAULT '0',
  `clock_in` datetime DEFAULT NULL,
  `clock_out` datetime DEFAULT NULL,
  `final_update_date` date DEFAULT NULL,
  `final_update_employee_id` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists `company_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `tax_id_number` int DEFAULT '0',
  `owner_name` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `establishment_date` date DEFAULT NULL,
  `capital_amount` int DEFAULT '0',
  `employee_count` int DEFAULT '0',
  `status` varchar(45) DEFAULT NULL,
  `create_at` datetime DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `work_start_time` time NOT NULL DEFAULT '09:00:00',
  `lunch_start_time` time NOT NULL DEFAULT '12:00:00',
  `lunch_end_time` time NOT NULL DEFAULT '13:30:00',
  `timezone` varchar(50) NOT NULL DEFAULT 'Asia/Taipei',
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists `email_verification` (
  `email` varchar(100) NOT NULL,
  `code` varchar(10) NOT NULL,
  `expire_at` datetime NOT NULL,
  `verified` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`email`)
);

CREATE TABLE if not exists `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` varchar(50) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(60) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `gender` tinyint NOT NULL DEFAULT '0',
  `grade` int NOT NULL DEFAULT '0',
  `entry_date` date DEFAULT NULL,
  `resignation_date` date DEFAULT NULL,
  `resignation_reason` varchar(1000) DEFAULT NULL,
  `salaries` int DEFAULT '0',
  `position` varchar(45) DEFAULT NULL,
  `employed` tinyint NOT NULL DEFAULT '0',
  `remaining_previous_annual_leave` decimal(10,4) DEFAULT '0.0000',
  `remaining_current_annual_leave` decimal(10,4) DEFAULT '0.0000',
  `remaining_paid_sick_leave` decimal(10,4) DEFAULT '0.0000',
  `unpaid_leave_start_date` date DEFAULT NULL,
  `unpaid_leave_end_date` date DEFAULT NULL,
  `unpaid_leave_reason` varchar(200) DEFAULT NULL,
  `final_update_date` date DEFAULT NULL,
  `final_update_employee_id` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists `employee_application` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_id` int DEFAULT '0',
  `comment` varchar(200) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `data` varchar(1000) DEFAULT NULL,
  `apply_date_time` datetime DEFAULT NULL,
  `submit_up` tinyint DEFAULT NULL,
  `approval_pending_role` varchar(20) DEFAULT NULL,
  `applyer_id` int DEFAULT '0',
  `application_group` int DEFAULT '0',
  PRIMARY KEY (`id`)
);

CREATE TABLE if not exists `employee_application_record` (
  `application_id` int NOT NULL,
  `employee_id` int DEFAULT '0',
  `comment` varchar(200) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `data` varchar(1000) DEFAULT NULL,
  `apply_date_time` datetime DEFAULT NULL,
  `submit_up` tinyint DEFAULT NULL,
  `approved` tinyint DEFAULT NULL,
  `approver_id` int DEFAULT '0',
  `approver_role` varchar(20) DEFAULT NULL,
  `rejection_reason` varchar(100) DEFAULT NULL,
  `application_group` int DEFAULT '0',
  `approved_date_time` datetime DEFAULT NULL,
  `applyer_id` int DEFAULT '0',
  PRIMARY KEY (`application_id`)
);

CREATE TABLE if not exists `fixed_holiday` (
  `month` int NOT NULL,
  `day` int NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`month`,`day`)
);

CREATE TABLE if not exists `leave_application` (
  `leave_id` int NOT NULL AUTO_INCREMENT,
  `employee_name` varchar(45) DEFAULT NULL,
  `employer_id` int NOT NULL DEFAULT '0',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `leave_type` varchar(20) DEFAULT NULL,
  `certificate` mediumblob,
  `apply_date_time` datetime DEFAULT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `approval_pending_role` varchar(20) DEFAULT NULL,
  `submit_up` tinyint DEFAULT NULL,
  `certificate_file_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`leave_id`)
);

CREATE TABLE if not exists `leave_record` (
  `leave_application_id` int NOT NULL DEFAULT '0',
  `employee_name` varchar(45) DEFAULT NULL,
  `employee_id` int NOT NULL DEFAULT '0',
  `leave_type` varchar(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `reason` varchar(200) DEFAULT NULL,
  `certificate` mediumblob,
  `apply_date_time` datetime NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `rejection_reason` varchar(100) DEFAULT NULL,
  `approved` tinyint DEFAULT NULL,
  `approved_date_time` datetime DEFAULT NULL,
  `approval_pending_role` varchar(20) DEFAULT NULL,
  `approver_id` int DEFAULT '0',
  `submit_up` tinyint DEFAULT NULL,
  `certificate_file_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`leave_application_id`)
);

CREATE TABLE if not exists `salaries` (
  `id` int NOT NULL AUTO_INCREMENT,
  `year` int DEFAULT '0',
  `month` int DEFAULT '0',
  `employee_id` int NOT NULL DEFAULT '0',
  `salary` int NOT NULL DEFAULT '0',
  `bonus` int DEFAULT '0',
  `pay_date` date DEFAULT NULL,
  `payer_id` int NOT NULL DEFAULT '0',
  `final_update_date` date DEFAULT NULL,
  `final_update_employee_id` int DEFAULT '0',
  PRIMARY KEY (`id`)
);





/*以下是修改欄位用的程式碼*/

/*-- 檢查 TEST 欄位是否存在
SELECT COUNT(*) INTO @column_exists_test
FROM information_schema.columns 
WHERE table_schema = 'hr_management_system'
  AND table_name = 'salaries'
  AND column_name = 'TEST';

-- 檢查 TEST1 欄位是否存在
SELECT COUNT(*) INTO @column_exists_test1
FROM information_schema.columns 
WHERE table_schema = 'hr_management_system'
  AND table_name = 'salaries'
  AND column_name = 'TEST1';

-- 如果 TEST 欄位不存在，則執行 ALTER TABLE
IF @column_exists_test = 0 THEN
    ALTER TABLE `hr_management_system`.`salaries`
    ADD COLUMN `TEST` VARCHAR(45) NULL AFTER `final_update_employee_id`;
END IF;

-- 如果 TEST1 欄位不存在，則執行 ALTER TABLE
IF @column_exists_test1 = 0 THEN
    ALTER TABLE `hr_management_system`.`salaries`
    ADD COLUMN `TEST1` INT NULL DEFAULT 0 AFTER `TEST`;
END IF;

-- 檢查 year 欄位是否存在
SELECT COUNT(*) INTO @column_exists_year
FROM information_schema.columns 
WHERE table_schema = 'hr_management_system'
  AND table_name = 'salaries'
  AND column_name = 'year';

-- 檢查 month 欄位是否存在
SELECT COUNT(*) INTO @column_exists_month
FROM information_schema.columns 
WHERE table_schema = 'hr_management_system'
  AND table_name = 'salaries'
  AND column_name = 'month';

-- 如果 year 和 month 欄位不存在，則執行 ALTER TABLE
IF @column_exists_year = 0 AND @column_exists_month = 0 THEN
	ALTER TABLE `hr_management_system`.`salaries` 
	ADD COLUMN `year` INT NULL DEFAULT 0 AFTER `id`,
	ADD COLUMN `month` INT NULL DEFAULT 0 AFTER `year`;
END IF;
**/