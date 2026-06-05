# Contact Management System - SonarQube Grade A Improvement Plan

## Overview
This plan outlines the steps to improve the Contact Management System from SonarQube Grade C to Grade A (80-90+ score) while maintaining an internship-style commit history (≈3 commits/day).

## Key Issues Identified
1. Security Vulnerabilities: Hardcoded secrets in application.properties
2. Low Test Coverage: Minimal backend tests, no frontend tests
3. Code Smells: Field injection, large methods, lack of validation
4. Code Duplication: Minimal, but some in service methods

## Step-by-Step Improvement Plan (Daily Commit Breakdown)

### Day 1: Environment & Security Setup
**Commit 1**: Add dotenv dependency and update application.properties to use env vars
  - Add `io.github.cdimascio:dotenv-java` to pom.xml
  - Replace hardcoded values with placeholders like `${SPRING_DATASOURCE_URL}`
  - Create backend/.env.example

**Commit 2**: Add file upload size limits in application.properties
  - Set `spring.servlet.multipart.max-file-size=10MB`
  - Set `spring.servlet.multipart.max-request-size=10MB`

**Commit 3**: Update CORS configuration for better security
  - Move CORS config to separate bean in SecurityConfig
  - Add `allowCredentials(true)`
  - Add frontend/.env.example and update .gitignore


### Day 2: Dependency Injection & Lombok Setup
**Commit 1**: Add Lombok dependency and configure it
  - Add Lombok to pom.xml with scope provided
  - Add Lombok plugin config if needed

**Commit 2**: Replace field injection with constructor injection in AuthService and ContactService
  - Use @RequiredArgsConstructor
  - Make dependencies final

**Commit 3**: Replace field injection in controllers and security components
  - Update AuthController, ContactController
  - Update SecurityConfig, UserDetailsServiceImpl
  - Update JwtAuthenticationFilter to use constructor injection


### Day 3: Input Validation & DTO Improvements
**Commit 1**: Improve DTOs with Lombok and better validation
  - Update SignupRequest, LoginRequest, ChangePasswordRequest
  - Add message attributes to validation annotations
  - Use @Data, @NoArgsConstructor, @AllArgsConstructor

**Commit 2**: Enhance ContactDto validation
  - Add @Size constraints on name fields
  - Add @Email validation for email addresses list
  - Use Lombok annotations

**Commit 3**: Update remaining DTOs (JwtResponse, MessageResponse, UserProfileDto)
  - Apply Lombok to all DTOs
  - Ensure consistent structure


### Day 4: Refactor ContactService
**Commit 1**: Extract CSV import logic to private methods
  - Create importFromCsv(), createContactFromCsvRecord()

**Commit 2**: Extract Excel import logic to private methods
  - Create importFromExcel(), createContactFromExcelRow()

**Commit 3**: Extract export and helper methods
  - Create writeContactToCsv(), createExcelHeader(), writeContactToExcelRow()
  - Update mapToDto to use ContactDto constructor


### Day 5: Backend Tests - Repository & Service
**Commit 1**: Add ContactRepositoryTest
  - Test findByUserId(), findByIdAndUserId(), search functionality
  - Use @DataJpaTest

**Commit 2**: Enhance AuthServiceTest with more test cases
  - Test authenticateUser(), changePassword(), getUserProfile()
  - Add success and failure scenarios

**Commit 3**: Enhance ContactServiceTest with more test cases
  - Test getContacts(), searchContacts(), updateContact(), deleteContact()
  - Add user not found and contact not found scenarios


### Day 6: Backend Tests - Controllers
**Commit 1**: Add AuthControllerTest
  - Test register, login, change password, get profile endpoints
  - Use @WebMvcTest and @WithMockUser
  - Mock AuthService

**Commit 2**: Add ContactControllerTest
  - Test CRUD, search, import endpoints
  - Use MockMultipartFile for upload test
  - Mock ContactService

**Commit 3**: Fix any test issues and run all tests
  - Verify all tests pass
  - Ensure test coverage meets minimum requirements


### Day 7: Frontend Testing Setup
**Commit 1**: Add frontend test dependencies and scripts
  - Update package.json with vitest, @testing-library/react, etc.
  - Add test, test:ui, test:coverage scripts

**Commit 2**: Configure Vite for testing
  - Update vite.config.js with test config
  - Create setupTests.js

**Commit 3**: Add basic frontend tests
  - Test Button component with different variants, loading, disabled states
  - Verify basic rendering and functionality


## Expected SonarQube Score
- After all changes: 85-95 (Grade A)
- Key improvements:
  - 0 critical security vulnerabilities
  - 80%+ test coverage
  - 0 code smells related to field injection
  - Reduced cognitive complexity in service methods
  - Proper input validation


## Notes
- All commits follow conventional commit style (e.g., "feat: ...", "refactor: ...", "test: ...")
- Each commit is self-contained and meaningful
- No functionality is broken during refactoring
- All tests pass before final commit
