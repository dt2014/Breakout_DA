
-- phpMyAdmin SQL Dump
-- version 2.11.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 01, 2015 at 09:19 AM
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
  PRIMARY KEY (`ball_id`),
  KEY `owner_id` (`owner_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Ball`
--

INSERT INTO `Ball` VALUES(1, '1', 0.999, 0.888, 0.999, 0.888, 'no', 2, 2);
INSERT INTO `Ball` VALUES(2, '2', 0.333, 0.701, 1.000, 1.000, 'no', 0, 0);

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

INSERT INTO `Brick` VALUES(1, 'INACTIVE', 100, 'N', 0.590, 0.465, 'no', 1, 1);
INSERT INTO `Brick` VALUES(2, 'ACTIVE', 300, 'N', 0.123, 0.578, 'no', 0, 0);
INSERT INTO `Brick` VALUES(3, 'ACTIVE', 400, 'N', 0.930, 0.765, 'no', 0, 0);
INSERT INTO `Brick` VALUES(4, 'ACTIVE', 105, 'Y', 0.656, 0.347, 'no', 0, 0);

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

INSERT INTO `Player` VALUES('jbb4', 505, 0.123, 'A', 'ACTIVE', 1, 'no', 2, 2);
INSERT INTO `Player` VALUES('jbb5', -105, 0.560, 'B', 'ACTIVE', 0, 'shared', 0, 0);
