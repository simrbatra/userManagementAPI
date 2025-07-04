package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
public class UserConrollerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private User user2;
    @BeforeEach()
    void setUp(){
        userRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());
        User initialUser1 = new User("John Doe", "john.doe@example.com");
        User initialUser2 = new User("John Doe", "john.doe@example.com");

        List<User> savedInitialUsers = userRepository.saveAll(Arrays.asList(initialUser1, initialUser2));

        this.user1 = savedInitialUsers.get(0);
        this.user2 = savedInitialUsers.get(1);

        manyUsers = IntStream.rangeClosed(1,25).mapToObj(i -> new User("User "+i, "user"+i +"@example.com")).collect(Collectors.toList());
        userRepository.saveAll(manyUsers);
    }
}
