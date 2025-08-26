package com.music.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class SongDTO {
    
    private Long id;
    
    @NotBlank(message = "歌曲标题不能为空")
    private String title;
    
    private String artist;
    
    private String album;
    
    private String genre;
    
    private Integer releaseYear;
    
    private Integer durationSeconds;
    
    private String fileName;
    
    private String filePath;
    
    private Long fileSize;
    
    private String fileType;
    
    private String minioObjectName;
    
    private Integer playCount;
    
    private String lyricsFileName;
    
    private String lyricsFilePath;
    
    private Long lyricsFileSize;
    
    private String lyricsFileType;
    
    private String lyricsMinioObjectName;
    
    private String language;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 