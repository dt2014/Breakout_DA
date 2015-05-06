
-- phpMyAdmin SQL Dump
-- version 2.11.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 05, 2015 at 12:46 AM
-- Server version: 5.1.57
-- PHP Version: 5.2.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `a7990677_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `Ball`
--

CREATE TABLE `Ball` (
  `ball_id` int(1) NOT NULL,
  `owner_name` varchar(30) DEFAULT NULL,
  `ball_position_x` decimal(4,3) DEFAULT NULL,
  `ball_position_y` decimal(4,3) DEFAULT NULL,
  `ball_speed_x` decimal(4,3) DEFAULT NULL,
  `ball_speed_y` decimal(4,3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0',
  `change_mark` int(1) DEFAULT NULL,
  PRIMARY KEY (`ball_id`),
  KEY `owner_id` (`owner_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Ball`
--

INSERT INTO `Ball` VALUES(1, 'mary', 0.500, 0.600, 0.005, 0.005, 'no', 0, 0, 0);
INSERT INTO `Ball` VALUES(2, 'lily', 0.500, 0.400, -0.005, -0.005, 'no', 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `Brick`
--

CREATE TABLE `Brick` (
  `brick_id` int(3) NOT NULL,
  `brick_status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  `brick_value` int(6) DEFAULT NULL,
  `brick_special` enum('Y','N') DEFAULT NULL,
  `brick_position_x` decimal(4,3) DEFAULT NULL,
  `brick_position_y` decimal(4,3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0',
  PRIMARY KEY (`brick_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Brick`
--

INSERT INTO `Brick` VALUES(1, 'ACTIVE', 100, 'N', 0.020, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(2, 'ACTIVE', 100, 'N', 0.100, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(3, 'ACTIVE', 100, 'N', 0.180, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(4, 'ACTIVE', 100, 'N', 0.260, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(5, 'ACTIVE', 100, 'N', 0.340, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(6, 'ACTIVE', 100, 'Y', 0.420, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(7, 'ACTIVE', 100, 'N', 0.500, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(8, 'ACTIVE', 100, 'N', 0.580, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(9, 'ACTIVE', 100, 'N', 0.660, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(10, 'ACTIVE', 100, 'N', 0.740, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(11, 'ACTIVE', 100, 'N', 0.820, 0.500, 'no', 0, 0);
INSERT INTO `Brick` VALUES(12, 'ACTIVE', 100, 'N', 0.900, 0.500, 'no', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `Player`
--

CREATE TABLE `Player` (
  `player_name` varchar(30) NOT NULL,
  `player_score` int(10) DEFAULT NULL,
  `bar_position_x` decimal(4,3) DEFAULT NULL,
  `map_side` enum('A','B') DEFAULT NULL,
  `player_status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  `latest_eliminated_brick_id` int(3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0',
  PRIMARY KEY (`player_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Player`
--

INSERT INTO `Player` VALUES('13mary', 0, 0.300, 'A', 'INACTIVE', 0, 'no', 0, 0);
INSERT INTO `Player` VALUES('35lily', 0, 0.300, 'B', 'INACTIVE', 0, 'no', 0, 0);
