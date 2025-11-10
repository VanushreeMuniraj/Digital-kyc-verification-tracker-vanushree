package in.zeta.zea_opc_b03_digital_kyc.dto;

import in.zeta.zea_opc_b03_digital_kyc.dto.request.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validLoginRequest_NoViolations() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankUsername_HasViolation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void nullUsername_HasViolation() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void blankPassword_HasViolation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void nullPassword_HasViolation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword(null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void bothFieldsBlank_HasMultipleViolations() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user123");
        request.setPassword("pass456");

        assertEquals("user123", request.getUsername());
        assertEquals("pass456", request.getPassword());
    }
}
