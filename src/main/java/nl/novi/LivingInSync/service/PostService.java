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
import java.util.Optional;
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

    public Long createPost(PostInputDto postInputDto, UserDetails userDetails) throws IOException {
        Post post = mapToEntity(postInputDto);

        // Retrieve the authenticated admin user
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the admin for the post
        post.setAdmin(admin);

        // Save the post first to get the post ID
        post = postRepository.save(post);

        // Handle the image if it exists
        if (postInputDto.getImage() != null && !postInputDto.getImage().isEmpty()) {
            ImageData imgData = new ImageData();
            imgData.setName(postInputDto.getImage().getOriginalFilename());
            imgData.setType(postInputDto.getImage().getContentType());
            imgData.setImageData(ImageUtil.compressImage(postInputDto.getImage().getBytes()));
            imgData.setPost(post);

            ImageData savedImage = imageDataRepository.save(imgData);
            post.setImage(savedImage);

            // Update the post with the saved image
            postRepository.save(post);
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

    public void updatePost(Long id, PostInputDto postInputDto) throws IOException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setTitle(postInputDto.getTitle());
        post.setName(postInputDto.getName());
        post.setDescription(postInputDto.getDescription());

        if (postInputDto.getImage() != null){
            ImageData image = post.getImageData();
            imageDataRepository.delete(image);

            ImageData imgData = new ImageData();
            imgData.setName(postInputDto.getImage().getOriginalFilename());
            imgData.setType(postInputDto.getImage().getContentType());
            imgData.setImageData(ImageUtil.compressImage(postInputDto.getImage().getBytes()));
            imgData.setPost(post);

            ImageData savedImage = imageDataRepository.save(imgData);
            post.setImage(savedImage);
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

    private Post mapToEntity(PostInputDto postInputDto) {
        Post post = new Post();
        post.setTitle(postInputDto.getTitle());
        post.setName(postInputDto.getName());
        post.setDescription(postInputDto.getDescription());
        return post;
    }

    private PostOutputDto mapToOutputDto(Post post) {
        PostOutputDto postOutputDto = new PostOutputDto();
        postOutputDto.setId(post.getId());
        postOutputDto.setTitle(post.getTitle());
        postOutputDto.setName(post.getName());
        postOutputDto.setDescription(post.getDescription());
        if (post.getImageData() != null){
            postOutputDto.setImgdata( ImageUtil.decompressImage(post.getImageData().getImageData()));
        }
        return postOutputDto;
    }

}
