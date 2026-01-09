package com.imagemanager.repository;

import com.imagemanager.entity.Image;
import com.imagemanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    List<Image> findByUserOrderByUploadDateDesc(User user);
    
    Page<Image> findByUserOrderByUploadDateDesc(User user, Pageable pageable);
    
    @Query("SELECT i FROM Image i WHERE i.user = :user AND " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))))")
    List<Image> searchByUserAndQuery(@Param("user") User user, 
                                   @Param("query") String query);
    
    @Query("SELECT i FROM Image i WHERE i.user = :user AND " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))))")
    Page<Image> searchByUserAndQuery(@Param("user") User user, 
                                   @Param("query") String query,
                                   Pageable pageable);
    
    Optional<Image> findByFileName(String fileName);
    
    Page<Image> findByUser(User user, Pageable pageable);
    
    @Query("SELECT i FROM Image i WHERE i.user = :user AND LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Image> findByUserAndTitleContainingIgnoreCase(@Param("user") User user, 
                                                     @Param("title") String title, 
                                                     Pageable pageable);
    
    
    @Query("SELECT i FROM Image i ORDER BY i.uploadDate DESC")
    Page<Image> findAllByOrderByUploadDateDesc(Pageable pageable);
    
    @Query("SELECT i FROM Image i WHERE " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.user.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))) " +
           "ORDER BY i.uploadDate DESC")
    Page<Image> searchByQuery(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT i FROM Image i WHERE " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.user.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "EXISTS (SELECT t FROM i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))) " +
           "ORDER BY i.uploadDate DESC")
    List<Image> searchByQuery(@Param("query") String query);
    
    @Query("SELECT COUNT(i) FROM Image i")
    long countAllImages();
    
    @Query("SELECT COUNT(i) FROM Image i WHERE i.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT i FROM Image i JOIN i.tags t WHERE t.name = :tagName ORDER BY i.uploadDate DESC")
    Page<Image> findByTagName(@Param("tagName") String tagName, Pageable pageable);
    
    @Query("SELECT i FROM Image i ORDER BY i.uploadDate DESC")
    List<Image> findTop10ByOrderByUploadDateDesc(Pageable pageable);
    
    @Query("SELECT DISTINCT i FROM Image i JOIN i.tags t WHERE t.name IN :tagNames ORDER BY i.uploadDate DESC")
    Page<Image> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
    @Query("SELECT i.user.username, COUNT(i) FROM Image i GROUP BY i.user.username ORDER BY COUNT(i) DESC")
    List<Object[]> getImageCountsByUser();
    
    
    @Query("SELECT i FROM Image i WHERE " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%')) OR " +
           "EXISTS (SELECT t FROM i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tag, '%')))) " +
           "ORDER BY i.uploadDate DESC")
    Page<Image> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTagsNameContainingIgnoreCase(
            @Param("title") String title,
            @Param("description") String description,
            @Param("tag") String tag,
            Pageable pageable);
    
    Page<Image> findAll(Pageable pageable);
    
    Page<Image> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Image> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    @Query("SELECT i FROM Image i JOIN i.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :tagName, '%'))")
    Page<Image> findByTagsNameContainingIgnoreCase(@Param("tagName") String tagName, Pageable pageable);
}