/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`curb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `curb`;
/*Table structure for table `app` */

DROP TABLE IF EXISTS `app`;

CREATE TABLE `app` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '应用系统id',
  `name` varchar(32) DEFAULT NULL COMMENT '应用系统名',
  `domain` varchar(64) DEFAULT NULL COMMENT '系统域名',
  `secret` char(64) NOT NULL DEFAULT '' COMMENT '密钥用于参数校验',
  `description` varchar(256) DEFAULT NULL COMMENT '系统描述,UI界面显示使用',
  `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP comment '最近更新时间',
  `create_time` timestamp COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='应用系统表';

/*Data for the table `permission` */

/*Table structure for table `permission` */

DROP TABLE IF EXISTS `permission`;

CREATE TABLE `permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '权限id',
  `name` varchar(32) DEFAULT NULL COMMENT '权限名',
  `sign` varchar(128) DEFAULT NULL COMMENT '权限标识,程序中判断使用,如"/user/create"',
  `app_id` bigint(20) unsigned NOT NULL COMMENT '应用系统id',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '权限状态 0:不可用 1:可用',
  `description` varchar(256) DEFAULT NULL COMMENT '权限描述,UI界面显示使用',
  `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP comment '最近更新时间',
  `create_time` timestamp COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE idx_sign (app_id, `sign`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='权限表';

/*Data for the table `permission` */

insert  into `permission`(`id`,`name`,`sign`,`app_id`, `description`, `create_time`) values (1,'用户新增','user:add', 1, '添加用户', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('用户删除','user:delete', 1, '删除用户', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('用户列表','user:list', 1, '用户列表', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色列表','role:list', 1, '角色列表', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色创建','role:create', 1, '创建新角色', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色删除','role:delete', 1, '删除角色', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色权限','role:permission:detail', 1, '角色权限详情', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色权限znodes','role:permission:znodes', 1, '角色权限详情zNodes', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色添加权限','role:permission:add', 1, '角色添加新权限', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('角色删除权限','role:permission:delete', 1, '角色删除权限', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用列表','app:list', 1, '应用列表', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用创建','app:create', 1, '创建新应用', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用删除','app:delete', 1, '删除应用', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用权限','app:permission:detail', 1, '应用权限详情', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用权限znodes','app:permission:znodes', 1, '应用权限详情zNodes', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用添加权限','app:permission:add', 1, '应用添加新权限', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('应用删除权限','app:permission:delete', 1, '应用删除权限', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('用户角色','user:role:detail', 1, '用户角色详情', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('用户添加角色','user:role:add', 1, '用户添加新角色', now());
insert  into `permission`(`name`,`sign`,`app_id`, `description`, `create_time`) values ('用户删除角色','user:role:delete', 1, '用户删除角色', now());

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(32) DEFAULT NULL COMMENT '角色名',
  `sign` varchar(128) DEFAULT NULL COMMENT '角色标识,程序中判断使用,如"admin"',
  `description` varchar(256) DEFAULT NULL COMMENT '角色描述,UI界面显示使用',
  `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP comment '最近更新时间',
  `create_time` timestamp COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE idx_sign (`sign`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='角色表';

/*Data for the table `role` */

insert  into `role`(`id`,`name`,`sign`,`description`, `create_time`) values (1,'超级管理员','admin','管理员', now());

/*Table structure for table `role_permission` */

DROP TABLE IF EXISTS `role_permission`;

CREATE TABLE `role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '表id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id',
  `permission_id` bigint(20) unsigned DEFAULT NULL COMMENT '权限id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE idx_role_perm (role_id, permission_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='角色与权限关联表';

/*Data for the table `role_permission` */

insert  into `role_permission`(`id`,`role_id`,`permission_id`, `create_time`) values (1,1,1, now());

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱账号',
  `type` TINYINT DEFAULT 0 COMMENT '用户类型 0:内网用户, 1:公网用户',
  `update_time` timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP comment '最近更新时间',
  `create_time` timestamp COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE idx_name (`name`, `type`),
  UNIQUE idx_email (email, `type`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户表';

/*Data for the table `user` */

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE idx_user_role (user_id, role_id)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户与角色关联表';

/*Data for the table `user_role` */

insert  into `user_role`(`id`,`user_id`,`role_id`, `create_time`) values (1,1,1, now());

/*Table structure for table `op_log` */

DROP TABLE IF EXISTS `op_log`;

CREATE TABLE `op_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(64) DEFAULT NULL COMMENT '用户信息',
  `type` TINYINT NOT NULL COMMENT `操作类型`,
  `content` varchar(200) NOT NULL COMMENT '操作',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='操作记录';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
