package com.imagemanager.controller;

import com.imagemanager.entity.Image;
import com.imagemanager.entity.Tag;
import com.imagemanager.entity.User;
import com.imagemanager.repository.ImageRepository;
import com.imagemanager.repository.TagRepository;
import com.imagemanager.service.ImageService;
import com.imagemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ImageController {
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private TagRepository tagRepository;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/upload")
    public String showUploadForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userRole = getUserRole(userDetails);
        model.addAttribute("userRole", userRole);
        return "upload";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, 
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "12") int size,
                          Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            model.addAttribute("userRole", userRole);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
            Page<Image> imagePage;
            
            if (userRole.equals("ADMIN") || userRole.equals("MODERATOR")) {
                imagePage = imageRepository.findAll(pageable);
            } else {
                imagePage = imageRepository.findAll(pageable);
            }
            
            model.addAttribute("images", imagePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", imagePage.getTotalPages());
            model.addAttribute("totalItems", imagePage.getTotalElements());
            model.addAttribute("pageSize", size);
            
            if (!model.containsAttribute("pageSize")) {
                model.addAttribute("pageSize", size);
            }
            
        } catch (Exception e) {
            String userRole = getUserRole(userDetails);
            model.addAttribute("userRole", userRole);
            model.addAttribute("error", "Error loading images: " + e.getMessage());
            model.addAttribute("images", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("pageSize", 12);
        }
        return "dashboard";
    }
    
    @PostMapping("/upload")
    public String uploadImage(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam String title,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String tags,
                            Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("UPLOADER") && !userRole.equals("MODERATOR") && !userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to upload images");
                model.addAttribute("userRole", userRole);
                return "upload";
            }
            
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                model.addAttribute("userRole", userRole);
                return "upload";
            }
            
            imageService.saveImage(file, title, description, tags, user);
            model.addAttribute("success", "Image uploaded successfully!");
            
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }
        
        String userRole = getUserRole(userDetails);
        model.addAttribute("userRole", userRole);
        return "upload";
    }
    
    @GetMapping("/search")
    public String searchImages(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(required = false) String query,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "12") int size,
                             Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            model.addAttribute("userRole", userRole);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadDate").descending());
            Page<Image> imagePage;
            
            if (query == null || query.trim().isEmpty()) {
                imagePage = imageRepository.findAll(pageable);
            } else {
                imagePage = imageRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTagsNameContainingIgnoreCase(
                    query, query, query, pageable);
            }
            
            model.addAttribute("images", imagePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", imagePage.getTotalPages());
            model.addAttribute("totalItems", imagePage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("query", query);
            
        } catch (Exception e) {
            String userRole = getUserRole(userDetails);
            model.addAttribute("userRole", userRole);
            model.addAttribute("error", "Error searching images: " + e.getMessage());
            model.addAttribute("images", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("query", query);
        }
        return "search";
    }
    
    @PostMapping("/image/{id}/delete")
    public String deleteImage(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("MODERATOR") && !userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to delete images");
                return "redirect:/dashboard";
            }
            
            imageService.deleteImage(id, user);
            model.addAttribute("success", "Image deleted successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete image: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/image/{id}/add-tag")
    public String addTag(@PathVariable Long id,
                        @RequestParam String tagName,
                        @AuthenticationPrincipal UserDetails userDetails,
                        Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("TAGGER") && !userRole.equals("UPLOADER") && 
                !userRole.equals("MODERATOR") && !userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to add tags");
                return "redirect:/dashboard";
            }
            
            Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
            
            if (tagName != null && !tagName.trim().isEmpty()) {
                String cleanedTag = tagName.trim().toLowerCase();
                Tag tag = tagRepository.findByName(cleanedTag)
                    .orElseGet(() -> tagRepository.save(new Tag(cleanedTag)));
                
                image.addTag(tag);
                imageRepository.save(image);
                
                model.addAttribute("success", "Tag '" + tagName + "' added successfully!");
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add tag: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/image/{imageId}/tag/{tagId}/delete")
    public String deleteTag(@PathVariable Long imageId,
                           @PathVariable Long tagId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("TAGGER") && !userRole.equals("UPLOADER") && 
                !userRole.equals("MODERATOR") && !userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to delete tags");
                return "redirect:/dashboard";
            }
            
            Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
            
            Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
            
            image.removeTag(tag);
            imageRepository.save(image);
            
            if (tag.getImages().isEmpty()) {
                tagRepository.delete(tag);
            }
            
            model.addAttribute("success", "Tag '" + tag.getName() + "' removed successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete tag: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/image/{id}/update-description")
    public String updateDescription(@PathVariable Long id,
                                  @RequestParam String description,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to edit descriptions");
                return "redirect:/dashboard";
            }
            
            Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
            
            image.setDescription(description);
            imageRepository.save(image);
            
            model.addAttribute("success", "Description updated successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update description: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/dashboard";
    }
    
    @GetMapping("/create-user")
    public String showCreateUserForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userRole = getUserRole(userDetails);
        if (!userRole.equals("ADMIN")) {
            model.addAttribute("error", "Only administrators can create users");
            return "redirect:/dashboard";
        }
        
        model.addAttribute("userRole", userRole);
        return "create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        try {
            // Check if user is ADMIN
            String currentUserRole = getUserRole(userDetails);
            if (!currentUserRole.equals("ADMIN")) {
                model.addAttribute("error", "Only administrators can create users");
                return "create-user";
            }
            
            String email = username + "@example.com";
            
            userService.createUser(username, email, password, role);
            model.addAttribute("success", "User '" + username + "' created successfully with role: " + role);
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create user: " + e.getMessage());
        }
        
        model.addAttribute("userRole", getUserRole(userDetails));
        return "create-user";
    }
    
    @PostMapping("/image/add-to-database")
    public String addToDatabase(@RequestParam String fileName,
                              @RequestParam String title,
                              @RequestParam String originalFileName,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("MODERATOR") && !userRole.equals("ADMIN")) {
                model.addAttribute("error", "You don't have permission to add images to database");
                return "redirect:/dashboard";
            }
            
            Optional<Image> existingImage = imageRepository.findByFileName(fileName);
            if (existingImage.isPresent()) {
                model.addAttribute("error", "Image already exists in database!");
                return "redirect:/dashboard";
            }
            
            Image image = new Image();
            image.setTitle(title);
            image.setFileName(fileName);
            image.setOriginalFileName(originalFileName);
            image.setUser(user);
            image.setDescription("Added from file system");
            image.setUploadDate(LocalDateTime.now());
            
            imageRepository.save(image);
            model.addAttribute("success", "Image added to database successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add image to database: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/sync-files")
    public String syncFiles(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String userRole = getUserRole(userDetails);
            if (!userRole.equals("ADMIN")) {
                model.addAttribute("error", "Only administrators can sync files");
                return "redirect:/dashboard";
            }
            
            imageService.syncFileSystemImages(user);
            model.addAttribute("success", "File system images synced to database!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Error syncing files: " + e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
    
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id, 
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
            
            byte[] imageData = imageService.getImageData(image.getFileName());
            
            HttpHeaders headers = new HttpHeaders();
            
            String fileName = image.getFileName().toLowerCase();
            if (fileName.endsWith(".png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if (fileName.endsWith(".gif")) {
                headers.setContentType(MediaType.IMAGE_GIF);
            } else {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    private String getUserRole(UserDetails userDetails) {
        if (userDetails == null) {
            return "VIEWER";
        }
        
        return userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("VIEWER");
    }
}