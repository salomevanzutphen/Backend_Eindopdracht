package nl.novi.LivingInSync.model;

import jakarta.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ImageData imageData;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
        if (imageData != null) {
            imageData.setPost(this); // Ensure bidirectional relationship consistency
        }
    }
}
