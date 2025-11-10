package in.zeta.zea_opc_b03_digital_kyc.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void testAuditLogNotFoundException() {
        AuditLogNotFoundException ex = new AuditLogNotFoundException("Not found");
        assertEquals("Not found", ex.getMessage());
    }

    @Test
    void testAuditLogPersistenceException() {
        AuditLogPersistenceException ex = new AuditLogPersistenceException("Persistence error");
        assertEquals("Persistence error", ex.getMessage());
    }

    @Test
    void testDocumentUploadException() {
        DocumentUploadException ex = new DocumentUploadException("Upload failed");
        assertEquals("Upload failed", ex.getMessage());
    }

    @Test
    void testEnumValidationException() {
        EnumValidationException ex = new EnumValidationException("Invalid enum");
        assertEquals("Invalid enum", ex.getMessage());
    }

    @Test
    void testEventPublishingException() {
        EventPublishingException ex = new EventPublishingException("Event failed");
        assertEquals("Event failed", ex.getMessage());
    }

    @Test
    void testFileDownloadException() {
        FileDownloadException ex = new FileDownloadException("Download failed");
        assertEquals("Download failed", ex.getMessage());
    }

    @Test
    void testFileReadException() {
        FileReadException ex = new FileReadException("Read failed");
        assertEquals("Read failed", ex.getMessage());
    }

    @Test
    void testFileStorageException() {
        FileStorageException ex = new FileStorageException("Storage failed");
        assertEquals("Storage failed", ex.getMessage());
    }

    @Test
    void testInvalidFileException() {
        InvalidFileException ex = new InvalidFileException("Invalid file");
        assertEquals("Invalid file", ex.getMessage());
    }

    @Test
    void testInvalidRequestException() {
        InvalidRequestException ex = new InvalidRequestException("Invalid request");
        assertEquals("Invalid request", ex.getMessage());
    }

    @Test
    void testPublishEventFailedException() {
        PublishEventFailedException ex = new PublishEventFailedException("Publish failed");
        assertEquals("Publish failed", ex.getMessage());
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", ex.getMessage());
    }

    @Test
    void testUnauthorizedAccessException() {
        UnauthorizedAccessException ex = new UnauthorizedAccessException("Unauthorized");
        assertEquals("Unauthorized", ex.getMessage());
    }
}