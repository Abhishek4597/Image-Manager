package com.imagemanager.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageUploadDTO {
    private MultipartFile file;
    private String title;
    private String description;
    private String tags;
    
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}