package nl.novi.LivingInSync.service;

import nl.novi.LivingInSync.model.ImageData;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.repository.ImageDataRepository;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.utils.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageDataService {

    private final ImageDataRepository imageDataRepository;
    private final PostRepository postRepository;

    public ImageDataService(ImageDataRepository imageDataRepository, PostRepository postRepository) {
        this.imageDataRepository = imageDataRepository;
        this.postRepository = postRepository;
    }

    // Upload or update image
    public String uploadImage(MultipartFile multipartFile, Long postId) throws IOException {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new IllegalArgumentException("Post with id " + postId + " not found");
        }

        Post existingPost = post.get();

        ImageData imageData;

        // Check if an image already exists for this post
        if (existingPost.getImageData() != null) {
            // Update existing image data
            imageData = existingPost.getImageData();
        } else {
            // Create a new image data
            imageData = new ImageData();
            imageData.setPost(existingPost);  // Set post for the new image data
        }

        // Update image details
        imageData.setName(multipartFile.getOriginalFilename());
        imageData.setType(multipartFile.getContentType());
        imageData.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));

        // Save or update image data
        ImageData savedImage = imageDataRepository.save(imageData);
        existingPost.setImageData(savedImage);
        postRepository.save(existingPost);

        return savedImage.getName();
    }

    // Download image
    public byte[] downloadImage(Long postId) throws IOException {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty() || post.get().getImageData() == null) {
            throw new IllegalArgumentException("Image not found for post with id " + postId);
        }

        ImageData imageData = post.get().getImageData();
        return ImageUtil.decompressImage(imageData.getImageData());
    }}


