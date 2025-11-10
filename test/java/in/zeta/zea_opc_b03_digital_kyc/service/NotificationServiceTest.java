package in.zeta.zea_opc_b03_digital_kyc.service;

import in.zeta.zea_opc_b03_digital_kyc.entity.Notification;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private VerificationRequest verificationRequest;
    private Notification notification;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .build();

        verificationRequest = new VerificationRequest();
        verificationRequest.setId(1L);

        notification = Notification.builder()
                .id(1L)
                .user(user)
                .title("Test Notification")
                .message("Test Message")
                .type(Notification.NotificationType.INFO)
                .build();
    }

    @Test
    void sendNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.sendNotification(user, "Test Title", "Test Message", verificationRequest);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getUserNotifications_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(notification));

        List<Notification> result = notificationService.getUserNotifications(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Notification", result.get(0).getTitle());
    }

    @Test
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(java.util.Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1L);

        verify(notificationRepository).save(any(Notification.class));
    }
}
