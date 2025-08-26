package com.music.service;

import com.music.dto.SongDTO;
import com.music.dto.SongUploadRequest;
import com.music.entity.Song;
import com.music.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class SongService {
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private MinioService minioService;
    
    /**
     * 上传歌曲文件和信息
     */
    public SongDTO uploadSong(MultipartFile file, SongUploadRequest request) throws Exception {
        return uploadSong(file, null, request);
    }
    
    /**
     * 上传歌曲文件和歌词文件
     */
    public SongDTO uploadSong(MultipartFile file, MultipartFile lyricsFile, SongUploadRequest request) throws Exception {
        // 验证音频文件
        if (file.isEmpty()) {
            throw new RuntimeException("音频文件不能为空");
        }
        
        // 验证音频文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new RuntimeException("只能上传音频文件");
        }
        
        // 上传音频文件到MinIO
        String minioObjectName = minioService.uploadFile(file);
        
        // 创建歌曲实体
        Song song = new Song();
        song.setTitle(request.getTitle());
        song.setArtist(request.getArtist());
        song.setAlbum(request.getAlbum());
        song.setGenre(request.getGenre());
        song.setReleaseYear(request.getReleaseYear());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setLanguage(request.getLanguage());
        song.setFileName(file.getOriginalFilename());
        song.setFilePath(minioService.getFileUrl(minioObjectName));
        song.setFileSize(file.getSize());
        song.setFileType(contentType);
        song.setMinioObjectName(minioObjectName);
        
        // 处理歌词文件
        if (lyricsFile != null && !lyricsFile.isEmpty()) {
            // 验证歌词文件类型
            String lyricsContentType = lyricsFile.getContentType();
            if (lyricsContentType == null || !isValidLyricsFile(lyricsContentType, lyricsFile.getOriginalFilename())) {
                throw new RuntimeException("歌词文件格式不支持，支持格式：.txt, .lrc, .srt");
            }
            
            // 上传歌词文件到MinIO
            String lyricsMinioObjectName = minioService.uploadFile(lyricsFile);
            
            song.setLyricsFileName(lyricsFile.getOriginalFilename());
            song.setLyricsFilePath(minioService.getFileUrl(lyricsMinioObjectName));
            song.setLyricsFileSize(lyricsFile.getSize());
            song.setLyricsFileType(lyricsContentType);
            song.setLyricsMinioObjectName(lyricsMinioObjectName);
        }
        
        // 保存到数据库
        Song savedSong = songRepository.save(song);
        
        return convertToDTO(savedSong);
    }
    
    /**
     * 验证歌词文件格式
     */
    private boolean isValidLyricsFile(String contentType, String fileName) {
        if (fileName == null) return false;
        
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".txt") || 
               lowerFileName.endsWith(".lrc") || 
               lowerFileName.endsWith(".srt") ||
               contentType.equals("text/plain");
    }
    
    /**
     * 获取所有歌曲
     */
    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取歌曲
     */
    public SongDTO getSongById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        return song.map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
    }
    
    /**
     * 更新歌曲信息
     */
    public SongDTO updateSong(Long id, SongDTO songDTO) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        song.setTitle(songDTO.getTitle());
        song.setArtist(songDTO.getArtist());
        song.setAlbum(songDTO.getAlbum());
        song.setGenre(songDTO.getGenre());
        song.setReleaseYear(songDTO.getReleaseYear());
        song.setDurationSeconds(songDTO.getDurationSeconds());
        song.setLanguage(songDTO.getLanguage());
        
        Song updatedSong = songRepository.save(song);
        return convertToDTO(updatedSong);
    }
    
    /**
     * 删除歌曲
     */
    public void deleteSong(Long id) throws Exception {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        // 删除MinIO中的音频文件
        if (song.getMinioObjectName() != null) {
            minioService.deleteFile(song.getMinioObjectName());
        }
        
        // 删除MinIO中的歌词文件
        if (song.getLyricsMinioObjectName() != null) {
            minioService.deleteFile(song.getLyricsMinioObjectName());
        }
        
        // 删除数据库记录
        songRepository.deleteById(id);
    }
    
    /**
     * 搜索歌曲
     */
    public List<SongDTO> searchSongs(String keyword) {
        return songRepository.searchByKeyword(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据标题搜索歌曲
     */
    public List<SongDTO> searchByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据艺术家搜索歌曲
     */
    public List<SongDTO> searchByArtist(String artist) {
        return songRepository.findByArtistContainingIgnoreCase(artist).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据专辑搜索歌曲
     */
    public List<SongDTO> searchByAlbum(String album) {
        return songRepository.findByAlbumContainingIgnoreCase(album).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据流派搜索歌曲
     */
    public List<SongDTO> searchByGenre(String genre) {
        return songRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据语言搜索歌曲
     */
    public List<SongDTO> searchByLanguage(String language) {
        return songRepository.findByLanguage(language).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取热门歌曲
     */
    public List<SongDTO> getTopPlayedSongs() {
        return songRepository.findTopPlayedSongs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最新歌曲
     */
    public List<SongDTO> getLatestSongs() {
        return songRepository.findLatestSongs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 增加播放次数
     */
    public void incrementPlayCount(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
    }
    
    /**
     * 获取歌曲文件流
     */
    public java.io.InputStream getSongFile(Long id) throws Exception {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        if (song.getMinioObjectName() == null) {
            throw new RuntimeException("歌曲文件不存在");
        }
        
        return minioService.downloadFile(song.getMinioObjectName());
    }
    
    /**
     * 获取歌词文件流
     */
    public java.io.InputStream getLyricsFile(Long id) throws Exception {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        if (song.getLyricsMinioObjectName() == null) {
            throw new RuntimeException("歌词文件不存在");
        }
        
        return minioService.downloadFile(song.getLyricsMinioObjectName());
    }
    
    /**
     * 上传歌词文件
     */
    public SongDTO uploadLyricsFile(Long id, MultipartFile lyricsFile) throws Exception {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        // 验证歌词文件类型
        String lyricsContentType = lyricsFile.getContentType();
        if (lyricsContentType == null || !isValidLyricsFile(lyricsContentType, lyricsFile.getOriginalFilename())) {
            throw new RuntimeException("歌词文件格式不支持，支持格式：.txt, .lrc, .srt");
        }
        
        // 如果已有歌词文件，先删除
        if (song.getLyricsMinioObjectName() != null) {
            minioService.deleteFile(song.getLyricsMinioObjectName());
        }
        
        // 上传新的歌词文件到MinIO
        String lyricsMinioObjectName = minioService.uploadFile(lyricsFile);
        
        song.setLyricsFileName(lyricsFile.getOriginalFilename());
        song.setLyricsFilePath(minioService.getFileUrl(lyricsMinioObjectName));
        song.setLyricsFileSize(lyricsFile.getSize());
        song.setLyricsFileType(lyricsContentType);
        song.setLyricsMinioObjectName(lyricsMinioObjectName);
        
        Song updatedSong = songRepository.save(song);
        return convertToDTO(updatedSong);
    }
    
    /**
     * 删除歌词文件
     */
    public void deleteLyricsFile(Long id) throws Exception {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        if (song.getLyricsMinioObjectName() != null) {
            minioService.deleteFile(song.getLyricsMinioObjectName());
            
            song.setLyricsFileName(null);
            song.setLyricsFilePath(null);
            song.setLyricsFileSize(null);
            song.setLyricsFileType(null);
            song.setLyricsMinioObjectName(null);
            
            songRepository.save(song);
        }
    }
    
    /**
     * 实体转DTO
     */
    private SongDTO convertToDTO(Song song) {
        SongDTO dto = new SongDTO();
        BeanUtils.copyProperties(song, dto);
        return dto;
    }
} 