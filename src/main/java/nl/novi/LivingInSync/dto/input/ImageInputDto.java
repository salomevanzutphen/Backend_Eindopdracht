package nl.novi.LivingInSync.dto.input;

import org.springframework.web.multipart.MultipartFile;

public class ImageInputDto {
    private MultipartFile image;

    public ImageInputDto() {
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}

