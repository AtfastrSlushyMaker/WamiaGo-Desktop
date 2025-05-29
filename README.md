# WamiaGo Desktop

<div align="center">
  <a href="https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop">
    <img src="https://i.imgur.com/759dC4H.png" alt="WamiaGo Logo" width="500">
  </a>
  
  <p><strong>Advanced Desktop Transportation Management Platform with AI Integration</strong></p>
  
  [![Java](https://img.shields.io/badge/Java-17-orange.svg?style=flat&logo=openjdk)](https://www.oracle.com/java/)
  [![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg?style=flat&logo=openjdk)](https://openjfx.io/)
  [![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg?style=flat&logo=apache-maven)](https://maven.apache.org/)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1.svg?style=flat&logo=mysql)](https://mysql.com/)
  [![OpenAI](https://img.shields.io/badge/OpenAI-GPT--3.5-412991.svg?style=flat&logo=openai)](https://openai.com/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
</div>

## Table of Contents

- [🎯 About The Project](#-about-the-project)
- [✨ Key Features](#-key-features)
- [🏗️ Technology Stack](#️-technology-stack)
- [🔧 Architecture Overview](#-architecture-overview)
- [🚀 Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Environment Configuration](#environment-configuration)
- [📖 Usage Guide](#-usage-guide)
  - [For End Users (Clients)](#-for-end-users-clients)
  - [For Drivers & Transporters](#-for-drivers--transporters)
  - [For Administrators](#-for-administrators)
- [🏢 Project Structure](#-project-structure)
- [🔌 API Integration](#-api-integration)
- [🧪 Development](#-development)
- [🐛 Troubleshooting](#-troubleshooting)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [📞 Contact](#-contact)
- [🙏 Acknowledgments](#-acknowledgments)

---

---

## 🎯 About The Project

**WamiaGo Desktop** is a comprehensive JavaFX-based desktop application that serves as a unified platform for multi-modal transportation services in Tunisia. The application integrates electric bicycle rentals, taxi services, carpooling, relocation transport, and station management into a single, user-friendly interface.

> 🌐 **Related Project**: Check out the [WamiaGo Web Application](https://github.com/CherifChebbi/WamiaGo-Webapp) for the companion web platform.

### What Makes WamiaGo Desktop Special?

🤖 **AI-Powered Intelligence** - OpenAI GPT integration with Azure Speech Services for voice recognition  
🚴‍♂️ **Smart Bicycle Management** - Real-time tracking with QR code integration  
🚗 **Multi-Modal Transport** - Unified platform for taxis, carpooling, and relocation services  
💳 **Secure Payments** - Payment processing with PDF receipt generation  
🌍 **Environmental Tracking** - Climatiq API for carbon footprint monitoring  
📱 **Real-time Communication** - Twilio SMS integration and messaging system  
🏗️ **Enterprise Architecture** - Scalable JavaFX application with MySQL backend

Built with modern Java technologies and featuring AI-powered assistance, payment processing, communication systems, and environmental tracking, WamiaGo aims to revolutionize urban mobility management in Tunisia.

---

## ✨ Key Features

### 🚴‍♂️ Electric Bicycle Rental System
- **📍 Station Management** - Real-time bicycle tracking and availability monitoring
- **📱 QR Code Integration** - Bike access with QR code scanning
- **⚡ Rental Management** - Automated billing and rental tracking
- **🗺️ Station Locations** - Interactive station mapping and information

### 🚖 Transportation Services
- **👨‍✈️ Driver Management** - Driver registration and profile management
- **🚙 Ride Services** - Taxi and ride booking functionality
- **⭐ Rating System** - Driver and service evaluation
- **📍 Location Services** - GPS integration for location tracking

### 🚗 Carpooling & Ride Sharing
- **👥 Passenger Coordination** - Multi-passenger ride management
- **📅 Scheduling** - Flexible pickup and delivery scheduling
- **💰 Cost Management** - Ride cost calculation and splitting

### 📦 Relocation Services
- **📋 Booking Management** - Goods and furniture transportation booking
- **📅 Scheduling** - Pickup and delivery time management
- **💬 Communication** - Direct messaging between users and transporters

### 🤖 AI & Smart Technologies
- **🧠 OpenAI Integration** - AI-powered assistance and chat functionality
- **🎤 Voice Recognition** - Azure Speech Services integration
- **📝 Audio Transcription** - Whisper API for voice message processing

### 💳 Payment & Document Services
- **📄 PDF Generation** - Automated receipt and document creation
- **💳 Payment Processing** - Integrated payment handling
- **📊 Financial Tracking** - Payment status and transaction management

### 📱 Communication & Messaging
- **💬 Real-time Chat** - In-app messaging system
- **📲 SMS Notifications** - Twilio SMS integration
- **📧 Email Services** - Email notifications and communication
- **📢 Announcements** - Zone-based messaging system

### 🌍 Environmental Tracking
- **📊 Carbon Footprint** - Climatiq API for emissions calculation
- **🌱 Environmental Impact** - Sustainability tracking and reporting

### 🎨 Modern User Experience
- **🖥️ JavaFX Interface** - Professional desktop application
- **✨ Smooth Animations** - AnimateFX integration for enhanced interactions
- **🎨 Icon Integration** - FontAwesome icons with Ikonli framework
- **🎯 Responsive Design** - Adaptive layouts and modern styling
- **✨ Smooth Animations** - AnimateFX integration for enhanced user interactions
- **🎨 Icon Integration** - FontAwesome icons with Ikonli framework
- **🌙 Theme Support** - Multiple UI themes and accessibility options
- **⌨️ Keyboard Navigation** - Full accessibility compliance and shortcuts

---

---

## 🏗️ Technology Stack

### 🛠️ Core Framework & Runtime

- **[Java 17](https://www.oracle.com/java/)** - Modern LTS version with enhanced performance
- **[JavaFX 17](https://openjfx.io/)** - Rich desktop application framework with FXML, Web, and Media support
- **[Maven](https://maven.apache.org/)** - Project build and dependency management
- **[MySQL 8.0](https://www.mysql.com/)** - Primary database with JDBC connectivity

## 🏗️ Technology Stack

### 🛠️ Core Framework & Runtime

- **[Java 17](https://www.oracle.com/java/)** - Modern LTS version with enhanced performance
- **[JavaFX 17](https://openjfx.io/)** - Rich desktop application framework with FXML, Web, and Media support
- **[Maven](https://maven.apache.org/)** - Project build and dependency management
- **[MySQL 8.0](https://www.mysql.com/)** - Primary database with JDBC connectivity

### 🤖 AI & Machine Learning

- **[OpenAI GPT-3.5](https://openai.com/)** - AI-powered assistance and natural language processing
- **[Azure Speech Services](https://azure.microsoft.com/services/cognitive-services/speech-services/)** - Voice recognition and speech-to-text
- **[Whisper API](https://openai.com/research/whisper)** - Audio transcription services

### 💳 Payment & Document Processing

- **[iText PDF 5.5 & 7.2](https://itextpdf.com/)** - Professional PDF generation for receipts and reports
- **Payment Processing** - Integrated payment handling with PDF receipt generation

### 📱 Communication & Messaging

- **[Twilio](https://www.twilio.com/)** - SMS notifications and messaging services
- **[JavaMail](https://javaee.github.io/javamail/)** - Email service integration
- **[OkHttp 4.9](https://square.github.io/okhttp/)** - HTTP client for API communications

### 🌍 External APIs & Services

- **[Climatiq API](https://www.climatiq.io/)** - Carbon footprint and environmental impact tracking
- **Traffic Services** - Real-time traffic data and route calculation

### 🎨 UI & User Experience

- **[AnimateFX 1.2.1](https://github.com/Typhon0/AnimateFX)** - Smooth UI animations and transitions
- **[ControlsFX 11.1.2](https://controlsfx.org/)** - Enhanced JavaFX controls and features
- **[Ikonli 12.3.1](https://kordamp.org/ikonli/)** - FontAwesome icon integration
- **Custom CSS Styling** - Modern and responsive design themes

### 🔒 Security & Utilities

- **[Password4j 1.8.0](https://password4j.com/)** - Secure password hashing and encryption
- **[ZXing 3.5.1](https://github.com/zxing/zxing)** - QR code generation and scanning
- **[Gson 2.8.9](https://github.com/google/gson)** - JSON parsing and serialization
- **[Jackson](https://github.com/FasterXML/jackson)** - JSON data binding
- **[SLF4J 2.0.7](https://www.slf4j.org/)** - Logging framework

### 📊 Data Processing

- **[JSON Processing](https://github.com/stleary/JSON-java)** - JSON data handling
- **[JNA 5.13.0](https://github.com/java-native-access/jna)** - Native library access
- **Session Management** - Secure user session handling and timeout management
- **Data Encryption** - Sensitive data protection and secure API communications
- **Role-based Access Control** - Multi-level user permissions and authorization

### 🧪 Development Tools & Quality Assurance
- **[SLF4J 2.0.7](https://www.slf4j.org/)** - Comprehensive logging framework with multiple backends
- **[JNA 5.13.0](https://github.com/java-native-access/jna)** - Native library access for system integration
- **Maven Surefire** - Automated testing and continuous integration
- **Code Quality Tools** - Static analysis and code coverage reporting

---

## 🔧 Architecture Overview

### System Architecture

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

### Application Architecture Principles

#### 🏗️ Modular Design
- **Separation of Concerns** - Clear separation between UI, business logic, and data layers
- **Component-based Architecture** - Reusable components across different modules
- **Service Layer Pattern** - Centralized business logic with clean interfaces
- **Repository Pattern** - Data access abstraction and caching strategies

#### 🔄 Integration Architecture  
- **RESTful API Integration** - Standardized communication with external services
- **Event-driven Communication** - Asynchronous message handling for real-time updates
- **Circuit Breaker Pattern** - Fault tolerance for external API dependencies
- **Caching Strategy** - Multi-level caching for performance optimization

#### 📊 Data Architecture
- **Normalized Database Schema** - Optimized for ACID compliance and performance
- **Connection Pooling** - Efficient database connection management
- **Transaction Management** - Consistent data integrity across operations
- **Backup and Recovery** - Automated data protection and disaster recovery

### External Service Architecture

```
                    ┌─────────────────┐
                    │  WamiaGo Desktop │
                    │    (JavaFX)     │
                    └─────────┬───────┘
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
    ┌────▼─────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │OpenAI/   │      │  Payment &  │      │Communication│
    │Azure AI  │      │  Financial  │      │ Services   │
    │Services  │      │  Services   │      │           │
    └────┬─────┘      └──────┬──────┘      └─────┬─────┘
         │                   │                   │
    ┌────▼─────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │• OpenAI  │      │• Konnect    │      │• Twilio   │
    │• Azure   │      │• PDF Gen    │      │• JavaMail │
    │• Whisper │      │• TomTom     │      │• SMS      │
    └──────────┘      └─────────────┘      └───────────┘
```

---

---

## 🚀 Getting Started

WamiaGo Desktop provides a complete development environment with comprehensive setup instructions for both development and production deployments.

### Prerequisites

#### 🔧 Core Requirements
- **[Java Development Kit (JDK 17+)](https://www.oracle.com/java/technologies/javase-downloads.html)** - Oracle or OpenJDK distribution
- **[JavaFX SDK 17+](https://gluonhq.com/products/javafx/)** - Desktop UI framework (included in some JDK distributions)
- **[Maven 3.6+](https://maven.apache.org/download.cgi)** - Project build and dependency management
- **[Git](https://git-scm.com/downloads)** - Version control for repository cloning

#### 🗄️ Database Requirements  
- **[MySQL 8.0+](https://dev.mysql.com/downloads/)** - Primary database (recommended)
- **Alternative:** **[MariaDB 10.4+](https://mariadb.org/download/)** - MySQL-compatible database

#### 🌐 External Services (Optional but Recommended)
- **OpenAI API Key** - For AI-powered assistance features
- **Azure Speech Services** - For voice recognition capabilities  
- **TomTom API Key** - For traffic data and routing services
- **Konnect API Credentials** - For payment processing in Tunisia
- **Twilio Account** - For SMS notifications
- **Climatiq API Key** - For environmental impact tracking

### Installation

1. **📥 Clone the Repository**
   ```powershell
   git clone https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop.git
   cd WamiaGo-Desktop
   ```

2. **🔧 Install Dependencies & Compile**
   ```powershell
   # Clean previous builds and install dependencies
   mvn clean install
   
   # Skip tests for faster build (optional)
   mvn clean install -DskipTests
   ```

3. **🗄️ Database Setup**
   
   **Option A: Quick Setup (Recommended)**
   ```powershell
   # Start MySQL service
   # Windows
   net start mysql
   # macOS/Linux
   sudo systemctl start mysql
   
   # Create database
   mysql -u root -p -e "CREATE DATABASE wamia_go CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   
   # Import schema and sample data
   mysql -u root -p wamia_go < src/main/resources/dataBase/wamia_go.sql
   ```
   
   **Option B: Manual Setup**
   ```sql
   -- Connect to MySQL
   mysql -u root -p
   
   -- Create database
   CREATE DATABASE wamia_go CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE wamia_go;
   
   -- Import the provided schema
   SOURCE src/main/resources/dataBase/wamia_go.sql;
   
   -- Verify tables
   SHOW TABLES;
   ```

4. **⚙️ Configure Database Connection**
   
   Update database settings in `src/main/java/utils/DataBase.java`:
   ```java
   private final String URL = "jdbc:mysql://127.0.0.1:3306/wamia_go?useSSL=false&serverTimezone=UTC";
   private final String LOGIN = "your_username";      // Replace with your MySQL username
   private final String PWD = "your_password";        // Replace with your MySQL password
   ```

5. **🚀 Build and Run**
   
   **Method 1: Maven JavaFX Plugin (Development)**
   ```powershell
   mvn javafx:run
   ```
   
   **Method 2: Windows Batch File**
   ```powershell
   .\run.bat
   ```
   
   **Method 3: Standalone JAR (Production)**
   ```powershell
   # Build JAR
   mvn clean package
   
   # Run JAR
   java -jar target/wamiaGo-1.0-SNAPSHOT.jar
   ```

### Environment Configuration

#### 🔑 API Keys Setup

Set up environment variables for external services:

**Windows (PowerShell):**
```powershell
# AI Services
$env:OPENAI_API_KEY = "your_openai_api_key_here"

# Azure Speech Services  
$env:AZURE_SPEECH_KEY = "your_azure_speech_key_here"
$env:AZURE_SPEECH_REGION = "your_azure_region"  # e.g., "eastus"

# Payment Services
$env:KONNECT_API_KEY = "your_konnect_api_key_here"
$env:KONNECT_WALLET_ID = "your_wallet_id_here"

# Communication Services
$env:TWILIO_ACCOUNT_SID = "your_twilio_account_sid"
$env:TWILIO_AUTH_TOKEN = "your_twilio_auth_token"

# Environmental Tracking
$env:CLIMATIQ_API_KEY = "your_climatiq_api_key_here"

# Traffic & Routing
$env:TOMTOM_API_KEY = "your_tomtom_api_key_here"
```

**macOS/Linux (Bash):**
```bash
# Add to ~/.bashrc or ~/.zshrc
export OPENAI_API_KEY="your_openai_api_key_here"
export AZURE_SPEECH_KEY="your_azure_speech_key_here"
export AZURE_SPEECH_REGION="your_azure_region"
export KONNECT_API_KEY="your_konnect_api_key_here"
export TWILIO_ACCOUNT_SID="your_twilio_account_sid"
export TWILIO_AUTH_TOKEN="your_twilio_auth_token"
export CLIMATIQ_API_KEY="your_climatiq_api_key_here"
export TOMTOM_API_KEY="your_tomtom_api_key_here"

# Reload environment
source ~/.bashrc
```

#### 📝 Application Configuration

Create `application.properties` in `src/main/resources/` (optional):
```properties
# Database Configuration
database.url=jdbc:mysql://127.0.0.1:3306/wamia_go
database.username=your_username
database.password=your_password
database.maxConnections=20

# Application Settings
app.name=WamiaGo Desktop
app.version=1.0-SNAPSHOT
app.debug=true

# Cache Settings
cache.enabled=true
cache.ttl=3600

# External Services
services.openai.enabled=true
services.azure.enabled=true
services.twilio.enabled=true
services.konnect.enabled=true
```

---

## 📖 Usage Guide

### 🎯 For End Users (Clients)

#### 🚴‍♂️ Electric Bicycle Rental

1. **Find Available Bikes**
   ```
   Dashboard → Bicycle Services → Station Map
   ```
   - View real-time bike availability across all stations
   - Check battery levels and estimated range
   - Filter by location and bike type

2. **Rent a Bicycle**
   ```
   Select Station → Choose Bike → Scan QR Code → Start Rental
   ```
   - Automatic billing starts upon successful unlock
   - Real-time GPS tracking and route guidance
   - Return to any available docking station

3. **Monitor Your Ride**
   - Live tracking with battery consumption
   - Route optimization suggestions
   - Safety alerts and traffic updates

#### 🚖 Taxi & Ride Services

1. **Request a Ride**
   ```
   Transportation → Taxi Service → Set Pickup & Destination
   ```
   - Real-time driver matching based on location
   - Fare estimation with dynamic pricing
   - Driver profile and rating display

2. **Track Your Journey**
   - Live driver location and ETA
   - Direct messaging with driver
   - Trip details and receipt generation

#### 🚗 Carpooling Services

1. **Join or Create Rides**
   ```
   Transportation → Carpooling → Browse/Create Rides
   ```
   - Search by route, time, and preferences
   - View driver profiles and vehicle details
   - Automatic cost splitting calculation

2. **Coordinate with Co-passengers**
   - Group chat functionality
   - Pickup point coordination
   - Payment distribution management

#### 📦 Relocation Services

1. **Book Transportation**
   ```
   Services → Relocation → Create Booking Request
   ```
   - Upload photos of items for accurate pricing
   - Schedule pickup and delivery times
   - Choose vehicle type and services

2. **Track Your Shipment**
   - Real-time location updates
   - Direct communication with transporter
   - Delivery confirmation and feedback

---

### 🚛 For Drivers & Transporters

#### 👨‍✈️ Driver Registration & Setup

1. **Account Creation**
   ```
   Registration → Driver Profile → Upload Documents
   ```
   - Personal information and emergency contacts
   - License verification and vehicle registration
   - Insurance and safety certification upload

2. **Vehicle Management**
   ```
   Dashboard → My Vehicles → Add/Edit Vehicle
   ```
   - Multiple vehicle support (taxi, carpool, transport)
   - Real-time location sharing and tracking
   - Maintenance scheduling and reminders

#### 📋 Service Management

1. **Accept Ride Requests**
   ```
   Dashboard → Active Requests → Accept/Decline
   ```
   - Real-time request notifications
   - Route optimization and traffic analysis
   - Customer communication tools

2. **Earnings & Analytics**
   ```
   Dashboard → Earnings → View Reports
   ```
   - Daily, weekly, monthly revenue tracking
   - Performance metrics and ratings
   - Payment history and tax documentation

3. **Relocation Services**
   ```
   Services → Transport Requests → Browse Available Jobs
   ```
   - Filter by cargo type, distance, and payment
   - Photo documentation for quotes
   - Delivery confirmation and customer feedback

---

### 🏢 For Administrators

#### 🎛️ System Management

1. **User Management**
   ```
   Admin Panel → Users → View/Edit/Suspend
   ```
   - Customer and driver account oversight
   - Verification status and document review
   - Activity monitoring and fraud detection

2. **Service Configuration**
   ```
   Admin Panel → Services → Configure Pricing/Availability
   ```
   - Dynamic pricing rule management
   - Service area and zone configuration
   - Promotional campaigns and discount management

#### 📊 Analytics & Reporting

1. **Business Intelligence**
   ```
   Admin Panel → Analytics → Generate Reports
   ```
   - Revenue analysis across all services
   - User behavior and service utilization
   - Environmental impact reporting

2. **Station Management**
   ```
   Admin Panel → Stations → Monitor/Configure
   ```
   - Real-time bicycle inventory management
   - Maintenance scheduling and alerts
   - Capacity optimization and expansion planning

#### 🔧 System Administration

1. **API Configuration**
   ```
   Admin Panel → Settings → External APIs
   ```
   - Payment gateway configuration (Konnect)
   - AI service management (OpenAI, Azure)
   - Communication settings (Twilio, Email)

2. **Announcement System**
   ```
   Admin Panel → Communications → Broadcast Messages
   ```
   - Zone-based announcements by governorate
   - Emergency notifications and service updates
   - Marketing campaigns and feature launches

---

## 🔌 API Integration

WamiaGo Desktop integrates with multiple external APIs to provide comprehensive transportation services.

### 🤖 AI & Machine Learning APIs

#### OpenAI GPT Integration
```java
// src/main/java/services/OpenAIService.java
public class OpenAIService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey = System.getenv("OPENAI_API_KEY");
    
    public String generateResponse(String prompt) {
        // Natural language processing for customer support
        // Route optimization suggestions
        // Intelligent booking assistance
    }
}
```

#### Azure Speech Services
```java
// src/main/java/services/AzureSpeechService.java
public class AzureSpeechService {
    private final String speechKey = System.getenv("AZURE_SPEECH_KEY");
    private final String speechRegion = System.getenv("AZURE_SPEECH_REGION");
    
    public String recognizeSpeech(InputStream audioStream) {
        // Voice commands for hands-free operation
        // Speech-to-text for accessibility
        // Multi-language voice recognition
    }
}
```

### 💳 Payment & Financial APIs

#### Konnect Payment Processing
```java
// src/main/java/services/PaymentService.java
public class PaymentService {
    private static final String KONNECT_API_BASE = "https://api.konnect.network/api/v2";
    private final String apiKey = System.getenv("KONNECT_API_KEY");
    
    public PaymentResult processPayment(PaymentRequest request) {
        // Secure payment processing for Tunisian market
        // Multiple payment methods support
        // Real-time payment status tracking
        // Automated refund processing
    }
}
```

### 📱 Communication APIs

#### Twilio SMS Integration
```java
// src/main/java/services/TwilioService.java
public class TwilioService {
    private final String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
    private final String authToken = System.getenv("TWILIO_AUTH_TOKEN");
    
    public void sendSMS(String phoneNumber, String message) {
        // Ride confirmations and updates
        // Driver arrival notifications
        // Emergency alerts and support messages
        // Multi-language SMS support
    }
}
```

### 🌍 Environmental & Location APIs

#### Climatiq Environmental Tracking
```java
// src/main/java/services/ClimatiqService.java
public class ClimatiqService {
    private static final String CLIMATIQ_API_BASE = "https://beta3.api.climatiq.io";
    private final String apiKey = System.getenv("CLIMATIQ_API_KEY");
    
    public CarbonFootprint calculateEmissions(TripData tripData) {
        // Carbon footprint calculation for all transportation modes
        // Environmental impact reports
        // Sustainability recommendations
        // Green transportation incentives
    }
}
```

#### TomTom Traffic & Routing
```java
// src/main/java/services/TrafficService.java  
public class TrafficService {
    private static final String TOMTOM_API_BASE = "https://api.tomtom.com/routing/1";
    private final String apiKey = System.getenv("TOMTOM_API_KEY");
    
    public RouteResponse calculateRoute(Location start, Location end) {
        // Real-time traffic data integration
        // Dynamic route optimization
        // Travel time estimation
        // Traffic-based pricing calculations
    }
}
```

### 🔐 API Security & Best Practices

#### Secure API Communication
- **HTTPS Only** - All API communications use TLS encryption
- **API Key Management** - Environment variables for secure credential storage
- **Rate Limiting** - Built-in throttling to respect API limits
- **Error Handling** - Comprehensive error handling and retry mechanisms
- **Logging** - Detailed API call logging for debugging and monitoring

#### Circuit Breaker Pattern
```java
@Component
public class ApiCircuitBreaker {
    private final CircuitBreaker circuitBreaker;
    
    public <T> T executeWithFallback(Supplier<T> apiCall, Supplier<T> fallback) {
        // Fault tolerance for external API dependencies
        // Automatic fallback mechanisms
        // Service degradation handling
    }
}
```

---

## 🧪 Development

### Development Environment Setup

#### 🛠️ IDE Configuration

**IntelliJ IDEA (Recommended):**
```xml
<!-- .idea/runConfigurations/WamiaGo_Desktop.xml -->
<configuration name="WamiaGo Desktop" type="Application">
  <option name="MAIN_CLASS_NAME" value="controllers.Home" />
  <option name="VM_PARAMETERS" value="--module-path &quot;path/to/javafx/lib&quot; --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media" />
  <option name="PROGRAM_PARAMETERS" value="" />
  <module name="WamiaGo_desktop" />
</configuration>
```

**VS Code:**
```json
// .vscode/launch.json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "WamiaGo Desktop",
            "request": "launch",
            "mainClass": "controllers.Home",
            "vmArgs": "--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media"
        }
    ]
}
```

### 🧪 Testing & Quality Assurance

#### Unit Testing
```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn test jacoco:report
```

#### Integration Testing
```powershell
# Run integration tests
mvn verify -P integration-tests

# Test with external APIs (requires API keys)
mvn verify -P integration-tests -Dtest.external.apis=true
```

#### Code Quality
```powershell
# Static analysis with SpotBugs
mvn spotbugs:check

# Code style checking
mvn checkstyle:check

# Dependency vulnerability scanning
mvn dependency:analyze
```

### 🔄 Development Workflow

#### Hot Reloading Setup
```powershell
# Terminal 1: Start with file watching
mvn javafx:run -Djavafx.args="--watch"

# Terminal 2: Auto-compile on file changes
mvn compile -o -T1C
```

#### Database Development
```powershell
# Create development database
mysql -u root -p -e "CREATE DATABASE wamia_go_dev;"

# Run with development profile
mvn javafx:run -Dspring.profiles.active=dev

# Database migrations (manual)
mysql -u root -p wamia_go_dev < src/main/resources/migrations/001_add_payment_tables.sql
```

### 📊 Performance Profiling

#### Memory Analysis
```powershell
# Run with JVM profiling
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdumps/ -jar target/wamiaGo-1.0-SNAPSHOT.jar

# Performance monitoring
java -javaagent:path/to/jmx-agent.jar -jar target/wamiaGo-1.0-SNAPSHOT.jar
```

#### JavaFX Performance
```java
// Enable JavaFX performance indicators
System.setProperty("prism.verbose", "true");
System.setProperty("prism.showdirty", "true");
System.setProperty("javafx.animation.fullspeed", "true");
```

---

## 🐛 Troubleshooting

### Common Issues & Solutions

#### 🗄️ Database Connection Problems

**Symptoms:**
- Application fails to start with database connection errors
- "Connection refused" or "Access denied" errors

**Solutions:**
```powershell
# Check MySQL service status
# Windows
sc query mysql
net start mysql

# macOS/Linux  
sudo systemctl status mysql
sudo systemctl start mysql

# Test connection manually
mysql -u root -p -h 127.0.0.1 -P 3306 -e "SELECT 1;"

# Verify database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'wamia_go';"
```

**Configuration Check:**
```java
// Update DataBase.java with correct credentials
private final String URL = "jdbc:mysql://127.0.0.1:3306/wamia_go?useSSL=false&serverTimezone=UTC";
private final String LOGIN = "root";  // Your MySQL username
private final String PWD = "password"; // Your MySQL password
```

#### 🚀 JavaFX Runtime Issues

**Symptoms:**
- "Module javafx.controls not found" errors
- Blank windows or missing UI components
- Application crashes on startup

**Solutions:**
```powershell
# Verify JavaFX installation
java --list-modules | grep javafx

# Run with explicit module path
java --module-path "C:\path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media -cp target/classes controllers.Home

# Alternative: Use Maven JavaFX plugin
mvn javafx:run
```

**JVM Arguments Setup:**
```bash
# Add to your IDE or run configuration
--module-path "path/to/javafx/lib"
--add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
```

#### 🔑 API Key & External Service Issues

**Symptoms:**
- AI features not working (OpenAI/Azure)
- Payment processing failures
- SMS notifications not sending

**Solutions:**
```powershell
# Verify environment variables
echo $env:OPENAI_API_KEY      # Windows PowerShell
echo $OPENAI_API_KEY          # macOS/Linux

# Test API connectivity
curl -H "Authorization: Bearer $OPENAI_API_KEY" https://api.openai.com/v1/models

# Validate Twilio credentials
curl -X GET "https://api.twilio.com/2010-04-01/Accounts/$TWILIO_ACCOUNT_SID.json" \
     -u "$TWILIO_ACCOUNT_SID:$TWILIO_AUTH_TOKEN"
```

**API Configuration Validation:**
```java
// Add to your service classes for debugging
public void validateConfiguration() {
    String apiKey = System.getenv("OPENAI_API_KEY");
    if (apiKey == null || apiKey.isEmpty()) {
        logger.warn("OpenAI API key not configured");
    }
    
    // Test API connectivity
    try {
        // Make a test API call
        makeTestCall();
        logger.info("API connectivity successful");
    } catch (Exception e) {
        logger.error("API connectivity failed", e);
    }
}
```

#### 💾 Memory & Performance Issues

**Symptoms:**
- Slow application startup
- High memory usage
- UI freezing or lag

**Solutions:**
```powershell
# Increase JVM memory
java -Xms2g -Xmx4g -jar target/wamiaGo-1.0-SNAPSHOT.jar

# Enable G1 garbage collector
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar target/wamiaGo-1.0-SNAPSHOT.jar

# Profile memory usage
java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -jar target/wamiaGo-1.0-SNAPSHOT.jar
```

**Performance Optimization:**
```java
// In your JavaFX controllers
@Override
public void initialize(URL location, ResourceBundle resources) {
    // Use background threads for heavy operations
    Task<Void> loadDataTask = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
            // Heavy data loading
            return null;
        }
    };
    
    new Thread(loadDataTask).start();
}
```

#### 🎨 UI & Styling Issues

**Symptoms:**
- Missing icons or fonts
- Incorrect styling or layout
- CSS not loading properly

**Solutions:**
```powershell
# Verify resource files exist
ls src/main/resources/styles/
ls src/main/resources/images/
ls src/main/resources/Fonts/

# Check FXML file references
grep -r "stylesheets" src/main/resources/*.fxml
```

**Resource Loading Debugging:**
```java
// In your controllers
public void debugResourceLoading() {
    URL cssUrl = getClass().getResource("/styles/application.css");
    URL fontUrl = getClass().getResource("/Fonts/your-font.ttf");
    
    logger.info("CSS URL: " + cssUrl);
    logger.info("Font URL: " + fontUrl);
    
    if (cssUrl == null) {
        logger.error("CSS file not found in resources");
    }
}
```

### 📞 Getting Help

#### Debug Mode
```powershell
# Run with debug logging
java -Dlog.level=DEBUG -jar target/wamiaGo-1.0-SNAPSHOT.jar

# Enable JavaFX debug output
java -Dprism.verbose=true -Djavafx.verbose=true -jar target/wamiaGo-1.0-SNAPSHOT.jar
```

#### Log Analysis
```bash
# View application logs
tail -f logs/wamiaGo.log

# Search for specific errors
grep -i "error\|exception" logs/wamiaGo.log

# Check database connection logs
grep -i "database\|mysql" logs/wamiaGo.log
```

#### Community Support
- 🐛 **Bug Reports**: [Create an Issue](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues)
- 💡 **Feature Requests**: [Feature Request Template](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=feature_request.md)
- ❓ **Questions**: [Q&A Discussions](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/discussions/categories/q-a)
- 📚 **Documentation**: [Wiki](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/wiki)

---

## 🤝 Contributing

We welcome contributions from the community! WamiaGo Desktop is built with modern technologies and follows industry best practices. Here's how you can contribute:

### 🚀 Development Workflow

#### 🔄 Getting Started
1. **Fork the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/WamiaGo-Desktop.git
   cd WamiaGo-Desktop
   ```

2. **Set Up Development Environment**
   ```bash
   # Install dependencies
   mvn clean install
   
   # Set up environment variables
   cp .env.example .env
   # Edit .env with your API keys
   ```

3. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   git checkout -b bugfix/issue-description
   git checkout -b enhancement/improvement-name
   ```

#### 🛠️ Development Guidelines

**Code Style & Standards**
- Follow Java coding conventions and Oracle style guide
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments for public methods
- Maintain consistent indentation (4 spaces)
- Keep classes focused and follow Single Responsibility Principle

**JavaFX Best Practices**
- Separate FXML layouts from controller logic
- Use CSS for styling and avoid inline styles
- Implement proper data binding with observables
- Handle UI updates on JavaFX Application Thread

**Database Guidelines**
- Use prepared statements to prevent SQL injection
- Follow database naming conventions (snake_case)
- Add appropriate indexes for performance
- Include proper foreign key constraints

**Testing Requirements**
- Write unit tests for service layer methods
- Include integration tests for database operations
- Test API integrations with mock services
- Maintain minimum 70% code coverage

#### 📋 Areas for Contribution

🚀 **High Priority Features**
- Multi-language support (Arabic, French)
- Offline mode and data synchronization
- Advanced analytics dashboard
- Mobile app companion integration
- Real-time notifications system

🔧 **Technical Improvements**
- Performance optimization and caching
- Enhanced error handling and logging
- Automated testing and CI/CD pipeline
- Code refactoring and documentation
- Security enhancements and penetration testing

🎨 **UI/UX Enhancements**
- Dark mode and accessibility features
- Custom themes and branding options
- Improved animations and transitions
- Responsive design improvements
- User experience research and optimization

🌍 **Localization & Internationalization**
- Arabic language support (RTL)
- French language localization
- Cultural adaptations for Tunisian market
- Currency and date formatting
- Regional payment method integration

### 📝 Contribution Process

#### 🔍 Before You Start
1. **Check Existing Issues**
   - Browse [open issues](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues)
   - Look for `good first issue` or `help wanted` labels
   - Comment on issues you'd like to work on

2. **Discuss Major Changes**
   - Open a discussion for significant features
   - Get feedback before starting large implementations
   - Ensure alignment with project roadmap

#### 🔨 Making Changes
1. **Code Implementation**
   ```bash
   # Make your changes
   mvn clean compile
   mvn test
   ```

2. **Testing & Validation**
   ```bash
   # Run all tests
   mvn clean test
   
   # Check code coverage
   mvn jacoco:report
   
   # Run integration tests
   mvn verify
   ```

3. **Documentation Updates**
   - Update JavaDoc comments
   - Modify README.md if needed
   - Add or update relevant wiki pages

#### 📤 Submitting Changes
1. **Commit Guidelines**
   ```bash
   # Use conventional commit format
   git commit -m "feat: add real-time chat functionality"
   git commit -m "fix: resolve payment processing bug"
   git commit -m "docs: update API integration guide"
   ```

2. **Pull Request Process**
   - Create descriptive PR title and description
   - Reference related issues with "Fixes #123"
   - Include screenshots for UI changes
   - Add reviewers and appropriate labels

3. **Code Review**
   - Address feedback promptly
   - Make requested changes in separate commits
   - Ensure CI/CD pipeline passes
   - Maintain clean commit history

### 🏆 Recognition & Rewards

**Contributors Hall of Fame**
- Featured in project README
- Special contributor badges
- Priority access to new features
- Invitation to project planning meetings

**Technical Growth**
- Mentorship opportunities
- Code review participation
- Architecture decision involvement
- Conference speaking opportunities

### 📞 Getting Help

**Development Support**
- 💬 **Discord**: Join our [developer community](https://discord.gg/wamiago)
- 📧 **Email**: dev-support@wamiago.com
- 📚 **Wiki**: [Developer Documentation](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/wiki)
- 🎥 **Video Guides**: [YouTube Channel](https://youtube.com/c/WamiaGo)

**Issue Templates**
- 🐛 [Bug Report](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=bug_report.md)
- ✨ [Feature Request](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=feature_request.md)
- 📖 [Documentation](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=documentation.md)

---

### 🎉 Recent Achievements

#### 🏆 Development Milestones
- ✅ **Core Architecture**: Established JavaFX-based desktop application
- ✅ **Database Integration**: MySQL connectivity with entity models
- ✅ **API Framework**: External API integration structure
- ✅ **UI Components**: Modern JavaFX interface with FXML layouts
- ✅ **Service Layer**: Business logic separation and organization

#### 📈 Technical Progress
- **Development Framework**: Complete JavaFX setup with Maven build
- **Core Services**: Basic service architecture implemented
- **Database Schema**: Entity models and database structure
- **API Integration**: Ready for external service integration
- **Modern UI**: Professional desktop application interface

### 🔮 Vision & Roadmap

#### 🌍 Project Vision
Develop a comprehensive desktop application for transportation management in Tunisia, integrating modern technologies like AI assistance, payment processing, and communication systems to create a unified platform for urban mobility services.

#### 🚀 Development Goals
- **Feature Completion**: Implement core transportation services
- **Technology Integration**: Connect external APIs for enhanced functionality
- **User Experience**: Create intuitive and responsive desktop interface
- **Code Quality**: Maintain clean, documented, and testable codebase
- **Community**: Build an open-source project with contributor support

---

## 🙏 Acknowledgments

### 🤝 Technology Partners

#### 🤖 AI & Machine Learning Services
- **[OpenAI](https://openai.com/)** - API for AI-powered assistance capabilities
- **[Microsoft Azure](https://azure.microsoft.com/)** - Speech Services for voice features
- **[Whisper API](https://openai.com/research/whisper)** - Audio transcription services

#### 💳 Payment Processing
- **Payment Integration** - Framework ready for Tunisian payment providers
- **PDF Generation** - Document creation for receipts and reports

#### 📱 Communication Services
- **[Twilio](https://www.twilio.com/)** - SMS and messaging API integration
- **Email Services** - SMTP integration for notifications

### 🛠️ Open Source Libraries

#### ☕ Java & JavaFX Ecosystem
- **[OpenJFX](https://openjfx.io/)** - Modern JavaFX framework for rich desktop applications
- **[ControlsFX](https://controlsfx.org/)** - Additional UI controls and features for JavaFX
- **[AnimateFX](https://github.com/Typhon0/AnimateFX)** - Beautiful animations and transitions
- **[Ikonli](https://kordamp.org/ikonli/)** - Icon packs integration (FontAwesome, Material Design)

#### 🗄️ Database & Persistence
- **[MySQL](https://www.mysql.com/)** - Reliable and scalable relational database
- **[HikariCP](https://github.com/brettwooldridge/HikariCP)** - High-performance JDBC connection pooling
- **[Flyway](https://flywaydb.org/)** - Database version control and migration management

#### 🌐 HTTP & API Integration
- **[OkHttp](https://square.github.io/okhttp/)** - Efficient HTTP client for API communications
- **[Jackson](https://github.com/FasterXML/jackson)** - JSON processing and object mapping
- **[Apache Commons](https://commons.apache.org/)** - Utility libraries for various functionalities

#### 🎨 UI & Design
- **[FontAwesome](https://fontawesome.com/)** - Comprehensive icon library for modern interfaces
- **[Material Design Icons](https://materialdesignicons.com/)** - Google's Material Design icon set
- **[CSS Themes](https://github.com/openjfx/javafx-gradle-plugin)** - Modern styling and theming

#### 📄 Document Generation & Processing
- **[iText PDF](https://itextpdf.com/)** - Professional PDF generation and manipulation
- **[ZXing](https://github.com/zxing/zxing)** - QR code generation and barcode processing
- **[Apache POI](https://poi.apache.org/)** - Microsoft Office document processing

### 💡 Inspiration & Motivation

This project was inspired by the need for sustainable, efficient, and intelligent transportation solutions in Tunisia. We believe technology can solve real-world problems while creating economic opportunities and environmental benefits for our community.

**Our mission**: To democratize access to modern transportation services while promoting environmental sustainability and supporting local economic growth.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### 📋 License Summary

```
MIT License

Copyright (c) 2024 WamiaGo Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### 🔓 What This Means

✅ **Commercial Use** - Use for commercial purposes  
✅ **Modification** - Modify and adapt the code  
✅ **Distribution** - Distribute original or modified versions  
✅ **Private Use** - Use privately for any purpose  
✅ **Patent Use** - Patent rights are granted

⚠️ **Conditions**  
📄 **License Notice** - Include copyright and license notice  
🔗 **Same License** - Include same license for distributions

❌ **Limitations**  
🛡️ **Liability** - No warranty or liability  
🔒 **Trademark** - No trademark rights granted

---

## 📞 Contact

### 👥 WamiaGo Team

#### 👨‍💻 Core Contributors

- **[Malek Bsaissa](https://github.com/AtfastrSlushyMaker)** - Bicycle & Station Management
- **[Ezer Abrougui](https://github.com/ezer2002)** - Transport Related Management  
- **[Farah Derbell](https://github.com/farahderbell)** - Taxi Related Management
- **[Abd Razek Nakhli](https://github.com/Rzouga01)** - User Related Management
- **[Mohamed Islem Ghazouani](https://github.com/islem201)** - Carpooling Related Management
- **[Walaa Eddine Ghrairi](https://github.com/walaaghrairi)** - Feedback Related Management

> **🎓 Academic Project Note**  
> This project is developed as part of an academic program at **[ESPRIT](https://esprit.tn/)** (École Supérieure Privée d'Ingénierie et de Technologies), Tunisia. All core contributors are currently students pursuing their engineering degrees in software development and information systems.

### 📧 Get in Touch

- **📮 Project Issues**: [Create an Issue](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new)
- **🐛 Bug Reports**: [Report Bug](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=bug_report.md)
- **💡 Feature Requests**: [Request Feature](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues/new?template=feature_request.md)
- **🤝 Contributions**: [Contributing Guidelines](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/blob/main/CONTRIBUTING.md)

### 🌐 Online Presence

- **🔗 GitHub**: [@AtfastrSlushyMaker/WamiaGo-Desktop](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop)
- **📚 Documentation**: [Project Wiki](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/wiki)
- **💭 Discussions**: [GitHub Discussions](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/discussions)

### 💬 Community Support

- **💭 Discussions**: [GitHub Discussions](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/discussions)
- **📚 Documentation**: [Project Wiki](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/wiki)
- **❓ Issues**: [GitHub Issues](https://github.com/AtfastrSlushyMaker/WamiaGo-Desktop/issues)

### 🏢 Project Information

**WamiaGo Desktop - Transportation Management Application**  
📍 **Development**: Open Source Project  
🌍 **Target Region**: Tunisia  
🎯 **Purpose**: Transportation and mobility management  
📅 **Started**: 2024  

---

<div align="center">
  
### 🌟 Thank You for Your Interest in WamiaGo Desktop!

**Made with ❤️ in Tunisia 🇹🇳**

*Transforming urban mobility, one ride at a time.*

---

<img src="https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=openjdk" alt="Java">
<img src="https://img.shields.io/badge/JavaFX-17-blue.svg?style=for-the-badge&logo=openjdk" alt="JavaFX">
<img src="https://img.shields.io/badge/MySQL-8.0-4479A1.svg?style=for-the-badge&logo=mysql" alt="MySQL">
<img src="https://img.shields.io/badge/OpenAI-GPT--3.5-412991.svg?style=for-the-badge&logo=openai" alt="OpenAI">
<img src="https://img.shields.io/badge/Maven-3.6+-red.svg?style=for-the-badge&logo=apache-maven" alt="Maven">
<img src="https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge" alt="License">
</div>

**⭐ Star us on GitHub** • **🍴 Fork the project** • **🐛 Report bugs** • **💡 Request features**

[⬆ Back to top](#wamiago-desktop)

</div>