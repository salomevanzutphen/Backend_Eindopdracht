package com.testing.serviceTest;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
    private ImageData imageData;

    @BeforeEach
    void setUp() {
        // Arrange
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

        imageData = new ImageData();
        imageData.setId(1L);
        imageData.setName("testImage.jpg");
        imageData.setType("image/jpeg");
        imageData.setImageData("compressed-image-data".getBytes());

        lenient().when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void testCreatePost_Success() throws IOException {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(image.getBytes()).thenReturn("image-data".getBytes());

        // Act
        Long postId = postService.createPost(postInputDto, userDetails);

        // Assert
        assertEquals(1L, postId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(imageDataRepository, times(1)).save(any(ImageData.class));  // Corrected assertion
    }

    @Test
    void testCreatePost_WithImage() throws IOException {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(image.getOriginalFilename()).thenReturn("testImage.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getBytes()).thenReturn("image-data".getBytes());
        when(imageDataRepository.save(any(ImageData.class))).thenReturn(imageData);

        // Act
        Long postId = postService.createPost(postInputDto, userDetails);

        // Assert
        assertEquals(1L, postId);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(imageDataRepository, times(1)).save(any(ImageData.class));
    }

    @Test
    void testCreatePost_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(postInputDto, userDetails));
    }

    @Test
    void testGetPost_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        PostOutputDto result = postService.getPost(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Subtitle", result.getSubtitle());
        assertEquals("Test Description", result.getDescription());
        assertNull(result.getImgdata()); // No image data set initially
    }

    @Test
    void testGetPost_PostNotFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testGetAllPosts() {
        // Arrange
        List<Post> posts = List.of(post);  // Create a list with a single post
        when(postRepository.findAll()).thenReturn(posts);

        // Act
        List<PostOutputDto> result = postService.getAllPosts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        assertEquals("Test Subtitle", result.get(0).getSubtitle());
        assertEquals("Test Description", result.get(0).getDescription());
    }

    @Test
    void testUpdatePost_Success() throws IOException {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(image.getOriginalFilename()).thenReturn("testImage.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getBytes()).thenReturn("image-data".getBytes());

        // Act
        postService.updatePost(1L, postInputDto);

        // Assert
        verify(postRepository, times(1)).save(post);
        verify(imageDataRepository, atMostOnce()).save(any(ImageData.class));
    }

    @Test
    void testDeletePost_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertDoesNotThrow(() -> postService.deletePost(1L, userDetails));
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void testDeletePost_UserNotAuthorized() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        post.setAdmin(anotherUser);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(SecurityException.class, () -> postService.deletePost(1L, userDetails));
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L, userDetails));
        verify(postRepository, never()).delete(any(Post.class));
    }
}
