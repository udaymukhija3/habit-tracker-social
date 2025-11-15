# Habit Tracker Social

A comprehensive habit tracking application with social features, built with Spring Boot and React.

---

## ⚠️ WORK IN PROGRESS

**Status**: Active Development
**Last Updated**: 2025-11-15

### TODO List
- [ ] Complete mobile app integration and testing
- [ ] Implement advanced analytics dashboard
- [ ] Add habit templates and suggestions
- [ ] Integrate fitness tracker APIs
- [ ] Enhance real-time notification system
- [ ] Add comprehensive unit and integration tests
- [ ] Deploy to production environment
- [ ] Set up monitoring and logging infrastructure

**Note**: This project requires continued development. See the Roadmap section below for planned features.

---

## 🚀 Features

### Core Features
- **Habit Management**: Create, track, and manage habits with different types and frequencies
- **Streak Tracking**: Automatic streak calculation with milestone notifications
- **Social Features**: Friend system, friend requests, and social connections
- **Competitions**: Create and join habit-based competitions
- **Notifications**: Multi-channel notifications (Email, SMS, Push)
- **Real-time Updates**: WebSocket support for live notifications

### Habit Types
- Health & Fitness
- Productivity
- Learning & Education
- Social & Relationships
- Financial
- Mindfulness & Wellness
- Creative & Hobbies
- Maintenance & Chores

## 🏗️ Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0 with Java 17
- **Security**: Spring Security with JWT authentication
- **Database**: MySQL for primary data, Redis for caching, MongoDB for analytics
- **Design Patterns**: Factory, Decorator, Observer, Strategy, Template Method
- **API Documentation**: Swagger/OpenAPI 3.0
- **Real-time**: WebSocket support

### Frontend (React)
- **Framework**: React 18 with TypeScript
- **UI Library**: Material-UI (MUI)
- **State Management**: React Query for server state
- **Routing**: React Router
- **Real-time**: Socket.io client

## 🛠️ Technology Stack

### Backend
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Spring WebSocket
- MySQL 8.0
- Redis 7
- MongoDB 7
- JWT (jjwt)
- Swagger/OpenAPI

### Frontend
- React 18
- TypeScript
- Material-UI
- React Query
- React Router
- Axios
- Socket.io Client

## 📦 Installation & Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7
- MongoDB 7

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd habit-tracker-social
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - API Documentation: http://localhost:8080/swagger-ui.html

### Manual Setup

#### Backend Setup
1. **Navigate to backend directory**
   ```bash
   cd habit-tracker-social
   ```

2. **Configure database**
   - Update `application.yml` with your database credentials
   - Create MySQL database: `habit_tracker`

3. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

#### Frontend Setup
1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   ```bash
   cp .env.example .env
   # Update REACT_APP_API_URL to point to your backend
   ```

4. **Start development server**
   ```bash
   npm start
   ```

## 🔧 Configuration

### Environment Variables

#### Backend
```yaml
DB_USERNAME: your_db_username
DB_PASSWORD: your_db_password
REDIS_HOST: localhost
REDIS_PORT: 6379
MONGODB_URI: mongodb://localhost:27017/habit_tracker_analytics
JWT_SECRET: your_jwt_secret
```

#### Frontend
```env
REACT_APP_API_URL=http://localhost:8080/api
```

## 📚 API Documentation

Once the backend is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

#### Habits
- `GET /api/habits` - Get user habits
- `POST /api/habits` - Create habit
- `PUT /api/habits/{id}` - Update habit
- `DELETE /api/habits/{id}` - Delete habit
- `POST /api/habits/{id}/complete` - Complete habit

#### Social Features
- `GET /api/friends` - Get friends
- `POST /api/friends/request/{userId}` - Send friend request
- `POST /api/friends/accept/{friendshipId}` - Accept friend request

#### Notifications
- `GET /api/notifications` - Get notifications
- `POST /api/notifications/{id}/read` - Mark as read

## 🎨 Design Patterns

### Factory Pattern
- `HabitFactory` - Creates type-specific habits with default configurations

### Decorator Pattern
- `HabitDecorator` - Adds functionality to habits
- `ReminderHabitDecorator` - Adds reminder functionality
- `RewardHabitDecorator` - Adds reward functionality

### Observer Pattern
- `HabitSubject` - Notifies observers of habit changes
- `StreakObserver` - Handles streak-related notifications

### Strategy Pattern
- `NotificationStrategy` - Different notification channels
- `StreakCalculator` - Different streak calculation algorithms

### Template Method
- `StreakCalculator` - Defines algorithm structure with customizable steps

## 🧪 Testing

### Backend Tests
```bash
cd habit-tracker-social
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Production Build

#### Backend
```bash
mvn clean package
java -jar target/habit-tracker-social-1.0.0.jar
```

#### Frontend
```bash
npm run build
# Serve the build directory with a web server
```

### Docker Deployment
```bash
docker-compose -f docker-compose.prod.yml up -d
```

## 📱 Features Overview

### Dashboard
- Overview of habits, streaks, and achievements
- Quick actions and statistics
- Recent activity feed

### Habit Management
- Create habits with custom types and frequencies
- Track completions and streaks
- Visual progress indicators
- Habit analytics

### Social Features
- Friend system with requests and management
- Social feed of friend activities
- Friend-based challenges

### Competitions
- Create and join habit competitions
- Leaderboards and rankings
- Time-based challenges

### Notifications
- Real-time notifications
- Multiple notification channels
- Notification preferences

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support, email support@habittracker.com or create an issue in the repository.

## 🔮 Roadmap

- [ ] Mobile app (React Native)
- [ ] Advanced analytics and insights
- [ ] Habit templates and suggestions
- [ ] Integration with fitness trackers
- [ ] Team challenges and group habits
- [ ] AI-powered habit recommendations
