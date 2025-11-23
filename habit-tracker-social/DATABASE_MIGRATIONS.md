# Database Migrations Guide

## Overview

This project uses **Flyway** for database schema versioning and migrations. Flyway automatically manages database changes in a controlled, versioned manner.

## Why Flyway?

✅ **Version Control**: Track all database changes like code
✅ **Repeatable**: Same migrations work across all environments
✅ **Safe**: Prevents data loss from schema changes
✅ **Automated**: Runs automatically on application startup
✅ **Production-Ready**: Industry standard for database migrations

## Migration Files Location

```
src/main/resources/db/migration/
├── V1__Initial_schema.sql
├── V2__Add_performance_indexes.sql
└── V3__Your_next_migration.sql
```

## Naming Convention

Flyway migrations follow a strict naming pattern:

```
V{VERSION}__{DESCRIPTION}.sql
```

**Examples**:
- `V1__Initial_schema.sql`
- `V2__Add_performance_indexes.sql`
- `V3__Add_user_preferences.sql`
- `V4__Alter_habits_add_color.sql`

**Rules**:
- Version must be unique and sequential (V1, V2, V3...)
- Two underscores (`__`) separate version from description
- Use underscores (`_`) for spaces in description
- Description should be clear and concise
- Once applied, NEVER modify existing migrations

## Configuration

Flyway is configured in `application.yml`:

```yaml
spring:
  flyway:
    enabled: true                    # Enable Flyway
    baseline-on-migrate: true        # Handle existing databases
    baseline-version: 0              # Starting version for baseline
    locations: classpath:db/migration # Migration files location
    validate-on-migrate: true        # Validate before applying
```

## Hibernate Configuration

**IMPORTANT**: Hibernate DDL auto is now set to `validate` (changed from `update`):

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Validates schema but makes NO changes
```

This means:
- ✅ Hibernate validates entities match database schema
- ❌ Hibernate will NOT create/modify tables
- ✅ All schema changes MUST go through Flyway migrations

## Creating a New Migration

### Step 1: Determine the Version Number

Check existing migrations to find the next version:

```bash
ls -la src/main/resources/db/migration/
# V1__Initial_schema.sql
# V2__Add_performance_indexes.sql
# Next should be V3
```

### Step 2: Create the Migration File

Create a new file with the appropriate version and description:

```bash
# Example: Adding a new column
touch src/main/resources/db/migration/V3__Add_habit_color_column.sql
```

### Step 3: Write the Migration SQL

```sql
-- V3__Add_habit_color_column.sql
-- Description: Add color column to habits table for UI customization
-- Author: Your Name
-- Date: 2025-11-21

ALTER TABLE habits
ADD COLUMN color VARCHAR(7) DEFAULT '#3B82F6' COMMENT 'Hex color code for habit display';

-- Add index if this column will be queried frequently
CREATE INDEX idx_habits_color ON habits(color);

-- Update existing records if needed
UPDATE habits SET color = '#3B82F6' WHERE color IS NULL;
```

### Step 4: Test the Migration

**Local Testing**:
```bash
# Start the application
mvn spring-boot:run

# Flyway will automatically:
# 1. Check for new migrations
# 2. Apply V3__Add_habit_color_column.sql
# 3. Update flyway_schema_history table
```

**Check Migration Status**:
```sql
-- Connect to MySQL
mysql -u root -p habit_tracker

-- View migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Step 5: Verify the Migration

```sql
-- Check that column was added
DESCRIBE habits;

-- Verify data
SELECT id, name, color FROM habits LIMIT 5;
```

## Common Migration Scenarios

### Adding a New Table

```sql
-- V3__Create_user_preferences_table.sql

CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    theme VARCHAR(20) DEFAULT 'light',
    notifications_enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_preferences_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Adding a Column

```sql
-- V4__Add_habit_icon_field.sql

ALTER TABLE habits
ADD COLUMN icon VARCHAR(50) DEFAULT 'default'
COMMENT 'Icon identifier for habit display';

