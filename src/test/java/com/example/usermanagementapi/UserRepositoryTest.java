package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  //(basePackageClasses = UserRepository.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAllInBatch();

        user1 = new User("Alice Wonderland", "alice@example.com");
        user2 = new User("Bob the Builder", "bob@example.com");

        entityManager.persist(user1);
        entityManager.persist(user2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindByEmailFound() {
        Optional<User> foundUser = userRepository.findByEmail(user1.getEmail());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user1.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.get().getId()).isEqualTo(user1.getId());
    }
    void testFindByEmailNotFound(){
        Optional<User> notFoundUser = userRepository.findByEmail("notfound@example.com");

        assertThat(notFoundUser).isNotPresent();
    }
    void testsaveUser(){
        User newUser = new User("Charlie chaplin", "charlie@example.com");
        userRepository.save(newUser);
        User saveduser = userRepository.save(newUser);
        assertThat(saveduser).isNotNull();
        assertThat(saveduser.getId()).isNotNull();
        assertThat(saveduser.getName()).isEqualTo("Charlie Chaplin");
        assertThat(saveduser.getEmail()).isEqualTo("charlie@example.com");

    }
    @Test
    void testUpdateUser() {
        String newName = "Manoj Reddy";
        String newEmail = "manojreddy@gmail.com";

        User user3 = new User("Yashika", "yashika@example.com");
        user3 = userRepository.save(user3);  // persist first

        user3.setName(newName);
        user3.setEmail(newEmail);

        User updatedUser = userRepository.save(user3);

        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }



}
