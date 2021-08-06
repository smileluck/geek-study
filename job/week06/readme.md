[toc]

---

# 题目

**1.（选做）**尝试使用 Lambda/Stream/Guava 优化之前作业的代码。

**2.（选做）**尝试使用 Lambda/Stream/Guava 优化工作中编码的代码。

**3.（选做）**根据课上提供的材料，系统性学习一遍设计模式，并在工作学习中思考如何用设计模式解决问题。

**4.（选做）**根据课上提供的材料，深入了解 Google 和 Alibaba 编码规范，并根据这些规范，检查自己写代码是否符合规范，有什么可以改进的。

**5.（选做）**基于课程中的设计原则和最佳实践，分析是否可以将自己负责的业务系统进行数据库设计或是数据库服务器方面的优化

**6.（必做）**基于电商交易场景（用户、商品、订单），设计一套简单的表结构，提交 DDL 的 SQL 文件到 Github（后面 2 周的作业依然要是用到这个表结构）。

**7.（选做）**尽可能多的从“常见关系数据库”中列的清单，安装运行，并使用上一题的 SQL 测试简单的增删改查。

**8.（选做）**基于上一题，尝试对各个数据库测试 100 万订单数据的增删改查性能。

**9.（选做**）尝试对 MySQL 不同引擎下测试 100 万订单数据的增删改查性能。

**10.（选做）**模拟 1000 万订单数据，测试不同方式下导入导出（数据备份还原）MySQL 的速度，包括 jdbc 程序处理和命令行处理。思考和实践，如何提升处理效率。

**11.（选做）**对 MySQL 配置不同的数据库连接池（DBCP、C3P0、Druid、Hikari），测试增删改查 100 万次，对比性能，生成报告。





# 作业6

[文件地址](https://github.com/smileluck/geek-study/tree/main/job/week06/geek_shop.sql)

```sql
/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50732
Source Host           : localhost:3306
Source Database       : geek_shop

Target Server Type    : MYSQL
Target Server Version : 50732
File Encoding         : 65001

Date: 2021-08-05 17:14:44
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
-- Table structure for tb_order_good
-- ----------------------------
DROP TABLE IF EXISTS `tb_order_good`;
CREATE TABLE `tb_order_good` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `good_type_id` bigint(20) NOT NULL COMMENT '商品种类id',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格，单位（分）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='订单商品快照\r\n';

-- ----------------------------
-- Records of tb_order_good
-- ----------------------------
INSERT INTO `tb_order_good` VALUES ('1', '1', '2', '荔枝', '150.00', '2021-07-30 16:09:04', '2021-07-30 16:09:42');

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

```