-- Backfill existing records
UPDATE habits SET icon = 'default' WHERE icon IS NULL;

-- Make non-nullable after backfill (optional)
ALTER TABLE habits MODIFY COLUMN icon VARCHAR(50) NOT NULL DEFAULT 'default';
```

### Modifying a Column

```sql
-- V5__Increase_notification_message_length.sql

ALTER TABLE notifications
MODIFY COLUMN message VARCHAR(2000) NOT NULL
COMMENT 'Increased from 1000 to 2000 characters';
```

### Adding an Index

```sql
-- V6__Add_composite_index_habits.sql

CREATE INDEX idx_habits_user_created
ON habits(user_id, created_at DESC);

ANALYZE TABLE habits;
```

### Renaming a Column

```sql
-- V7__Rename_habit_completion_value.sql

ALTER TABLE habit_completions
CHANGE COLUMN completion_value value INT
COMMENT 'Renamed from completion_value for brevity';
```

### Adding Foreign Key

```sql
-- V8__Add_category_foreign_key.sql

ALTER TABLE habits
ADD CONSTRAINT fk_habits_category
FOREIGN KEY (category_id) REFERENCES categories(id)
ON DELETE SET NULL;
```

### Data Migration

```sql
-- V9__Migrate_legacy_habit_types.sql

-- Update old enum values to new structure
UPDATE habits SET type = 'HEALTH_FITNESS'
WHERE type = 'Health & Fitness';

UPDATE habits SET type = 'PRODUCTIVITY'
WHERE type = 'Work';

-- Verify migration
SELECT type, COUNT(*)
FROM habits
GROUP BY type;
```

## Rolling Back Migrations

**IMPORTANT**: Flyway migrations are **one-way by default**.

### Undo Migrations (Enterprise Feature)

For Flyway Enterprise/Teams editions, you can create undo migrations:

```sql
-- U3__Undo_add_habit_color_column.sql

ALTER TABLE habits DROP COLUMN color;
DROP INDEX idx_habits_color ON habits;
```

### Manual Rollback (Community Edition)

If you need to undo a migration:

1. **Stop the application**
2. **Create a new forward migration that reverses the changes**:

```sql
-- V10__Remove_habit_color_column.sql
-- Reverses V3__Add_habit_color_column.sql

ALTER TABLE habits DROP COLUMN color;
DROP INDEX IF EXISTS idx_habits_color ON habits;
```

3. **Update flyway_schema_history** (if needed):

```sql
-- Only if migration failed mid-way
DELETE FROM flyway_schema_history WHERE version = '3';
```

## Best Practices

### ✅ DO

1. **Always test migrations locally first**
2. **Use transactions** (most DDL in MySQL auto-commits, but DML doesn't):
   ```sql
   START TRANSACTION;
   -- your changes
   COMMIT;
   ```
3. **Include rollback plan** in migration comments
4. **Add indexes** for new foreign keys
5. **Backfill data** before making columns NOT NULL
6. **Use descriptive names**: `V5__Add_user_avatar_url.sql` not `V5__Update.sql`
7. **Keep migrations small** and focused on one change
8. **Document breaking changes** in migration comments

### ❌ DON'T

1. **Never modify** existing migrations after they've been applied
2. **Never delete** migration files
3. **Don't skip versions**: Use sequential numbers (V1, V2, V3, not V1, V3, V5)
4. **Don't use** timestamps in version numbers
5. **Avoid large data migrations** in schema migrations (use separate scripts)
6. **Don't rename** existing migration files
7. **Don't commit broken** migrations to version control

## Migration Workflow

### Development Environment

```bash
# 1. Create migration file
touch src/main/resources/db/migration/V3__Your_change.sql

# 2. Write SQL
vim src/main/resources/db/migration/V3__Your_change.sql

# 3. Test locally
mvn spring-boot:run

# 4. Verify
mysql -u root -p habit_tracker -e "SELECT * FROM flyway_schema_history;"

