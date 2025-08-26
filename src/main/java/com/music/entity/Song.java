package com.music.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 100)
    private String artist;
    
    @Column(length = 100)
    private String album;
    
    @Column(length = 50)
    private String genre;
    
    @Column(name = "release_year")
    private Integer releaseYear;
    
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type", length = 20)
    private String fileType;
    
    @Column(name = "minio_object_name")
    private String minioObjectName;
    
    @Column(name = "play_count", columnDefinition = "INT DEFAULT 0")
    private Integer playCount = 0;
    
    @Column(name = "lyrics_file_name")
    private String lyricsFileName;
    
    @Column(name = "lyrics_file_path")
    private String lyricsFilePath;
    
    @Column(name = "lyrics_file_size")
    private Long lyricsFileSize;
    
    @Column(name = "lyrics_file_type", length = 20)
    private String lyricsFileType;
    
    @Column(name = "lyrics_minio_object_name")
    private String lyricsMinioObjectName;
    
    @Column(name = "language", length = 20)
    private String language;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 