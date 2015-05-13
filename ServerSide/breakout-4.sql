-- phpMyAdmin SQL Dump
-- version 4.3.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 13, 2015 at 03:54 PM
-- Server version: 5.6.24
-- PHP Version: 5.6.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `da`
--

-- --------------------------------------------------------

--
-- Table structure for table `ball`
--

CREATE TABLE IF NOT EXISTS `ball` (
  `ball_id` int(1) NOT NULL,
  `owner_name` varchar(30) DEFAULT NULL,
  `ball_position_x` decimal(4,3) DEFAULT NULL,
  `ball_position_y` decimal(4,3) DEFAULT NULL,
  `ball_speed_x` decimal(4,3) DEFAULT NULL,
  `ball_speed_y` decimal(4,3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0',
  `change_mark` int(1) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `ball`
--

INSERT INTO `ball` (`ball_id`, `owner_name`, `ball_position_x`, `ball_position_y`, `ball_speed_x`, `ball_speed_y`, `lock_type`, `serving_number`, `ticket`, `change_mark`) VALUES
(1, 'nexus', '0.073', '0.985', '0.000', '0.000', 'no', 0, 0, 0),
(2, 'samsung', '0.959', '0.135', '0.007', '0.005', 'no', 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `brick`
--

CREATE TABLE IF NOT EXISTS `brick` (
  `brick_id` int(3) NOT NULL,
  `brick_status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  `brick_value` int(6) DEFAULT NULL,
  `brick_special` enum('Y','N') DEFAULT NULL,
  `brick_position_x` decimal(4,3) DEFAULT NULL,
  `brick_position_y` decimal(4,3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `brick`
--

INSERT INTO `brick` (`brick_id`, `brick_status`, `brick_value`, `brick_special`, `brick_position_x`, `brick_position_y`, `lock_type`, `serving_number`, `ticket`) VALUES
(1, 'ACTIVE', 100, 'N', '0.020', '0.500', 'no', 0, 0),
(2, 'ACTIVE', 100, 'N', '0.100', '0.500', 'no', 0, 0),
(3, 'ACTIVE', 100, 'N', '0.180', '0.500', 'no', 0, 0),
(4, 'ACTIVE', 100, 'N', '0.260', '0.500', 'no', 0, 0),
(5, 'ACTIVE', 100, 'N', '0.340', '0.500', 'no', 0, 0),
(6, 'ACTIVE', 100, 'Y', '0.420', '0.500', 'no', 0, 0),
(7, 'ACTIVE', 100, 'N', '0.500', '0.500', 'no', 0, 0),
(8, 'ACTIVE', 100, 'N', '0.580', '0.500', 'no', 0, 0),
(9, 'ACTIVE', 100, 'N', '0.660', '0.500', 'no', 0, 0),
(10, 'ACTIVE', 100, 'N', '0.740', '0.500', 'no', 0, 0),
(11, 'ACTIVE', 100, 'N', '0.820', '0.500', 'no', 0, 0),
(12, 'ACTIVE', 100, 'N', '0.900', '0.500', 'no', 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE IF NOT EXISTS `player` (
  `player_name` varchar(30) NOT NULL,
  `player_score` int(10) DEFAULT NULL,
  `bar_position_x` decimal(4,3) DEFAULT NULL,
  `map_side` enum('A','B') DEFAULT NULL,
  `player_status` enum('ACTIVE','INACTIVE','LOSE') DEFAULT NULL,
  `latest_eliminated_brick_id` int(3) DEFAULT NULL,
  `lock_type` enum('no','shared','exclusive') DEFAULT 'no',
  `serving_number` int(8) DEFAULT '0',
  `ticket` int(8) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `player`
--

INSERT INTO `player` (`player_name`, `player_score`, `bar_position_x`, `map_side`, `player_status`, `latest_eliminated_brick_id`, `lock_type`, `serving_number`, `ticket`) VALUES
('51nexus', 0, '0.300', 'A', 'INACTIVE', 0, 'no', 0, 0),
('93samsung', 0, '0.300', 'B', 'INACTIVE', 0, 'no', 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ball`
--
ALTER TABLE `ball`
  ADD PRIMARY KEY (`ball_id`), ADD KEY `owner_id` (`owner_name`);

--
-- Indexes for table `brick`
--
ALTER TABLE `brick`
  ADD PRIMARY KEY (`brick_id`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`player_name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
