package in.zeta.zea_opc_b03_digital_kyc.dto;

import in.zeta.zea_opc_b03_digital_kyc.dto.response.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void testAuditLogDto() {
        AuditLogDto dto = AuditLogDto.builder()
                .id(1L)
                .action("CREATE")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("CREATE", dto.getAction());
    }

    @Test
    void testCommentDto() {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .comment("Test comment")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getComment());
    }

    @Test
    void testDocumentDto() {
        DocumentDto dto = DocumentDto.builder()
                .id(1L)
                .documentName("test.pdf")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("test.pdf", dto.getDocumentName());
    }

    @Test
    void testOfficerStats() {
        OfficerStats stats = OfficerStats.builder()
                .officerId(1L)
                .currentLoad(5)
                .build();
        assertEquals(1L, stats.getOfficerId());
        assertEquals(5, stats.getCurrentLoad());
    }

    @Test
    void testVerificationActionLog() {
        VerificationActionLog log = VerificationActionLog.builder()
                .id(1L)
                .action("APPROVED")
                .build();
        assertEquals(1L, log.getId());
        assertEquals("APPROVED", log.getAction());
    }

    @Test
    void testVerificationRequestDto() {
        VerificationRequestDto dto = VerificationRequestDto.builder()
                .id(1L)
                .status("PENDING")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void testPubSubEvent() {
        PubSubEvent event = new PubSubEvent();
        event.setEventType("TEST");
        event.setData("test data");
        assertEquals("TEST", event.getEventType());
        assertEquals("test data", event.getData());
    }
}