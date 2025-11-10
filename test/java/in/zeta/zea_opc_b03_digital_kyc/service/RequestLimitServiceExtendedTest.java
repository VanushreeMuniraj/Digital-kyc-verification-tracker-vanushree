package in.zeta.zea_opc_b03_digital_kyc.service;

import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLimitServiceExtendedTest {

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @InjectMocks
    private RequestLimitService requestLimitService;

    @Test
    void testCanCreateRequestWithinLimit() {
        Long customerId = 1L;

        when(verificationRequestRepository.countByRequestedByAndYear(eq(1L), anyInt())).thenReturn(2L);

        boolean result = requestLimitService.canCreateRequest(customerId);

        assertTrue(result);
    }

    @Test
    void testCanCreateRequestExceedsLimit() {
        Long customerId = 1L;

        when(verificationRequestRepository.countByRequestedByAndYear(eq(1L), anyInt())).thenReturn(6L);

        boolean result = requestLimitService.canCreateRequest(customerId);

        assertFalse(result);
    }

    @Test
    void testGetRemainingRequests() {
        Long customerId = 1L;

        when(verificationRequestRepository.countByRequestedByAndYear(eq(1L), anyInt())).thenReturn(2L);

        long remaining = requestLimitService.getRemainingRequests(customerId);

        assertEquals(4, remaining);
    }
}
