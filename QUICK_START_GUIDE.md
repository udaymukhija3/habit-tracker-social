# Quick Start Guide - Phase 1 Week 1 Features

## Getting Started

### 1. Set Up Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# Generate a secure JWT secret
openssl rand -base64 32

# Edit .env and add your generated secret
nano .env
```

**Minimum Required Variables:**

```bash
# .env
JWT_SECRET=your-generated-256-bit-secret-here
JWT_EXPIRATION=3600000
DB_USERNAME=root
DB_PASSWORD=password
CORS_ORIGINS=http://localhost:3000
```

### 2. Start the Application

```bash
# Navigate to backend directory
cd habit-tracker-social

# Build and run
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

**Expected Output:**

```
Flyway migration started...
Flyway migration completed successfully
Application started on port 8080
```

### 3. Verify Installation

```bash
# Check health endpoint
curl http://localhost:8080/api/actuator/health

# Expected response:
# {
#   "status": "UP",
#   "components": {
#     "database": { "status": "UP" },
#     "redis": { "status": "UP" }
#   }
# }

# Check application info
curl http://localhost:8080/api/actuator/info

# Test rate limiting (should succeed)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

---

## Feature Testing Guide

### 1. Rate Limiting

**Test Authentication Rate Limit (5 requests per 15 minutes):**

```bash
# Send 6 requests rapidly
for i in {1..6}; do
  echo "Request $i:"
  curl -i -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test"}'
  echo ""
done

# Request 6 should return:
# HTTP/1.1 429 Too Many Requests
# X-RateLimit-Limit: 5
# X-RateLimit-Remaining: 0
# Retry-After: 900
```

**Check Rate Limit Headers:**

```bash
curl -i http://localhost:8080/api/health

# Response headers should include:
# X-RateLimit-Limit: 100
# X-RateLimit-Remaining: 99
# X-RateLimit-Reset: 1700000000
```

### 2. Database Migrations

**Check Migration Status:**

```bash
# Connect to MySQL
mysql -u root -p habit_tracker

# View migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

# Expected output:
# | installed_rank | version | description              | success |
# |----------------|---------|--------------------------|---------|
# | 1              | 1       | Initial schema           | 1       |
# | 2              | 2       | Add performance indexes  | 1       |
```

**Verify Tables Created:**

```bash
mysql -u root -p habit_tracker -e "SHOW TABLES;"

# Expected tables:
# users
# habits
# habit_completions
# streaks
# friendships
# user_friends
# competitions
# competition_participants
# notifications
# flyway_schema_history
```

### 3. Security Headers

**Check Security Headers:**

```bash
curl -I http://localhost:8080/api/health

# Should include headers:
# Content-Security-Policy: default-src 'self'; ...
# X-Frame-Options: DENY
# X-Content-Type-Options: nosniff
# X-XSS-Protection: 1; mode=block
# Referrer-Policy: strict-origin-when-cross-origin
```

### 4. Global Exception Handler

**Test Validation Error:**

```bash
# Send invalid registration data
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{}'

# Expected response:
# {
#   "timestamp": "2025-11-21T10:30:00",
#   "status": 400,
#   "error": "Validation Failed",
#   "message": "Input validation error",
#   "validationErrors": {
#     "username": "Username is required",
#     "email": "Email is required",
#     "password": "Password is required"
#   }
# }
```

**Test Authentication Error:**

```bash
# Send wrong credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"wrong","password":"wrong"}'

# Expected response:
# {
#   "timestamp": "...",
#   "status": 401,
#   "error": "Authentication Failed",
#   "message": "Invalid username or password"
# }
```

### 5. Actuator Monitoring

**Health Check (Public):**

```bash
curl http://localhost:8080/api/actuator/health
```

**Application Info (Public):**

```bash
curl http://localhost:8080/api/actuator/info

# Expected response:
# {
#   "app": {
#     "name": "habit-tracker-social",
#     "description": "Comprehensive habit tracking application...",
#     "version": "1.0.0"
#   }
# }
```

**Metrics (Requires Authentication):**

```bash
# First, login to get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"youruser","password":"yourpass"}' \
  | jq -r '.token')

# Then access metrics
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/actuator/metrics
```

**Prometheus Metrics (Requires Authentication):**

```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/actuator/prometheus
```

### 6. Input Sanitization

The `InputSanitizer` utility is available for use in your code:

```java
import com.habittracker.util.InputSanitizer;

// Sanitize HTML content
String safe = InputSanitizer.sanitizeHtml(userInput);

// Validate and sanitize username
String username = InputSanitizer.validateAndSanitizeUsername(rawUsername);

// Validate and sanitize email
String email = InputSanitizer.validateAndSanitizeEmail(rawEmail);

