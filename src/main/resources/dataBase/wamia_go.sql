-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 12, 2025 at 12:18 AM
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
  `date` int(11) NOT NULL,
  `zone` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
  `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bicycle`
--

CREATE TABLE `bicycle` (
  `id_bike` int(11) NOT NULL,
  `id_station` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `battery_level` float DEFAULT NULL,
  `range_km` float DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bicycle`
--

INSERT INTO `bicycle` (`id_bike`, `id_station`, `status`, `battery_level`, `range_km`, `last_updated`) VALUES
(1, 1, 1, 100, 50, '2025-02-12 00:10:24'),
(2, 1, 1, 80, 40, '2025-02-12 00:10:24'),
(3, 2, 1, 90, 45, '2025-02-12 00:10:24'),
(4, 2, 1, 70, 35, '2025-02-12 00:10:24'),
(5, 3, 1, 60, 30, '2025-02-12 00:10:24'),
(6, 4, 1, 100, 50, '2025-02-12 00:10:24'),
(7, 5, 1, 50, 25, '2025-02-12 00:10:24'),
(8, 6, 1, 40, 20, '2025-02-12 00:10:24'),
(9, 7, 1, 30, 15, '2025-02-12 00:10:24'),
(10, 8, 1, 20, 10, '2025-02-12 00:10:24');

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
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `distance_km` float NOT NULL,
  `battery_used` float NOT NULL,
  `cost` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  `status` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bicycle_station`
--

INSERT INTO `bicycle_station` (`id_station`, `name`, `id_location`, `total_docks`, `available_docks`, `available_bikes`, `charging_bikes`, `status`) VALUES
(1, 'Station Tunis Centre', 1, 20, 15, 5, 0, 1),
(2, 'Station Sousse Medina', 2, 15, 10, 5, 0, 1),
(3, 'Station Port El Kantaoui', 3, 10, 8, 2, 0, 1),
(4, 'Station Sfax City', 5, 25, 20, 5, 0, 1),
(5, 'Station Bizerte Port', 6, 12, 10, 2, 0, 1),
(6, 'Station Gabès Oasis', 7, 8, 6, 2, 0, 1),
(7, 'Station Monastir Marina', 8, 10, 7, 3, 0, 1),
(8, 'Station Nabeul Beach', 9, 15, 12, 3, 0, 1),
(9, 'Station Mahdia Old Town', 10, 10, 8, 2, 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `booking`
--

CREATE TABLE `booking` (
  `id_booking` int(11) NOT NULL,
  `id_trip` int(11) NOT NULL,
  `id_passenger` int(11) NOT NULL,
  `reserved_seats` int(11) NOT NULL CHECK (`reserved_seats` > 0),
  `status` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `booking`
--

INSERT INTO `booking` (`id_booking`, `id_trip`, `id_passenger`, `reserved_seats`, `status`) VALUES
(1, 1, 2, 2, 1),
(2, 2, 3, 1, 1),
(3, 3, 4, 2, 1),
(4, 4, 5, 3, 1),
(5, 5, 6, 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `driver`
--

CREATE TABLE `driver` (
  `id_driver` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `permit_number` varchar(20) NOT NULL,
  `role` enum('taxi_driver','transporter','carpool_driver') NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `driver`
--

INSERT INTO `driver` (`id_driver`, `id_user`, `permit_number`, `role`, `status`) VALUES
(1, 1, 'TN123456789', 'taxi_driver', 1),
(2, 2, 'TN987654321', 'transporter', 1),
(3, 3, 'TN456789123', 'carpool_driver', 1),
(4, 4, 'TN321654987', 'taxi_driver', 1),
(5, 5, 'TN654321987', 'transporter', 1),
(6, 6, 'TN789123456', 'carpool_driver', 1),
(7, 7, 'TN321987654', 'taxi_driver', 1),
(8, 8, 'TN654987321', 'transporter', 1),
(9, 9, 'TN987321654', 'carpool_driver', 1),
(10, 10, 'TN123987456', 'taxi_driver', 1);

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

-- --------------------------------------------------------

--
-- Table structure for table `reclamation`
--

CREATE TABLE `reclamation` (
  `id_reclamation` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `content` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `relocation`
--

CREATE TABLE `relocation` (
  `id_relocation` int(11) NOT NULL,
  `id_reservation` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `status` tinyint(1) NOT NULL,
  `cost` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  `status` tinyint(1) NOT NULL DEFAULT 0,
  `request_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `request`
--

INSERT INTO `request` (`id_request`, `id_client`, `id_taxi`, `id_departure_location`, `id_arrival_location`, `status`, `request_date`) VALUES
(1, 1, 1, 1, 2, 1, '2025-02-12 00:10:24'),
(2, 2, 2, 2, 3, 1, '2025-02-12 00:10:24'),
(3, 3, 3, 3, 4, 1, '2025-02-12 00:10:24'),
(4, 4, 4, 4, 5, 1, '2025-02-12 00:10:24'),
(5, 5, 5, 5, 6, 1, '2025-02-12 00:10:24');

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
  `id_reservation` int(11) NOT NULL,
  `date` datetime NOT NULL,
  `status` tinyint(1) NOT NULL,
  `description` varchar(255) NOT NULL,
  `id_start_location` int(11) NOT NULL,
  `id_end_location` int(11) NOT NULL,
  `id_announcement` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `response`
--

CREATE TABLE `response` (
  `id_response` int(11) NOT NULL,
  `id_reclamation` int(11) NOT NULL,
  `content` varchar(255) NOT NULL,
  `date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  `status` tinyint(1) NOT NULL DEFAULT 0,
  `ride_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ride`
--

INSERT INTO `ride` (`id_ride`, `id_taxi`, `id_client`, `id_request`, `distance`, `duration`, `price`, `status`, `ride_date`) VALUES
(1, 1, 1, 1, 15.50, 30, 25.00, 1, '2025-02-12 00:10:24'),
(2, 2, 2, 2, 10.00, 20, 15.00, 1, '2025-02-12 00:10:24'),
(3, 3, 3, 3, 8.50, 15, 12.50, 1, '2025-02-12 00:10:24'),
(4, 4, 4, 4, 12.00, 25, 20.00, 1, '2025-02-12 00:10:24'),
(5, 5, 5, 5, 5.00, 10, 8.00, 1, '2025-02-12 00:10:24');

-- --------------------------------------------------------

--
-- Table structure for table `trip`
--

CREATE TABLE `trip` (
  `id_trip` int(11) NOT NULL,
  `departure_city` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
  `arrival_city` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
  `departure_date` datetime NOT NULL,
  `available_seats` int(11) NOT NULL CHECK (`available_seats` >= 0),
  `price_per_passenger` decimal(5,2) DEFAULT NULL,
  `id_driver` int(11) NOT NULL,
  `id_vehicle` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `trip`
--

INSERT INTO `trip` (`id_trip`, `departure_city`, `arrival_city`, `departure_date`, `available_seats`, `price_per_passenger`, `id_driver`, `id_vehicle`) VALUES
(1, 'Tunis', 'Sousse', '2023-10-15 08:00:00', 4, 20.00, 1, 1),
(2, 'Sousse', 'Tunis', '2023-10-15 18:00:00', 3, 20.00, 2, 2),
(3, 'Sfax', 'Gabès', '2023-10-16 09:00:00', 2, 15.00, 3, 3),
(4, 'Bizerte', 'Tunis', '2023-10-17 07:00:00', 5, 25.00, 4, 4),
(5, 'Monastir', 'Mahdia', '2023-10-18 10:00:00', 4, 10.00, 5, 5);

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
  `role` enum('client','admin') NOT NULL,
  `id_location` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `name`, `email`, `password`, `phone_number`, `role`, `id_location`) VALUES
(1, 'Mohamed Ali', 'mohamed.ali@example.com', 'password123', '20123456', 'client', 1),
(2, 'Fatma Ben Ammar', 'fatma.benammar@example.com', 'password123', '22123456', 'client', 2),
(3, 'Ahmed Trabelsi', 'ahmed.trabelsi@example.com', 'password123', '23123456', 'client', 3),
(4, 'Samira Bouazizi', 'samira.bouazizi@example.com', 'password123', '24123456', 'client', 4),
(5, 'Youssef Gharbi', 'youssef.gharbi@example.com', 'password123', '25123456', 'admin', 5),
(6, 'Leila Mansouri', 'leila.mansouri@example.com', 'password123', '26123456', 'client', 6),
(7, 'Hichem Ben Salah', 'hichem.bensalah@example.com', 'password123', '27123456', 'client', 7),
(8, 'Amira Chaabane', 'amira.chaabane@example.com', 'password123', '28123456', 'client', 8),
(9, 'Karim Boukadida', 'karim.boukadida@example.com', 'password123', '29123456', 'client', 9),
(10, 'Sana Jlassi', 'sana.jlassi@example.com', 'password123', '30123456', 'client', 10);

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
(1, 1, 'TN-1234-A', 'Red', 'Corolla', 'Toyota'),
(2, 2, 'TN-5678-B', 'Blue', 'Clio', 'Renault'),
(3, 3, 'TN-9101-C', 'White', 'Golf', 'Volkswagen'),
(4, 4, 'TN-1121-D', 'Black', 'Focus', 'Ford'),
(5, 5, 'TN-3141-E', 'Silver', '208', 'Peugeot'),
(6, 6, 'TN-5161-F', 'Green', 'Polo', 'Volkswagen'),
(7, 7, 'TN-7181-G', 'Yellow', 'Yaris', 'Toyota'),
(8, 8, 'TN-9202-H', 'Gray', 'Megane', 'Renault'),
(9, 9, 'TN-1222-I', 'Orange', 'C3', 'Citroen'),
(10, 10, 'TN-3242-J', 'Purple', 'i10', 'Hyundai');

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
  MODIFY `id_announcement` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bicycle`
--
ALTER TABLE `bicycle`
  MODIFY `id_bike` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
  MODIFY `id_user_rental` int(11) NOT NULL AUTO_INCREMENT;

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
  MODIFY `id_driver` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `id_location` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `rating`
--
ALTER TABLE `rating`
  MODIFY `id_rating` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reclamation`
--
ALTER TABLE `reclamation`
  MODIFY `id_reclamation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `relocation`
--
ALTER TABLE `relocation`
  MODIFY `id_relocation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `request`
--
ALTER TABLE `request`
  MODIFY `id_request` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `id_reservation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `response`
--
ALTER TABLE `response`
  MODIFY `id_response` int(11) NOT NULL AUTO_INCREMENT;

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
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

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
