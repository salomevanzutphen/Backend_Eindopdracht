package nl.novi.LivingInSync.service;


import nl.novi.LivingInSync.model.ImageData;
import nl.novi.LivingInSync.model.Post;
import nl.novi.LivingInSync.repository.ImageDataRepository;
import nl.novi.LivingInSync.repository.PostRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.utils.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageDataService {

    private final ImageDataRepository imageDataRepository;
    private final PostRepository postRepository;


    public ImageDataService(ImageDataRepository imageDataRepository, PostRepository postRepository){
        this.imageDataRepository = imageDataRepository;
        this.postRepository = postRepository;
    }

    //de identifier van een post is een long id
    public String uploadImage(MultipartFile multipartFile, Long id) throws IOException {
        Optional<Post> post = postRepository.findById(id);
        Post post1 = post.get();

        ImageData imgData = new ImageData();
        imgData.setName(multipartFile.getName());
        imgData.setType(multipartFile.getContentType());
        imgData.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));
        imgData.setPost(post1);

        ImageData savedImage = imageDataRepository.save(imgData);
        post1.setImageData(savedImage);
        postRepository.save(post1);
        return savedImage.getName();

    }


    // soms moet hier een if statement om te kijken of de post wel bestaat
    // dit is even een andere methode

    public byte[] downloadImage(Long id) throws IOException{
        Optional<Post> post = postRepository.findById(id);
        Post post1 = post.get();
        ImageData imageData = post1.getImageData();
        return ImageUtil.decompressImage(imageData.getImageData());
    }
}