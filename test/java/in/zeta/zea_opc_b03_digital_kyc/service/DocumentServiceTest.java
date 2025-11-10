package in.zeta.zea_opc_b03_digital_kyc.service;

import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.DocumentDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.Document;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.repository.DocumentRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.UserRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OfficerAssignmentService officerAssignmentService;

    @Mock
    private StatusHistoryService statusHistoryService;

    @Mock
    private FileValidationService fileValidationService;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private Gson gson;

    @InjectMocks
    private DocumentService documentService;

    @TempDir
    Path tempDir;

    private VerificationRequest verificationRequest;
    private Document document;
    private User requestor;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());

        verificationRequest = new VerificationRequest();
        verificationRequest.setId(1L);
        verificationRequest.setCustomerId(1L);
        verificationRequest.setRequestedBy(2L);
        verificationRequest.setStatus(VerificationStatus.PENDING);

        document = Document.builder()
                .id(1L)
                .verificationRequest(verificationRequest)
                .documentName("test.pdf")
                .documentType("PAN_CARD")
                .filePath(tempDir.resolve("test.pdf").toString())
                .fileSize(1024L)
                .contentType("application/pdf")
                .fileHash("abc123")
                .uploadedAt(LocalDateTime.now())
                .build();

        requestor = User.builder()
                .id(2L)
                .username("requestor")
                .email("requestor@test.com")
                .fullName("Requestor User")
                .build();

        mockFile = mock(MultipartFile.class);
    }

    @Test
    void uploadDocument_Success() throws Exception {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(verificationRequest));
        when(verificationRequestRepository.save(any())).thenReturn(verificationRequest);
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
        when(fileValidationService.calculateSHA256(any())).thenReturn("abc123");

        DocumentDto result = documentService.uploadDocument(1L, mockFile, "PAN_CARD");

        assertNotNull(result);
        assertEquals("test.pdf", result.getDocumentName());
        verify(documentRepository).save(any(Document.class));
        verify(notificationService, atLeastOnce()).sendNotification(any(), anyString(), anyString(), any());
    }

    @Test
    void uploadDocument_EmptyFile_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.uploadDocument(1L, mockFile, "PAN_CARD"));

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void uploadDocument_InvalidFileType_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("text/plain");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.uploadDocument(1L, mockFile, "PAN_CARD"));

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    @Test
    void uploadDocument_FileSizeExceeded_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getSize()).thenReturn(11 * 1024 * 1024L); // 11MB

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.uploadDocument(1L, mockFile, "PAN_CARD"));

        assertTrue(exception.getMessage().contains("File size exceeds"));
    }

    @Test
    void uploadDocument_VerificationNotFound_ThrowsException() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getSize()).thenReturn(1024L);
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.uploadDocument(1L, mockFile, "PAN_CARD"));

        assertEquals("Verification request not found", exception.getMessage());
    }

    @Test
    void getDocumentsByVerificationId_Success() {
        when(documentRepository.findByVerificationRequestId(1L))
                .thenReturn(Arrays.asList(document));

        List<DocumentDto> result = documentService.getDocumentsByVerificationId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test.pdf", result.get(0).getDocumentName());
    }

    @Test
    void getDocumentById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        DocumentDto result = documentService.getDocumentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test.pdf", result.getDocumentName());
    }

    @Test
    void getDocumentById_NotFound_ThrowsException() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> documentService.getDocumentById(1L));

        assertEquals("Document not found", exception.getMessage());
    }

    @Test
    void deleteDocument_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        documentService.deleteDocument(1L);

        verify(documentRepository).delete(document);
    }

    @Test
    void updateDocumentStatus_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        DocumentDto result = documentService.updateDocumentStatus(1L, "APPROVED", "Document verified");

        assertNotNull(result);
        verify(documentRepository).save(any(Document.class));
        // verify(eventProducer).publishEvent(anyString(), eq("document.status.updated"));
    }

    @Test
    void getDocumentsByStatus_Success() {
        when(documentRepository.findAll()).thenReturn(Arrays.asList(document));

        List<DocumentDto> result = documentService.getDocumentsByStatus("APPROVED");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getDocumentVersions_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.findByVerificationRequestId(1L))
                .thenReturn(Arrays.asList(document));

        var result = documentService.getDocumentVersions(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
