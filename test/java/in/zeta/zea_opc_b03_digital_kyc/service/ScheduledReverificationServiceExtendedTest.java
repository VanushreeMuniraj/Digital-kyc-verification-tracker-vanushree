package in.zeta.zea_opc_b03_digital_kyc.service;

import in.zeta.zea_opc_b03_digital_kyc.entity.ScheduledReverification;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.repository.ScheduledReverificationRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledReverificationServiceExtendedTest {

    @Mock
    private ScheduledReverificationRepository reverificationRepository;

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @InjectMocks
    private ScheduledReverificationService reverificationService;

    @Test
    void testScheduleReverification() {
        String customerId = "1";
        String documentId = "doc1";
        int monthsFromNow = 12;

        when(reverificationRepository.save(any())).thenReturn(new ScheduledReverification());

        reverificationService.scheduleReverification(customerId, documentId, monthsFromNow);

        verify(reverificationRepository).save(any());
    }



    @Test
    void testProcessScheduledReverifications() {
        ScheduledReverification rev = new ScheduledReverification();
        rev.setId(1L);
        rev.setCustomerId("1");
        rev.setDocumentId("doc1");
        rev.setStatus(ScheduledReverification.ReverificationStatus.SCHEDULED);

        when(reverificationRepository.findByScheduledDateAndStatus(any(), any())).thenReturn(Arrays.asList(rev));
        when(reverificationRepository.save(any())).thenReturn(rev);

        reverificationService.processScheduledReverifications();

        verify(reverificationRepository).save(rev);
    }

    @Test
    void testCompleteReverification() {
        ScheduledReverification rev = new ScheduledReverification();
        rev.setScheduleId("schedule1");

        when(reverificationRepository.findByScheduleId("schedule1")).thenReturn(Optional.of(rev));
        when(reverificationRepository.save(any())).thenReturn(rev);

        reverificationService.completeReverification("schedule1");

        verify(reverificationRepository).save(rev);
    }
}
