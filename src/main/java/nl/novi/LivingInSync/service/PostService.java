package nl.novi.LivingInSync.service;

import jakarta.transaction.Transactional;
import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.model.Post;
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

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Long createPost(PostInputDto postInputDto, UserDetails userDetails) throws IOException {
        Post post = mapToEntity(postInputDto);

        // Find the admin user from the repository
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the admin user for the post
        post.setAdmin(admin);

        // Save the post first to generate an ID for the post
        post = postRepository.save(post);

        // Handle the image if it exists
        if (postInputDto.getImage() != null && !postInputDto.getImage().isEmpty()) {
            processImage(postInputDto.getImage(), post);
        }

        return post.getId();
    }

    public PostOutputDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return mapToOutputDto(post);
    }

    public List<PostOutputDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::mapToOutputDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePost(Long id, PostInputDto postInputDto) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setTitle(postInputDto.getTitle());
        post.setSubtitle(postInputDto.getSubtitle());
        post.setDescription(postInputDto.getDescription());

        // Handle the image if it exists
        if (postInputDto.getImage() != null && !postInputDto.getImage().isEmpty()) {
            processImage(postInputDto.getImage(), post);
        }

        postRepository.save(post);
    }

    public void deletePost(Long id, UserDetails userDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!post.getAdmin().equals(admin)) {
            throw new SecurityException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    private void processImage(MultipartFile image, Post post) throws IOException {
        // Extract the image details from MultipartFile
        post.setImageName(image.getOriginalFilename());
        post.setImageType(image.getContentType());
        post.setImageData(ImageUtil.compressImage(image.getBytes()));

        // Save the post with image details
        postRepository.save(post);
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

        // Check if imageData is not null before decompressing
        if (post.getImageData() != null && post.getImageData().length > 0) {
            postOutputDto.setImgdata(ImageUtil.decompressImage(post.getImageData()));
        }

        return postOutputDto;
    }
}
