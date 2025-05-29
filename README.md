# WamiaGo Desktop

---

<div align="center">
  <a href="https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop">
    <img src="https://i.imgur.com/759dC4H.png" alt="WamiaGo Logo" width="500">
  </a>
</div>

---

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

## About The Project

WamiaGo Desktop is a comprehensive JavaFX-based desktop application that serves as a unified platform for multi-modal transportation services in Tunisia. The application integrates electric bicycle rentals, taxi services, carpooling, relocation transport, and station management into a single, user-friendly interface. Built with modern Java technologies and featuring real-time data synchronization, AI-powered assistance, payment processing, communication systems, and advanced analytics, WamiaGo aims to revolutionize urban mobility management.

The application supports multiple user roles including clients, drivers, transporters, and administrators, each with tailored interfaces and functionalities. It features sophisticated booking systems, real-time tracking, integrated payment processing, AI-powered chatbots, voice recognition, SMS notifications, and comprehensive reporting tools with environmental impact tracking.

---

## Features

🚴 **Electric Bicycle Rental System**
- Real-time bike availability tracking
- Battery level monitoring and range calculation
- Station-to-station rental management
- Automated billing and cost calculation
- QR code integration for quick bike access

🚖 **Taxi & Ride Services**
- Driver registration and management
- Real-time ride requests and matching
- Distance and duration calculation using TomTom API
- Dynamic pricing based on traffic conditions
- Driver ratings and reviews system

🚗 **Carpooling & Ride Sharing**
- Multi-passenger ride coordination
- Route optimization
- Cost sharing calculations
- Driver and passenger verification

📦 **Relocation Transport**
- Goods and furniture transportation
- Vehicle capacity management
- Booking and scheduling system
- Route planning and optimization
- Real-time chat communication

🤖 **AI & Smart Features**
- OpenAI GPT integration for intelligent assistance
- Azure Speech Services for voice recognition
- Whisper transcription services
- AI-powered route recommendations

💳 **Payment & Financial Services**
- Konnect API integration for secure payments
- Multiple payment methods support
- Automated billing and invoicing
- PDF receipt generation
- Real-time payment status tracking

📱 **Communication Systems**
- Twilio SMS notifications
- Real-time chat messaging
- Email service integration
- Multi-channel customer support
- Announcements by geographic zones

🌍 **Environmental Impact Tracking**
- Climatiq API integration for carbon footprint calculation
- Energy consumption monitoring
- Environmental impact reports
- Sustainability metrics and recommendations

📊 **Advanced Analytics & Statistics**
- Comprehensive dashboard with real-time metrics
- Usage statistics and trends
- Revenue tracking and financial reports
- Performance analytics
- Environmental impact analysis
- Data visualization and export capabilities

🎨 **Modern UI/UX**
- Responsive JavaFX interface with custom styling
- Smooth animations with AnimateFX
- FontAwesome icon integration
- Multi-theme support
- Accessibility features

---

## Built With

### 🛠️ Core Technologies

