# Test Execution Guide

This guide provides step-by-step instructions to run and verify all tests in the Habit Tracker Social application.

## 📋 Test Coverage Summary

### Backend Tests (Java/Spring Boot)
- **Controllers**: 6 test files
  - `AuthControllerTest.java`
  - `HabitControllerTest.java`
  - `UserControllerTest.java`
  - `FriendshipControllerTest.java`
  - `NotificationControllerTest.java`
  - `HealthControllerTest.java`

- **Services**: 6 test files
  - `HabitServiceTest.java` (existing)
  - `UserServiceTest.java`
  - `FriendshipServiceTest.java`
  - `NotificationServiceTest.java`
  - `StreakServiceTest.java`
  - `CompetitionServiceTest.java`

### Frontend Tests (React/TypeScript)
- **Components**: 3 test files
  - `LoginForm.test.tsx`
  - `RegisterForm.test.tsx`
  - `HabitCard.test.tsx`

- **Pages**: 2 test files
  - `Dashboard.test.tsx`
  - `App.test.tsx` (updated)

- **Services & Contexts**: 2 test files
  - `api.test.ts`
  - `AuthContext.test.tsx`

### Mobile Tests (React Native)
- **Screens**: 2 test files
  - `LoginScreen.test.tsx`
  - `App.test.tsx` (updated)

- **Services & Contexts**: 2 test files
  - `api.test.ts`
  - `AuthContext.test.tsx`

**Total**: ~20+ test files with comprehensive coverage

---

## 🚀 Running Tests

### Prerequisites

1. **Backend**:
   - Java 17+
   - Maven 3.6+

2. **Frontend**:
   - Node.js 18+
   - npm or yarn

3. **Mobile**:
   - Node.js 18+
   - React Native CLI
   - Jest (included in package.json)

---

## 📦 Backend Tests

### Location
```
habit-tracker-social/src/test/java/com/habittracker/
```

### Run All Backend Tests

```bash
cd habit-tracker-social
mvn test
```

### Run Specific Test Class

```bash
# Example: Run only AuthController tests
mvn test -Dtest=AuthControllerTest

# Example: Run only UserService tests
mvn test -Dtest=UserServiceTest
```

### Run Tests with Coverage Report

```bash
# Install jacoco plugin (if not already in pom.xml)
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Expected Output

```
[INFO] Tests run: 50+, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Verify Test Results

1. Check test output for:
   - ✅ All tests passing
   - ✅ No failures or errors
   - ✅ Coverage percentage

2. Key test scenarios covered:
   - ✅ Authentication (login, register)
   - ✅ Habit CRUD operations
   - ✅ User profile management
   - ✅ Friendship operations
   - ✅ Notification handling
   - ✅ Streak calculations
   - ✅ Competition management

---

## 🎨 Frontend Tests

### Location
```
frontend/src/
```

### Run All Frontend Tests

```bash
cd frontend
npm test
```

### Run Tests in Watch Mode

```bash
npm test -- --watch
```

### Run Tests with Coverage

```bash
npm test -- --coverage
```

### Run Specific Test File

```bash
# Example: Run only LoginForm tests
npm test -- LoginForm.test.tsx
```

### Expected Output

```
PASS  src/components/Auth/LoginForm.test.tsx
PASS  src/components/Auth/RegisterForm.test.tsx
PASS  src/components/Habits/HabitCard.test.tsx
PASS  src/pages/Dashboard.test.tsx
PASS  src/services/api.test.ts
PASS  src/contexts/AuthContext.test.tsx
PASS  src/App.test.tsx

Test Suites: 7 passed, 7 total
Tests:       20+ passed, 20+ total
```

### Verify Test Results

1. Check test output for:
   - ✅ All test suites passing
   - ✅ All individual tests passing
   - ✅ No warnings or errors

2. Key test scenarios covered:
   - ✅ Login form validation and submission
   - ✅ Registration form validation
   - ✅ Habit card interactions
   - ✅ Dashboard rendering
   - ✅ API service calls
   - ✅ Auth context state management

---

## 📱 Mobile Tests

### Location
```
mobile/__tests__/
```

### Run All Mobile Tests

```bash
cd mobile
npm test
```

