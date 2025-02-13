-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 12, 2025 at 10:57 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.3.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `wamia_go`
--

-- --------------------------------------------------------

--
-- Table structure for table `announcement`
--

CREATE TABLE `announcement` (
                                `id_announcement` int(11) NOT NULL,
                                `id_transporter` int(11) NOT NULL,
                                `title` varchar(50) NOT NULL,
                                `content` varchar(255) NOT NULL,
                                `date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                                `zone` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
                                `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `announcement`
--

INSERT INTO `announcement` (`id_announcement`, `id_transporter`, `title`, `content`, `date`, `zone`, `status`) VALUES
                                                                                                                   (1, 2, 'New Transport Service', 'We are launching a new transport service in Tunis.', '2025-02-01 22:55:20', 'Tunis', 1),
                                                                                                                   (2, 2, 'Maintenance Notice', 'Maintenance work will be carried out on the Sousse station.', '2025-02-01 22:55:27', 'Sousse', 0);

-- --------------------------------------------------------

--
-- Table structure for table `bicycle`
--

CREATE TABLE `bicycle` (
                           `id_bike` int(11) NOT NULL,
                           `id_station` int(11) NOT NULL,
                           `status` enum('available','in_use','charging','maintenance','reserved') NOT NULL,
                           `battery_level` float DEFAULT NULL,
                           `range_km` float DEFAULT NULL,
                           `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bicycle`
--

INSERT INTO `bicycle` (`id_bike`, `id_station`, `status`, `battery_level`, `range_km`, `last_updated`) VALUES
                                                                                                           (1, 1, 'available', 100, 50, '2025-02-12 16:48:37'),
                                                                                                           (2, 1, 'available', 80, 40, '2025-02-12 16:48:37'),
                                                                                                           (3, 2, 'available', 90, 45, '2025-02-12 16:48:37'),
                                                                                                           (4, 2, 'available', 70, 35, '2025-02-12 16:48:37'),
                                                                                                           (5, 3, 'available', 60, 30, '2025-02-12 16:48:37'),
                                                                                                           (6, 4, 'available', 100, 50, '2025-02-12 16:48:37'),
                                                                                                           (7, 5, 'available', 50, 25, '2025-02-12 16:48:37'),
                                                                                                           (8, 6, 'available', 40, 20, '2025-02-12 16:48:37'),
                                                                                                           (9, 7, 'available', 30, 15, '2025-02-12 16:48:37'),
                                                                                                           (10, 8, 'available', 20, 10, '2025-02-12 16:48:37');

-- --------------------------------------------------------

--
-- Table structure for table `bicycle_rental`
--

CREATE TABLE `bicycle_rental` (
                                  `id_user_rental` int(11) NOT NULL,
                                  `id_user` int(11) NOT NULL,
                                  `id_bike` int(11) NOT NULL,
                                  `id_start_station` int(11) NOT NULL,
                                  `id_end_station` int(11) NOT NULL,
                                  `start_time` timestamp NOT NULL DEFAULT current_timestamp(),
                                  `end_time` timestamp NULL DEFAULT NULL,
                                  `distance_km` float NOT NULL,
                                  `battery_used` float NOT NULL,
                                  `cost` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bicycle_rental`
--

INSERT INTO `bicycle_rental` (`id_user_rental`, `id_user`, `id_bike`, `id_start_station`, `id_end_station`, `start_time`, `end_time`, `distance_km`, `battery_used`, `cost`) VALUES
                                                                                                                                                                                 (1, 1, 1, 1, 2, '2025-02-12 09:00:00', '2025-02-12 09:30:00', 5, 10, 2.5),
                                                                                                                                                                                 (2, 4, 3, 2, 3, '2025-02-12 10:00:00', '2025-02-12 10:45:00', 7.5, 15, 3.75);

-- --------------------------------------------------------

--
-- Table structure for table `bicycle_station`
--

CREATE TABLE `bicycle_station` (
                                   `id_station` int(11) NOT NULL,
                                   `name` varchar(50) NOT NULL,
                                   `id_location` int(11) NOT NULL,
                                   `total_docks` int(11) NOT NULL,
                                   `available_docks` int(11) NOT NULL,
                                   `available_bikes` int(11) NOT NULL,
                                   `charging_bikes` int(11) NOT NULL,
                                   `status` enum('active','inactive','maintenance','disabled') NOT NULL DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bicycle_station`
--

INSERT INTO `bicycle_station` (`id_station`, `name`, `id_location`, `total_docks`, `available_docks`, `available_bikes`, `charging_bikes`, `status`) VALUES
                                                                                                                                                         (1, 'Station Tunis Centre', 1, 20, 15, 5, 0, 'active'),
                                                                                                                                                         (2, 'Station Sousse Medina', 2, 15, 10, 5, 0, 'active'),
                                                                                                                                                         (3, 'Station Port El Kantaoui', 3, 10, 8, 2, 0, 'active'),
                                                                                                                                                         (4, 'Station Sfax City', 5, 25, 20, 5, 0, 'active'),
                                                                                                                                                         (5, 'Station Bizerte Port', 6, 12, 10, 2, 0, 'active'),
                                                                                                                                                         (6, 'Station Gabès Oasis', 7, 8, 6, 2, 0, 'active'),
                                                                                                                                                         (7, 'Station Monastir Marina', 8, 10, 7, 3, 0, 'active'),
                                                                                                                                                         (8, 'Station Nabeul Beach', 9, 15, 12, 3, 0, 'active'),
                                                                                                                                                         (9, 'Station Mahdia Old Town', 10, 10, 8, 2, 0, 'active');

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
                           `id_booking` int(11) NOT NULL,
                           `id_trip` int(11) NOT NULL,
                           `id_passenger` int(11) NOT NULL,
                           `reserved_seats` int(11) NOT NULL CHECK (`reserved_seats` > 0),
                           `status` enum('Pending','Confirmed','Canceled') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking`
--

INSERT INTO `booking` (`id_booking`, `id_trip`, `id_passenger`, `reserved_seats`, `status`) VALUES
                                                                                                (1, 1, 1, 1, 'Confirmed'),
                                                                                                (2, 1, 4, 2, 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
                          `id_driver` int(11) NOT NULL,
                          `id_user` int(11) NOT NULL,
                          `permit_number` varchar(20) NOT NULL,
                          `role` enum('TAXI_DRIVER','TRANSPORTER','CARPOOL_DRIVER') NOT NULL,
                          `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `driver`
--

INSERT INTO `driver` (`id_driver`, `id_user`, `permit_number`, `role`, `status`) VALUES
                                                                                     (1, 1, 'ABC123456', 'TAXI_DRIVER', 1),
                                                                                     (2, 2, 'XYZ789101', 'TRANSPORTER', 0),
                                                                                     (3, 3, 'DEF345678', 'CARPOOL_DRIVER', 1),
                                                                                     (4, 4, 'GHI567890', 'TAXI_DRIVER', 0),
                                                                                     (5, 5, 'JKL123890', 'CARPOOL_DRIVER', 1),
                                                                                     (6, 1, 'A1B2C3456', 'TAXI_DRIVER', 1),
                                                                                     (7, 2, 'XY779101', 'TRANSPORTER', 0);

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
                            `id_location` int(11) NOT NULL,
                            `address` varchar(255) NOT NULL,
                            `latitude` decimal(9,6) NOT NULL,
                            `longitude` decimal(9,6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`id_location`, `address`, `latitude`, `longitude`) VALUES
                                                                               (1, 'Avenue Habib Bourguiba, Tunis', 36.806500, 10.181500),
                                                                               (2, 'Medina of Sousse, Sousse', 35.825400, 10.636900),
                                                                               (3, 'Port El Kantaoui, Sousse', 35.892500, 10.593300),
                                                                               (4, 'Rue de la Kasbah, Tunis', 36.798900, 10.165500),
                                                                               (5, 'Avenue Farhat Hached, Sfax', 34.740600, 10.760300),
                                                                               (6, 'Rue Ali Belhouane, Bizerte', 37.274400, 9.873900),
                                                                               (7, 'Avenue Habib Thameur, Gabès', 33.888100, 10.097200),
                                                                               (8, 'Rue de la République, Monastir', 35.778000, 10.826200),
                                                                               (9, 'Avenue de l\'UMA, Nabeul', 36.456100, 10.737600),
                                                                               (10, 'Rue Hédi Chaker, Mahdia', 35.504700, 11.062200);

-- --------------------------------------------------------

--
-- Table structure for table `rating`
--

CREATE TABLE `rating` (
                          `id_rating` int(11) NOT NULL,
                          `id_user` int(11) NOT NULL,
                          `id_driver` int(11) NOT NULL,
                          `comment` varchar(255) NOT NULL,
                          `rating` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rating`
--

INSERT INTO `rating` (`id_rating`, `id_user`, `id_driver`, `comment`, `rating`) VALUES
                                                                                    (1, 1, 1, 'Great service!', 5),
                                                                                    (2, 4, 3, 'Comfortable ride.', 4);

-- --------------------------------------------------------

--
-- Table structure for table `reclamation`
--

CREATE TABLE `reclamation` (
                               `id_reclamation` int(11) NOT NULL,
                               `id_user` int(11) NOT NULL,
                               `content` varchar(255) NOT NULL,
                               `date` timestamp NOT NULL DEFAULT current_timestamp(),
                               `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reclamation`
--

INSERT INTO `reclamation` (`id_reclamation`, `id_user`, `content`, `date`, `status`) VALUES
                                                                                         (1, 1, 'Bicycle was not working properly.', '2025-02-12 11:00:00', 1),
                                                                                         (2, 4, 'Late arrival of the taxi.', '2025-02-12 11:30:00', 0);

-- --------------------------------------------------------

--
-- Table structure for table `relocation`
--

CREATE TABLE `relocation` (
                              `id_relocation` int(11) NOT NULL,
                              `id_reservation` int(11) NOT NULL,
                              `date` timestamp NOT NULL DEFAULT current_timestamp(),
                              `status` tinyint(1) NOT NULL,
                              `cost` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `relocation`
--

INSERT INTO `relocation` (`id_relocation`, `id_reservation`, `date`, `status`, `cost`) VALUES
                                                                                           (1, 1, '2025-02-12 12:00:00', 1, 10),
                                                                                           (2, 2, '2025-02-12 12:30:00', 0, 15);

-- --------------------------------------------------------

--
-- Table structure for table `request`
--

CREATE TABLE `request` (
                           `id_request` int(11) NOT NULL,
                           `id_client` int(11) DEFAULT NULL,
                           `id_taxi` int(11) DEFAULT NULL,
                           `id_departure_location` int(11) DEFAULT NULL,
                           `id_arrival_location` int(11) DEFAULT NULL,
                           `status` enum('PENDING','ACCEPTED','REJECTED','CANCELED') NOT NULL DEFAULT 'PENDING',
                           `request_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `request`
--

INSERT INTO `request` (`id_request`, `id_client`, `id_taxi`, `id_departure_location`, `id_arrival_location`, `status`, `request_date`) VALUES
                                                                                                                                           (1, 1, 1, 1, 2, 'ACCEPTED', '2025-02-12 13:00:00'),
                                                                                                                                           (2, 4, 3, 2, 3, 'PENDING', '2025-02-12 13:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
                               `id_reservation` int(11) NOT NULL,
                               `date` timestamp NOT NULL DEFAULT current_timestamp(),
                               `status` enum('CONFIRMED','CANCELLED','COMPLETED','ON_GOING') NOT NULL DEFAULT 'CONFIRMED',
                               `description` varchar(255) NOT NULL,
                               `id_start_location` int(11) NOT NULL,
                               `id_end_location` int(11) NOT NULL,
                               `id_announcement` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservation`
--

INSERT INTO `reservation` (`id_reservation`, `date`, `status`, `description`, `id_start_location`, `id_end_location`, `id_announcement`) VALUES
                                                                                                                                             (1, '2025-02-12 14:00:00', 'CONFIRMED', 'Reservation for transport service.', 1, 2, 1),
                                                                                                                                             (2, '2025-02-12 14:30:00', 'ON_GOING', 'Reservation for maintenance notice.', 2, 3, 2);

-- --------------------------------------------------------

--
-- Table structure for table `response`
--

CREATE TABLE `response` (
                            `id_response` int(11) NOT NULL,
                            `id_reclamation` int(11) NOT NULL,
                            `content` varchar(255) NOT NULL,
                            `date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `response`
--

INSERT INTO `response` (`id_response`, `id_reclamation`, `content`, `date`) VALUES
                                                                                (1, 1, 'We apologize for the inconvenience. The issue has been resolved.', '2025-02-12 15:00:00'),
                                                                                (2, 2, 'We are investigating the delay. Thank you for your patience.', '2025-02-12 15:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `ride`
--

CREATE TABLE `ride` (
                        `id_ride` int(11) NOT NULL,
                        `id_taxi` int(11) NOT NULL,
                        `id_client` int(11) NOT NULL,
                        `id_request` int(11) NOT NULL,
                        `distance` decimal(5,2) DEFAULT NULL,
                        `duration` int(11) DEFAULT NULL,
                        `price` decimal(10,2) DEFAULT NULL,
                        `status` enum('ONGOING','COMPLETED','CANCELED') NOT NULL DEFAULT 'ONGOING',
                        `ride_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ride`
--

INSERT INTO `ride` (`id_ride`, `id_taxi`, `id_client`, `id_request`, `distance`, `duration`, `price`, `status`, `ride_date`) VALUES
                                                                                                                                 (1, 1, 1, 1, 5.00, 30, 10.00, 'COMPLETED', '2025-02-12 16:00:00'),
                                                                                                                                 (2, 3, 4, 2, 7.50, 45, 15.00, 'ONGOING', '2025-02-12 16:30:00');

-- --------------------------------------------------------

--
-- Table structure for table `trip`
--

CREATE TABLE `trip` (
                        `id_trip` int(11) NOT NULL,
                        `departure_city` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
                        `arrival_city` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
                        `departure_date` timestamp NOT NULL DEFAULT current_timestamp(),
                        `available_seats` int(11) NOT NULL CHECK (`available_seats` >= 0),
                        `price_per_passenger` decimal(5,2) DEFAULT NULL,
                        `id_driver` int(11) NOT NULL,
                        `id_vehicle` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `trip`
--

INSERT INTO `trip` (`id_trip`, `departure_city`, `arrival_city`, `departure_date`, `available_seats`, `price_per_passenger`, `id_driver`, `id_vehicle`) VALUES
                                                                                                                                                            (1, 'Tunis', 'Sousse', '2025-02-12 17:00:00', 4, 20.00, 3, 1),
                                                                                                                                                            (2, 'Sousse', 'Sfax', '2025-02-12 17:30:00', 2, 25.00, 5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
                        `id_user` int(11) NOT NULL,
                        `name` varchar(100) NOT NULL,
                        `email` varchar(50) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `phone_number` varchar(15) NOT NULL,
                        `role` enum('CLIENT','ADMIN') NOT NULL,
                        `id_location` int(11) DEFAULT NULL,
                        `gender` enum('MALE','FEMALE') NOT NULL,
                        `profile_picture` varchar(255) DEFAULT NULL,
                        `is_verified` tinyint(1) NOT NULL DEFAULT 0,
                        `account_status` enum('ACTIVE','BANNED','DEACTIVATED') NOT NULL DEFAULT 'ACTIVE',
                        `date_of_birth` date DEFAULT NULL,
                        `status` enum('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `name`, `email`, `password`, `phone_number`, `role`, `id_location`, `gender`, `profile_picture`, `is_verified`, `account_status`, `date_of_birth`, `status`) VALUES
                                                                                                                                                                                                (1, 'Mohamed Ali', 'mohamed.ali@example.com', 'password123', '20123456', 'CLIENT', 1, 'MALE', 'https://example.com/profile_pics/male_user1.jpg', 1, 'ACTIVE', '1995-04-15', 'ONLINE'),
                                                                                                                                                                                                (2, 'Fatma Ben Ammar', 'fatma.benammar@example.com', 'password123', '22123456', 'CLIENT', 1, 'FEMALE', 'https://example.com/profile_pics/female_user1.jpg', 0, 'DEACTIVATED', '1990-12-22', 'OFFLINE'),
                                                                                                                                                                                                (3, 'Ahmed Trabelsi', 'ahmed.trabelsi@example.com', 'password123', '23123456', 'CLIENT', 1, 'MALE', NULL, 0, 'BANNED', '2000-02-03', 'OFFLINE'),
                                                                                                                                                                                                (4, 'Samira Bouazizi', 'samira.bouazizi@example.com', 'password123', '24123456', 'CLIENT', 2, 'FEMALE', 'https://example.com/profile_pics/female_user2.jpg', 1, 'ACTIVE', '1998-07-30', 'ONLINE'),
                                                                                                                                                                                                (5, 'Youssef Gharbi', 'youssef.gharbi@example.com', 'password123', '25123456', 'ADMIN', 2, 'MALE', 'https://example.com/profile_pics/male_user2.jpg', 1, 'ACTIVE', '1992-11-05', 'OFFLINE'),
                                                                                                                                                                                                (6, 'Leila Mansouri', 'leila.mansouri@example.com', 'password123', '26123456', 'CLIENT', 3, 'FEMALE', 'https://example.com/profile_pics/female_user3.jpg', 1, 'ACTIVE', '1991-05-15', 'OFFLINE'),
                                                                                                                                                                                                (7, 'Hichem Ben Salah', 'hichem.bensalah@example.com', 'password123', '27123456', 'CLIENT', 3, 'MALE', 'https://example.com/profile_pics/male_user3.jpg', 0, 'ACTIVE', '1989-08-22', 'ONLINE'),
                                                                                                                                                                                                (8, 'Amira Chaabane', 'amira.chaabane@example.com', 'password123', '28123456', 'CLIENT', 4, 'FEMALE', 'https://example.com/profile_pics/female_user4.jpg', 1, 'DEACTIVATED', '1996-11-11', 'OFFLINE'),
                                                                                                                                                                                                (9, 'Karim Boukadida', 'karim.boukadida@example.com', 'password123', '29123456', 'CLIENT', 4, 'MALE', 'https://example.com/profile_pics/male_user4.jpg', 0, 'ACTIVE', '1992-03-10', 'ONLINE'),
                                                                                                                                                                                                (10, 'Sana Jlassi', 'sana.jlassi@example.com', 'password123', '30123456', 'CLIENT', 5, 'FEMALE', 'https://example.com/profile_pics/female_user5.jpg', 1, 'ACTIVE', '1995-06-20', 'OFFLINE');

-- --------------------------------------------------------

--
-- Table structure for table `vehicle`
--

CREATE TABLE `vehicle` (
                           `id_vehicle` int(11) NOT NULL,
                           `id_driver` int(11) NOT NULL,
                           `registration` varchar(20) NOT NULL,
                           `color` varchar(50) NOT NULL,
                           `model` varchar(100) NOT NULL,
                           `brand` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicle`
--

INSERT INTO `vehicle` (`id_vehicle`, `id_driver`, `registration`, `color`, `model`, `brand`) VALUES
                                                                                                 (1, 3, 'TN1234AB', 'Red', 'Model X', 'Tesla'),
                                                                                                 (2, 5, 'TN5678CD', 'Blue', 'Model S', 'Tesla');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `announcement`
--
ALTER TABLE `announcement`
    ADD PRIMARY KEY (`id_announcement`),
    ADD KEY `announcement_ibfk_1` (`id_transporter`);

--
-- Indexes for table `bicycle`
--
ALTER TABLE `bicycle`
    ADD PRIMARY KEY (`id_bike`),
    ADD KEY `bicycle_ibfk_1` (`id_station`);

--
-- Indexes for table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    ADD PRIMARY KEY (`id_user_rental`),
    ADD KEY `bicycle_rental_ibfk_1` (`id_user`),
    ADD KEY `bicycle_rental_ibfk_2` (`id_bike`),
    ADD KEY `bicycle_rental_ibfk_3` (`id_start_station`),
    ADD KEY `bicycle_rental_ibfk_4` (`id_end_station`);

--
-- Indexes for table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    ADD PRIMARY KEY (`id_station`),
    ADD KEY `bicycle_station_ibfk_1` (`id_location`);

--
-- Indexes for table `booking`
--
ALTER TABLE `booking`
    ADD PRIMARY KEY (`id_booking`),
    ADD KEY `booking_ibfk_1` (`id_passenger`),
    ADD KEY `booking_ibfk_2` (`id_trip`);

--
-- Indexes for table `driver`
--
ALTER TABLE `driver`
    ADD PRIMARY KEY (`id_driver`),
    ADD UNIQUE KEY `permit_number` (`permit_number`),
    ADD KEY `driver_ibfk_1` (`id_user`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
    ADD PRIMARY KEY (`id_location`);

--
-- Indexes for table `rating`
--
ALTER TABLE `rating`
    ADD PRIMARY KEY (`id_rating`),
    ADD KEY `rating_ibfk_1` (`id_driver`),
    ADD KEY `rating_ibfk_2` (`id_user`);

--
-- Indexes for table `reclamation`
--
ALTER TABLE `reclamation`
    ADD PRIMARY KEY (`id_reclamation`),
    ADD KEY `reclamation_ibfk_1` (`id_user`);

--
-- Indexes for table `relocation`
--
ALTER TABLE `relocation`
    ADD PRIMARY KEY (`id_relocation`),
    ADD KEY `relocation_ibfk_1` (`id_reservation`);

--
-- Indexes for table `request`
--
ALTER TABLE `request`
    ADD PRIMARY KEY (`id_request`),
    ADD KEY `fk_request_client` (`id_client`),
    ADD KEY `fk_request_departure_location` (`id_departure_location`),
    ADD KEY `fk_request_arrival_location` (`id_arrival_location`),
    ADD KEY `fk_request_taxi` (`id_taxi`);

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
    ADD PRIMARY KEY (`id_reservation`),
    ADD KEY `reservation_ibfk_1` (`id_start_location`),
    ADD KEY `reservation_ibfk_2` (`id_end_location`),
    ADD KEY `reservation_ibfk_3` (`id_announcement`);

--
-- Indexes for table `response`
--
ALTER TABLE `response`
    ADD PRIMARY KEY (`id_response`),
    ADD KEY `response_ibfk_1` (`id_reclamation`);

--
-- Indexes for table `ride`
--
ALTER TABLE `ride`
    ADD PRIMARY KEY (`id_ride`),
    ADD KEY `ride_ibfk_1` (`id_taxi`),
    ADD KEY `ride_ibfk_2` (`id_client`),
    ADD KEY `ride_ibfk_3` (`id_request`);

--
-- Indexes for table `trip`
--
ALTER TABLE `trip`
    ADD PRIMARY KEY (`id_trip`),
    ADD KEY `trip_ibfk_1` (`id_vehicle`),
    ADD KEY `trip_ibfk_2` (`id_driver`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`id_user`),
    ADD UNIQUE KEY `email` (`email`),
    ADD UNIQUE KEY `phone_number` (`phone_number`),
    ADD KEY `user_ibfk_1` (`id_location`);

--
-- Indexes for table `vehicle`
--
ALTER TABLE `vehicle`
    ADD PRIMARY KEY (`id_vehicle`),
    ADD UNIQUE KEY `registration` (`registration`),
    ADD KEY `vehicle_ibfk_1` (`id_driver`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `announcement`
--
ALTER TABLE `announcement`
    MODIFY `id_announcement` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `bicycle`
--
ALTER TABLE `bicycle`
    MODIFY `id_bike` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    MODIFY `id_user_rental` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    MODIFY `id_station` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `booking`
--
ALTER TABLE `booking`
    MODIFY `id_booking` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `driver`
--
ALTER TABLE `driver`
    MODIFY `id_driver` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
    MODIFY `id_location` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `rating`
--
ALTER TABLE `rating`
    MODIFY `id_rating` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `reclamation`
--
ALTER TABLE `reclamation`
    MODIFY `id_reclamation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `relocation`
--
ALTER TABLE `relocation`
    MODIFY `id_relocation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `request`
--
ALTER TABLE `request`
    MODIFY `id_request` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
    MODIFY `id_reservation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `response`
--
ALTER TABLE `response`
    MODIFY `id_response` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `ride`
--
ALTER TABLE `ride`
    MODIFY `id_ride` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `trip`
--
ALTER TABLE `trip`
    MODIFY `id_trip` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
    MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=105;

--
-- AUTO_INCREMENT for table `vehicle`
--
ALTER TABLE `vehicle`
    MODIFY `id_vehicle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcement`
--
ALTER TABLE `announcement`
    ADD CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`id_transporter`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bicycle`
--
ALTER TABLE `bicycle`
    ADD CONSTRAINT `bicycle_ibfk_1` FOREIGN KEY (`id_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    ADD CONSTRAINT `bicycle_rental_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `bicycle_rental_ibfk_2` FOREIGN KEY (`id_bike`) REFERENCES `bicycle` (`id_bike`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `bicycle_rental_ibfk_3` FOREIGN KEY (`id_start_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `bicycle_rental_ibfk_4` FOREIGN KEY (`id_end_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    ADD CONSTRAINT `bicycle_station_ibfk_1` FOREIGN KEY (`id_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `booking`
--
ALTER TABLE `booking`
    ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`id_passenger`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`id_trip`) REFERENCES `trip` (`id_trip`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `driver`
--
ALTER TABLE `driver`
    ADD CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `rating`
--
ALTER TABLE `rating`
    ADD CONSTRAINT `rating_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `rating_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reclamation`
--
ALTER TABLE `reclamation`
    ADD CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `relocation`
--
ALTER TABLE `relocation`
    ADD CONSTRAINT `relocation_ibfk_1` FOREIGN KEY (`id_reservation`) REFERENCES `reservation` (`id_reservation`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `request`
--
ALTER TABLE `request`
    ADD CONSTRAINT `fk_request_arrival_location` FOREIGN KEY (`id_arrival_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_request_client` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_request_departure_location` FOREIGN KEY (`id_departure_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_request_taxi` FOREIGN KEY (`id_taxi`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reservation`
--
ALTER TABLE `reservation`
    ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`id_start_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`id_end_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `reservation_ibfk_3` FOREIGN KEY (`id_announcement`) REFERENCES `announcement` (`id_announcement`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `response`
--
ALTER TABLE `response`
    ADD CONSTRAINT `response_ibfk_1` FOREIGN KEY (`id_reclamation`) REFERENCES `reclamation` (`id_reclamation`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `ride`
--
ALTER TABLE `ride`
    ADD CONSTRAINT `ride_ibfk_1` FOREIGN KEY (`id_taxi`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `ride_ibfk_2` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `ride_ibfk_3` FOREIGN KEY (`id_request`) REFERENCES `request` (`id_request`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `trip`
--
ALTER TABLE `trip`
    ADD CONSTRAINT `trip_ibfk_1` FOREIGN KEY (`id_vehicle`) REFERENCES `vehicle` (`id_vehicle`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `trip_ibfk_2` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
    ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `vehicle`
--
ALTER TABLE `vehicle`
    ADD CONSTRAINT `vehicle_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
