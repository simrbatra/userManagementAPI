package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testsaveUser() {
        User user = new User("Utkarsh", "utkarsh@gmail.com");
        user.setName("Simar");
        user.setEmail("simar@example.com");


        User savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testFindByEmailFound() {
        User user = new User("Utkarsh", "utkarsh@gmail.com");
        user.setName("Simar");
        user.setEmail("simar@example.com");


        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("simar@example.com");
        assertThat(found).isPresent();
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("notfound@example.com");
        assertThat(found).isNotPresent();
    }

    @Test
    public void testUpdateUser() {
        User user = new User("Utkarsh", "utkarsh@gmail.com");
        user.setName("Simar");
        user.setEmail("simar@example.com");

        User saved = userRepository.save(user);
        saved.setName("Updated Simar");

        User updated = userRepository.save(saved);
        assertThat(updated.getName()).isEqualTo("Updated Simar");
    }
}
