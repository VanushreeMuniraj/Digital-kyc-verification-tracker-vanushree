package in.zeta.zea_opc_b03_digital_kyc.service;

import in.zeta.zea_opc_b03_digital_kyc.entity.Officer;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import in.zeta.zea_opc_b03_digital_kyc.repository.OfficerRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import in.zeta.zea_opc_b03_digital_kyc.service.RequestAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficerAssignmentServiceExtendedTest {

    @Mock
    private OfficerRepository officerRepository;

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @Mock
    private RequestAuditService requestAuditService;

    @InjectMocks
    private OfficerAssignmentService officerAssignmentService;

    private Officer officer;
    private VerificationRequest request;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        
        officer = Officer.builder()
                .id(1L)
                .currentLoad(0)
                .maxCapacity(10)
                .isAvailable(true)
                .user(user)
                .build();

        request = new VerificationRequest();
        request.setId(1L);
        request.setStatus(VerificationStatus.PENDING);
    }

    @Test
    void testAssignOfficerSuccess() {
        when(officerRepository.findAvailableOfficersOrderByLoad()).thenReturn(Arrays.asList(officer));
        when(verificationRequestRepository.save(any())).thenReturn(request);
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        User assigned = officerAssignmentService.assignOfficerToVerification(1L);

        assertNotNull(assigned);
        verify(officerRepository).save(officer);
    }

    @Test
    void testFindLeastLoadedOfficer() {
        when(officerRepository.findAvailableOfficersOrderByLoad()).thenReturn(Arrays.asList(officer));

        Officer found = officerAssignmentService.findLeastLoadedOfficer();

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testReleaseOfficerLoad() {
        when(officerRepository.findByUserId(1L)).thenReturn(Optional.of(officer));
        when(officerRepository.save(any())).thenReturn(officer);

        officerAssignmentService.releaseOfficerLoad(1L);

        verify(officerRepository).save(officer);
    }

    @Test
    void testGetOfficerWorkload() {
        when(officerRepository.findByUserId(1L)).thenReturn(Optional.of(officer));
        when(verificationRequestRepository.findByAssignedTo(1L)).thenReturn(Arrays.asList(request));

        var workload = officerAssignmentService.getOfficerWorkload(1L);

        assertNotNull(workload);
        assertEquals(1L, workload.get("officerId"));
    }
}
