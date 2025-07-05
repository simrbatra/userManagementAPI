package com.example.usermanagementapi;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private List<User> manyUsers;
    private long initialUserCount;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        User initialUser1 = new User("John Doe", "john.doe1@example.com");
        User initialUser2 = new User("Jane Doe", "jane.doe2@example.com");

        List<User> savedInitialUsers = userRepository.saveAll(Arrays.asList(initialUser1, initialUser2));
        this.user1 = savedInitialUsers.get(0);
        this.user2 = savedInitialUsers.get(1);


        manyUsers = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> new User("User " + i, "user" + i + "@example.com"))
                .collect(Collectors.toList());
        userRepository.saveAll(manyUsers);

        initialUserCount = userRepository.count(); // important for your delete tests
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void testCreateUsers() throws Exception {
        User newUser1 = new User("Alice Brown", "alice.brown@example.com");
        User newUser2 = new User("Jane Smith", "jane.smith@example.com");
        List<User> newUsers = Arrays.asList(newUser1, newUser2);

        mockMvc.perform(post("/api/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Alice Brown")))
                .andExpect(jsonPath("$[0].email", is("alice.brown@example.com")))
                .andExpect(jsonPath("$[1].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane.smith@example.com")));
    }

    @Test
    void testDeleteUserFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user1.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user1.getId()));
        assertEquals(initialUserCount - 1, userRepository.count());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long nonExistentId = user1.getId() + 1000; // safe assumption it doesn't exist
        mockMvc.perform(delete("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        assertEquals(initialUserCount, userRepository.count());
    }
}
