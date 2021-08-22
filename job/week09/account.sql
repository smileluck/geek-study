/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : geek_bank

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2021-08-22 23:26:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_user_balance
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_balance`;
CREATE TABLE `tb_user_balance` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cash_type` varchar(10) NOT NULL COMMENT '钱种类',
  `money` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '金额w',
  `frozen_money` decimal(10,0) NOT NULL DEFAULT '0' COMMENT '冻结金额',
  PRIMARY KEY (`user_id`,`cash_type`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of tb_user_balance
-- ----------------------------
INSERT INTO `tb_user_balance` VALUES ('1', 'dollar', '10', '0');
INSERT INTO `tb_user_balance` VALUES ('1', 'rmb', '70', '0');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '用户名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1', 'A');
