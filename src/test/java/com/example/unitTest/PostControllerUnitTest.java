package com.example.unitTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.controller.PostController;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.security.JwtService;
import nl.novi.LivingInSync.security.CustomUserDetailsService;
import nl.novi.LivingInSync.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;

import static org.hamcrest.Matchers.is;

@WebMvcTest(PostController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {LivingInSyncApplication.class, PostController.class})
class PostControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtService jwtService;

    @MockBean
    PostService postService;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.reset(postService, jwtService, customUserDetailsService);
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldRetrieveCorrectPost() throws Exception {
        byte[] imageBytes = "test image content".getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(1L);
        postOutputDto.setTitle("Test Title");
        postOutputDto.setSubtitle("Test Name");
        postOutputDto.setDescription("Test Description");
        postOutputDto.setImgdata(imageBytes);

        Mockito.when(postService.getPost(1L)).thenReturn(postOutputDto);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/posts/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("Test Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Test Subtitle")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("Test Description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imgdata", is(imageBase64)));
    }
}
