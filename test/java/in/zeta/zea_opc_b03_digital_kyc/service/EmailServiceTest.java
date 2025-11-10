package in.zeta.zea_opc_b03_digital_kyc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendKycStatusEmail_Success() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendKycStatusEmail("test@example.com", "Test User", "APPROVED", 1L, "All good");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendKycStatusEmail_WithException_ThrowsException() {
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () -> 
            emailService.sendKycStatusEmail("test@example.com", "Test User", "APPROVED", 1L, "All good"));

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
