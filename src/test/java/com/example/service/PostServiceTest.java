package com.example.service;

import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreatePost() {
        byte[] imageBytes = "test.jpg".getBytes();

        PostInputDto inputDto = new PostInputDto();
        inputDto.setTitle("Test Title");
        inputDto.setName("Test Name");
        inputDto.setDescription("Test Description");
        inputDto.setImage(imageBytes);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setName("Test Name");
        post.setDescription("Test Description");
        post.setImage(imageBytes);

        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long postId = null;
        try {
            postId = postService.createPost(inputDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(postId, "Post ID should not be null.");
        assertEquals(1L, postId, "Post ID should be 1.");
    }

    @Test
    void testGetPost() {
        byte[] imageBytes = "test.jpg".getBytes();

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setName("Test Name");
        post.setDescription("Test Description");
        post.setImage(imageBytes);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostOutputDto outputDto = postService.getPost(1L);

        assertNotNull(outputDto, "Output DTO should not be null.");
        assertEquals(1L, outputDto.getId(), "Post ID should be 1.");
        assertEquals("Test Title", outputDto.getTitle(), "Post title should match.");
        assertEquals("Test Name", outputDto.getName(), "Post name should match.");
        assertEquals("Test Description", outputDto.getDescription(), "Post description should match.");
        assertArrayEquals(imageBytes, outputDto.getImage(), "Post image should match.");
    }

    @Test
    void testGetPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(1L), "Should throw ResourceNotFoundException if post is not found.");
    }

    @Test
    void testGetAllPosts() {
        byte[] imageBytes1 = "test1.jpg".getBytes();
        byte[] imageBytes2 = "test2.jpg".getBytes();

        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Test Title 1");
        post1.setName("Test Name 1");
        post1.setDescription("Test Description 1");
        post1.setImage(imageBytes1);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Test Title 2");
        post2.setName("Test Name 2");
        post2.setDescription("Test Description 2");
        post2.setImage(imageBytes2);

        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        List<PostOutputDto> outputDtos = postService.getAllPosts();

        assertNotNull(outputDtos, "Output DTOs should not be null.");
        assertEquals(2, outputDtos.size(), "There should be 2 posts.");
    }

    @Test
    void testUpdatePost() {
        byte[] oldImageBytes = "old.jpg".getBytes();
        byte[] newImageBytes = "new.jpg".getBytes();

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Old Title");
        post.setName("Old Name");
        post.setDescription("Old Description");
        post.setImage(oldImageBytes);

        PostInputDto inputDto = new PostInputDto();
        inputDto.setTitle("New Title");
        inputDto.setName("New Name");
        inputDto.setDescription("New Description");
        inputDto.setImage(newImageBytes);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        try {
            postService.updatePost(1L, inputDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        verify(postRepository, times(1)).save(post);
        assertEquals("New Title", post.getTitle(), "Post title should be updated.");
        assertEquals("New Name", post.getName(), "Post name should be updated.");
        assertEquals("New Description", post.getDescription(), "Post description should be updated.");
        assertArrayEquals(newImageBytes, post.getImage(), "Post image should be updated.");
    }

    @Test
    void testDeletePost() {
        byte[] imageBytes = "test.jpg".getBytes();

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setName("Test Name");
        post.setDescription("Test Description");
        post.setImage(imageBytes);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        verify(postRepository, times(1)).delete(post);
    }
}
