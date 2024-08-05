package com.example.controller;

import static org.hamcrest.Matchers.is;
import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = LivingInSyncApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostService postService;

    @Test
    void shouldCreateCorrectPost() throws Exception {

        String requestJson = """
                {
                    "title": "Test Title",
                    "name": "Test Name",
                    "description": "Test Description",
                    "image": "test.jpg".getBytes()
                }
                """;

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void shouldRetrieveCorrectPost() throws Exception {

        // Setup a post in the database
        PostInputDto postInputDto = new PostInputDto();
        postInputDto.setTitle("Test Title");
        postInputDto.setName("Test Name");
        postInputDto.setDescription("Test Description");
        postInputDto.setImage("test.jpg".getBytes());

        Long postId = postService.createPost(postInputDto);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/posts/" + postId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("Test Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Test Name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("Test Description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image", is("test.jpg".getBytes())));
    }
}