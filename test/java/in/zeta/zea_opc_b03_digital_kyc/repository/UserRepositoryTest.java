package in.zeta.zea_opc_b03_digital_kyc.repository;

import in.zeta.zea_opc_b03_digital_kyc.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_Success() {
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .fullName("Test User")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
    }

    @Test
    void findByUsername_Success() {
        User user = User.builder()
                .username("findme")
                .password("password123")
                .email("findme@example.com")
                .fullName("Find Me")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("findme");

        assertTrue(found.isPresent());
        assertEquals("findme", found.get().getUsername());
    }

    @Test
    void findByEmail_Success() {
        User user = User.builder()
                .username("emailtest")
                .password("password123")
                .email("unique@example.com")
                .fullName("Email Test")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("unique@example.com");

        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }

    @Test
    void countByRole_Success() {
        User user1 = User.builder()
                .username("officer1")
                .password("password123")
                .email("officer1@example.com")
                .fullName("Officer One")
                .role(User.Role.OFFICER)
                .isActive(true)
                .build();

        User user2 = User.builder()
                .username("officer2")
                .password("password123")
                .email("officer2@example.com")
                .fullName("Officer Two")
                .role(User.Role.OFFICER)
                .isActive(true)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        long count = userRepository.countByRole(User.Role.OFFICER);

        assertTrue(count >= 2);
    }

    @Test
    void countByIsActive_Success() {
        User activeUser = User.builder()
                .username("active")
                .password("password123")
                .email("active@example.com")
                .fullName("Active User")
                .role(User.Role.CUSTOMER)
                .isActive(true)
                .build();

        userRepository.save(activeUser);

        long count = userRepository.countByIsActive(true);

        assertTrue(count >= 1);
    }
}
