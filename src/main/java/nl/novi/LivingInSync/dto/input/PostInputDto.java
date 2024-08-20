package nl.novi.LivingInSync.dto.input;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class PostInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String subtitle;

    @NotBlank
    private String description;

    private MultipartFile image;



    // Default constructor
    public PostInputDto() {
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String name) {
        this.subtitle = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }


}



