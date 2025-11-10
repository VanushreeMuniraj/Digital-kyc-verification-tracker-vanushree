package in.zeta.zea_opc_b03_digital_kyc.repository;

import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.Priority;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VerificationRequestRepositoryTest {

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    @Test
    void saveVerificationRequest_Success() {
        VerificationRequest request = new VerificationRequest();
        request.setCustomerId(1L);
        request.setRequestedBy(2L);
        request.setStatus(VerificationStatus.PENDING);
        request.setPriority(Priority.MEDIUM);
        request.setRequestReason("KYC verification");

        VerificationRequest saved = verificationRequestRepository.save(request);

        assertNotNull(saved.getId());
        assertEquals(VerificationStatus.PENDING, saved.getStatus());
    }

    @Test
    void findByCustomerId_Success() {
        VerificationRequest request = new VerificationRequest();
        request.setCustomerId(100L);
        request.setRequestedBy(2L);
        request.setStatus(VerificationStatus.PENDING);
        request.setPriority(Priority.HIGH);
        request.setRequestReason("KYC verification");

        verificationRequestRepository.save(request);

        List<VerificationRequest> found = verificationRequestRepository.findByCustomerId(100L);

        assertFalse(found.isEmpty());
        assertEquals(100L, found.get(0).getCustomerId());
    }

    @Test
    void findByStatus_Success() {
        VerificationRequest request = new VerificationRequest();
        request.setCustomerId(1L);
        request.setRequestedBy(2L);
        request.setStatus(VerificationStatus.APPROVED);
        request.setPriority(Priority.LOW);
        request.setRequestReason("KYC verification");

        verificationRequestRepository.save(request);

        List<VerificationRequest> found = verificationRequestRepository.findByStatus(VerificationStatus.APPROVED);

        assertFalse(found.isEmpty());
    }

    @Test
    void findByAssignedTo_Success() {
        VerificationRequest request = new VerificationRequest();
        request.setCustomerId(1L);
        request.setRequestedBy(2L);
        request.setAssignedTo(5L);
        request.setStatus(VerificationStatus.IN_REVIEW);
        request.setPriority(Priority.MEDIUM);
        request.setRequestReason("KYC verification");

        verificationRequestRepository.save(request);

        List<VerificationRequest> found = verificationRequestRepository.findByAssignedTo(5L);

        assertFalse(found.isEmpty());
        assertEquals(5L, found.get(0).getAssignedTo());
    }
}
