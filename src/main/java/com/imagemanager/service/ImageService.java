package com.imagemanager.service;

import com.imagemanager.entity.Image;
import com.imagemanager.entity.Tag;
import com.imagemanager.entity.User;
import com.imagemanager.repository.ImageRepository;
import com.imagemanager.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private TagRepository tagRepository;

	@Value("${file.upload-dir:./uploads}")
	private String uploadDir;

	public Page<Image> getPaginatedImages(User user, Pageable pageable) {
		return imageRepository.findByUserOrderByUploadDateDesc(user, pageable);
	}

	public Page<Image> searchImagesPaginated(User user, String query, Pageable pageable) {
		if (query == null || query.trim().isEmpty()) {
			return imageRepository.findByUserOrderByUploadDateDesc(user, pageable);
		}
		return imageRepository.searchByUserAndQuery(user, query, pageable);
	}

	public List<Image> getAllImages(User user) {
		List<Image> allImages = new ArrayList<>();

		List<Image> dbImages = getUserImages(user);
		allImages.addAll(dbImages);

		try {
			Path uploadPath = Paths.get(uploadDir);
			if (Files.exists(uploadPath)) {
				List<Path> fileSystemImages = Files.list(uploadPath).filter(path -> !Files.isDirectory(path))
						.filter(this::isImageFile).collect(Collectors.toList());

				for (Path filePath : fileSystemImages) {
					String fileName = filePath.getFileName().toString();

					boolean existsInDb = dbImages.stream().anyMatch(img -> img.getFileName().equals(fileName));

					if (!existsInDb) {
						Image fsImage = createImageFromFile(filePath, user);
						allImages.add(fsImage);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading file system images: " + e.getMessage());
		}

		allImages.sort((img1, img2) -> {
			LocalDateTime date1 = img1.getUploadDate() != null ? img1.getUploadDate() : LocalDateTime.MIN;
			LocalDateTime date2 = img2.getUploadDate() != null ? img2.getUploadDate() : LocalDateTime.MIN;
			return date2.compareTo(date1);
		});

		return allImages;
	}

	public List<Image> getUserImages(User user) {
		return imageRepository.findByUserOrderByUploadDateDesc(user);
	}

	public List<Image> searchImages(User user, String query) {
		if (query == null || query.trim().isEmpty()) {
			return getAllImages(user);
		}

		List<Image> allImages = getAllImages(user);
		String lowerQuery = query.toLowerCase().trim();

		return allImages.stream().filter(image -> image.getTitle().toLowerCase().contains(lowerQuery)
				|| (image.getDescription() != null && image.getDescription().toLowerCase().contains(lowerQuery))
				|| image.getOriginalFileName().toLowerCase().contains(lowerQuery)
				|| (image.getTags() != null
						&& image.getTags().stream().anyMatch(tag -> tag.getName().toLowerCase().contains(lowerQuery))))
				.collect(Collectors.toList());
	}

	private boolean isImageFile(Path path) {
		String fileName = path.getFileName().toString().toLowerCase();
		return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
				|| fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".webp")
				|| fileName.endsWith(".mp4") || fileName.endsWith(".mp4");

	}

	private Image createImageFromFile(Path filePath, User user) {
		String fileName = filePath.getFileName().toString();
		String originalFileName = fileName;

		String title = fileName;
		if (fileName.contains(".")) {
			title = fileName.substring(0, fileName.lastIndexOf('.'));
		}

		Image image = new Image();
		image.setTitle(title);
		image.setFileName(fileName);
		image.setOriginalFileName(originalFileName);
		image.setUser(user);
		image.setDescription("");

		try {
			LocalDateTime fileTime = Files.getLastModifiedTime(filePath).toInstant()
					.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
			image.setUploadDate(fileTime);
		} catch (IOException e) {
			image.setUploadDate(LocalDateTime.now());
		}

		return image;
	}

	public Image getImageById(Long id) {
		return imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found with id: " + id));
	}

	public Image getImageByIdAndUser(Long id, User user) {
		Image image = getImageById(id);
		if (!image.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Not authorized to access this image");
		}
		return image;
	}

	public Image saveImage(MultipartFile file, String title, String description, String tags, User user)
			throws IOException {
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		String originalFileName = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFileName != null && originalFileName.contains(".")) {
			fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
		}
		String fileName = UUID.randomUUID().toString() + fileExtension;

		Path filePath = uploadPath.resolve(fileName);
		Files.copy(file.getInputStream(), filePath);

		Image image = new Image(title, fileName, originalFileName, user);
		image.setDescription(description);

		if (tags != null && !tags.trim().isEmpty()) {
			String[] tagNames = tags.split(",");
			for (String tagName : tagNames) {
				String cleanedTag = tagName.trim().toLowerCase();
				if (!cleanedTag.isEmpty()) {
					Tag tag = tagRepository.findByName(cleanedTag)
							.orElseGet(() -> tagRepository.save(new Tag(cleanedTag)));
					image.addTag(tag);
				}
			}
		}

		return imageRepository.save(image);
	}

	public byte[] getImageData(String fileName) throws IOException {
		Path filePath = Paths.get(uploadDir).resolve(fileName);
		if (!Files.exists(filePath)) {
			throw new IOException("Image file not found: " + fileName);
		}
		return Files.readAllBytes(filePath);
	}

	public void deleteImage(Long imageId, User user) throws IOException {
		Image image = getImageById(imageId);

		String userRole = user.getRole(); // Or however you get the role

		if (!(userRole.equals("MODERATOR") || userRole.equals("ADMIN"))
				&& !image.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("Not authorized to delete this image");
		}

		Path filePath = Paths.get(uploadDir).resolve(image.getFileName());
		if (Files.exists(filePath)) {
			Files.delete(filePath);
		}

		imageRepository.delete(image);
	}

	public void syncFileSystemImages(User user) {
		try {
			Path uploadPath = Paths.get(uploadDir);
			if (Files.exists(uploadPath)) {
				List<Path> fileSystemImages = Files.list(uploadPath).filter(path -> !Files.isDirectory(path))
						.filter(this::isImageFile).collect(Collectors.toList());

				for (Path filePath : fileSystemImages) {
					String fileName = filePath.getFileName().toString();

					boolean existsInDb = imageRepository.findByFileName(fileName).isPresent();

					if (!existsInDb) {
						Image fsImage = createImageFromFile(filePath, user);
						imageRepository.save(fsImage);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error syncing file system images: " + e.getMessage());
		}
	}

	public Page<Image> getPaginatedImages(Pageable pageable) {
		return imageRepository.findAllByOrderByUploadDateDesc(pageable);
	}

	public Page<Image> searchImagesPaginated(String query, Pageable pageable) {
		if (query == null || query.trim().isEmpty()) {
			return imageRepository.findAllByOrderByUploadDateDesc(pageable);
		}
		return imageRepository.searchByQuery(query, pageable);
	}

	public void deleteImageById(Long id) {
		Image image = imageRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Image not found with id: " + id));

		try {
			Path filePath = Paths.get(uploadDir, image.getFileName());
			Files.deleteIfExists(filePath);
			System.out.println("Deleted file: " + filePath.toString());
		} catch (IOException e) {
			System.out.println("Warning: Could not delete physical file: " + e.getMessage());
		}

		imageRepository.delete(image);
	}
}