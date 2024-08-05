package nl.novi.LivingInSync.service;

import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

        // Retrieve the authenticated admin user
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the admin for the post
        post.setAdmin(admin);

        // Save the post with the admin association
        post = postRepository.save(post);
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

    public void updatePost(Long id, PostInputDto postInputDto) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setTitle(postInputDto.getTitle());
        post.setName(postInputDto.getName());
        post.setDescription(postInputDto.getDescription());
        if (postInputDto.getImage() != null) {
            post.setImage(postInputDto.getImage());
        }
        postRepository.save(post);
    }

    public void deletePost(Long id, UserDetails userDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Retrieve the authenticated admin user
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the admin has permission to delete the post
        if (!post.getAdmin().equals(admin)) {
            throw new SecurityException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    private Post mapToEntity(PostInputDto postInputDto) throws IOException {
        Post post = new Post();
        post.setTitle(postInputDto.getTitle());
        post.setName(postInputDto.getName());
        post.setDescription(postInputDto.getDescription());
        if (postInputDto.getImage() != null) {
            post.setImage(postInputDto.getImage());
        }
        return post;
    }

    private PostOutputDto mapToOutputDto(Post post) {
        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(post.getId());
        postOutputDto.setTitle(post.getTitle());
        postOutputDto.setName(post.getName());
        postOutputDto.setDescription(post.getDescription());
        postOutputDto.setImage(post.getImage());
        return postOutputDto;
    }
}
