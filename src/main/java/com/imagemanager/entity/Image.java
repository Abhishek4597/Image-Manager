package com.imagemanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String fileName;
    
    private String originalFileName;
    
    @Column(length = 10000)  
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime uploadDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "image_tags",
        joinColumns = @JoinColumn(name = "image_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();
    
    public Image() {
        this.uploadDate = LocalDateTime.now();
    }
    
    public Image(String title, String fileName, String originalFileName, User user) {
        this();
        this.title = title;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getImages().add(this);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getImages().remove(this);
    }
    
    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(50, description.length())) + "..." : "null") + '\'' +
                ", uploadDate=" + uploadDate +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", tags=" + (tags != null ? tags.size() : 0) +
                '}';
    }
}