# Security Configuration Guide

## Critical Security Setup

### 1. JWT Secret Configuration

**IMPORTANT**: The JWT secret MUST be changed before deploying to production.

#### Generate a Secure Secret

```bash
# Generate a 256-bit (32-byte) random secret
openssl rand -base64 32
```

#### Set Environment Variable

**For Local Development:**
```bash
# Create .env file in project root
echo "JWT_SECRET=$(openssl rand -base64 32)" > .env
```

**For Production (AWS):**
```bash
# Store in AWS Secrets Manager
aws secretsmanager create-secret \
  --name habit-tracker/jwt-secret \
  --secret-string "$(openssl rand -base64 32)"

# Or use AWS Systems Manager Parameter Store
aws ssm put-parameter \
  --name /habit-tracker/jwt-secret \
  --type SecureString \
  --value "$(openssl rand -base64 32)"
```

**For Production (Docker):**
```bash
# Set in docker-compose.yml or pass as environment variable
docker run -e JWT_SECRET="your-secure-secret-here" ...
```

### 2. JWT Token Expiration

The default token expiration has been changed from **24 hours to 1 hour** for improved security.

**Current Configuration:**
- Development: 1 hour (3600000 ms)
- Production: 1 hour (recommended)

**To customize:**
```bash
# Set in .env file
JWT_EXPIRATION=3600000  # 1 hour in milliseconds
```

**Common Values:**
- 15 minutes: `900000`
- 30 minutes: `1800000`
- 1 hour: `3600000`
- 2 hours: `7200000`

### 3. Database Security

**Current Status:**
- ✅ Passwords use BCrypt hashing
- ✅ Database credentials use environment variables
- ⚠️ SSL not enabled for database connections

**Production Recommendations:**

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=true&requireSSL=true&verifyServerCertificate=true
```

### 4. CORS Configuration

**Current Configuration:**
```yaml
app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:3000}
```

**Production Setup:**
```bash
# .env file
CORS_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

**Never use `*` (wildcard) in production!**

### 5. Rate Limiting

**Status**: ⚠️ To be implemented in Phase 1, Week 1

Will include:
- Login endpoint: 5 attempts per 15 minutes
- API endpoints: 100 requests per minute
- Registration: 3 attempts per hour

### 6. Input Validation & Sanitization

**Current Status:**
- ✅ Bean Validation (`@Valid`) annotations in place
- ⚠️ XSS protection to be added

**Recommendations:**
- Use OWASP Java Encoder for output encoding
- Implement Content Security Policy (CSP) headers
- Enable CSRF protection for non-API endpoints

## Security Checklist for Deployment

### Before Production Deployment

- [ ] Generate and set secure JWT secret (256-bit minimum)
- [ ] Change all default passwords
- [ ] Enable database SSL/TLS connections
- [ ] Configure proper CORS origins (no wildcards)
- [ ] Implement rate limiting
- [ ] Enable HTTPS/TLS for application
- [ ] Add security headers (CSP, HSTS, X-Frame-Options)
- [ ] Disable debug logging in production
- [ ] Disable Swagger UI in production (or restrict access)
- [ ] Implement monitoring and alerting
- [ ] Set up automated security scanning
- [ ] Configure database backups
- [ ] Enable Spring Boot Actuator with authentication
- [ ] Review and update dependencies for vulnerabilities
- [ ] Implement account lockout after failed login attempts

### Environment Variables Required

Minimum required environment variables:

```bash
# Security
JWT_SECRET=<generated-256-bit-key>
JWT_EXPIRATION=3600000

# Database
DB_USERNAME=<secure-username>
DB_PASSWORD=<strong-password>

# CORS
CORS_ORIGINS=https://yourdomain.com
```

## Security Vulnerabilities Fixed

### Version 1.1.0 (Current)

✅ **CRITICAL: Weak JWT Secret**
- **Issue**: Default secret "mySecretKey" was too weak
- **Fix**: Now requires environment variable, clear warning in default
- **Impact**: Prevents JWT token forgery

✅ **HIGH: Long Token Expiration**
- **Issue**: 24-hour token expiration too long
- **Fix**: Reduced to 1 hour by default
- **Impact**: Reduces window for token theft exploitation

✅ **MEDIUM: Hardcoded Configuration**
- **Issue**: Sensitive values in configuration files
- **Fix**: All sensitive values now use environment variables
- **Impact**: Prevents credential leakage in version control

## Ongoing Security Improvements

### Planned (Phase 1)

- [ ] Implement refresh token mechanism
- [ ] Add rate limiting with Bucket4j
- [ ] Global exception handler (prevents information leakage)
- [ ] Enhanced input validation and sanitization
- [ ] Security audit logging

### Planned (Phase 2)

- [ ] Multi-factor authentication (MFA)
- [ ] Account lockout mechanism
- [ ] Email verification for new accounts
- [ ] Password strength requirements
- [ ] Session management improvements

### Planned (Phase 3)

- [ ] Automated security scanning in CI/CD
- [ ] Penetration testing
- [ ] GDPR compliance features
- [ ] SOC 2 compliance preparation

## Reporting Security Vulnerabilities

If you discover a security vulnerability, please email security@habittracker.com (or create a private issue).

**Do NOT** create public issues for security vulnerabilities.

## Security Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)

## Compliance & Standards

This application follows:
- OWASP Top 10 security guidelines
- JWT RFC 8725 best practices
- Spring Security best practices
- Industry-standard password hashing (BCrypt)

## Last Updated

- **Date**: November 21, 2025
- **Version**: 1.1.0
- **Reviewed by**: Security audit during Phase 1 implementation
