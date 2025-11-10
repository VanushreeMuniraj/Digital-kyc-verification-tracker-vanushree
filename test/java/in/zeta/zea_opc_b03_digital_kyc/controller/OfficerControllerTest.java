package in.zeta.zea_opc_b03_digital_kyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.VerificationRequestDto;
import in.zeta.zea_opc_b03_digital_kyc.enums.Priority;
import in.zeta.zea_opc_b03_digital_kyc.enums.VerificationStatus;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficerController.class)
@ContextConfiguration(classes = {OfficerController.class})
class OfficerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VerificationService verificationService;

    private VerificationRequestDto verificationDto;

    @BeforeEach
    void setUp() {
        verificationDto = new VerificationRequestDto();
        verificationDto.setId(1L);
        verificationDto.setCustomerId(1L);
        verificationDto.setStatus(VerificationStatus.PENDING);
        verificationDto.setPriority(Priority.MEDIUM);
        verificationDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void updateVerificationStatus_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("status", "APPROVED");
        request.put("comments", "Verified");

        when(verificationService.updateVerificationStatus(anyLong(), any(), anyLong()))
                .thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/customers/1/verification/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getOpenVerifications_Success() throws Exception {
        Map<String, Object> verification = new HashMap<>();
        verification.put("id", 1L);
        when(verificationService.getOpenVerifications()).thenReturn(Arrays.asList(verification));

        mockMvc.perform(get("/api/v1/verifications/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getVerificationDetails_Success() throws Exception {
        when(verificationService.getVerificationById(1L)).thenReturn(verificationDto);

        mockMvc.perform(get("/api/v1/officer/verification/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAssignedVerifications_Success() throws Exception {
        Map<String, Object> verification = new HashMap<>();
        verification.put("id", 1L);
        when(verificationService.getAssignedVerifications(anyLong()))
                .thenReturn(Arrays.asList(verification));

        mockMvc.perform(get("/api/v1/verifications/assigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getPendingVerifications_Success() throws Exception {
        Map<String, Object> verification = new HashMap<>();
        verification.put("id", 1L);
        when(verificationService.getPendingVerifications()).thenReturn(Arrays.asList(verification));

        mockMvc.perform(get("/api/v1/verifications/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getVerificationStatus_Success() throws Exception {
        when(verificationService.getVerificationById(1L)).thenReturn(verificationDto);

        mockMvc.perform(get("/api/v1/verifications/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void assignVerification_Success() throws Exception {
        when(verificationService.assignVerification(anyLong(), anyLong())).thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/verification/1/assign"))
                .andExpect(status().isOk());
    }

    @Test
    void reviewVerification_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("status", "APPROVED");
        request.put("comments", "All good");

        when(verificationService.updateVerificationStatus(anyLong(), any(), anyLong()))
                .thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/verification/1/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getDashboardStats_Success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("assignedCount", 5);
        when(verificationService.getOfficerStats(anyLong())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedCount").value(5));
    }

    @Test
    void addComments_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("comment", "Test comment");

        when(verificationService.getVerificationById(1L)).thenReturn(verificationDto);

        mockMvc.perform(put("/api/v1/customers/1/verification/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comments added successfully"));
    }
}
