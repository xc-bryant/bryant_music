package com.music.controller;

import com.music.dto.ApiResponse;
import com.music.dto.SongDTO;
import com.music.dto.SongUploadRequest;
import com.music.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class SongController {
    
    @Autowired
    private SongService songService;
    
    /**
     * 上传歌曲
     */
    @PostMapping("/upload")
    public ApiResponse<SongDTO> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "lyricsFile", required = false) MultipartFile lyricsFile,
            @Valid @ModelAttribute SongUploadRequest request) {
        try {
            SongDTO song = songService.uploadSong(file, lyricsFile, request);
            return ApiResponse.success("歌曲上传成功", song);
        } catch (Exception e) {
            log.error("歌曲上传失败", e);
            return ApiResponse.error("歌曲上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有歌曲
     */
    @GetMapping
    public ApiResponse<List<SongDTO>> getAllSongs() {
        try {
            List<SongDTO> songs = songService.getAllSongs();
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("获取歌曲列表失败", e);
            return ApiResponse.error("获取歌曲列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取歌曲
     */
    @GetMapping("/{id}")
    public ApiResponse<SongDTO> getSongById(@PathVariable Long id) {
        try {
            SongDTO song = songService.getSongById(id);
            return ApiResponse.success(song);
        } catch (Exception e) {
            log.error("获取歌曲失败", e);
            return ApiResponse.error("获取歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新歌曲信息
     */
    @PutMapping("/{id}")
    public ApiResponse<SongDTO> updateSong(
            @PathVariable Long id,
            @Valid @RequestBody SongDTO songDTO) {
        try {
            SongDTO updatedSong = songService.updateSong(id, songDTO);
            return ApiResponse.success("歌曲更新成功", updatedSong);
        } catch (Exception e) {
            log.error("更新歌曲失败", e);
            return ApiResponse.error("更新歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除歌曲
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSong(@PathVariable Long id) {
        try {
            songService.deleteSong(id);
            return ApiResponse.success("歌曲删除成功", null);
        } catch (Exception e) {
            log.error("删除歌曲失败", e);
            return ApiResponse.error("删除歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索歌曲
     */
    @GetMapping("/search")
    public ApiResponse<List<SongDTO>> searchSongs(@RequestParam String keyword) {
        try {
            List<SongDTO> songs = songService.searchSongs(keyword);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("搜索歌曲失败", e);
            return ApiResponse.error("搜索歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据标题搜索歌曲
     */
    @GetMapping("/search/title")
    public ApiResponse<List<SongDTO>> searchByTitle(@RequestParam String title) {
        try {
            List<SongDTO> songs = songService.searchByTitle(title);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("根据标题搜索歌曲失败", e);
            return ApiResponse.error("根据标题搜索歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据艺术家搜索歌曲
     */
    @GetMapping("/search/artist")
    public ApiResponse<List<SongDTO>> searchByArtist(@RequestParam String artist) {
        try {
            List<SongDTO> songs = songService.searchByArtist(artist);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("根据艺术家搜索歌曲失败", e);
            return ApiResponse.error("根据艺术家搜索歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据专辑搜索歌曲
     */
    @GetMapping("/search/album")
    public ApiResponse<List<SongDTO>> searchByAlbum(@RequestParam String album) {
        try {
            List<SongDTO> songs = songService.searchByAlbum(album);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("根据专辑搜索歌曲失败", e);
            return ApiResponse.error("根据专辑搜索歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据流派搜索歌曲
     */
    @GetMapping("/search/genre")
    public ApiResponse<List<SongDTO>> searchByGenre(@RequestParam String genre) {
        try {
            List<SongDTO> songs = songService.searchByGenre(genre);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("根据流派搜索歌曲失败", e);
            return ApiResponse.error("根据流派搜索歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门歌曲
     */
    @GetMapping("/top-played")
    public ApiResponse<List<SongDTO>> getTopPlayedSongs() {
        try {
            List<SongDTO> songs = songService.getTopPlayedSongs();
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("获取热门歌曲失败", e);
            return ApiResponse.error("获取热门歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最新歌曲
     */
    @GetMapping("/latest")
    public ApiResponse<List<SongDTO>> getLatestSongs() {
        try {
            List<SongDTO> songs = songService.getLatestSongs();
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("获取最新歌曲失败", e);
            return ApiResponse.error("获取最新歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 播放歌曲（增加播放次数）
     */
    @PostMapping("/{id}/play")
    public ApiResponse<Void> playSong(@PathVariable Long id) {
        try {
            songService.incrementPlayCount(id);
            return ApiResponse.success("播放次数已更新", null);
        } catch (Exception e) {
            log.error("更新播放次数失败", e);
            return ApiResponse.error("更新播放次数失败: " + e.getMessage());
        }
    }
    
    /**
     * 下载歌曲文件
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadSong(@PathVariable Long id) {
        try {
            InputStream inputStream = songService.getSongFile(id);
            SongDTO song = songService.getSongById(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + song.getFileName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, song.getFileType());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("下载歌曲失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 流式播放歌曲
     */
    @GetMapping("/{id}/stream")
    public ResponseEntity<InputStreamResource> streamSong(@PathVariable Long id) {
        try {
            InputStream inputStream = songService.getSongFile(id);
            SongDTO song = songService.getSongById(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, song.getFileType());
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("流式播放歌曲失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 下载歌词文件
     */
    @GetMapping("/{id}/lyrics/download")
    public ResponseEntity<InputStreamResource> downloadLyricsFile(@PathVariable Long id) {
        try {
            InputStream inputStream = songService.getLyricsFile(id);
            SongDTO song = songService.getSongById(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + song.getLyricsFileName() + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, song.getLyricsFileType());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("下载歌词文件失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取歌词文件信息
     */
    @GetMapping("/{id}/lyrics")
    public ApiResponse<SongDTO> getSongLyricsInfo(@PathVariable Long id) {
        try {
            SongDTO song = songService.getSongById(id);
            return ApiResponse.success(song);
        } catch (Exception e) {
            log.error("获取歌词信息失败", e);
            return ApiResponse.error("获取歌词信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传歌词文件
     */
    @PostMapping("/{id}/lyrics")
    public ApiResponse<SongDTO> uploadLyricsFile(
            @PathVariable Long id,
            @RequestParam("lyricsFile") MultipartFile lyricsFile) {
        try {
            SongDTO song = songService.uploadLyricsFile(id, lyricsFile);
            return ApiResponse.success("歌词文件上传成功", song);
        } catch (Exception e) {
            log.error("歌词文件上传失败", e);
            return ApiResponse.error("歌词文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除歌词文件
     */
    @DeleteMapping("/{id}/lyrics")
    public ApiResponse<Void> deleteLyricsFile(@PathVariable Long id) {
        try {
            songService.deleteLyricsFile(id);
            return ApiResponse.success("歌词文件删除成功", null);
        } catch (Exception e) {
            log.error("歌词文件删除失败", e);
            return ApiResponse.error("歌词文件删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 按语言搜索歌曲
     */
    @GetMapping("/search/language")
    public ApiResponse<List<SongDTO>> searchByLanguage(@RequestParam String language) {
        try {
            List<SongDTO> songs = songService.searchByLanguage(language);
            return ApiResponse.success(songs);
        } catch (Exception e) {
            log.error("按语言搜索歌曲失败", e);
            return ApiResponse.error("按语言搜索歌曲失败: " + e.getMessage());
        }
    }
} 