package in.zeta.zea_opc_b03_digital_kyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.CreateVerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.UpdateStatusRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.VerificationRequestDto;
import in.zeta.zea_opc_b03_digital_kyc.enums.Priority;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.service.AuditService;
import in.zeta.zea_opc_b03_digital_kyc.service.VerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationController.class)
@ContextConfiguration(classes = {VerificationController.class})
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private EventProducer eventProducer;

    @MockBean
    private Gson gson;

    @MockBean
    private AuditService auditService;

    private VerificationRequestDto verificationDto;
    private CreateVerificationRequest createRequest;

    @BeforeEach
    void setUp() {
        verificationDto = new VerificationRequestDto();
        verificationDto.setId(1L);
        verificationDto.setCustomerId(1L);
        verificationDto.setStatus(VerificationStatus.PENDING);
        verificationDto.setPriority(Priority.MEDIUM);
        verificationDto.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateVerificationRequest();
        createRequest.setCustomerId(1L);
        createRequest.setPriority(Priority.MEDIUM);
        createRequest.setRequestReason("KYC verification");
        createRequest.setDescription("Test");
    }

    @Test
    void createVerification_Success() throws Exception {
        CompletableFuture<VerificationRequestDto> future = CompletableFuture.completedFuture(verificationDto);
        when(verificationService.createVerificationRequest(any(), anyLong())).thenReturn(future);

        mockMvc.perform(post("/api/v1/verifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getVerification_Success() throws Exception {
        when(verificationService.getVerificationById(1L)).thenReturn(verificationDto);

        mockMvc.perform(get("/api/v1/verifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateStatus_Success() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(VerificationStatus.APPROVED);
        request.setComments("Approved");

        when(verificationService.updateVerificationStatus(anyLong(), any(), anyLong()))
                .thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/verifications/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllVerifications_Success() throws Exception {
        when(verificationService.getAllVerifications()).thenReturn(Arrays.asList(verificationDto));

        mockMvc.perform(get("/api/v1/verifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllVerifications_WithStatus() throws Exception {
        when(verificationService.getVerificationsByStatus("PENDING"))
                .thenReturn(Arrays.asList(verificationDto));

        mockMvc.perform(get("/api/v1/verifications?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void assignToSelf_Success() throws Exception {
        when(verificationService.assignVerification(anyLong(), anyLong())).thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/verifications/1/assign"))
                .andExpect(status().isOk());
    }

    @Test
    void getVerificationTimeline_Success() throws Exception {
        Map<String, Object> event = new HashMap<>();
        event.put("action", "CREATED");
        when(auditService.getVerificationTimeline(1L)).thenReturn(Arrays.asList(event));

        mockMvc.perform(get("/api/v1/verifications/1/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].action").value("CREATED"));
    }
}
