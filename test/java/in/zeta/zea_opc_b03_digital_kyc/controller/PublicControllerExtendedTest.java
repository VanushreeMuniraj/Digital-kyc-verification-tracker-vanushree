package in.zeta.zea_opc_b03_digital_kyc.controller;

import in.zeta.zea_opc_b03_digital_kyc.dto.request.RegisterRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.UserDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicControllerExtendedTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PublicController publicController;

    @Test
    void testGetSupportedDocumentTypes() {
        ResponseEntity<?> response = publicController.getSupportedDocumentTypes();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetVerificationStatuses() {
        ResponseEntity<?> response = publicController.getVerificationStatuses();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<?> response = publicController.healthCheck();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
