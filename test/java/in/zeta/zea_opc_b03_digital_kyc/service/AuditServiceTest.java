package in.zeta.zea_opc_b03_digital_kyc.service;

import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.AuditLogDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.AuditLog;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private Gson gson;

    @InjectMocks
    private AuditService auditService;

    private AuditLog auditLog;
    private User user;
    private VerificationRequest verificationRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .build();

        verificationRequest = new VerificationRequest();
        verificationRequest.setId(1L);

        auditLog = AuditLog.builder()
                .id(1L)
                .action("CREATE")
                .user(user)
                .verificationRequest(verificationRequest)
                .oldStatus(null)
                .newStatus("PENDING")
                .build();
    }

    @Test
    void logAction_Success() {

        auditService.logAction("1", "CREATE", "User created", "127.0.0.1");

        // verify(eventProducer).publishEvent(anyString(), eq("audit.action.logged"));
    }

    @Test
    void getAuditLogsByVerificationId_Success() {
        when(auditLogRepository.findByVerificationRequestId(1L)).thenReturn(Arrays.asList(auditLog));

        List<AuditLogDto> result = auditService.getAuditLogsByVerificationId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAuditLogsByUserId_Success() {
        when(auditLogRepository.findByUserId(1L)).thenReturn(Arrays.asList(auditLog));

        List<AuditLogDto> result = auditService.getAuditLogsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
