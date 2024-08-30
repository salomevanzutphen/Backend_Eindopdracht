//package nl.novi.LivingInSync.service;
//
//import nl.novi.LivingInSync.model.ImageData;
//import nl.novi.LivingInSync.model.Post;
//import nl.novi.LivingInSync.repository.ImageDataRepository;
//import nl.novi.LivingInSync.repository.PostRepository;
//import nl.novi.LivingInSync.utils.ImageUtil;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Service
//public class ImageDataService {
//
//    private final ImageDataRepository imageDataRepository;
//    private final PostRepository postRepository;
//
//    public ImageDataService(ImageDataRepository imageDataRepository, PostRepository postRepository) {
//        this.imageDataRepository = imageDataRepository;
//        this.postRepository = postRepository;
//    }
//
//    public String uploadImage(MultipartFile multipartFile, Long id) throws IOException {
//        Optional<Post> post = postRepository.findById(id);
//        Post post1 = post.get();
//
//        ImageData imgData = new ImageData();
//        imgData.setName(multipartFile.getName());
//        imgData.setType(multipartFile.getContentType());
//        imgData.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));
//        imgData.setPost(post1);
//
//        ImageData savedImage = imageDataRepository.save(imgData);
//        post1.setImage(savedImage);
//        postRepository.save(post1);
//        return savedImage.getName();
//    }
//
//    public byte[] downloadImage(Long id) throws IOException {
//        Optional<Post> post = postRepository.findById(id);
//        Post post1 = post.get();
//        ImageData imageData = post1.getImageData();
//        return ImageUtil.decompressImage(imageData.getImageData());
//    }
//
//    public String updateImage(Long id, MultipartFile multipartFile) throws IOException {
//        Optional<Post> post = postRepository.findById(id);
//        if (post.isPresent()) {
//            Post post1 = post.get();
//            ImageData existingImageData = post1.getImageData();
//
//            existingImageData.setName(multipartFile.getName());
//            existingImageData.setType(multipartFile.getContentType());
//            existingImageData.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));
//
//            ImageData updatedImageData = imageDataRepository.save(existingImageData);
//            return updatedImageData.getName();
//        } else {
//            throw new RuntimeException("Post not found");
//        }
//    }
//
//    public void deleteImage(Long id) {
//        Optional<Post> post = postRepository.findById(id);
//        if (post.isPresent()) {
//            Post post1 = post.get();
//            ImageData imageData = post1.getImageData();
//            imageDataRepository.delete(imageData);
//            post1.setImage(null);
//            postRepository.save(post1);
//        } else {
//            throw new RuntimeException("Post not found");
//        }
//    }
//}
