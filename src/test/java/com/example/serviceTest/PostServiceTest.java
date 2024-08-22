package com.example.serviceTest;

import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
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
        postInputDto.setSubtitle("Test Subtitle");
        postInputDto.setDescription("Test Description");
        postInputDto.setImage(image);

        user = new User();
        user.setUsername("testUser");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setSubtitle("Test Subtitle");
        post.setDescription("Test Description");
        post.setAdmin(user);

        // Use lenient stubbing to avoid UnnecessaryStubbingException
        lenient().when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void testCreatePost_Success() throws IOException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(image.getBytes()).thenReturn("image-data".getBytes());

        Long postId = postService.createPost(postInputDto, userDetails);

        assertEquals(1L, postId);
        verify(postRepository, times(2)).save(any(Post.class)); // Expecting two saves: one before image, one after
    }

    @Test
    void testCreatePost_UserNotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(postInputDto, userDetails));
    }

    @Test
    void testCreatePost_WithImage() throws IOException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(image.getOriginalFilename()).thenReturn("testImage.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getBytes()).thenReturn("image-data".getBytes());

        Long postId = postService.createPost(postInputDto, userDetails);

        assertEquals(1L, postId);
        verify(imageDataRepository, times(1)).save(any(ImageData.class));
        verify(postRepository, times(2)).save(any(Post.class)); // Expecting two saves: one before image, one after
    }

    @Test
    void testGetPost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostOutputDto result = postService.getPost(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Subtitle", result.getSubtitle());
        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void testGetPost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testDeletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> postService.deletePost(1L, userDetails));
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void testDeletePost_UserNotAuthorized() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        post.setAdmin(anotherUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> postService.deletePost(1L, userDetails));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L, userDetails));
        verify(postRepository, never()).delete(any(Post.class));
    }
}