- **[Java 17](https://www.oracle.com/java/)** - Modern LTS version with enhanced performance
- **[JavaFX 17](https://openjfx.io/)** - Rich desktop application framework with FXML, Web, and Media support
- **[Maven](https://maven.apache.org/)** - Project build and dependency management
- **[MySQL 8.0](https://www.mysql.com/)** - Primary database with JDBC connectivity

### 🤖 AI & Machine Learning

- **[OpenAI GPT-3.5](https://openai.com/)** - AI-powered assistance and natural language processing
- **[Azure Speech Services](https://azure.microsoft.com/services/cognitive-services/speech-services/)** - Voice recognition and speech-to-text
- **[Whisper API](https://openai.com/research/whisper)** - Advanced audio transcription services
- **[Microsoft Cognitive Services](https://azure.microsoft.com/services/cognitive-services/)** - Speech SDK integration

### 💳 Payment & Financial Services

- **[Konnect API](https://konnect.network/)** - Secure payment processing for Tunisia
- **[Jackson](https://github.com/FasterXML/jackson)** - JSON data binding for payment APIs
- **iText PDF 5.5 & 7.2** - Professional PDF generation for receipts and reports

### 📱 Communication & Messaging

- **[Twilio](https://www.twilio.com/)** - SMS notifications and messaging services
- **[JavaMail](https://javaee.github.io/javamail/)** - Email service integration
- **[OkHttp 4.9](https://square.github.io/okhttp/)** - HTTP client for API communications

### 🌍 APIs & External Services

- **[TomTom Routing API](https://developer.tomtom.com/)** - Real-time traffic data and route calculation
- **[Climatiq API](https://www.climatiq.io/)** - Carbon footprint and environmental impact tracking
- **[Weather APIs]** - Real-time weather information integration

### 🎨 UI & User Experience

- **[AnimateFX 1.2.1](https://github.com/Typhon0/AnimateFX)** - Smooth UI animations and transitions
- **[ControlsFX 11.1.2](https://controlsfx.org/)** - Enhanced JavaFX controls and features
- **[Ikonli 12.3.1](https://kordamp.org/ikonli/)** - FontAwesome icon integration
- **Custom CSS Styling** - Modern and responsive design themes

### 🔒 Security & Utilities

- **[Password4j 1.8.0](https://password4j.com/)** - Secure password hashing and encryption
- **[ZXing 3.5.1](https://github.com/zxing/zxing)** - QR code generation and scanning
- **[Gson 2.8.9](https://github.com/google/gson)** - JSON parsing and serialization
- **[SLF4J 2.0.7](https://www.slf4j.org/)** - Logging framework

### 📊 Data Processing & Analytics

- **[JSON Processing](https://github.com/stleary/JSON-java)** - Advanced JSON data handling
- **[JNA 5.13.0](https://github.com/java-native-access/jna)** - Native library access
- **Statistical Analysis Tools** - Built-in analytics and reporting

### 🏗️ Architecture Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                    WamiaGo Desktop                          │
├─────────────────────────────────────────────────────────────┤
│               Presentation Layer (JavaFX)                  │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   User UI   │ │  Driver UI  │ │  Admin UI   │          │
│  │ Controllers │ │ Controllers │ │ Controllers │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                Business Logic Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │    Core     │ │     AI      │ │   Payment   │          │
│  │  Services   │ │  Services   │ │  Services   │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│                    Data Layer                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   Entity    │ │   Entity    │ │   Entity    │          │
│  │   Models    │ │   Models    │ │   Models    │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
├─────────────────────────────────────────────────────────────┤
│           External APIs & Integrations                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │   MySQL     │ │ OpenAI/     │ │ Payment/    │          │
│  │  Database   │ │ Azure APIs  │ │ SMS APIs    │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

---

## Getting Started

Follow these steps to set up and run WamiaGo Desktop on your local machine.

### Prerequisites

Ensure you have the following installed:

- **[Java Development Kit (JDK 17+)](https://www.oracle.com/java/technologies/javase-downloads.html)**
- **[JavaFX SDK 17+](https://gluonhq.com/products/javafx/)**
- **[Git](https://git-scm.com/downloads)**
- **[Maven 3.6+](https://maven.apache.org/download.cgi)**
- **[MySQL 8.0+](https://dev.mysql.com/downloads/)** or **[MariaDB 10.4+](https://mariadb.org/download/)**

### Installation

1. **Clone the repository:**
   ```powershell
   git clone https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop.git
   cd WamiaGo-Desktop
   ```

2. **Install dependencies:**
   ```powershell
   mvn clean install
   ```

3. **Set up the database:**
   - Start MySQL/MariaDB server
   - Create the database:
   ```sql
   CREATE DATABASE wamia_go;
   ```
   - Import the provided schema:
   ```powershell
   mysql -u root -p wamia_go < src/main/resources/dataBase/wamia_go.sql
   ```
   - Update database connection settings in `src/main/java/utils/DataBase.java` if needed

4. **Configure API keys and services:**
   - **OpenAI:** Set environment variable `OPENAI_API_KEY`
   - **Azure Speech Services:** Configure speech service credentials
   - **TomTom API:** Update API key in `TrafficService.java`
   - **Konnect Payment:** Configure payment API credentials
   - **Twilio:** Update SMS service credentials in `TwilioService.java`
   - **Climatiq:** Set up environmental impact tracking API

5. **Build and run the application:**
   ```powershell
   mvn javafx:run
   ```

   Or alternatively using the batch file:
   ```powershell
   .\run.bat
   ```

   Or run the JAR directly:
   ```powershell
   mvn clean package
   java -jar target/wamiaGo-1.0-SNAPSHOT.jar
   ```

### 🗄️ Database Schema

The application uses a comprehensive MySQL database with the following main tables:

**Core User Management:**
- `user` - User accounts, profiles, and authentication
- `driver` - Driver information, permits, and verification status
- `location` - Geographic coordinates and address information

**Transportation Services:**
- `bicycle` - Individual bicycles with status and battery levels
- `bicycle_station` - Bike rental stations and capacity information
- `bicycle_rental` - Rental transactions and usage history
- `vehicle` - Vehicle registration and specifications
- `ride` - Completed rides and trip details
- `request` - Ride requests from users
- `booking` - Advanced reservations and scheduling

**Business Operations:**
- `trip` - Planned journeys and route optimization
- `relocation` - Goods transportation requests
- `reservation` - Service reservations and confirmations
- `response` - System responses to user requests

**Communication & Management:**
- `announcement` - System announcements by geographic zone (24 Tunisian governorates)
- `rating` - Driver and service ratings/reviews
- `reclamation` - User complaints and feedback system
- `message` - Internal messaging system

**Analytics & Tracking:**
- Usage statistics and environmental impact data
- Payment transaction logs
- Performance metrics and reporting data

---

## Usage

### 👤 For End Users (Clients)

1. **Registration & Login:**
   - Launch the application using `mvn javafx:run` or `run.bat`
   - Create a new account or log in with existing credentials
   - Complete profile setup with location and preferences
   - Upload profile photo and verify contact information

2. **Electric Bicycle Rental:**
   - Browse available bike stations on the interactive map
   - Check real-time bike availability and battery levels
   - Reserve a bicycle using QR code scanning
   - Track rental duration and automatic cost calculation
   - Return to any available station with docking confirmation

3. **Taxi & Ride Services:**
   - Request immediate or scheduled rides with pickup location
   - Set destination and view estimated cost using TomTom routing
   - Track driver location in real-time on the map
   - Receive SMS notifications about ride status
   - Complete payment through integrated Konnect payment system
   - Rate and review driver experience

4. **Carpooling & Ride Sharing:**
   - Search for available shared rides to your destination
   - Create new carpool trips with passenger capacity
   - Join existing trips with automatic cost splitting
   - Connect with verified drivers and passengers through chat
   - Environmental impact tracking for shared journeys

5. **Relocation Services:**
   - Book goods and furniture transportation
   - Upload photos and describe items for accurate pricing
   - Schedule pickup and delivery times
   - Real-time chat with transporters
   - Track shipment status and receive updates

### 🚗 For Drivers & Transporters

1. **Driver Registration:**
   - Register as taxi driver, transporter, or carpool driver
   - Upload driving permit and vehicle documents
   - Complete verification process with photo ID
   - Set up payment information for earnings

2. **Service Management:**
   - Set availability status and working hours
   - Accept or decline ride/transport requests
   - Use integrated TomTom navigation for optimal routes
   - Process payments and generate PDF receipts
   - Manage customer communications through chat and SMS

3. **Vehicle & Fleet Management:**
   - Register multiple vehicles with specifications
   - Track vehicle maintenance and inspection schedules
   - Monitor fuel consumption and environmental impact
   - Access performance statistics and earnings reports

### 👨‍💼 For Administrators

1. **System Management:**
   - Monitor all transportation services through comprehensive dashboard
   - Manage user accounts, driver verification, and permissions
   - Oversee bicycle station operations and maintenance schedules
   - Generate detailed reports and analytics
   - Configure system settings and API integrations

2. **Communication & Support:**
   - Send announcements by governorate zones (24 regions in Tunisia)
   - Handle user complaints and feedback through reclamation system
   - Monitor service quality metrics and ratings
   - Manage customer support chat and messaging

3. **Financial & Analytics:**
   - Track revenue across all service categories
   - Monitor payment transactions and financial reports
   - Analyze usage patterns and demand forecasting
   - Generate environmental impact reports

### 🎯 Advanced Features

**AI-Powered Assistant:**
- Voice commands using Azure Speech Services
- Natural language queries with OpenAI GPT integration
- Intelligent route recommendations
- Automated customer support responses

**Real-time Communication:**
- Instant messaging between users, drivers, and support
- SMS notifications for critical updates
- Email integration for receipts and confirmations
- Multi-language support for diverse user base

**Environmental Tracking:**
- Carbon footprint calculation using Climatiq API
- Energy consumption monitoring for electric vehicles
- Sustainability metrics and recommendations
- Green transportation incentives and rewards

**Payment & Security:**
- Secure payment processing with Konnect API
- Multiple payment methods (cards, mobile money, bank transfer)
- Encrypted data transmission and storage
- Fraud detection and prevention

### 🎨 User Interface Features

**Dashboard Customization:**
- Personalized dashboard widgets
- Real-time statistics and metrics
- Interactive maps with live traffic data
- Weather integration for trip planning

**Accessibility:**
- High contrast themes for visual accessibility
- Keyboard navigation support
- Font size adjustment options
- Voice input and output capabilities

---

## 🏢 Project Structure

```
WamiaGo_desktop/
├── src/main/
│   ├── java/
│   │   ├── Main.java                 # Console application entry point
│   │   ├── controllers/              # JavaFX UI Controllers
│   │   │   ├── Home.java            # Application launcher
│   │   │   ├── dashboard/           # Main dashboard and analytics
│   │   │   ├── user/                # User management and authentication
│   │   │   ├── bicycle/             # Bike rental system
│   │   │   ├── station/             # Station management
│   │   │   ├── taxi/                # Taxi services (admin/driver/user)
│   │   │   ├── rides/               # Ride management
│   │   │   ├── rentals/             # Rental operations
│   │   │   ├── booking/             # Reservation system
│   │   │   ├── Relocation/          # Transport services
│   │   │   ├── Chat_Relocation/     # Real-time messaging
│   │   │   ├── Reclamation/         # Complaints and feedback
│   │   │   ├── Announcement/        # System announcements
│   │   │   ├── Response/            # Response management
│   │   │   ├── Reservation/         # Advanced reservations
│   │   │   ├── trip/                # Trip planning
│   │   │   └── StaticDash/          # Statistics dashboard
│   │   ├── entities/                # Data Models
│   │   │   ├── User.java            # User entity with roles
│   │   │   ├── Driver.java          # Driver entity with permits
│   │   │   ├── Bicycle.java         # Bicycle with status tracking
│   │   │   ├── Station.java         # Station with capacity info
│   │   │   ├── Ride.java            # Ride details and history
│   │   │   ├── Message.java         # Chat messaging system
│   │   │   ├── Payment entities     # Payment processing models
│   │   │   └── ...                  # Other business entities
│   │   ├── services/                # Business Logic Layer
│   │   │   ├── UserService.java     # User operations and auth
│   │   │   ├── PaymentService.java  # Konnect payment integration
│   │   │   ├── OpenAIService.java   # AI assistance
│   │   │   ├── TwilioService.java   # SMS notifications
│   │   │   ├── ClimatiqService.java # Environmental tracking
│   │   │   ├── TrafficService.java  # TomTom routing
│   │   │   ├── AzureSpeechService.java # Voice recognition
│   │   │   └── ...                  # Other service classes
│   │   └── utils/                   # Utility Classes
│   │       └── DataBase.java        # MySQL connection management
│   └── resources/                   # Application Resources
│       ├── dataBase/                # SQL schemas and migrations
│       ├── *.fxml                   # JavaFX UI layouts
│       ├── styles/                  # CSS styling files
│       ├── images/                  # UI assets and icons
│       ├── Fonts/                   # Custom typography
│       ├── maps/                    # Map resources
│       └── various UI modules/      # Feature-specific resources
├── lib/                             # Native libraries
│   ├── client-sdk-1.25.0.jar      # Azure Speech SDK
│   └── vosk-0.3.32.jar             # Speech recognition
├── target/                          # Compiled artifacts
│   └── wamiaGo-1.0-SNAPSHOT.jar    # Executable JAR
├── run.bat                          # Windows launch script
├── pom.xml                          # Maven configuration
└── README.md                        # Documentation
```

---

## 🔧 Configuration & Deployment

### Environment Variables
Set up the following environment variables for full functionality:
```powershell
# AI Services
$env:OPENAI_API_KEY = "your_openai_api_key"

# Azure Speech Services
$env:AZURE_SPEECH_KEY = "your_azure_speech_key"
$env:AZURE_SPEECH_REGION = "your_region"

# Payment Services
$env:KONNECT_API_KEY = "your_konnect_api_key"

# Environmental Tracking
$env:CLIMATIQ_API_KEY = "your_climatiq_api_key"
```

### Database Configuration
Update database settings in `src/main/java/utils/DataBase.java`:
```java
private final String URL = "jdbc:mysql://127.0.0.1:3306/wamia_go";
private final String LOGIN = "your_username";
private final String PWD = "your_password";
```

### Development vs Production
- **Development:** Use `mvn javafx:run` for hot reloading
- **Production:** Build with `mvn clean package` and deploy the JAR file
- **Testing:** Run with test profiles for API mocking

### System Requirements
- **Java:** JDK 17 or higher
- **Memory:** Minimum 4GB RAM (8GB recommended for AI features)
- **Storage:** 1GB free space (additional space for media and maps)
- **OS:** Windows 10+, macOS 10.14+, Linux Ubuntu 18.04+
- **Database:** MySQL 8.0+ or MariaDB 10.4+
- **Network:** Internet connection for API services

---

## 🚀 Performance & Scalability

### Optimization Features
- **Database Connection Pooling** for improved performance
- **Caching Systems** for frequently accessed data
- **Asynchronous Processing** for API calls and file operations
- **Memory Management** with efficient JavaFX rendering

### Monitoring & Analytics
- **Real-time Performance Metrics** tracking
- **Error Logging** with SLF4J framework
- **Usage Analytics** for feature optimization
- **Environmental Impact Monitoring**

---

## 🤝 Contributing

We welcome contributions to improve WamiaGo Desktop! Here's how you can help:

### Getting Started
1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions and best practices
- Add comprehensive comments and JavaDoc documentation
- Write unit tests for new features
- Update README.md for significant changes
- Test with multiple user roles and scenarios

### Code Style
- Use consistent indentation (4 spaces)
- Follow Maven project structure
- Implement proper error handling
- Use meaningful variable and method names

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact

**WamiaGo Development Team**
- **Email:** [wamiago@gmail.com](mailto:wamiago@gmail.com)
- **Project Repository:** [WamiaGo-Desktop](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop)

For technical support, feature requests, bug reports, or partnership opportunities, please reach out via email or create an issue on our GitHub repository.

---

## 🙏 Acknowledgments

### Technology Partners
- **[OpenAI](https://openai.com/)** - AI-powered assistance and natural language processing
- **[Microsoft Azure](https://azure.microsoft.com/)** - Speech recognition and cognitive services
- **[TomTom](https://developer.tomtom.com/)** - Routing, traffic, and mapping services
- **[Konnect](https://konnect.network/)** - Payment processing for Tunisian market
- **[Twilio](https://www.twilio.com/)** - SMS and communication services
- **[Climatiq](https://www.climatiq.io/)** - Environmental impact tracking

### Open Source Libraries
- **[JavaFX Community](https://openjfx.io/)** - Rich desktop application framework
- **[AnimateFX](https://github.com/Typhon0/AnimateFX)** - Beautiful UI animations
- **[ControlsFX](https://controlsfx.org/)** - Enhanced JavaFX controls
- **[iText](https://itextpdf.com/)** - Professional PDF generation
- **[ZXing](https://github.com/zxing/zxing)** - QR code functionality
- **[Password4j](https://password4j.com/)** - Secure password handling

### Special Thanks
- **All Contributors** - Thank you for making this project better!
- **Beta Testers** - Your feedback shaped the user experience
- **Tunisian Transportation Community** - For domain expertise and insights

---

## 📊 Project Status

**Current Version:** 1.0-SNAPSHOT  
**Development Status:** Active Development  
**Last Updated:** May 2025  
**Target Release:** Summer 2025

### ✅ Completed Features
- ✅ Multi-modal transportation integration
- ✅ Real-time bicycle rental system
- ✅ Advanced payment processing with Konnect
- ✅ AI-powered assistance with OpenAI GPT
- ✅ Voice recognition with Azure Speech Services
- ✅ SMS notifications via Twilio
- ✅ Environmental impact tracking
- ✅ Comprehensive analytics dashboard
- ✅ Real-time chat and messaging
- ✅ PDF generation for receipts and reports
- ✅ QR code integration
- ✅ Multi-role user management

---

<div align="center">
  <strong>Made with ❤️ by the WamiaGo Team</strong><br>
  <sub>Building the future of smart transportation</sub>
</div>

