package nl.novi.LivingInSync.service;

import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.ImageData;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.ImageDataRepository;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.utils.ImageUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageDataRepository imageDataRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, ImageDataRepository imageDataRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageDataRepository = imageDataRepository;
    }

    public Long createPost(PostInputDto postInputDto, UserDetails userDetails) {
        User admin = findAdminUser(userDetails);
        Post post = mapToEntity(postInputDto);
        post.setAdmin(admin);

        post = postRepository.save(post);

        if (postInputDto.getImage() != null) {
            saveImageForPost(postInputDto.getImage(), post);
        }

        return post.getId();
    }

    public PostOutputDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return mapToOutputDto(post);
    }

    public List<PostOutputDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToOutputDto)
                .collect(Collectors.toList());
    }

    public void updatePost(Long id, PostInputDto postInputDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        updatePostDetails(post, postInputDto);

        if (postInputDto.getImage() != null && !postInputDto.getImage().isEmpty()) {
            saveImageForPost(postInputDto.getImage(), post);
        }

        postRepository.save(post);
    }

    public void deletePost(Long id, UserDetails userDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User admin = findAdminUser(userDetails);

        if (!post.getAdmin().equals(admin)) {
            throw new SecurityException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    // Helper method to find admin user
    private User findAdminUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Helper method to save image for a post
    private void saveImageForPost(MultipartFile imageFile, Post post) {
        try {
            ImageData imgData = new ImageData();
            imgData.setName(imageFile.getOriginalFilename());
            imgData.setType(imageFile.getContentType());
            imgData.setImageData(ImageUtil.compressImage(imageFile.getBytes()));
            imgData.setPost(post);
            post.setImageData(imageDataRepository.save(imgData));
        } catch (IOException e) {
            throw new RuntimeException("Error while processing image", e);
        }
    }

    // Helper method to update post details
    private void updatePostDetails(Post post, PostInputDto postInputDto) {
        post.setTitle(postInputDto.getTitle());
        post.setSubtitle(postInputDto.getSubtitle());
        post.setDescription(postInputDto.getDescription());
    }

    private Post mapToEntity(PostInputDto postInputDto) {
        Post post = new Post();
        post.setTitle(postInputDto.getTitle());
        post.setSubtitle(postInputDto.getSubtitle());
        post.setDescription(postInputDto.getDescription());
        return post;
    }

    private PostOutputDto mapToOutputDto(Post post) {
        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(post.getId());
        postOutputDto.setTitle(post.getTitle());
        postOutputDto.setSubtitle(post.getSubtitle());
        postOutputDto.setDescription(post.getDescription());

        if (post.getImageData() != null) {
            postOutputDto.setImgdata(ImageUtil.decompressImage(post.getImageData().getImageData()));
        }

        return postOutputDto;
    }
}
