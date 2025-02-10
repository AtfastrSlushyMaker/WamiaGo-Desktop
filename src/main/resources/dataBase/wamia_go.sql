-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 10 fév. 2025 à 02:50
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `wamia_go`
--

-- --------------------------------------------------------

--
-- Structure de la table `announcement`
--

CREATE TABLE `announcement` (
                                `id_announcement` int(11) NOT NULL,
                                `id_transporter` int(11) NOT NULL,
                                `title` varchar(50) NOT NULL,
                                `content` varchar(255) NOT NULL,
                                `date` date NOT NULL,
                                `zone` enum('Ariana','Béja','Ben Arous','Bizerte','Gabès','Gafsa','Jendouba','Kairouan','Kasserine','Kebili','Kef','Mahdia','Manouba','Medenine','Monastir','Nabeul','Sfax','Sidi Bouzid','Siliana','Sousse','Tataouine','Tozeur','Tunis','Zaghouan') NOT NULL,
                                `status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `announcement`
--

INSERT INTO `announcement` (`id_announcement`, `id_transporter`, `title`, `content`, `date`, `zone`, `status`) VALUES
    (2, 1, 'New Route', 'We have added a new route to the app!', '2025-02-10', 'Tunis', 1);

-- --------------------------------------------------------

--
-- Structure de la table `bicycle`
--

CREATE TABLE `bicycle` (
                           `id_bike` int(11) NOT NULL,
                           `id_station` int(11) NOT NULL,
                           `status` enum('available','in_use','charging','maintenance','reserved') NOT NULL,
                           `battery_level` float DEFAULT NULL,
                           `range_km` float DEFAULT NULL,
                           `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `bicycle_rental`
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
-- Structure de la table `bicycle_station`
--

CREATE TABLE `bicycle_station` (
                                   `id_station` int(11) NOT NULL,
                                   `name` varchar(50) NOT NULL,
                                   `id_location` int(11) NOT NULL,
                                   `total_docks` int(11) NOT NULL,
                                   `available_docks` int(11) NOT NULL,
                                   `available_bikes` int(11) NOT NULL,
                                   `charging_bikes` int(11) NOT NULL,
                                   `status` enum('active','inactive','maintenance','disabled') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `booking`
--

CREATE TABLE `booking` (
                           `id_booking` int(11) NOT NULL,
                           `id_trip` int(11) NOT NULL,
                           `id_passenger` int(11) NOT NULL,
                           `reserved_seats` int(11) NOT NULL CHECK (`reserved_seats` > 0),
                           `status` enum('Confirmed','Pending','Canceled') DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `driver`
--

CREATE TABLE `driver` (
                          `id_driver` int(11) NOT NULL,
                          `id_user` int(11) NOT NULL,
                          `permit_number` int(20) NOT NULL,
                          `role` enum('taxi_driver','transporter','carpool_driver','') NOT NULL,
                          `status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `driver`
--

INSERT INTO `driver` (`id_driver`, `id_user`, `permit_number`, `role`, `status`) VALUES
    (1, 2, 2, 'transporter', 0);

-- --------------------------------------------------------

--
-- Structure de la table `location`
--

CREATE TABLE `location` (
                            `id_location` int(11) NOT NULL,
                            `address` varchar(255) NOT NULL,
                            `latitude` float NOT NULL,
                            `longitude` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `location`
--

INSERT INTO `location` (`id_location`, `address`, `latitude`, `longitude`) VALUES
    (1, 'aa', 5.2, 52);

-- --------------------------------------------------------

--
-- Structure de la table `rating`
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
-- Structure de la table `reclamation`
--

CREATE TABLE `reclamation` (
                               `id_reclamtion` int(11) NOT NULL,
                               `id_user` int(11) NOT NULL,
                               `content` varchar(255) NOT NULL,
                               `date` datetime NOT NULL,
                               `status` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `relocation`
--

CREATE TABLE `relocation` (
                              `id_relocation` int(11) NOT NULL,
                              `id_reservation` int(11) NOT NULL,
                              `date` datetime NOT NULL,
                              `status` int(11) NOT NULL,
                              `cost` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `request`
--

CREATE TABLE `request` (
                           `id_request` int(11) NOT NULL,
                           `id_client` int(11) DEFAULT NULL,
                           `id_taxi` int(11) DEFAULT NULL,
                           `id_departure_location` int(11) DEFAULT NULL,
                           `id_arrival_location` int(11) DEFAULT NULL,
                           `status` enum('Pending','Accepted','Rejected','Canceled') DEFAULT 'Pending',
                           `request_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reservation`
--

CREATE TABLE `reservation` (
                               `id_reservation` int(11) NOT NULL,
                               `date` datetime NOT NULL,
                               `status` enum('confirmed','cancelled','completed','on_going') NOT NULL,
                               `description` varchar(255) NOT NULL,
                               `id_start_location` int(11) NOT NULL,
                               `id_end_location` int(11) NOT NULL,
                               `id_announcement` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `response`
--

CREATE TABLE `response` (
                            `id_response` int(11) NOT NULL,
                            `id_reclamation` int(11) NOT NULL,
                            `content` varchar(255) NOT NULL,
                            `date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ride`
--

CREATE TABLE `ride` (
                        `id_ride` int(11) NOT NULL,
                        `id_taxi` int(11) DEFAULT NULL,
                        `id_client` int(11) DEFAULT NULL,
                        `id_request` int(11) NOT NULL,
                        `distance` decimal(5,2) DEFAULT NULL,
                        `duration` int(11) DEFAULT NULL,
                        `price` decimal(10,2) DEFAULT NULL,
                        `status` enum('Ongoing','Completed','Canceled') DEFAULT NULL,
                        `ride_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `trip`
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

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
                        `id_user` int(11) NOT NULL,
                        `name` varchar(100) NOT NULL,
                        `email` varchar(50) NOT NULL,
                        `password` varchar(50) NOT NULL,
                        `phone_number` varchar(8) NOT NULL,
                        `role` enum('client','admin','','') NOT NULL,
                        `id_location` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id_user`, `name`, `email`, `password`, `phone_number`, `role`, `id_location`) VALUES
    (2, 'ezer', 'Ezer@gmail.com', '0000', '99478730', 'client', 1);

-- --------------------------------------------------------

--
-- Structure de la table `vehicle`
--

CREATE TABLE `vehicle` (
                           `id_vehicle` int(11) NOT NULL,
                           `id_driver` int(11) NOT NULL,
                           `registration` int(20) NOT NULL,
                           `color` varchar(100) NOT NULL,
                           `model` varchar(100) NOT NULL,
                           `brand` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `announcement`
--
ALTER TABLE `announcement`
    ADD PRIMARY KEY (`id_announcement`),
  ADD KEY `id_transporter` (`id_transporter`);

--
-- Index pour la table `bicycle`
--
ALTER TABLE `bicycle`
    ADD PRIMARY KEY (`id_bike`),
  ADD KEY `idStation` (`id_station`);

--
-- Index pour la table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    ADD PRIMARY KEY (`id_user_rental`),
  ADD KEY `idUser` (`id_user`),
  ADD KEY `idBike` (`id_bike`),
  ADD KEY `idStart_station` (`id_start_station`),
  ADD KEY `idEnd_station` (`id_end_station`);

--
-- Index pour la table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    ADD PRIMARY KEY (`id_station`),
  ADD KEY `idLocation` (`id_location`);

--
-- Index pour la table `booking`
--
ALTER TABLE `booking`
    ADD PRIMARY KEY (`id_booking`),
  ADD KEY `id_passenger` (`id_passenger`),
  ADD KEY `id_trip` (`id_trip`);

--
-- Index pour la table `driver`
--
ALTER TABLE `driver`
    ADD PRIMARY KEY (`id_driver`),
  ADD UNIQUE KEY `permit_number` (`permit_number`),
  ADD KEY `idUser` (`id_user`);

--
-- Index pour la table `location`
--
ALTER TABLE `location`
    ADD PRIMARY KEY (`id_location`);

--
-- Index pour la table `rating`
--
ALTER TABLE `rating`
    ADD PRIMARY KEY (`id_rating`),
  ADD KEY `idDriver` (`id_driver`),
  ADD KEY `idUser` (`id_user`);

--
-- Index pour la table `reclamation`
--
ALTER TABLE `reclamation`
    ADD PRIMARY KEY (`id_reclamtion`),
  ADD KEY `idUser` (`id_user`);

--
-- Index pour la table `relocation`
--
ALTER TABLE `relocation`
    ADD PRIMARY KEY (`id_relocation`),
  ADD KEY `id_reservation` (`id_reservation`);

--
-- Index pour la table `request`
--
ALTER TABLE `request`
    ADD PRIMARY KEY (`id_request`),
  ADD KEY `id_client` (`id_client`),
  ADD KEY `idArrival_location` (`id_arrival_location`),
  ADD KEY `idDeparture_location` (`id_departure_location`),
  ADD KEY `id_taxi` (`id_taxi`);

--
-- Index pour la table `reservation`
--
ALTER TABLE `reservation`
    ADD PRIMARY KEY (`id_reservation`),
  ADD KEY `id_start_location` (`id_start_location`),
  ADD KEY `id_end_location` (`id_end_location`),
  ADD KEY `id_announcement` (`id_announcement`);

--
-- Index pour la table `response`
--
ALTER TABLE `response`
    ADD PRIMARY KEY (`id_response`),
  ADD KEY `idReclamation` (`id_reclamation`);

--
-- Index pour la table `ride`
--
ALTER TABLE `ride`
    ADD PRIMARY KEY (`id_ride`),
  ADD KEY `id_taxi` (`id_taxi`),
  ADD KEY `id_client` (`id_client`),
  ADD KEY `id_request` (`id_request`);

--
-- Index pour la table `trip`
--
ALTER TABLE `trip`
    ADD PRIMARY KEY (`id_trip`),
  ADD KEY `id_vehicle` (`id_vehicle`),
  ADD KEY `id_driver` (`id_driver`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idLocation` (`id_location`);

--
-- Index pour la table `vehicle`
--
ALTER TABLE `vehicle`
    ADD PRIMARY KEY (`id_vehicle`),
  ADD UNIQUE KEY `registration` (`registration`),
  ADD KEY `vehicle_ibfk_1` (`id_driver`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `announcement`
--
ALTER TABLE `announcement`
    MODIFY `id_announcement` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `bicycle`
--
ALTER TABLE `bicycle`
    MODIFY `id_bike` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    MODIFY `id_user_rental` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    MODIFY `id_station` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `booking`
--
ALTER TABLE `booking`
    MODIFY `id_booking` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `driver`
--
ALTER TABLE `driver`
    MODIFY `id_driver` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `location`
--
ALTER TABLE `location`
    MODIFY `id_location` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `rating`
--
ALTER TABLE `rating`
    MODIFY `id_rating` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reclamation`
--
ALTER TABLE `reclamation`
    MODIFY `id_reclamtion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `relocation`
--
ALTER TABLE `relocation`
    MODIFY `id_relocation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `request`
--
ALTER TABLE `request`
    MODIFY `id_request` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservation`
--
ALTER TABLE `reservation`
    MODIFY `id_reservation` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `response`
--
ALTER TABLE `response`
    MODIFY `id_response` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `ride`
--
ALTER TABLE `ride`
    MODIFY `id_ride` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `trip`
--
ALTER TABLE `trip`
    MODIFY `id_trip` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
    MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `vehicle`
--
ALTER TABLE `vehicle`
    MODIFY `id_vehicle` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `announcement`
--
ALTER TABLE `announcement`
    ADD CONSTRAINT `announcement_ibfk_1` FOREIGN KEY (`id_transporter`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `bicycle`
--
ALTER TABLE `bicycle`
    ADD CONSTRAINT `bicycle_ibfk_1` FOREIGN KEY (`id_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `bicycle_rental`
--
ALTER TABLE `bicycle_rental`
    ADD CONSTRAINT `bicycle_rental_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bicycle_rental_ibfk_2` FOREIGN KEY (`id_bike`) REFERENCES `bicycle` (`id_bike`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bicycle_rental_ibfk_3` FOREIGN KEY (`id_start_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bicycle_rental_ibfk_4` FOREIGN KEY (`id_end_station`) REFERENCES `bicycle_station` (`id_station`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `bicycle_station`
--
ALTER TABLE `bicycle_station`
    ADD CONSTRAINT `bicycle_station_ibfk_1` FOREIGN KEY (`id_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `booking`
--
ALTER TABLE `booking`
    ADD CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`id_passenger`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`id_trip`) REFERENCES `trip` (`id_trip`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `driver`
--
ALTER TABLE `driver`
    ADD CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `rating`
--
ALTER TABLE `rating`
    ADD CONSTRAINT `rating_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rating_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `reclamation`
--
ALTER TABLE `reclamation`
    ADD CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `relocation`
--
ALTER TABLE `relocation`
    ADD CONSTRAINT `relocation_ibfk_1` FOREIGN KEY (`id_reservation`) REFERENCES `reservation` (`id_reservation`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `request`
--
ALTER TABLE `request`
    ADD CONSTRAINT `request_ibfk_1` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `request_ibfk_2` FOREIGN KEY (`id_arrival_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `request_ibfk_3` FOREIGN KEY (`id_departure_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `request_ibfk_4` FOREIGN KEY (`id_taxi`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `reservation`
--
ALTER TABLE `reservation`
    ADD CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`id_start_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`id_end_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `reservation_ibfk_3` FOREIGN KEY (`id_announcement`) REFERENCES `announcement` (`id_announcement`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `response`
--
ALTER TABLE `response`
    ADD CONSTRAINT `response_ibfk_1` FOREIGN KEY (`id_reclamation`) REFERENCES `reclamation` (`id_reclamtion`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `ride`
--
ALTER TABLE `ride`
    ADD CONSTRAINT `ride_ibfk_1` FOREIGN KEY (`id_taxi`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ride_ibfk_2` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ride_ibfk_3` FOREIGN KEY (`id_request`) REFERENCES `request` (`id_request`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `trip`
--
ALTER TABLE `trip`
    ADD CONSTRAINT `trip_ibfk_1` FOREIGN KEY (`id_vehicle`) REFERENCES `vehicle` (`id_vehicle`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `trip_ibfk_2` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `user`
--
ALTER TABLE `user`
    ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_location`) REFERENCES `location` (`id_location`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `vehicle`
--
ALTER TABLE `vehicle`
    ADD CONSTRAINT `vehicle_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
