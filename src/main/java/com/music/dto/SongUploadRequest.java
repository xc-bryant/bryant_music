package com.music.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SongUploadRequest {
    
    @NotBlank(message = "歌曲标题不能为空")
    private String title;
    
    private String artist;
    
    private String album;
    
    private String genre;
    
    private Integer releaseYear;
    
    private Integer durationSeconds;
    
    private String language;

} 