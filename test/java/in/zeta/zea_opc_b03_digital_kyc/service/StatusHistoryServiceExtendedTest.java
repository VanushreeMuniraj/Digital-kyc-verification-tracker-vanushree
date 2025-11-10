package in.zeta.zea_opc_b03_digital_kyc.service;

import in.zeta.zea_opc_b03_digital_kyc.entity.StatusHistory;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import in.zeta.zea_opc_b03_digital_kyc.repository.StatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusHistoryServiceExtendedTest {

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private StatusHistoryService statusHistoryService;

    @Test
    void testRecordStatusChange() {
        VerificationRequest request = new VerificationRequest();
        request.setId(1L);

        when(statusHistoryRepository.save(any())).thenReturn(new StatusHistory());

        StatusHistory result = statusHistoryService.recordStatusChange(request, VerificationStatus.PENDING, VerificationStatus.APPROVED, 1L, "Test reason");

        assertNotNull(result);
        verify(statusHistoryRepository).save(any());
    }

    @Test
    void testGetStatusHistory() {
        StatusHistory history = new StatusHistory();
        history.setId(1L);

        when(statusHistoryRepository.findByVerificationRequestIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(history));

        var result = statusHistoryService.getStatusHistory(1L);

        assertEquals(1, result.size());
    }


}
