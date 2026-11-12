package io.github.ramdathdylan.brickreviews.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import io.github.ramdathdylan.brickreviews.TestcontainersConfiguration;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Import(TestcontainersConfiguration.class)  
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void newUserGetsSafeDefaults() throws Exception {
    
        User user = new User("dylan123@g.rit.edu", "fakehash");

        User saved = userRepository.saveAndFlush(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRole()).isEqualTo(Role.STUDENT);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(saved.isVerified()).isFalse();
        assertThat(saved.canContribute()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();

    };

    @Test
    void emailIsStoredLowercaseAndTrimmed() {
        User user = new User("  DYLAN123@G.RIT.EDU  ", "fakehash");
 
        User saved = userRepository.saveAndFlush(user);
 
        assertThat(saved.getEmail()).isEqualTo("dylan123@g.rit.edu");
        assertThat(userRepository.findByEmail("dylan123@g.rit.edu")).isPresent();
        assertThat(userRepository.existsByEmail("dylan123@g.rit.edu")).isTrue();
    }

    @Test
    void duplicateEmailIsRejectedByTheDatabase() {
        userRepository.saveAndFlush(new User("dylan123@g.rit.edu", "fakehash"));
 
        User duplicate = new User("dylan123@g.rit.edu", "otherhash");
 
        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

