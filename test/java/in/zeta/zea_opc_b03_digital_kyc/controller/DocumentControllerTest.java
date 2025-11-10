package in.zeta.zea_opc_b03_digital_kyc.controller;

import in.zeta.zea_opc_b03_digital_kyc.dto.response.DocumentDto;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@ContextConfiguration(classes = {DocumentController.class})
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private EventProducer eventProducer;

    private DocumentDto documentDto;

    @BeforeEach
    void setUp() {
        documentDto = new DocumentDto();
        documentDto.setId(1L);
        documentDto.setVerificationRequestId(1L);
        documentDto.setDocumentName("test.pdf");
        documentDto.setDocumentType("PAN_CARD");
        documentDto.setFilePath("/uploads/test.pdf");
        documentDto.setFileSize(1024L);
        documentDto.setContentType("application/pdf");
        documentDto.setUploadedAt(LocalDateTime.now());
    }

    @Test
    void uploadDocument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "test".getBytes());

        when(documentService.uploadDocument(anyLong(), any(), anyString()))
                .thenReturn(documentDto);

        mockMvc.perform(multipart("/api/v1/customers/1/verification/1/documents/upload")
                .file(file)
                .param("documentType", "PAN_CARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.documentName").value("test.pdf"));
    }

    @Test
    void getDocuments_Success() throws Exception {
        when(documentService.getDocumentsByVerificationId(1L))
                .thenReturn(Arrays.asList(documentDto));

        mockMvc.perform(get("/api/v1/customers/1/verification/1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getDocumentById_Success() throws Exception {
        when(documentService.getDocumentById(1L)).thenReturn(documentDto);

        mockMvc.perform(get("/api/v1/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateDocumentStatus_Success() throws Exception {
        when(documentService.updateDocumentStatus(anyLong(), anyString(), anyString()))
                .thenReturn(documentDto);

        mockMvc.perform(put("/api/v1/documents/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"APPROVED\",\"reason\":\"Verified\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteDocument_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Document deleted successfully"));
    }
}
