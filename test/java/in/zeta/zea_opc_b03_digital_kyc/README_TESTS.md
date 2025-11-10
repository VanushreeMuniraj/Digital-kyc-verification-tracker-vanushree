# Test Suite Documentation

## Test Structure

```
src/test/java/in/zeta/zea_opc_b03_digital_kyc/
├── controller/          # Controller layer tests (MockMvc)
├── service/            # Service layer tests (Mockito)
├── repository/         # Repository layer tests (DataJpaTest)
├── dto/               # DTO validation tests
├── mapper/            # Mapper tests
├── utils/             # Utility tests
├── integration/       # Integration tests
└── config/            # Test configuration
```

## Test Coverage

### Controllers
- CustomerControllerTest
- UserControllerTest
- DocumentControllerTest

### Services
- UserServiceTest
- VerificationServiceTest
- DocumentServiceTest
- NotificationServiceTest
- EmailServiceTest
- AuditServiceTest
- CommentServiceTest

### DTOs
- LoginRequestTest
- RegisterRequestTest

### Repositories
- UserRepositoryTest
- VerificationRequestRepositoryTest

### Utils
- FileUtilsTest

### Mappers
- EntityDtoMapperTest

### Integration
- VerificationIntegrationTest

## Running Tests

### Run all tests
```bash
mvn clean test
```

### Run specific test class
```bash
mvn test -Dtest=UserServiceTest
```

### Run with coverage
```bash
mvn clean test jacoco:report
```

### Run SonarQube analysis
```bash
mvn clean verify sonar:sonar
```

## Test Best Practices

1. **Naming Convention**: `methodName_scenario_expectedResult`
2. **AAA Pattern**: Arrange, Act, Assert
3. **Mocking**: Use Mockito for dependencies
4. **Isolation**: Each test is independent
5. **Coverage**: Aim for >80% code coverage

## SonarQube Compliance

All tests follow SonarQube rules:
- No code smells
- No duplications
- Proper exception handling
- Clean code principles
- Security best practices

## Test Annotations

- `@ExtendWith(MockitoExtension.class)` - Unit tests with Mockito
- `@WebMvcTest` - Controller tests
- `@DataJpaTest` - Repository tests
- `@SpringBootTest` - Integration tests
- `@ActiveProfiles("test")` - Use test profile
