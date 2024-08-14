package nl.novi.LivingInSync.dto.input;

import jakarta.validation.constraints.NotBlank;

public class PostInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
