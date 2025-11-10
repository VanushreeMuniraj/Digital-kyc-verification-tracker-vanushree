package in.zeta.zea_opc_b03_digital_kyc.service;

import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.CreateCommentRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.CommentDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationComment;
import in.zeta.zea_opc_b03_digital_kyc.entity.VerificationRequest;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.repository.UserRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationCommentRepository;
import in.zeta.zea_opc_b03_digital_kyc.repository.VerificationRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private VerificationCommentRepository commentRepository;

    @Mock
    private VerificationRequestRepository verificationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private Gson gson;

    @InjectMocks
    private CommentService commentService;

    private VerificationRequest verificationRequest;
    private VerificationComment comment;
    private CreateCommentRequest createRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .build();

        verificationRequest = new VerificationRequest();
        verificationRequest.setId(1L);

        comment = VerificationComment.builder()
                .id(1L)
                .verificationRequest(verificationRequest)
                .user(user)
                .comment("Test comment")
                .build();

        createRequest = new CreateCommentRequest();
        createRequest.setComment("New comment");
    }

    @Test
    void addComment_Success() {
        when(verificationRequestRepository.findById(1L)).thenReturn(Optional.of(verificationRequest));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(VerificationComment.class))).thenReturn(comment);

        CommentDto result = commentService.addComment(1L, createRequest, 1L);

        assertNotNull(result);
        verify(commentRepository).save(any(VerificationComment.class));
    }

    @Test
    void addComment_VerificationNotFound_ThrowsException() {
        when(verificationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> commentService.addComment(1L, createRequest, 1L));
    }

    @Test
    void getCommentsByVerificationId_Success() {
        when(commentRepository.findByVerificationRequestIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(comment));

        List<CommentDto> result = commentService.getCommentsByVerificationId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_NotFound_ThrowsException() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> commentService.deleteComment(1L, 1L));
    }
}
