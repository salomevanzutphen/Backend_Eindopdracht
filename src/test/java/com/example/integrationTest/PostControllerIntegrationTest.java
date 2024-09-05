package com.example.integrationTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LivingInSyncApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Ensure test user exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User testUser = new User();
            testUser.setUsername("admin");
            testUser.setPassword("password");
            testUser.setName("Salomé");
            testUser.setEmail("livinginsync@example.com");
            testUser.setBirthday(LocalDate.of(1990, 1, 1));

            userRepository.save(testUser);
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateCorrectPost() throws Exception {
        // Mock files for multipart request
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", "image/jpeg", "Test Image Content".getBytes());

        MockMultipartFile titlePart = new MockMultipartFile(
                "title", "", "text/plain", "Hallo banaan".getBytes());

        MockMultipartFile subtitlePart = new MockMultipartFile(
                "subtitle", "", "text/plain", "Test Subtitle".getBytes());

        MockMultipartFile descriptionPart = new MockMultipartFile(
                "description", "", "text/plain", "This is a description of the test post.".getBytes());

        // Perform the request and validate response
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(imageFile)
                        .file(titlePart)
                        .file(subtitlePart)
                        .file(descriptionPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/posts/1")) // Expect full URL
                .andExpect(MockMvcResultMatchers.content().string("1")); // Expect the ID as a plain string
    }
}
