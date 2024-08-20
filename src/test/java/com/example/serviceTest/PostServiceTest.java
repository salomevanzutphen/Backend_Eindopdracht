package com.example.serviceTest;

import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.ImageData;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.ImageDataRepository;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.service.PostService;
import nl.novi.LivingInSync.utils.ImageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageDataRepository imageDataRepository;

    @InjectMocks
    private PostService postService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private MultipartFile image;

    private PostInputDto postInputDto;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        postInputDto = new PostInputDto();
        postInputDto.setTitle("Test Title");
        postInputDto.setSubtitle("Test Name");
        postInputDto.setDescription("Test Description");
        postInputDto.setImage(image);

        user = new User();
        user.setUsername("testUser");

        post = new Post();
        post.setId(1L);
        post.setAdmin(user);
    }

    @Test
    void testCreatePost_Success() throws IOException {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long postId = postService.createPost(postInputDto, userDetails);

        assertEquals(1L, postId);
        verify(postRepository, times(2)).save(any(Post.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(postInputDto, userDetails));
    }

    @Test
    void testCreatePost_WithImage() throws IOException {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(image.getOriginalFilename()).thenReturn("testImage.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getBytes()).thenReturn("image-data".getBytes());

        Long postId = postService.createPost(postInputDto, userDetails);

        assertEquals(1L, postId);
        verify(imageDataRepository, times(1)).save(any(ImageData.class));
    }
}
