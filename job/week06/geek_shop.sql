/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50732
Source Host           : localhost:3306
Source Database       : geek_shop

Target Server Type    : MYSQL
Target Server Version : 50732
File Encoding         : 65001

Date: 2021-07-30 16:10:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_good
-- ----------------------------
DROP TABLE IF EXISTS `tb_good`;
CREATE TABLE `tb_good` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `good_type_id` bigint(20) NOT NULL COMMENT '商品种类id',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格，单位（分）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- Records of tb_good
-- ----------------------------
INSERT INTO `tb_good` VALUES ('1', '2', '荔枝', '150.00', '2021-07-30 16:09:04', '2021-07-30 16:09:42');

-- ----------------------------
-- Table structure for tb_good_type
-- ----------------------------
DROP TABLE IF EXISTS `tb_good_type`;
CREATE TABLE `tb_good_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父类ID',
  `name` varchar(255) NOT NULL COMMENT '种类名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='商品种类';

-- ----------------------------
-- Records of tb_good_type
-- ----------------------------
INSERT INTO `tb_good_type` VALUES ('1', '0', '根', '2021-07-30 16:08:10', null);
INSERT INTO `tb_good_type` VALUES ('2', '1', '水果', '2021-07-30 16:08:22', null);

-- ----------------------------
-- Table structure for tb_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_order`;
CREATE TABLE `tb_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `good_id` bigint(20) NOT NULL COMMENT '商品ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `real_price` decimal(10,2) NOT NULL COMMENT '实际价格',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '1 未支付，2已支付，3已过期',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- Records of tb_order
-- ----------------------------
INSERT INTO `tb_order` VALUES ('1', '1', '1', '200.00', '150.00', '1', '2021-07-30 16:09:36', null);

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1', '用户1', '2021-07-30 15:48:08', '2021-07-30 16:09:16');
INSERT INTO `tb_user` VALUES ('2', '3', '2021-07-30 15:48:38', null);
INSERT INTO `tb_user` VALUES ('3', '4', '2021-07-30 15:48:47', null);
INSERT INTO `tb_user` VALUES ('4', '4', '2021-07-30 15:49:00', null);
