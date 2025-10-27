-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: competition_training_system
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `contest`
--

DROP TABLE IF EXISTS `contest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contest` (
  `contest_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `visibility` enum('PUBLIC','PRIVATE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `state` enum('HIDDEN','USING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creator_id` bigint NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`contest_id`),
  UNIQUE KEY `title` (`title`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `contest_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contest`
--

LOCK TABLES `contest` WRITE;
/*!40000 ALTER TABLE `contest` DISABLE KEYS */;
INSERT INTO `contest` VALUES (1,'2025年春季赛（修改）','春季编程竞赛修改版','2025-09-15 19:00:00','2025-09-15 22:00:00','PUBLIC','HIDDEN',NULL,2,'2025-09-10 10:00:00','2025-10-11 11:53:04'),(2,'九月算法月赛','难度适中的综合性算法竞赛，包含动态规划、图论等主题。','2025-09-20 14:00:00','2025-09-20 18:00:00','PUBLIC','HIDDEN',NULL,2,'2025-09-15 11:00:00','2025-09-15 11:00:00'),(3,'数据结构专项练习赛','专题训练：链表、栈、队列、树、哈希表。','2025-09-22 09:00:00','2025-09-27 09:00:00','PRIVATE','HIDDEN','$2a$10$US.T3..gq7p4a8R72xV1Eet.Yg1V1jgDLIsfwe828zGjIpqV2p9Vq',2,'2025-09-20 12:00:00','2025-09-20 12:00:00'),(4,'国庆欢乐赛','趣味性题目，庆祝国庆节，轻松愉快。','2025-10-01 10:00:00','2025-10-01 15:00:00','PUBLIC','USING',NULL,2,'2025-09-28 14:00:00','2025-09-28 14:00:00'),(5,'动态规划挑战周（上）','DP专题第一场，线性DP和背包问题。','2025-10-02 09:00:00','2025-10-05 23:59:59','PUBLIC','USING',NULL,2,'2025-09-30 15:00:00','2025-09-30 15:00:00'),(6,'蓝桥杯模拟赛第一场','模拟蓝桥杯省赛题型和难度。','2025-10-06 13:00:00','2025-10-06 17:00:00','PUBLIC','USING',NULL,2,'2025-10-01 16:00:00','2025-10-01 16:00:00'),(7,'字符串算法专题','KMP, Trie, AC自动机等字符串处理算法。','2025-10-08 19:00:00','2025-10-08 22:00:00','PUBLIC','HIDDEN',NULL,2,'2025-10-05 17:00:00','2025-10-05 17:00:00'),(8,'十月上旬个人排位赛','检验近期学习成果，用于校内排名。','2025-10-10 19:00:00','2025-10-10 23:00:00','PUBLIC','HIDDEN',NULL,2,'2025-10-08 18:00:00','2025-10-08 18:00:00'),(9,'图论基础练习','DFS, BFS, 最短路, 最小生成树。','2025-10-09 09:00:00','2025-10-11 23:59:59','PUBLIC','USING',NULL,2,'2025-10-08 19:00:00','2025-10-08 19:00:00'),(10,'ICPC 选拔赛模拟（私有）','面向校队选拔的高难度模拟赛，请凭密码参加。','2025-10-10 13:00:00','2025-10-10 18:00:00','PRIVATE','HIDDEN','$2a$10$US.T3..gq7p4a8R72xV1Eet.Yg1V1jgDLIsfwe828zGjIpqV2p9Vq',2,'2025-10-09 20:00:00','2025-10-09 20:00:00'),(11,'十月中旬算法月赛','综合性算法考察，难度略高于上月。','2024-10-18 14:00:00','2025-10-18 18:00:00','PUBLIC','USING',NULL,2,'2025-10-11 10:00:00','2025-10-11 10:00:00'),(12,'蓝桥杯模拟赛第二场','模拟蓝桥杯省赛题型和难度，查漏补缺。','2025-10-20 13:00:00','2025-10-20 17:00:00','PUBLIC','USING',NULL,2,'2025-10-12 11:00:00','2025-10-12 11:00:00'),(13,'数学思维专题赛','组合数学、数论、概率论等。','2025-10-22 19:00:00','2025-10-22 22:00:00','PUBLIC','USING',NULL,2,'2025-10-13 12:00:00','2025-10-13 12:00:00'),(14,'动态规划挑战周（下）','DP专题第二场，区间DP、树形DP、状压DP。','2024-10-25 09:00:00','2025-10-28 23:59:59','PUBLIC','USING',NULL,2,'2025-10-14 14:00:00','2025-10-14 14:00:00'),(15,'十月下旬个人排位赛','月末冲刺，检验本月学习成果。','2025-10-30 19:00:00','2025-10-30 23:00:00','PUBLIC','USING',NULL,2,'2025-10-15 15:00:00','2025-10-15 15:00:00'),(16,'十一月算法月赛','新的月份，新的挑战。','2025-11-15 14:00:00','2025-11-15 18:00:00','PUBLIC','USING',NULL,2,'2025-11-01 10:00:00','2025-10-18 17:54:24'),(17,'贪心算法专题','考察各种贪心策略和证明。','2025-11-05 19:00:00','2025-11-05 22:00:00','PUBLIC','USING',NULL,2,'2025-11-02 11:00:00','2025-11-02 11:00:00'),(18,'蓝桥杯模拟赛第三场','最后冲刺模拟。','2025-11-10 13:00:00','2025-11-10 17:00:00','PUBLIC','USING',NULL,2,'2025-11-03 12:00:00','2025-11-03 12:00:00');
/*!40000 ALTER TABLE `contest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contest_participant`
--

DROP TABLE IF EXISTS `contest_participant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contest_participant` (
  `contest_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `registered_at` datetime NOT NULL,
  `is_official` tinyint(1) NOT NULL,
  `team_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`contest_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `contest_participant_ibfk_1` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`),
  CONSTRAINT `contest_participant_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contest_participant`
--

LOCK TABLES `contest_participant` WRITE;
/*!40000 ALTER TABLE `contest_participant` DISABLE KEYS */;
INSERT INTO `contest_participant` VALUES (13,18,'2025-10-20 18:13:30',1,NULL),(15,101,'2025-10-11 14:02:46',1,'我的队伍'),(16,18,'2025-10-14 20:43:20',1,NULL),(17,18,'2025-10-27 11:46:22',1,NULL);
/*!40000 ALTER TABLE `contest_participant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contest_problem`
--

DROP TABLE IF EXISTS `contest_problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contest_problem` (
  `contest_id` bigint NOT NULL,
  `problem_id` bigint NOT NULL,
  `display_order` int NOT NULL,
  PRIMARY KEY (`contest_id`,`problem_id`),
  KEY `problem_id` (`problem_id`),
  CONSTRAINT `contest_problem_ibfk_1` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`),
  CONSTRAINT `contest_problem_ibfk_2` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contest_problem`
--

LOCK TABLES `contest_problem` WRITE;
/*!40000 ALTER TABLE `contest_problem` DISABLE KEYS */;
INSERT INTO `contest_problem` VALUES (1,1,1),(1,2,2),(1,3,3),(1,4,4),(2,7,1),(2,8,2),(2,9,3),(2,11,4),(2,12,5),(2,13,6),(2,14,7),(3,15,1),(3,16,2),(3,17,3),(3,18,4),(3,19,5),(3,20,6),(3,21,7),(3,22,8),(4,23,1),(4,24,2),(4,25,3),(4,26,4),(4,27,5),(4,28,6),(5,1,4),(5,5,5),(5,9,6),(5,13,7),(5,29,1),(5,30,2),(5,31,3),(6,17,1),(6,18,2),(6,19,3),(6,20,4),(6,21,5),(6,22,6),(6,23,7),(6,24,8),(7,2,3),(7,4,4),(7,6,5),(7,8,6),(7,25,1),(7,26,2),(8,11,1),(8,13,2),(8,15,3),(8,17,4),(8,19,5),(8,21,6),(9,23,1),(9,24,2),(9,25,3),(9,26,4),(9,27,5),(9,28,6),(9,29,7),(10,24,8),(10,25,7),(10,26,6),(10,27,5),(10,28,4),(10,29,3),(10,30,2),(10,31,1),(11,1,1),(11,3,2),(11,5,3),(11,7,4),(11,9,5),(11,11,6),(11,13,7),(12,2,1),(12,4,2),(12,6,3),(12,8,4),(12,12,5),(12,14,6),(12,16,7),(12,18,8),(13,20,1),(13,21,2),(13,22,3),(13,23,4),(13,24,5),(13,25,6),(14,26,1),(14,27,2),(14,28,3),(14,29,4),(14,30,5),(14,31,6),(15,7,1),(15,8,2),(15,9,3),(15,11,4),(15,12,5),(15,13,6),(15,14,7),(16,15,1),(16,16,2),(16,17,3),(16,18,4),(16,19,5),(16,20,6),(17,21,1),(17,22,2),(17,23,3),(17,24,4),(17,25,5),(17,26,6),(17,27,7),(18,1,5),(18,2,6),(18,28,1),(18,29,2),(18,30,3),(18,31,4);
/*!40000 ALTER TABLE `contest_problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contests_score_cache`
--

DROP TABLE IF EXISTS `contests_score_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contests_score_cache` (
  `contest_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `solved_count` int NOT NULL,
  `penalty_minutes` int NOT NULL,
  `per_problem_stats` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY (`contest_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `contests_score_cache_ibfk_1` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`),
  CONSTRAINT `contests_score_cache_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contests_score_cache`
--

LOCK TABLES `contests_score_cache` WRITE;
/*!40000 ALTER TABLE `contests_score_cache` DISABLE KEYS */;
/*!40000 ALTER TABLE `contests_score_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `problem`
--

DROP TABLE IF EXISTS `problem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problem` (
  `problem_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `input_spec` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `output_spec` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `sample_input` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `sample_output` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `remark` text COLLATE utf8mb4_unicode_ci,
  `time_limit` int NOT NULL,
  `memory_limit` int NOT NULL,
  `creator_id` bigint NOT NULL,
  `is_hidden` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`problem_id`),
  UNIQUE KEY `title` (`title`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `problem_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problem`
--

LOCK TABLES `problem` WRITE;
/*!40000 ALTER TABLE `problem` DISABLE KEYS */;
INSERT INTO `problem` VALUES (1,'A+B Problem（修改版）','计算两个数之和（修改版）','输入包含两行，第一行是整数数组（以空格分隔），第二行是目标值。','输出两个整数，即数组中和为目标值的两个元素的下标，以空格分隔。','2 7 11 15\n9','0 1','基础题，建议使用哈希表。',2000,64,1,0,'2025-10-06 16:55:57','2025-10-19 18:25:01'),(2,'反转整数 #02','给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。','输入一个整数 x。','输出反转后的整数。如果反转结果超出 32 位有符号整数范围，则返回 0。','123','321','注意溢出问题。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(3,'回文数 #03','判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。','输入一个整数 x。','输出 True 或 False。','121','True','可以考虑不转换成字符串。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(4,'罗马数字转整数 #04','给定一个罗马数字，将其转换成整数。输入确保在 1 到 3999 的范围内。','输入一个字符串，表示罗马数字。','输出对应的整数。','IV','4','注意特殊组合，如 IV, IX 等。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(5,'最长公共前缀 #05','编写一个函数来查找字符串数组中的最长公共前缀。如果不存在公共前缀，返回空字符串 \"\"。','输入一个字符串数组，每行一个字符串。','输出最长公共前缀字符串。','flower\nflow\nflight','fl','可以逐个比较或使用分治法。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(6,'有效的括号 #06','给定一个只包括 \'(\'，\')\'，\'{\'，\'}\'，\'[\'，\']\' 的字符串，判断字符串是否有效。','输入一个字符串。','输出 True 或 False。','()[]{}','True','使用栈（Stack）来解决。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(7,'合并两个有序链表 #07','将两个升序链表合并为一个新的升序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。','输入两个链表的节点值（以空格分隔，表示链表）。','输出合并后的链表节点值（以空格分隔）。','1 2 4\n1 3 4','1 1 2 3 4 4','可以使用递归或迭代。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(8,'删除排序数组中的重复项 #08','给定一个排序数组，你需要在 原地 删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。','输入一个排序数组（以空格分隔）。','输出新的长度，以及修改后的数组前 M 个元素。','1 1 2','2\n1 2','使用双指针技巧。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(9,'移除元素 #09','给你一个数组 nums 和一个值 val，你需要 原地 移除所有数值等于 val 的元素，并返回移除后数组的新长度。','输入数组和值 val。','输出新的长度，以及修改后的数组前 M 个元素。','3 2 2 3\n3','2\n2 2','双指针或覆盖法。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(11,'搜索插入位置 #11','给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。','输入排序数组和目标值。','输出插入位置的索引。','1 3 5 6\n5','2','典型的二分查找应用。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(12,'最大子数组和 #12','给你一个整数数组 nums ，请你找出其中**连续子数组**中元素和最大的子数组，并返回其和。','输入一个整数数组。','输出最大的子数组和。','-2 1 -3 4 -1 2 1 -5 4','6','使用动态规划（Kadane算法）。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(13,'最后一个单词的长度 #13','给你一个字符串 s，由若干单词组成，单词前后用一些空格字符隔开。返回字符串中最后一个单词的长度。','输入一个字符串 s。','输出最后一个单词的长度。','Hello World','5','从后向前遍历。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(14,'加一 #14','给定一个由 整数 组成的 非空 数组所表示的非负整数，在该数的基础上加一。','输入一个非空数组，表示一个非负整数。','输出加一后的数组。','1 2 3','1 2 4','处理进位问题。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(15,'二进制求和 #15','给你两个二进制字符串 a 和 b ，以二进制字符串的形式返回它们的和。','输入两个二进制字符串 a 和 b。','输出它们的和（二进制字符串）。','1010\n1011','10101','模拟加法，注意进位。',1000,64,1,1,'2025-10-06 16:55:57','2025-10-18 17:54:35'),(16,'x 的平方根 #16','给你一个非负整数 x ，计算并返回 x 的算术平方根。','输入一个非负整数 x。','输出 x 的算术平方根的整数部分。','8','2','使用二分查找或牛顿迭代法。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(17,'爬楼梯 #17','假设你正在爬楼梯。需要 n 阶你才能到达楼顶。每次你可以爬 1 或 2 个台阶。有多少种不同的方法可以爬到楼顶呢？','输入一个整数 n。','输出不同的方法数。','3','3','典型的斐波那契数列问题。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(18,'删除有序链表中的重复元素 #18','给定一个已排序的链表的头 head ，删除所有重复的元素，使每个元素只出现一次。','输入一个排序链表的节点值。','输出删除重复元素后的链表节点值。','1 1 2 3 3','1 2 3','遍历链表，比较相邻节点。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(19,'合并两个有序数组 #19','给你两个有序整数数组 nums1 和 nums2，请将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。','输入 nums1, m, nums2, n。','输出合并后的 nums1 数组。','1 2 3 0 0 0\n3\n2 5 6\n3','1 2 3 5 6','从后向前合并是高效的方法。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(20,'同构字符串 #20','给定两个字符串 s 和 t ，判断它们是否是同构的。如果 s 中的字符可以按某种映射关系替换得到 t ，那么这两个字符串是同构的。','输入两个字符串 s 和 t。','输出 True 或 False。','egg\nadd','True','使用哈希表进行双向映射。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(21,'计数质数 #21','统计所有小于非负整数 n 的质数的数量。','输入一个整数 n。','输出小于 n 的质数数量。','10','4','使用埃拉托斯特尼筛法（Sieve of Eratosthenes）。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(22,'移动零 #22','给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。','输入一个整数数组。','输出移动零后的数组。','0 1 0 3 12','1 3 12 0 0','双指针，一个指向当前处理的元素，一个指向下一个非零元素应放置的位置。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(23,'存在重复元素 #23','给定一个整数数组，判断是否存在重复元素。如果任何值在数组中出现至少两次，返回 True。','输入一个整数数组。','输出 True 或 False。','1 2 3 1','True','使用哈希集合（Set）进行快速查找。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(24,'颠倒二进制位 #24','颠倒给定的 32 位无符号整数的二进制位。','输入一个 32 位无符号整数。','输出颠倒后的整数。','43261596','964176192','位运算，注意循环次数和位移操作。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(25,'多数元素 #25','给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数大于 $lfloor n/2 \rfloor$ 的元素。','输入一个整数数组。','输出多数元素。','2 2 1 1 1 2 2','2','使用 Boyer-Moore 投票算法。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(26,'各位相加 #26','给定一个非负整数 num，反复将各个位上的数字相加，直到结果为一位数。','输入一个非负整数 num。','输出最终的一位数。','38','2','找规律或使用数学方法（数字根）。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(27,'赎金信 #27','给定一个赎金信（ransomNote）字符串和一个杂志（magazine）字符串，判断赎金信能不能由杂志里的文字构成。','输入赎金信和杂志字符串。','输出 True 或 False。','a\nab','True','统计字符频率是关键。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(28,'斐波那契数 #28','斐波那契数，通常用 F(n) 表示，形成了一个数列 $0, 1, 1, 2, 3, 5, 8, ...$，其中 $F(0) = 0, F(1) = 1$，且对于 $n > 1$ 的情况，$F(n) = F(n - 1) + F(n - 2)$。','输入一个整数 n。','输出 F(n)。','4','3','可以使用递归、迭代或动态规划。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(29,'两数相加 II #29','给你两个 **非空** 链表来表示两个非负整数。数字位在链表中是 **反向** 存储的，并且每个节点只存储 **一位** 数字。请将两个数相加，并以**新的链表**的形式返回和。','输入两个反向存储的链表节点值（以空格分隔）。','输出和的链表节点值（以空格分隔）。','2 4 3\n5 6 4','7 0 8','（342 + 465 = 807）',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(30,'岛屿数量 #30','给你一个由 \'1\'（陆地）和 \'0\'（水）组成的二维网格，请你计算网格中岛屿的数量。','输入一个二维网格（例如：每行用分号分隔）。','输出岛屿的数量。','11110;11010;11000;00000','1','使用深度优先搜索（DFS）或广度优先搜索（BFS）来遍历。',1000,64,1,0,'2025-10-06 16:55:57','2025-10-06 16:55:57'),(31,'A+B Problem','计算两个数之和','输入两个整数','输出一个整数','1 2','3','无',1000,256,101,1,'2025-10-06 18:01:12','2025-10-14 22:18:49');
/*!40000 ALTER TABLE `problem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `problem_dataset`
--

DROP TABLE IF EXISTS `problem_dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problem_dataset` (
  `dataset_id` bigint NOT NULL AUTO_INCREMENT,
  `problem_id` bigint NOT NULL,
  `zip_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` int NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `added_by` bigint NOT NULL,
  `added_at` datetime NOT NULL,
  PRIMARY KEY (`dataset_id`),
  KEY `problem_id` (`problem_id`),
  KEY `added_by` (`added_by`),
  CONSTRAINT `problem_dataset_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`),
  CONSTRAINT `problem_dataset_ibfk_2` FOREIGN KEY (`added_by`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problem_dataset`
--

LOCK TABLES `problem_dataset` WRITE;
/*!40000 ALTER TABLE `problem_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `problem_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solution_report`
--

DROP TABLE IF EXISTS `solution_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solution_report` (
  `report_id` bigint NOT NULL AUTO_INCREMENT,
  `problem_id` bigint NOT NULL,
  `creator_id` bigint NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_published` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`report_id`),
  KEY `problem_id` (`problem_id`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `solution_report_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`),
  CONSTRAINT `solution_report_ibfk_2` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solution_report`
--

LOCK TABLES `solution_report` WRITE;
/*!40000 ALTER TABLE `solution_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `solution_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `submission_id` bigint NOT NULL AUTO_INCREMENT,
  `contest_id` bigint NOT NULL,
  `problem_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `code` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `code_length` int NOT NULL,
  `language` enum('C','C++','Java','Python','Other') COLLATE utf8mb4_unicode_ci NOT NULL,
  `compiler` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `result` enum('AC','WA','RE','CE','TLE','MLE','PE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `time_used` int NOT NULL,
  `memory_used` int NOT NULL,
  `created_at` datetime NOT NULL,
  `judge_log_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`submission_id`),
  KEY `contest_id` (`contest_id`),
  KEY `problem_id` (`problem_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `submission_ibfk_1` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`),
  CONSTRAINT `submission_ibfk_2` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`),
  CONSTRAINT `submission_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission_testcase`
--

DROP TABLE IF EXISTS `submission_testcase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission_testcase` (
  `submission_id` bigint NOT NULL,
  `case_index` int NOT NULL,
  `dataset_version` int NOT NULL,
  `result` enum('AC','WA','RE','CE','TLE','MLE','PE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `time_used` int NOT NULL,
  `memory_used` int NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`submission_id`,`case_index`),
  CONSTRAINT `submission_testcase_ibfk_1` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`submission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission_testcase`
--

LOCK TABLES `submission_testcase` WRITE;
/*!40000 ALTER TABLE `submission_testcase` DISABLE KEYS */;
/*!40000 ALTER TABLE `submission_testcase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_plan`
--

DROP TABLE IF EXISTS `training_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_plan` (
  `plan_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `creator_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `training_plan_pk` (`title`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `training_plan_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_plan`
--

LOCK TABLES `training_plan` WRITE;
/*!40000 ALTER TABLE `training_plan` DISABLE KEYS */;
INSERT INTO `training_plan` VALUES (1,'2024冬季算法集训','针对基础算法和数据结构的强化训练计划。',2,'2024-12-01 09:00:00','2025-02-01 18:00:00','2024-11-25 10:00:00','2024-11-25 10:00:00'),(2,'2025春季ICPC冲刺','面向ICPC竞赛的高强度冲刺训练，包含多场模拟赛。',2,'2025-09-01 09:00:00','2025-11-30 18:00:00','2025-08-20 11:00:00','2025-08-20 11:00:00'),(7,'春季集训（修改版）','2025年春季集训计划修改版',101,'2026-03-01 09:00:00','2026-05-01 18:00:00','2025-10-11 14:08:55','2025-10-11 15:31:21'),(11,'春季集训','2025年春季集训计划',101,'2025-10-11 17:08:00','2026-05-01 18:00:00','2025-10-11 17:07:54','2025-10-11 17:07:54'),(12,'春季集训（修','2025年春季集训计划修改版',101,'2025-10-11 17:09:00','2026-05-11 18:00:00','2025-10-11 17:08:48','2025-10-11 17:10:35');
/*!40000 ALTER TABLE `training_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_plan_contest`
--

DROP TABLE IF EXISTS `training_plan_contest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_plan_contest` (
  `plan_id` bigint NOT NULL,
  `contest_id` bigint NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `sequence` int NOT NULL,
  `added_time` datetime NOT NULL,
  PRIMARY KEY (`plan_id`,`contest_id`),
  KEY `contest_id` (`contest_id`),
  CONSTRAINT `training_plan_contest_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `training_plan` (`plan_id`),
  CONSTRAINT `training_plan_contest_ibfk_2` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_plan_contest`
--

LOCK TABLES `training_plan_contest` WRITE;
/*!40000 ALTER TABLE `training_plan_contest` DISABLE KEYS */;
INSERT INTO `training_plan_contest` VALUES (1,1,'第一周：基础数据结构',1,'2024-12-08 14:00:00'),(1,2,'第二周：搜索算法',2,'2024-12-15 14:00:00'),(1,3,'第三周：动态规划入门',3,'2024-12-22 14:00:00'),(1,4,'第四周：图论基础',4,'2024-12-29 14:00:00'),(2,5,'冲刺模拟赛(一)',1,'2025-09-07 13:00:00'),(2,6,'冲刺模拟赛(二)',2,'2025-09-14 13:00:00'),(2,7,'冲刺模拟赛(三)',3,'2025-09-21 13:00:00'),(2,8,'专题训练：数论',4,'2025-09-28 14:00:00'),(2,9,'专题训练：计算几何',5,'2025-10-05 14:00:00'),(2,10,'综合模拟赛',6,'2025-10-12 13:00:00'),(7,1,NULL,1,'2025-10-11 15:31:21'),(7,2,NULL,2,'2025-10-11 15:31:21'),(7,3,NULL,3,'2025-10-11 15:31:21'),(11,1,NULL,1,'2025-10-11 17:07:54'),(11,2,NULL,2,'2025-10-11 17:07:54'),(11,3,NULL,3,'2025-10-11 17:07:54'),(11,4,NULL,4,'2025-10-11 17:07:54'),(11,5,NULL,5,'2025-10-11 17:07:54'),(12,1,NULL,1,'2025-10-11 17:08:48'),(12,2,NULL,2,'2025-10-11 17:08:48'),(12,3,NULL,3,'2025-10-11 17:08:48'),(12,4,NULL,4,'2025-10-11 17:08:48'),(12,5,NULL,5,'2025-10-11 17:08:48'),(12,14,NULL,6,'2025-10-11 17:08:48'),(12,15,NULL,7,'2025-10-11 17:08:48'),(12,16,NULL,8,'2025-10-11 17:08:48');
/*!40000 ALTER TABLE `training_plan_contest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_plan_student`
--

DROP TABLE IF EXISTS `training_plan_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_plan_student` (
  `plan_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `enrolled_at` datetime NOT NULL,
  PRIMARY KEY (`plan_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `training_plan_student_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `training_plan` (`plan_id`),
  CONSTRAINT `training_plan_student_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_plan_student`
--

LOCK TABLES `training_plan_student` WRITE;
/*!40000 ALTER TABLE `training_plan_student` DISABLE KEYS */;
INSERT INTO `training_plan_student` VALUES (1,3,'2024-11-26 10:00:00'),(1,4,'2024-11-26 10:00:00'),(1,5,'2024-11-27 11:30:00'),(1,6,'2024-11-27 12:00:00'),(1,7,'2024-11-28 09:00:00'),(2,3,'2025-08-21 10:00:00'),(2,4,'2025-08-21 10:00:00'),(2,5,'2025-08-21 10:00:00'),(2,8,'2025-08-22 15:00:00'),(2,9,'2025-08-22 15:00:00'),(2,10,'2025-08-23 16:00:00'),(2,11,'2025-08-24 17:00:00'),(2,12,'2025-08-25 18:00:00'),(7,1,'2025-10-11 15:31:21'),(7,2,'2025-10-11 15:31:21'),(7,3,'2025-10-11 15:31:21'),(7,4,'2025-10-11 15:31:21'),(11,1,'2025-10-11 17:07:54'),(11,2,'2025-10-11 17:07:54'),(11,3,'2025-10-11 17:07:54'),(11,4,'2025-10-11 17:07:54'),(12,1,'2025-10-11 17:08:48'),(12,2,'2025-10-11 17:08:48'),(12,3,'2025-10-11 17:08:48'),(12,4,'2025-10-11 17:08:48');
/*!40000 ALTER TABLE `training_plan_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone_number` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('STUDENT','TEACHER','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'libai@example.com','13800010001','李白','20240001','lb12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(2,'dufu@example.com','13800010002','杜甫','20240002','df12345678','TEACHER',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(3,'sushi@example.com','13800010003','苏轼','20240003','ss12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(4,'xinqiji@example.com','13800010004','辛弃疾','20240004','xqjj12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(5,'baijuyi@example.com','13800010005','白居易','20240005','bjy12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(6,'lisan@example.com','13800010006','李商隐','20240006','lsy12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(7,'wangwei@example.com','13800010007','王维','20240007','ww12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(8,'meng@example.com','13800010008','孟浩然','20240008','mhr12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(9,'tao@example.com','13800010009','陶渊明','20240009','tym12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(10,'luyou@example.com','13800010010','陆游','20240010','ly12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(11,'wangzhihuan@example.com','13800010011','王之涣','20240011','wzh12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(12,'liu@example.com','13800010012','柳宗元','20240012','lzy12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(13,'han@example.com','13800010013','韩愈','20240013','hy12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(14,'ouyang@example.com','13800010014','欧阳修','20240014','oyx12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(15,'liqingzhao@example.com','13800010015','李清照','20240015','lqz12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(16,'du@example.com','13800010016','杜牧','20240016','dm12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(17,'cao@example.com','13800010017','曹操','20240017','cc12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(18,'caozhi@example.com','13800010018','曹植','20240018','cz12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-20 22:54:07'),(19,'wang@example.com','13800010019','王昌龄','20240019','wcl12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(20,'cen@example.com','13800010020','岑参','20240020','cs12345678','STUDENT',1,'2025-10-05 20:49:44','2025-10-05 20:49:44'),(21,'test@example.com','13800138000','刘芳','20250001','$2a$10$xAMX7lYFTIMCPICzP945veaTJKUAppR8FdynrxxJPT9DmR/iViom2','STUDENT',0,'2025-10-05 18:12:07','2025-10-05 22:21:18'),(22,'tes11t@example.com','14800000000','张三','20050001','$2a$10$7ww4qdWCkpMvCp.njlDK8eBAShzJw6VFR.7U.K8AeGTjeNYTN50cK','STUDENT',1,'2025-10-11 10:27:44','2025-10-11 10:27:44'),(101,'admin@example.com','13800010021','adminTest','00000000','admin12345678','ADMIN',1,'2025-10-05 20:51:46','2025-10-18 17:54:17');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-27 18:28:58
