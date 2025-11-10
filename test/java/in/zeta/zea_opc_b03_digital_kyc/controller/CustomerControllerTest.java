package in.zeta.zea_opc_b03_digital_kyc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.CreateVerificationRequest;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = {CustomerController.class})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VerificationService verificationService;

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
        createRequest.setDescription("Test verification");
    }

    @Test
    void createVerificationRequest_Success() throws Exception {
        CompletableFuture<VerificationRequestDto> future = new CompletableFuture<>();
        future.complete(verificationDto);
        
        when(verificationService.createVerificationRequest(any(), anyLong()))
                .thenReturn(future);

        mockMvc.perform(post("/api/v1/customers/1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getCustomerVerifications_WithResults() throws Exception {
        when(verificationService.getVerificationsByCustomer(1L))
                .thenReturn(Arrays.asList(verificationDto));

        mockMvc.perform(get("/api/v1/customers/1/verifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getCustomerVerifications_NoResults() throws Exception {
        when(verificationService.getVerificationsByCustomer(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/customers/1/verifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.totalRequests").value(0));
    }

    @Test
    void getVerificationStatus_Success() throws Exception {
        when(verificationService.getVerificationById(1L))
                .thenReturn(verificationDto);

        mockMvc.perform(get("/api/v1/customers/1/verification/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getCustomerProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.fullName").exists());
    }
}
