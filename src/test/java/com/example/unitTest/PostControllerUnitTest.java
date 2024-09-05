package com.example.unitTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.controller.PostController;
import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Base64;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
    void shouldCreatePostSuccessfully() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile title = new MockMultipartFile("title", "", "text/plain", "Test Title".getBytes());
        MockMultipartFile subtitle = new MockMultipartFile("subtitle", "", "text/plain", "Test Subtitle".getBytes());
        MockMultipartFile description = new MockMultipartFile("description", "", "text/plain", "Test Description".getBytes());

        Mockito.when(postService.createPost(any(PostInputDto.class), any())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts")
                        .file(image)
                        .file(title)
                        .file(subtitle)
                        .file(description)
                        .with(csrf())  // Add CSRF token
                        .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));

        verify(postService, times(1)).createPost(any(PostInputDto.class), any());
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldRetrieveCorrectPost() throws Exception {
        byte[] imageBytes = "test image content".getBytes();
        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(1L);
        postOutputDto.setTitle("Test Title");
        postOutputDto.setSubtitle("Test Subtitle");
        postOutputDto.setDescription("Test Description");
        postOutputDto.setImgdata(imageBytes);

        Mockito.when(postService.getPost(1L)).thenReturn(postOutputDto);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/posts/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", is("Test Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subtitle", is("Test Subtitle")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", is("Test Description")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.imgdata", is(imageBase64)));

        verify(postService, times(1)).getPost(1L);
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldRetrieveAllPosts() throws Exception {
        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(1L);
        postOutputDto.setTitle("Test Title");
        postOutputDto.setSubtitle("Test Subtitle");
        postOutputDto.setDescription("Test Description");

        Mockito.when(postService.getAllPosts()).thenReturn(Collections.singletonList(postOutputDto));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", is("Test Title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].subtitle", is("Test Subtitle")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", is("Test Description")));

        verify(postService, times(1)).getAllPosts();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void shouldUpdatePostSuccessfully() throws Exception {
        // Prepare multipart files
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        MockMultipartFile title = new MockMultipartFile("title", "", "text/plain", "Updated Title".getBytes());
        MockMultipartFile subtitle = new MockMultipartFile("subtitle", "", "text/plain", "Updated Subtitle".getBytes());
        MockMultipartFile description = new MockMultipartFile("description", "", "text/plain", "Updated Description".getBytes());

        // Perform multipart PUT request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/1")
                        .file(image)
                        .file(title)
                        .file(subtitle)
                        .file(description)
                        .with(csrf())  // Add CSRF token
                        .with(request -> {
                            request.setMethod("PUT"); // Set the HTTP method to PUT explicitly
                            return request;
                        })
                        .contentType("multipart/form-data"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Check for 200 OK status
                .andExpect(MockMvcResultMatchers.content().string("Post successfully updated"));

        // Verify that the service method was called once
        verify(postService, times(1)).updatePost(eq(1L), any(PostInputDto.class));
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldDeletePostSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1")
                        .with(csrf()))  // Add CSRF token
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Post successfully deleted"));

        verify(postService, times(1)).deletePost(eq(1L), any());
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Post not found")).when(postService).deletePost(eq(1L), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1")
                        .with(csrf()))  // Add CSRF token
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Post not found"));

        verify(postService, times(1)).deletePost(eq(1L), any());
    }

    @Test
    @WithMockUser(username="testuser", roles="ADMIN")
    void shouldReturnForbiddenWhenUserIsNotAuthorizedToDelete() throws Exception {
        doThrow(new SecurityException("You are not authorized to delete this post")).when(postService).deletePost(eq(1L), any());

        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/1")
                        .with(csrf()))  // Add CSRF token
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("You are not authorized to delete this post"));

        verify(postService, times(1)).deletePost(eq(1L), any());
    }
}
