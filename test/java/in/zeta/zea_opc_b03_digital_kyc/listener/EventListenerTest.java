package in.zeta.zea_opc_b03_digital_kyc.listener;

import in.zeta.zea_opc_b03_digital_kyc.service.EmailService;
import in.zeta.zea_opc_b03_digital_kyc.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EventListener eventListener;

    @Test
    void testHandleUserRegisteredEvent() {
        Map<String, Object> eventData = Map.of(
            "userId", "1",
            "email", "test@example.com",
            "fullName", "Test User",
            "role", "CUSTOMER"
        );

        eventListener.handleUserRegisteredEvent(eventData);

        verify(notificationService).createNotification(1L, "Welcome to Digital KYC", 
            "Your account has been successfully created. Welcome Test User!", null);
    }

    @Test
    void testHandleVerificationStatusChangedEvent() {
        Map<String, Object> eventData = Map.of(
            "verificationId", "1",
            "customerId", "100",
            "newStatus", "APPROVED",
            "officerComments", "All good"
        );

        eventListener.handleVerificationStatusChangedEvent(eventData);

        verify(notificationService).createNotification(100L, "Verification Status Updated", 
            "Your verification request status has been changed to: APPROVED. Comments: All good", 1L);
    }

    @Test
    void testHandleDocumentUploadedEvent() {
        Map<String, Object> eventData = Map.of(
            "verificationId", "1",
            "customerId", "100",
            "documentName", "passport.pdf",
            "documentType", "PASSPORT"
        );

        eventListener.handleDocumentUploadedEvent(eventData);

        verify(notificationService).createNotification(100L, "Document Uploaded Successfully", 
            "Your document passport.pdf (PASSPORT) has been uploaded successfully for verification request #1", 1L);
    }

    @Test
    void testHandleEmailNotificationEvent() {
        Map<String, Object> eventData = Map.of(
            "email", "test@example.com",
            "subject", "Test Subject",
            "recipientName", "Test User",
            "type", "WELCOME"
        );

        eventListener.handleEmailNotificationEvent(eventData);

        verify(emailService).sendKycStatusEmail("test@example.com", "Test Subject", "Test User", "WELCOME");
    }

    @Test
    void testHandleOfficerAssignedEvent() {
        Map<String, Object> eventData = Map.of(
            "verificationId", "1",
            "customerId", "100",
            "officerId", "200",
            "officerName", "Officer Smith"
        );

        eventListener.handleOfficerAssignedEvent(eventData);

        verify(notificationService).createNotification(100L, "Officer Assigned", 
            "Your verification request #1 has been assigned to Officer Smith for review", 1L);
    }
}