// Check for XSS
if (InputSanitizer.containsXss(input)) {
    throw new IllegalArgumentException("Dangerous input detected");
}
```

### 7. CORS Configuration

**Test from Allowed Origin:**

```bash
# From http://localhost:3000 (allowed)
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:8080/api/auth/login

# Should return:
# Access-Control-Allow-Origin: http://localhost:3000
# Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH
```

**Test from Disallowed Origin:**

```bash
# From http://evil.com (not allowed)
curl -H "Origin: http://evil.com" \
     -X POST \
     http://localhost:8080/api/auth/login

# Should be blocked by browser (no Access-Control-Allow-Origin header)
```

---

## Common Issues & Solutions

### Issue 1: Flyway Migration Failed

**Error**: `FlywayException: Found non-empty schema without metadata table`

**Solution**:

```yaml
# In application.yml, baseline-on-migrate is already enabled
spring:
  flyway:
    baseline-on-migrate: true  # Handles existing databases
```

If still failing, manually baseline:

```bash
mvn flyway:baseline
```

### Issue 2: JWT Secret Not Set

**Error**: `JWT secret is not configured`

**Solution**:

```bash
# Generate secret
openssl rand -base64 32

# Set environment variable
export JWT_SECRET="your-generated-secret"

# Or add to .env file
echo "JWT_SECRET=your-generated-secret" >> .env
```

### Issue 3: Rate Limit Too Restrictive

**Error**: Getting 429 Too Many Requests during development

**Solution**:

```java
// In RateLimitService.java, temporarily increase limits for development
private Bucket createAuthBucket() {
    Bandwidth limit = Bandwidth.classic(
            50,  // Increased from 5
            Refill.intervally(50, Duration.ofMinutes(15))
    );
    return Bucket.builder().addLimit(limit).build();
}
```

Or clear rate limits:

```java
// Inject RateLimitService in a controller
@PostMapping("/admin/clear-rate-limits")
public void clearRateLimits() {
    rateLimitService.clearAllCaches();
}
```

### Issue 4: CORS Blocking Requests

**Error**: Browser console shows CORS error

**Solution**:

```bash
# Add your frontend origin to .env
CORS_ORIGINS=http://localhost:3000,http://localhost:3001
```

Or for development only:

```yaml
# application-dev.yml
app:
  cors:
    allowed-origins: "*"  # NEVER use in production!
```

### Issue 5: Database Connection Failed

**Error**: `DatabaseHealthIndicator` shows DOWN

**Solution**:

```bash
# Check MySQL is running
mysql -u root -p

# Verify connection details in .env
DB_USERNAME=root
DB_PASSWORD=your-password
```

---

## Useful Commands

### Development

```bash
# Run tests
mvn test

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=development

# Skip tests
mvn spring-boot:run -DskipTests

# Build JAR
mvn clean package

# Run built JAR
java -jar target/habit-tracker-social-1.0.0.jar
```

### Database

```bash
# Connect to database
mysql -u root -p habit_tracker

# View migration history
SELECT * FROM flyway_schema_history;

# Describe table structure
DESCRIBE users;

# Count records
SELECT COUNT(*) FROM users;
```

### Docker

```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up --build
```

### Monitoring

```bash
# Watch health status
watch -n 5 'curl -s http://localhost:8080/api/actuator/health | jq'

# Monitor rate limits
watch -n 1 'curl -i http://localhost:8080/api/health 2>&1 | grep RateLimit'

# View application logs
tail -f logs/habit-tracker.log
```

---

## Production Deployment Checklist

Before deploying to production:

- [ ] Set secure JWT_SECRET (256-bit minimum)
- [ ] Configure production CORS origins (no wildcards!)
- [ ] Enable HTTPS/TLS
- [ ] Set up database backups
- [ ] Configure log rotation
- [ ] Set up monitoring alerts (Prometheus + Grafana)
- [ ] Test all actuator endpoints
- [ ] Verify rate limits are appropriate
- [ ] Run security scan
- [ ] Load test the application
- [ ] Document rollback procedure
- [ ] Create disaster recovery plan

---

## Additional Resources

- **Security Guide**: `SECURITY.md`
- **Database Migrations**: `DATABASE_MIGRATIONS.md`
- **Full Analysis**: `CODEBASE_ANALYSIS_REPORT.md`
- **Implementation Summary**: `PHASE1_WEEK1_IMPLEMENTATION_SUMMARY.md`

---

## Support

If you encounter issues:

1. Check this guide's "Common Issues & Solutions" section
2. Review the security and database migration documentation
3. Check application logs: `logs/habit-tracker.log`
4. Verify environment variables are set correctly
5. Ensure all services (MySQL, Redis) are running

---

**Last Updated**: November 21, 2025
**Version**: 1.0 (Phase 1, Week 1)
