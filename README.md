<a name="top"></a>
<!-- PROJECT LOGO -->
<div align="center">
  <a href="https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop">
    <img src="https://cdn.discordapp.com/attachments/1333456825223348225/1336831812055466158/wamiaGO.png?ex=67af2065&is=67adcee5&hm=61a94a2346f17a356fcfcd64e23da5341fba8081bcb5938b2358463c06844ef5&" alt="Logo" width="500">
  </a>
  <h3 align="center">WamiaGo Desktop</h3>
  <p align="center">
    A JavaFX-based application for smart transport integration.
  </p>
</div>


<!-- TABLE OF CONTENTS -->
## Table of Contents
- [About The Project](#about-the-project)
- [Features](#features)
- [Built With](#built-with)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [License](#license)
- [Contact](#contact)

---

<!-- ABOUT THE PROJECT -->
## About The Project
WamiaGo Desktop is a comprehensive JavaFX-based desktop application that serves as a unified platform for multi-modal transportation services in Tunisia. The application integrates electric bicycle rentals, taxi services, carpooling, relocation transport, and station management into a single, user-friendly interface. Built with modern Java technologies and featuring real-time data synchronization, weather integration, and advanced mapping capabilities, WamiaGo aims to revolutionize urban mobility management.

The application supports multiple user roles including clients, drivers, transporters, and administrators, each with tailored interfaces and functionalities. It features sophisticated booking systems, real-time tracking, payment processing, and comprehensive reporting tools.

---

## 🚀 Features

### Transportation Services
- **🚴 Electric Bicycle Rental System**
  - Real-time bike availability tracking
  - Battery level monitoring and range calculation
  - Station-to-station rental management
  - Automated billing and cost calculation
  - QR code integration for quick bike access

- **🚖 Taxi Services**
  - Driver registration and management
  - Real-time ride requests and matching
  - Distance and duration calculation using TomTom API
  - Dynamic pricing based on traffic conditions
  - Driver ratings and reviews system

- **🚗 Carpooling & Ride Sharing**
  - Multi-passenger ride coordination
  - Route optimization
  - Cost sharing calculations
  - Driver and passenger verification

- **📦 Relocation Transport**
  - Goods and furniture transportation
  - Vehicle capacity management
  - Booking and scheduling system
  - Route planning and optimization

### Core Features
- **👤 Multi-Role User Management**
  - Client, Driver, Transporter, and Admin roles
  - Profile management with photo upload
  - Account verification and status tracking
  - Gender-specific services and preferences

- **📍 Advanced Location Services**
  - Real-time GPS tracking
  - Interactive mapping with HTML5 integration
  - Station location management
  - Route calculation and optimization

- **🌤️ Weather Integration**
  - Real-time weather data for better trip planning
  - Wind speed and condition monitoring
  - Weather-based service recommendations

- **💳 Payment & Billing**
  - Automated cost calculation
  - Multiple payment options
  - Detailed billing and invoicing
  - PDF receipt generation

- **📊 Analytics & Reporting**
  - Usage statistics and trends
  - Revenue tracking
  - Performance metrics
  - Export capabilities

- **🔔 Communication System**
  - Announcements and notifications
  - Zone-based messaging (24 Tunisian governorates)
  - User feedback and reclamation system
  - Rating and review system

- **🎨 Modern UI/UX**
  - Responsive JavaFX interface
  - Smooth animations with AnimateFX
  - Custom fonts and iconography
  - Dark/light theme support

---

<!-- BUILT WITH -->
## 🛠️ Technology Stack

### Core Technologies
<div style="display: flex; flex-wrap: wrap; gap: 10px; align-items: center;">

<a href="https://www.oracle.com/java/" target="_blank">
  <img src="https://img.icons8.com/?size=100&id=GPfHz0SM85FX&format=png&color=000000" width="100" height="100">
</a>

<a href="https://openjfx.io/" target="_blank">
  <img src="https://upload.wikimedia.org/wikipedia/fr/c/cc/JavaFX_Logo.png?20190411191901" width="100" height="50">
</a>

<a href="https://maven.apache.org/" target="_blank">
  <img src="https://upload.wikimedia.org/wikipedia/commons/5/52/Apache_Maven_logo.svg" width="100" height="50">
</a>

<a href="https://www.mysql.com/" target="_blank">
  <img src="https://img.icons8.com/color/48/000000/mysql-logo.png" width="70" height="70">
</a>

</div>

### 📋 Complete Technology Stack

**Frontend & UI Framework:**
- **Java 17** - Modern LTS version with enhanced performance
- **JavaFX 17** - Rich desktop application framework
  - JavaFX Controls - UI components
  - JavaFX FXML - Declarative UI markup
  - JavaFX Web - HTML5 integration for maps
  - JavaFX Media - Multimedia support
- **AnimateFX 1.2.1** - Smooth UI animations and transitions
- **Ikonli 12.3.1** - FontAwesome icon integration

**Backend & Database:**
- **MySQL 8.0.33** - Primary database with JDBC connectivity
- **Maven** - Project build and dependency management
- **Password4j 1.8.0** - Secure password hashing and encryption

**External APIs & Services:**
- **TomTom Routing API** - Real-time traffic data and route calculation
- **Weather API Integration** - Real-time weather information
- **OkHttp 4.9.0** - HTTP client for API communications
- **Gson 2.8.9** - JSON parsing and serialization

**Document & Code Generation:**
- **iText PDF 5.5.13** - PDF document generation for receipts and reports
- **ZXing 3.5.1** - QR code generation and scanning
- **JSON 20240303** - JSON data processing

**Development Tools:**
- **JDBC** - Database connectivity layer
- **Singleton Pattern** - Database connection management
- **MVC Architecture** - Separation of concerns
- **Service Layer Pattern** - Business logic abstraction

### 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     WamiaGo Desktop                        │
├─────────────────────────────────────────────────────────────┤
│                  Presentation Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │  User UI    │ │ Driver UI   │ │  Admin UI   │          │
│  │ Controllers │ │ Controllers │ │ Controllers │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                   Business Layer                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   Service   │ │   Service   │ │   Service   │          │
│  │   Classes   │ │   Classes   │ │   Classes   │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                     Data Layer                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   Entity    │ │   Entity    │ │   Entity    │          │
│  │   Classes   │ │   Classes   │ │   Classes   │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                 Database & External APIs                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   MySQL     │ │  TomTom API │ │ Weather API │          │
│  │  Database   │ │             │ │             │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

---

<!-- GETTING STARTED -->
## Getting Started
Follow these steps to set up and run the WamiaGo Desktop application on your local machine.

### Prerequisites
Ensure you have the following installed:
- [Java Development Kit (JDK 17+)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [JavaFX SDK](https://gluonhq.com/products/javafx/)
- [Git](https://git-scm.com/downloads)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL](https://dev.mysql.com/downloads/)

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop.git
   cd WamiaGo-Desktop
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Set up the database:**
   - Start MySQL server
   - Create the database:
   ```sql
   CREATE DATABASE wamia_go;
   ```
   - Import the provided schema:
   ```bash
   mysql -u root -p wamia_go < src/main/resources/dataBase/wamia_go.sql
   ```
   - Update database connection settings in `src/main/java/utils/DataBase.java` if needed

4. **Configure API keys:**
   - Update TomTom API key in `TrafficService.java`
   - Configure weather API credentials if using weather features

5. **Build and run the application:**
   ```bash
   mvn javafx:run
   ```

   Or alternatively:
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

### 🗄️ Database Schema

The application uses a comprehensive MySQL database with the following main tables:

**Core Tables:**
- `user` - User accounts and profiles
- `location` - Geographic coordinates and addresses
- `driver` - Driver information and permits
- `vehicle` - Vehicle registration and details

**Bicycle System:**
- `bicycle_station` - Bike rental stations
- `bicycle` - Individual bicycles and their status
- `bicycle_rental` - Rental transactions and history

**Transportation Services:**
- `request` - Ride requests from users
- `ride` - Completed rides and trip details
- `booking` - Advanced reservations
- `trip` - Planned journeys and routes

**Management & Communication:**
- `announcement` - System announcements by zone
- `rating` - Driver and service ratings
- `reclamation` - User complaints and feedback
- `relocation` - Goods transportation requests
- `reservation` - Service reservations
- `response` - System responses to requests
<!-- USAGE -->
## 📱 Usage

### For End Users (Clients)
1. **Registration & Login:**
   - Launch the application
   - Create a new account or log in with existing credentials
   - Complete profile setup with location and preferences

2. **Electric Bicycle Rental:**
   - Browse available bike stations on the map
   - Check bike availability and battery levels
   - Reserve a bicycle using QR code scanning
   - Complete rental and return to any station

3. **Taxi & Ride Services:**
   - Request immediate or scheduled rides
   - Set pickup and destination locations
   - View estimated cost and arrival time
   - Track driver location in real-time
   - Rate and review your experience

4. **Carpooling:**
   - Find shared rides to your destination
   - Join existing trips or create new ones
   - Split costs automatically
   - Connect with verified drivers and passengers

### For Drivers
1. **Driver Registration:**
   - Register as taxi driver, transporter, or carpool driver
   - Upload permit and vehicle documents
   - Complete verification process

2. **Service Management:**
   - Set availability status and working hours
   - Accept or decline ride requests
   - Navigate using integrated routing
   - Process payments and generate receipts

### For Administrators
1. **System Management:**
   - Monitor all transportation services
   - Manage user accounts and driver verification
   - Oversee station operations and bicycle maintenance
   - Generate reports and analytics

2. **Communication:**
   - Send announcements by governorate zones
   - Handle user complaints and feedback
   - Manage service quality and ratings

### 🎯 Key Features in Action

**Real-time Tracking:** All vehicles and bicycles are tracked in real-time for optimal service delivery and security.

**Weather Integration:** Check current weather conditions to make informed transportation decisions.

**Multi-language Support:** Interface supports multiple languages for better accessibility.

**PDF Documentation:** Automatic generation of receipts, invoices, and trip summaries.

**QR Code Integration:** Quick access to bicycles and services through QR code scanning.

---

## 🏢 Project Structure

```
WamiaGo_desktop/
├── src/main/
│   ├── java/
│   │   ├── controllers/           # UI Controllers
│   │   │   ├── dashboard/         # Main dashboard
│   │   │   ├── user/             # User management
│   │   │   ├── bicycle/          # Bike rental system
│   │   │   ├── station/          # Station management
│   │   │   ├── taxi/             # Taxi services
│   │   │   ├── rides/            # Ride management
│   │   │   └── rentals/          # Rental operations
│   │   ├── entities/             # Data Models
│   │   │   ├── User.java         # User entity
│   │   │   ├── Driver.java       # Driver entity
│   │   │   ├── Bicycle.java      # Bicycle entity
│   │   │   ├── Station.java      # Station entity
│   │   │   ├── Ride.java         # Ride entity
│   │   │   └── ...               # Other entities
│   │   ├── services/             # Business Logic
│   │   │   ├── UserService.java  # User operations
│   │   │   ├── BicycleService.java # Bike operations
│   │   │   ├── TrafficService.java # Traffic API
│   │   │   └── ...               # Other services
│   │   └── utils/                # Utilities
│   │       └── DataBase.java     # DB connection
│   └── resources/
│       ├── dataBase/             # SQL schemas
│       ├── images/               # UI assets
│       ├── maps/                 # Map resources
│       ├── Fonts/                # Custom fonts
│       └── *.fxml               # UI layouts
├── target/                       # Compiled classes
├── pom.xml                      # Maven configuration
└── README.md                    # Documentation
```

---

## 🔧 Configuration

### Database Configuration
Update database connection settings in `src/main/java/utils/DataBase.java`:
```java
private final String URL="jdbc:mysql://127.0.0.1:3306/wamia_go";
private final String LOGIN="your_username";
private final String PWD="your_password";
```

### API Configuration
Configure external APIs in respective service classes:
- **TomTom API:** Update API key in `TrafficService.java`
- **Weather API:** Configure credentials in weather service implementation

### Application Settings
- **Default Language:** Configure in application properties
- **UI Theme:** Modify CSS files in resources folder
- **Database Pool:** Adjust connection settings for production use

---

## 🚀 Deployment

### Development Environment
```bash
# Start development server
mvn javafx:run

# Build without running
mvn clean compile

# Run tests
mvn test
```

### Production Build
```bash
# Create executable JAR
mvn clean package

# Run the JAR file
java -jar target/wamiaGo-1.0-SNAPSHOT.jar
```

### System Requirements
- **Java:** JDK 17 or higher
- **Memory:** Minimum 4GB RAM recommended
- **Storage:** 500MB free space
- **OS:** Windows 10+, macOS 10.14+, Linux Ubuntu 18.04+
- **Database:** MySQL 8.0+ or MariaDB 10.4+

---

## 🤝 Contributing

We welcome contributions to improve WamiaGo Desktop! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions
- Add appropriate comments and documentation
- Test your changes thoroughly
- Update README if needed

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
<!-- CONTACT -->
## 📞 Contact

**WamiaGo Team**
- **Email:** wamiago@gmail.com
- **Project Repository:** [WamiaGo-Desktop](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop)

For technical support, feature requests, or bug reports, please create an issue on our GitHub repository or contact us directly.

---

## 🙏 Acknowledgments

- **TomTom** - For providing routing and traffic APIs
- **OpenJFX Community** - For the excellent JavaFX framework
- **FontAwesome** - For beautiful icons and UI elements
- **iText** - For PDF generation capabilities
- **ZXing** - For QR code functionality
- **All Contributors** - Thank you for making this project better!

---

## 📊 Project Status

**Current Version:** 1.0-SNAPSHOT  
**Development Status:** Active Development  
**Last Updated:** February 2025  

### Recent Updates
- ✅ Complete database schema implementation
- ✅ Multi-role user management system
- ✅ Electric bicycle rental system
- ✅ Real-time traffic integration
- ✅ Weather service integration
- ✅ PDF generation for receipts
- ✅ QR code scanning functionality

### Upcoming Features
- 🔄 Mobile app synchronization
- 🔄 Advanced analytics dashboard
- 🔄 Multi-language support expansion
- 🔄 Payment gateway integration
- 🔄 Push notification system

<p align="right">(<a href="#top">back to top</a>)</p>
