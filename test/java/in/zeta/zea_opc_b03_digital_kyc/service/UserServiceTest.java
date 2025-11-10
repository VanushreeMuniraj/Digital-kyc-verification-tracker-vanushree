package in.zeta.zea_opc_b03_digital_kyc.service;

import com.google.gson.Gson;
import in.zeta.zea_opc_b03_digital_kyc.dto.request.RegisterRequest;
import in.zeta.zea_opc_b03_digital_kyc.dto.response.UserDto;
import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import in.zeta.zea_opc_b03_digital_kyc.producer.EventProducer;
import in.zeta.zea_opc_b03_digital_kyc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private Gson gson;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("Test User")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("new@example.com");
        registerRequest.setName("New User");
        registerRequest.setRole(RegisterRequest.UserRole.CUSTOMER);
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        // verify(eventProducer).publishEvent(anyString(), eq("user.registered"));
    }

    @Test
    void registerUser_UsernameExists_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(registerRequest));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailExists_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.registerUser(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserById(1L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUserRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUserRole(1L, "ADMIN");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        // verify(eventProducer).publishEvent(anyString(), eq("user.role.updated"));
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        verify(userRepository).save(any(User.class));
        // verify(eventProducer).publishEvent(anyString(), eq("user.deactivated"));
    }

    @Test
    void getSystemStats_Success() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByIsActive(true)).thenReturn(8L);
        when(userRepository.countByRole(any(User.Role.class))).thenReturn(2L);

        Map<String, Object> result = userService.getSystemStats();

        assertNotNull(result);
        assertEquals(10L, result.get("totalUsers"));
        assertEquals(8L, result.get("activeUsers"));
        assertEquals(2L, result.get("inactiveUsers"));
    }

    @Test
    void validateCredentials_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        UserDto result = userService.validateCredentials("testuser", "password123");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void validateCredentials_InvalidPassword_ReturnsNull() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        UserDto result = userService.validateCredentials("testuser", "wrongpassword");

        assertNull(result);
    }

    @Test
    void getCustomerDetailsByUserId_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Map<String, Object> result = userService.getCustomerDetailsByUserId("1");

        assertNotNull(result);
        assertEquals(1L, result.get("userId"));
        assertEquals("testuser", result.get("username"));
    }

    @Test
    void getCustomerDetailsByUserId_InvalidId_ReturnsError() {
        Map<String, Object> result = userService.getCustomerDetailsByUserId("invalid");

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }
}
