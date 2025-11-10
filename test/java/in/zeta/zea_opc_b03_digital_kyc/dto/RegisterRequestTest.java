package in.zeta.zea_opc_b03_digital_kyc.dto;

import in.zeta.zea_opc_b03_digital_kyc.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRegisterRequest_NoViolations() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setRole(RegisterRequest.UserRole.CUSTOMER);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_HasViolation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setRole(RegisterRequest.UserRole.CUSTOMER);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidEmail_HasViolation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("invalid-email");
        request.setName("Test User");
        request.setRole(RegisterRequest.UserRole.CUSTOMER);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void nullRole_HasViolation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setRole(null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user123");
        request.setPassword("pass456");
        request.setEmail("user@test.com");
        request.setName("User Name");
        request.setRole(RegisterRequest.UserRole.OFFICER);

        assertEquals("user123", request.getUsername());
        assertEquals("pass456", request.getPassword());
        assertEquals("user@test.com", request.getEmail());
        assertEquals("User Name", request.getName());
        assertEquals(RegisterRequest.UserRole.OFFICER, request.getRole());
    }
}
