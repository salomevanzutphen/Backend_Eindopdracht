package nl.novi.LivingInSync.controller;

import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import nl.novi.LivingInSync.dto.input.PostInputDto;
import nl.novi.LivingInSync.dto.output.PostOutputDto;
import nl.novi.LivingInSync.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService service) {
        this.postService = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostOutputDto> getPost(@PathVariable Long id) {
        PostOutputDto postOutputDto = postService.getPost(id);
        return ResponseEntity.ok(postOutputDto);
    }

    @GetMapping
    public ResponseEntity<List<PostOutputDto>> getAllPosts() {
        List<PostOutputDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Object> createPost(
            @RequestPart (value = "file", required = false) MultipartFile file,
            @RequestPart ("title") String title,
            @RequestPart ("name") String name,
            @RequestPart ("description") String description,
            BindingResult br,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        if (br.hasFieldErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fe : br.getFieldErrors()) {
                sb.append(fe.getField()).append(": ");
                sb.append(fe.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        PostInputDto postInputDto = new PostInputDto();
      //  postInputDto.setImage();
        postInputDto.setTitle(title);
        postInputDto.setName(name);
        postInputDto.setDescription(description);

        // Pass both postInputDto and userDetails to the service
        Long id = postService.createPost(postInputDto, userDetails);

        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/" + id).toUriString());

        return ResponseEntity.created(uri).body(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @Valid @RequestBody PostInputDto postInputDto, BindingResult br, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (br.hasFieldErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fe : br.getFieldErrors()) {
                sb.append(fe.getField()).append(": ");
                sb.append(fe.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        postService.updatePost(id, postInputDto);
        return new ResponseEntity<>("Post successfully updated", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            postService.deletePost(id, userDetails);
            return new ResponseEntity<>("Post successfully deleted", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>("You are not authorized to delete this post", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while deleting the post", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}