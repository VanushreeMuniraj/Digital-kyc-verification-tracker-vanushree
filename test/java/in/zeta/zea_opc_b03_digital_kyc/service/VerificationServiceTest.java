package in.zeta.zea_opc_b03_digital_kyc.service;

import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.CreateVerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.UpdateStatusRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.VerificationRequestDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.Priority;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.repository.UserRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StatusHistoryService statusHistoryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RequestLimitService requestLimitService;

    @Mock
    private RequestAuditService requestAuditService;

    @Mock
    private OfficerAssignmentService officerAssignmentService;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private Gson gson;

    @InjectMocks
    private VerificationService verificationService;

    private User customer;
    private User requestor;
    private User officer;
    private VerificationRequest verificationRequest;
    private CreateVerificationRequest createRequest;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .username("customer")
                .email("customer@test.com")
                .fullName("Customer User")
                .role(User.Role.CUSTOMER)
                .build();

        requestor = User.builder()
                .id(2L)
                .username("requestor")
                .email("requestor@test.com")
                .fullName("Requestor User")
                .role(User.Role.REQUESTOR)
                .build();

        officer = User.builder()
                .id(3L)
                .username("officer")
                .email("officer@test.com")
                .fullName("Officer User")
                .role(User.Role.OFFICER)
                .build();

        verificationRequest = new VerificationRequest();
        verificationRequest.setId(1L);
        verificationRequest.setCustomerId(1L);
        verificationRequest.setRequestedBy(2L);
        verificationRequest.setStatus(VerificationStatus.PENDING);
        verificationRequest.setPriority(Priority.MEDIUM);
        verificationRequest.setCreatedAt(LocalDateTime.now());
        verificationRequest.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateVerificationRequest();
        createRequest.setCustomerId(1L);
        createRequest.setRequestReason("KYC verification");
        createRequest.setDescription("Standard verification");
        createRequest.setPriority(Priority.MEDIUM);
    }

    @Test
    void createVerificationRequest_Success() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(verificationRequestRepository.save(any(VerificationRequest.class))).thenReturn(verificationRequest);
        doNothing().when(requestLimitService).validateRequestLimit(anyLong());

        CompletableFuture<VerificationRequestDto> result = verificationService.createVerificationRequest(createRequest, 2L);

        assertNotNull(result);
        VerificationRequestDto dto = result.get();
        assertEquals(1L, dto.getId());
        verify(verificationRequestRepository).save(any(VerificationRequest.class));
        verify(statusHistoryService).recordStatusChange(any(), any(), any(), anyLong(), anyString());
        verify(notificationService).sendNotification(any(), anyString(), anyString(), any());
    }

    @Test
    void createVerificationRequest_RequestorNotFound_ThrowsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> verificationService.createVerificationRequest(createRequest, 2L));
    }

    @Test
    void createVerificationRequest_CustomerNotFound_ThrowsException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> verificationService.createVerificationRequest(createRequest, 2L));
    }

    @Test
    void getVerificationById_Success() {
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(verificationRequest));

        VerificationRequestDto result = verificationService.getVerificationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(VerificationStatus.PENDING, result.getStatus());
    }

    @Test
    void getVerificationById_NotFound_ThrowsException() {
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> verificationService.getVerificationById(1L));
    }

    @Test
    void getVerificationsByCustomer_Success() {
        when(verificationRequestRepository.findByCustomerId(1L))
                .thenReturn(Arrays.asList(verificationRequest));

        List<VerificationRequestDto> result = verificationService.getVerificationsByCustomer(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCustomerId());
    }

    @Test
    void getAllVerifications_Success() {
        when(verificationRequestRepository.findAll())
                .thenReturn(Arrays.asList(verificationRequest));

        List<VerificationRequestDto> result = verificationService.getAllVerifications();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getVerificationsByStatus_Success() {
        when(verificationRequestRepository.findByStatus(VerificationStatus.PENDING))
                .thenReturn(Arrays.asList(verificationRequest));

        List<VerificationRequestDto> result = verificationService.getVerificationsByStatus("PENDING");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(VerificationStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void updateVerificationStatus_Success() {
        UpdateStatusRequest updateRequest = new UpdateStatusRequest();
        updateRequest.setStatus(VerificationStatus.APPROVED);
        updateRequest.setComments("Approved after review");

        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(verificationRequest));
        when(verificationRequestRepository.save(any(VerificationRequest.class))).thenReturn(verificationRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));

        VerificationRequestDto result = verificationService.updateVerificationStatus(1L, updateRequest, 3L);

        assertNotNull(result);
        verify(verificationRequestRepository).save(any(VerificationRequest.class));
        verify(statusHistoryService).recordStatusChange(any(), any(), any(), anyLong(), anyString());
        verify(officerAssignmentService).releaseOfficerLoad(3L);
    }

    @Test
    void assignVerification_Success() {
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(verificationRequest));
        when(verificationRequestRepository.save(any(VerificationRequest.class))).thenReturn(verificationRequest);
        when(userRepository.findById(3L)).thenReturn(Optional.of(officer));

        VerificationRequestDto result = verificationService.assignVerification(1L, 3L);

        assertNotNull(result);
        verify(verificationRequestRepository).save(any(VerificationRequest.class));
        verify(statusHistoryService).recordStatusChange(any(), any(), eq(VerificationStatus.IN_REVIEW), anyLong(), anyString());
        verify(notificationService).sendNotification(any(), anyString(), anyString(), any());
    }

    @Test
    void getAssignedVerifications_Success() {
        when(verificationRequestRepository.findByAssignedTo(3L))
                .thenReturn(Arrays.asList(verificationRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        List<Map<String, Object>> result = verificationService.getAssignedVerifications(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOpenVerifications_Success() {
        when(verificationRequestRepository.findByStatus(VerificationStatus.PENDING))
                .thenReturn(Arrays.asList(verificationRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        List<Map<String, Object>> result = verificationService.getOpenVerifications();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOfficerStats_Success() {
        when(verificationRequestRepository.findByAssignedTo(3L))
                .thenReturn(Arrays.asList(verificationRequest));
        when(verificationRequestRepository.findByStatus(VerificationStatus.PENDING))
                .thenReturn(Arrays.asList(verificationRequest));

        Map<String, Object> result = verificationService.getOfficerStats(3L);

        assertNotNull(result);
        assertTrue(result.containsKey("assignedCount"));
        assertTrue(result.containsKey("completedCount"));
        assertTrue(result.containsKey("pendingCount"));
    }
}