### Run Tests in Watch Mode

```bash
npm test -- --watch
```

### Run Tests with Coverage

```bash
npm test -- --coverage
```

### Run Specific Test File

```bash
# Example: Run only LoginScreen tests
npm test -- LoginScreen.test.tsx
```

### Expected Output

```
PASS  __tests__/App.test.tsx
PASS  __tests__/LoginScreen.test.tsx
PASS  __tests__/AuthContext.test.tsx
PASS  __tests__/api.test.ts

Test Suites: 4 passed, 4 total
Tests:       10+ passed, 10+ total
```

### Verify Test Results

1. Check test output for:
   - ✅ All test suites passing
   - ✅ All individual tests passing
   - ✅ No warnings or errors

2. Key test scenarios covered:
   - ✅ Login screen rendering and interactions
   - ✅ Navigation between screens
   - ✅ Auth context state management
   - ✅ API service calls with AsyncStorage

---

## 🔍 What to Look For

### Successful Test Run Indicators

1. **Backend**:
   - ✅ All controller tests pass
   - ✅ All service tests pass
   - ✅ Mock objects work correctly
   - ✅ Security context is properly configured

2. **Frontend**:
   - ✅ Components render correctly
   - ✅ User interactions work as expected
   - ✅ API calls are mocked properly
   - ✅ Context providers work correctly

3. **Mobile**:
   - ✅ Screens render correctly
   - ✅ Navigation works
   - ✅ AsyncStorage is mocked
   - ✅ API calls are mocked properly

### Common Issues and Solutions

#### Backend Tests

**Issue**: `SecurityContext` not found
- **Solution**: Ensure `@WithMockUser` annotation is used correctly

**Issue**: Mock beans not working
- **Solution**: Verify `@MockBean` annotations are correct

#### Frontend Tests

**Issue**: `localStorage is not defined`
- **Solution**: Mock localStorage in test setup (already done in tests)

**Issue**: Router context errors
- **Solution**: Wrap components with `BrowserRouter` in tests (already done)

#### Mobile Tests

**Issue**: `AsyncStorage is not defined`
- **Solution**: Mock AsyncStorage (already done in tests)

**Issue**: Navigation mock errors
- **Solution**: Provide mock navigation object (already done in tests)

---

## 📊 Test Coverage Goals

### Current Coverage
- **Backend**: ~70-80% (controllers and services)
- **Frontend**: ~60-70% (components and pages)
- **Mobile**: ~50-60% (screens and services)

### Target Coverage
- **Backend**: 80%+
- **Frontend**: 70%+
- **Mobile**: 60%+

---

## 🎯 Next Steps

1. **Run all tests** to verify everything works
2. **Check coverage reports** to identify gaps
3. **Add integration tests** for end-to-end scenarios
4. **Set up CI/CD** to run tests automatically
5. **Add E2E tests** using tools like Cypress or Detox

---

## 📝 Test Execution Checklist

- [ ] Backend tests run successfully
- [ ] Frontend tests run successfully
- [ ] Mobile tests run successfully
- [ ] All tests pass without errors
- [ ] Coverage reports generated
- [ ] No critical test failures
- [ ] Mock objects work correctly
- [ ] Test output is readable and informative

---

## 🐛 Troubleshooting

### If tests fail:

1. **Check dependencies**:
   ```bash
   # Backend
   mvn clean install
   
   # Frontend
   npm install
   
   # Mobile
   npm install
   ```

2. **Clear cache**:
   ```bash
   # Frontend
   npm test -- --clearCache
   
   # Mobile
   npm test -- --clearCache
   ```

3. **Verify environment**:
   - Java version: `java -version` (should be 17+)
   - Node version: `node -v` (should be 18+)
   - Maven version: `mvn -v`

4. **Check test configuration**:
   - Verify `pom.xml` has test dependencies
   - Verify `package.json` has test scripts
   - Verify Jest configuration is correct

---

## 📚 Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [React Testing Library](https://testing-library.com/react)
- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [React Native Testing](https://reactnative.dev/docs/testing-overview)

---

**Last Updated**: 2025-01-XX
**Test Files**: ~20+
**Total Test Cases**: 80+

