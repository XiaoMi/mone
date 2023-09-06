/*
 Navicat Premium Data Transfer

 Source Server         : milog
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 127.0.0.1:4100
 Source Schema         : milog

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 29/11/2022 11:36:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for milog_log_template
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template`;
CREATE TABLE `milog_log_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `ctime` bigint(20) NULL DEFAULT NULL COMMENT 'create time',
  `utime` bigint(20) NULL DEFAULT NULL COMMENT 'update time',
  `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'log template name',
  `type` int(11) NULL DEFAULT NULL COMMENT 'Log template type 0-custom log;1-app;2-nginx',
  `support_area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Support computer room',
  `order_col` int(11) NULL DEFAULT NULL COMMENT 'sort',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of milog_log_template
-- ----------------------------
INSERT INTO `milog_log_template` VALUES (1, 1635824217684, 1635824217684, 'Multi-line application log', 1, 'cn,ams,in,alsg,mos', 100);
INSERT INTO `milog_log_template` VALUES (2, 1635824217684, 1635824217684, 'nginx log', 2, 'cn,ams,in,alsg,mos', 200);
INSERT INTO `milog_log_template` VALUES (3, 1635824217684, 1635824217684, 'opentelemetry log', 3, 'cn,ams,in,alsg,mos', 300);
INSERT INTO `milog_log_template` VALUES (4, 1635824217684, 1635824217684, 'docker log', 4, 'cn,ams,in,alsg,mos', 400);
INSERT INTO `milog_log_template` VALUES (5, 1635824217684, 1635824217684, 'Custom log', 0, 'cn,ams,in,alsg,mos', 1000);
INSERT INTO `milog_log_template` VALUES (6, 1637658502795, 1637658502795, 'mis application log', 5, 'cn,ams,in,alsg,mos', 500);
INSERT INTO `milog_log_template` VALUES (7, 1635824217684, 1635824217684, 'loki log', 6, 'cn,ams,in,alsg,mos', 600);
INSERT INTO `milog_log_template` VALUES (8, 1635043240000, 1635043240000, 'matrix es log', 7, 'cn,ams,in,alsg,mos', 700);
INSERT INTO `milog_log_template` VALUES (9, 1656038440000, 1656038440000, 'Single line application log', 8, 'cn,ams,in,alsg,mos', 150);

-- ----------------------------
-- Table structure for milog_log_template_detail
-- ----------------------------
DROP TABLE IF EXISTS `milog_log_template_detail`;
CREATE TABLE `milog_log_template_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `ctime` bigint(20) NULL DEFAULT NULL COMMENT 'create time',
  `utime` bigint(20) NULL DEFAULT NULL COMMENT 'update time',
  `template_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template ID',
  `properties_key` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template attribute name; 1-required; 2-suggestion; 3-hidden',
  `properties_type` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'Log template attribute type',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60086 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of milog_log_template_detail
-- ----------------------------
INSERT INTO `milog_log_template_detail` VALUES (84, 1628508945923, 1628508945923, '1', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1', 'date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword');
INSERT INTO `milog_log_template_detail` VALUES (85, 1628508945923, 1628508945923, '5', 'logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'keyword,keyword,keyword,keyword,keyword,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (87, 1628508945923, 1628508945923, '2', 'message:1,hostname:1,http_code:1,method:1,protocol:1,referer:1,timestamp:1,ua:1,url:1,linenumber:3,logip:3', 'text,text,keyword,keyword,keyword,text,timestamp,text,text,long,keyword');
INSERT INTO `milog_log_template_detail` VALUES (88, 1628508945923, 1628508945923, '3', 'logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'keyword,keyword,keyword,keyword,keyword,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (89, 1628508945923, 1628508945923, '4', 'logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'keyword,keyword,keyword,keyword,keyword,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (90, 1637658502795, 1637658502795, '6', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'date,keyword,keyword,text,text,text,keyword,keyword,keyword,keyword,keyword,text,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (91, 1628508945923, 1628508945923, '7', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (92, 20220621101013, 20220621101013, '8', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3', 'date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long');
INSERT INTO `milog_log_template_detail` VALUES (93, 1628508945923, 1628508945923, '9', 'timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,message:1,logstore:3,logsource:3,mqtopic:3,mqtag:3,logip:3,tail:3,linenumber:3,podName:1', 'date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,keyword,keyword,keyword,keyword,long,keyword');

SET FOREIGN_KEY_CHECKS = 1;