# 5. Commit to git
git add src/main/resources/db/migration/V3__Your_change.sql
git commit -m "feat: add V3 migration for ..."
```

### Production Deployment

```bash
# 1. Pull latest code
git pull origin main

# 2. Backup database FIRST
mysqldump -u root -p habit_tracker > backup_$(date +%Y%m%d_%H%M%S).sql

# 3. Stop application (optional, for zero-downtime use blue-green)
systemctl stop habit-tracker

# 4. Build application
mvn clean package -DskipTests

# 5. Start application (Flyway runs automatically)
java -jar target/habit-tracker-social-1.0.0.jar

# 6. Verify migration
mysql -u root -p habit_tracker -e "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"

# 7. Smoke test application
curl http://localhost:8080/api/health
```

## Troubleshooting

### Migration Fails

**Error**: `Migration checksum mismatch`

**Cause**: Migration file was modified after being applied

**Solution**:
```sql
-- Option 1: Repair (if file unchanged in reality)
-- Set FLYWAY_REPAIR=true and restart

-- Option 2: Manual fix
UPDATE flyway_schema_history
SET checksum = <new_checksum>
WHERE version = 'X';
```

### Schema Validation Error

**Error**: `Schema validation failed`

**Cause**: Entity doesn't match database schema

**Solution**:
```bash
# Check what's different
mysql -u root -p habit_tracker -e "DESCRIBE table_name;"

# Create migration to fix
touch src/main/resources/db/migration/V<next>__Fix_schema_mismatch.sql
```

### Baseline Existing Database

**Error**: `Found non-empty schema without metadata table`

**Cause**: Database exists but Flyway never ran

**Solution**:
```yaml
# In application.yml
spring:
  flyway:
    baseline-on-migrate: true  # Already configured!
```

## Monitoring Migrations

### Check Migration Status

```sql
SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    checksum,
    installed_on,
    execution_time,
    success
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

### Failed Migrations

```sql
SELECT *
FROM flyway_schema_history
WHERE success = 0;
```

## CI/CD Integration

### GitHub Actions

Already configured in `.github/workflows/ci-cd.yml`:

```yaml
- name: Run Backend Tests
  run: |
    cd habit-tracker-social
    mvn clean test
  # Flyway migrations run automatically during tests
```

### Production Deployment

```bash
# Dry-run migration check
mvn flyway:info -Dflyway.url=jdbc:mysql://prod-db:3306/habit_tracker

# Validate migrations
mvn flyway:validate

# Apply migrations
mvn flyway:migrate
```

## Environment-Specific Migrations

### Development Data Seeds

```sql
-- V99__Dev_seed_data.sql
-- Only run in development environment

-- Check environment
SET @env = (SELECT IF(DATABASE() = 'habit_tracker_dev', 1, 0));

-- Conditional insert
INSERT INTO users (username, email, password, role, created_at)
SELECT 'testuser', 'test@example.com', '$2a$10$...', 'USER', NOW()
WHERE @env = 1;
```

## Migration Checklist

Before committing a migration:

- [ ] Migration follows naming convention (V{VERSION}__{DESCRIPTION}.sql)
- [ ] Version number is sequential
- [ ] Migration is idempotent (can run multiple times safely)
- [ ] Tested locally with fresh database
- [ ] Tested locally with existing data
- [ ] Rollback plan documented in comments
- [ ] Breaking changes documented
- [ ] Indexes added for new foreign keys
- [ ] Data backfilled before making columns NOT NULL
- [ ] Performance impact considered (for large tables)

## Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Best Practices](https://flywaydb.org/documentation/bestpractices)
- [MySQL Migration Guide](https://dev.mysql.com/doc/refman/8.0/en/alter-table.html)

## Support

For migration issues:
1. Check this guide first
2. Review Flyway logs in application output
3. Check `flyway_schema_history` table
4. Consult team lead or senior developer

---

**Last Updated**: November 21, 2025
**Flyway Version**: 9.x (via Spring Boot 3.2.0)
**Database**: MySQL 8.0
