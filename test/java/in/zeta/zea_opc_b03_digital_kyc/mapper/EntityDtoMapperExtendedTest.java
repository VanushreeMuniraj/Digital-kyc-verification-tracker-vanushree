package in.zeta.zea_opc_b03_digital_kyc.mapper;

import in.zeta.zea_opc_b03_digital_kyc.dto.response.VerificationRequestDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.Priority;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EntityDtoMapperExtendedTest {

    @Test
    void testToDtoMapping() {
        VerificationRequest entity = new VerificationRequest();
        entity.setId(1L);
        entity.setCustomerId(100L);
        entity.setRequestedBy(200L);
        entity.setAssignedTo(300L);
        entity.setStatus(VerificationStatus.PENDING);
        entity.setRequestReason("Test reason");
        entity.setDescription("Test description");
        entity.setPriority(Priority.HIGH);
        entity.setOfficerComments("Test comments");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        VerificationRequestDto dto = EntityDtoMapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getCustomerId());
        assertEquals(200L, dto.getRequestedBy());
        assertEquals(300L, dto.getAssignedTo());
        assertEquals(VerificationStatus.PENDING, dto.getStatus());
        assertEquals("Test reason", dto.getRequestReason());
        assertEquals("Test description", dto.getDescription());
        assertEquals(Priority.HIGH, dto.getPriority());
        assertEquals("Test comments", dto.getOfficerComments());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }
}