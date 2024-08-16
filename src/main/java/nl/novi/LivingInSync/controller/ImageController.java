package nl.novi.LivingInSync.controller;

import nl.novi.LivingInSync.model.ImageData;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.repository.ImageDataRepository;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.service.ImageDataService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageDataService imageDataService;
    private final ImageDataRepository imageDataRepository;
    private final PostRepository postRepository;

    public ImageController(ImageDataService imageDataService, ImageDataRepository imageDataRepository, PostRepository postRepository) {
        this.imageDataService = imageDataService;
        this.imageDataRepository = imageDataRepository;
        this.postRepository = postRepository;
    }

    @PostMapping
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile multipartFile, @RequestParam Long id) throws IOException {
        String image = imageDataService.uploadImage(multipartFile, id);
        return ResponseEntity.ok("Image has been uploaded, " + image);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> downloadImage(@PathVariable Long id) throws IOException {
        byte[] image = imageDataService.downloadImage(id);
        Optional<Post> post = postRepository.findById(id);
        Optional<ImageData> dbImageData = imageDataRepository.findById(post.get().getImageData().getId());
        MediaType mediaType = MediaType.valueOf(dbImageData.get().getType());
        return ResponseEntity.ok().contentType(mediaType).body(image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateImage(@PathVariable Long id, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        String updatedImage = imageDataService.updateImage(id, multipartFile);
        return ResponseEntity.ok("Image has been updated, " + updatedImage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteImage(@PathVariable Long id) {
        imageDataService.deleteImage(id);
        return ResponseEntity.ok("Image has been deleted");
    }
}
