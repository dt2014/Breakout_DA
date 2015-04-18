-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 18, 2015 at 07:32 AM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `Breakout`
--

-- --------------------------------------------------------

--
-- Table structure for table `Ball`
--

CREATE TABLE IF NOT EXISTS `Ball` (
  `ball_id` int(1) NOT NULL,
  `owner_name` varchar(30) DEFAULT NULL,
  `ball_position_x` decimal(4,3) DEFAULT NULL,
  `ball_position_y` decimal(4,3) DEFAULT NULL,
  `ball_speed_x` decimal(4,3) DEFAULT NULL,
  `ball_speed_y` decimal(4,3) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Ball`
--

INSERT INTO `Ball` (`ball_id`, `owner_name`, `ball_position_x`, `ball_position_y`, `ball_speed_x`, `ball_speed_y`) VALUES
(1, '1', '0.440', '0.410', '1.111', '2.222'),
(2, '2', '0.333', '0.701', '1.000', '1.000');

-- --------------------------------------------------------

--
-- Table structure for table `Brick`
--

CREATE TABLE IF NOT EXISTS `Brick` (
  `brick_id` int(3) NOT NULL,
  `brick_position_x` decimal(4,3) DEFAULT NULL,
  `brick_position_y` decimal(4,3) DEFAULT NULL,
  `brick_status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  `brick_value` int(6) DEFAULT NULL,
  `brick_special` enum('Y','N') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Brick`
--

INSERT INTO `Brick` (`brick_id`, `brick_position_x`, `brick_position_y`, `brick_status`, `brick_value`, `brick_special`) VALUES
(1, '0.222', '0.560', 'ACTIVE', 100, 'N'),
(2, '0.333', '0.202', 'ACTIVE', 300, 'N'),
(3, '0.444', '0.345', 'INACTIVE', 400, 'N'),
(4, '0.555', '0.135', 'INACTIVE', 105, 'Y');

-- --------------------------------------------------------

--
-- Table structure for table `Player`
--

CREATE TABLE IF NOT EXISTS `Player` (
  `player_name` varchar(30) NOT NULL,
  `player_score` int(10) DEFAULT NULL,
  `bar_position_x` decimal(4,3) DEFAULT NULL,
  `bar_position_y` decimal(4,3) DEFAULT NULL,
  `map_side` enum('A','B') DEFAULT NULL,
  `player_status` enum('ACTIVE','INACTIVE') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Player`
--

INSERT INTO `Player` (`player_name`, `player_score`, `bar_position_x`, `bar_position_y`, `map_side`, `player_status`) VALUES
('1', 9050, '0.222', '0.333', 'A', 'ACTIVE'),
('2', 4580, '0.560', '0.150', 'B', 'ACTIVE');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Ball`
--
ALTER TABLE `Ball`
 ADD PRIMARY KEY (`ball_id`), ADD KEY `owner_id` (`owner_name`);

--
-- Indexes for table `Brick`
--
ALTER TABLE `Brick`
 ADD PRIMARY KEY (`brick_id`);

--
-- Indexes for table `Player`
--
ALTER TABLE `Player`
 ADD PRIMARY KEY (`player_name`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Ball`
--
ALTER TABLE `Ball`
ADD CONSTRAINT `ball_ibfk_1` FOREIGN KEY (`owner_name`) REFERENCES `Player` (`player_name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
