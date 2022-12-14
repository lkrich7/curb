/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE TABLE `curb_app` (
  `app_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '应用ID(主键)',
  `group_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '所属项目组ID',
  `domain` varchar(64) NOT NULL COMMENT '域名',
  `main_page` varchar(64) NOT NULL COMMENT '首页路径',
  `name` varchar(32) NOT NULL COMMENT '应用名称',
  `description` varchar(255) NOT NULL COMMENT '应用描述',
  `state` tinyint(4) NOT NULL COMMENT '状态：0:未知;1:已启用;2:已禁用',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`app_id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE,
  UNIQUE KEY `uk_group_id_domain_main_page` (`group_id`, `domain`, `main_page`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用信息表';

CREATE TABLE IF NOT EXISTS `curb_app_secret` (
  `app_id` int(11) unsigned NOT NULL COMMENT '应用ID',
  `secret` varchar(64) NOT NULL COMMENT '应用密钥',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用密钥表';

CREATE TABLE IF NOT EXISTS `curb_group` (
  `group_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '项目组ID',
  `name` varchar(32) NOT NULL COMMENT '项目组名称',
  `domain` varchar(64) NOT NULL COMMENT '项目组根域名',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`group_id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE,
  UNIQUE KEY `uk_domain` (`domain`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目组信息表';

CREATE TABLE IF NOT EXISTS `curb_group_secret` (
  `group_id` int(11) unsigned NOT NULL COMMENT '项目组ID',
  `secret` varchar(64) NOT NULL COMMENT '密钥',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目组密钥表';

CREATE TABLE IF NOT EXISTS `curb_log` (
  `log_id` bigint(20) NOT NULL COMMENT '操作日志ID',
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `group_id` int(11) unsigned NOT NULL COMMENT '项目组ID',
  `app_id` int(11) unsigned NOT NULL COMMENT '应用ID',
  `oper_time` timestamp(3) NOT NULL COMMENT '操作时间',
  `oper_detail` varchar(4000) NOT NULL COMMENT '操作详情',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `idx_user_id_oper_time` (`user_id`, `oper_time`) USING BTREE,
  KEY `idx_group_id_app_id_oper_time` (`group_id`, `app_id`, `oper_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作记录表';

CREATE TABLE IF NOT EXISTS `curb_page` (
  `page_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '页面ID',
  `app_id` int(11) unsigned NOT NULL COMMENT '所属应用ID',
  `path` varchar(64) NOT NULL COMMENT '页面URL路径(不含参数)',
  `name` varchar(32) NOT NULL COMMENT '页面名称',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '页面类型(0:amis页面;1:普通HTML页面)',
  `access_level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '访问控制等级',
  `sign` varchar(255) NOT NULL COMMENT '权限标识',
  `version` int(11) unsigned NOT NULL COMMENT '当前版本号',
  `state` tinyint(4) NOT NULL COMMENT '状态：0:未知;1:已启用;2:已禁用',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`page_id`) USING BTREE,
  UNIQUE KEY `uk_app_id_path` (`app_id`,`path`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面信息表';

CREATE TABLE IF NOT EXISTS `curb_page_body` (
  `page_id` int(11) unsigned NOT NULL COMMENT '页面ID',
  `version` int(11) unsigned NOT NULL COMMENT '页面版本号',
  `body` text NOT NULL COMMENT '页面内容',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`page_id`, `version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面信息表';


CREATE TABLE IF NOT EXISTS `curb_permission` (
  `perm_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `app_id` int(11) unsigned NOT NULL COMMENT '应用ID',
  `sign` varchar(255) NOT NULL COMMENT '权限标识',
  `name` varchar(32) NOT NULL COMMENT '权限名称',
  `description` varchar(255) NOT NULL COMMENT '权限描述',
  `state` tinyint(4) NOT NULL COMMENT '状态：0:未知;1:已启用;2:已禁用',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`perm_id`) USING BTREE,
  UNIQUE KEY `uk_app_id_sign` (`app_id`,`sign`) USING BTREE,
  KEY `uk_app_id_name` (`app_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限信息表';

CREATE TABLE IF NOT EXISTS `curb_role` (
  `role_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `group_id` int(11) unsigned NOT NULL COMMENT '所属项目组ID',
  `sign` varchar(255) NOT NULL COMMENT '标识',
  `name` varchar(32) NOT NULL COMMENT '角色名',
  `description` varchar(255) NOT NULL COMMENT '角色描述',
  `state` tinyint(4) NOT NULL COMMENT '状态：0:未知;1:已启用;2:已禁用',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE KEY `uk_group_id_sign` (`group_id`,`sign`) USING BTREE,
  UNIQUE KEY `uk_group_id_name` (`group_id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

CREATE TABLE IF NOT EXISTS `curb_role_permission` (
  `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
  `perm_id` int(11) unsigned NOT NULL COMMENT '权限ID',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`role_id`,`perm_id`) USING BTREE,
  UNIQUE KEY `uk_perm_id_role_id` (`perm_id`, `role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与权限关系表';

CREATE TABLE IF NOT EXISTS `curb_user` (
  `user_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(64) NOT NULL COMMENT '邮箱账号',
  `type` tinyint(4) NOT NULL COMMENT '用户类型(0:内网用户, 1:公网用户)',
  `state` tinyint(4) NOT NULL COMMENT '用户状态(0:无效;1:有效)',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_EMAIL` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE IF NOT EXISTS `curb_user_role` (
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`user_id`,`role_id`),
  UNIQUE KEY `uk_role_id_user_id` (`role_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关系表';

CREATE TABLE IF NOT EXISTS `curb_user_role_system` (
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `group_id` int(11) unsigned NOT NULL COMMENT '项目组',
  `role_id` int(11) unsigned NOT NULL COMMENT '角色ID',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '数据创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最近更新时间',
  PRIMARY KEY (`user_id`,`group_id`,`role_id`) USING BTREE,
  UNIQUE KEY `UK_ROLE_USER` (`role_id`,`group_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与系统角色关系表';

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
