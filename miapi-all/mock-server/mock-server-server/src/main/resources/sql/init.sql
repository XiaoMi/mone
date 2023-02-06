/*
 Navicat Premium Data Transfer

 Source Server         : mock-server测试环境
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : 127.0.0.1:3106
 Source Schema         : mock_server

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 31/10/2022 15:27:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_mock_data
-- ----------------------------
DROP TABLE IF EXISTS `api_mock_data`;
CREATE TABLE `api_mock_data` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL COMMENT 'api的url',
  `api_mock_result` text COMMENT 'mock的数据',
  `params_md5` varchar(255) DEFAULT NULL,
  `enable` tinyint(1) DEFAULT NULL COMMENT '是否可用',
  `mock_expect_id` int(10) NOT NULL,
  `mock_proxy_url` varchar(255) DEFAULT NULL COMMENT '更合理的mockUrl',
  `use_mock_script` tinyint(1) DEFAULT '0',
  `mock_script` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `url和参数md5唯一` (`url`,`params_md5`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7446 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
