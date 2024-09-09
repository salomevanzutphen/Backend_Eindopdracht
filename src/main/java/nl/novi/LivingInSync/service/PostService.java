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
        this.imageDataRepository = imageDataRepository; // Inject the ImageDataRepository
    }

    public Long createPost(PostInputDto postInputDto, UserDetails userDetails) {
        Post post = mapToEntity(postInputDto);

        // Retrieve the authenticated admin user
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Set the admin for the post
        post.setAdmin(admin);

        // Save the post to generate an ID for it
        post = postRepository.save(post);

        // If there is an image, save it and associate it with the post
        if (postInputDto.getImage() != null) {
            MultipartFile imageFile = postInputDto.getImage();
            try {
                ImageData imgData = new ImageData();
                imgData.setName(imageFile.getOriginalFilename());
                imgData.setType(imageFile.getContentType());
                imgData.setImageData(ImageUtil.compressImage(imageFile.getBytes()));

                // Set the image's post reference
                imgData.setPost(post);

                // Save the image and set it to the post
                post.setImageData(imageDataRepository.save(imgData));
            } catch (IOException e) {
                throw new RuntimeException("Error while processing image", e);
            }
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

    public void updatePost(Long id, PostInputDto postInputDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Update post details
        post.setTitle(postInputDto.getTitle());
        post.setSubtitle(postInputDto.getSubtitle());
        post.setDescription(postInputDto.getDescription());

        // Check if a new image is provided
        if (postInputDto.getImage() != null && !postInputDto.getImage().isEmpty()) {
            MultipartFile imageFile = postInputDto.getImage();
            try {
                ImageData imgData = post.getImageData();
                if (imgData == null) {
                    imgData = new ImageData(); // Create new ImageData if it doesn't exist
                    imgData.setPost(post);
                    post.setImageData(imgData);
                }
                imgData.setName(imageFile.getOriginalFilename());
                imgData.setType(imageFile.getContentType());
                imgData.setImageData(ImageUtil.compressImage(imageFile.getBytes()));

                imageDataRepository.save(imgData); // Save the updated image data
            } catch (IOException e) {
                throw new RuntimeException("Error while processing image", e);
            }
        }

        postRepository.save(post); // Save the post after updating image
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

        // Set image data if it exists
        if (post.getImageData() != null) {
            postOutputDto.setImgdata(ImageUtil.decompressImage(post.getImageData().getImageData()));
        }

        return postOutputDto;
    }
}