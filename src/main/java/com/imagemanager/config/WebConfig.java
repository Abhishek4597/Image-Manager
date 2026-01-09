package com.imagemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            
            String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
            
            registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:D:/uploads/")
                    .setCachePeriod(3600); 
            
            System.out.println("📁 Serving static resources from: " + absolutePath);
            System.out.println("📁 Upload directory configured for: /uploads/** -> " + absolutePath);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to configure upload directory: " + e.getMessage());
        }
    }
}