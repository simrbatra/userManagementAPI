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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    private User user1;
    private User user2;
    private List<User> manyUsers;
    private long initialUserCount;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule());

        user1 = new User("John Doe", "John.doe@example.com");
        user2 = new User("Jane Smith", "Jane.smith@example.com");

        List<User> savedUsers = userRepository.saveAll(Arrays.asList(user1, user2));
        user1 = savedUsers.get(0);
        user2 = savedUsers.get(1);

        manyUsers = IntStream.rangeClosed(1, 25)
                .mapToObj(i -> new User("User " + i, "user" + i + "@example.com"))
                .collect(Collectors.toList());

        userRepository.saveAll(manyUsers);
        initialUserCount = userRepository.count();
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void testCreateUsers() throws Exception {
        List<User> newUsers = Arrays.asList(
                new User("Utkarsh", "utkarsh@gmail.com"),
                new User("Khushi", "khushi@gmail.com")
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUsers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is("Khushi")));
    }

    @Test
    void testDeleteUserFound() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", user1.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById((Long) user1.getId()));
        assertEquals(initialUserCount - 1, userRepository.count());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long nonExistentId = 9999L;
        mockMvc.perform(delete("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        assertEquals(initialUserCount, userRepository.count());
    }
}
