package com.music.repository;

import com.music.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
    List<Song> findByTitleContainingIgnoreCase(String title);
    
    List<Song> findByArtistContainingIgnoreCase(String artist);
    
    List<Song> findByAlbumContainingIgnoreCase(String album);
    
    List<Song> findByGenre(String genre);
    
    List<Song> findByLanguage(String language);
    
    List<Song> findByReleaseYear(Integer releaseYear);
    
    @Query("SELECT s FROM Song s WHERE s.title LIKE %:keyword% OR s.artist LIKE %:keyword% OR s.album LIKE %:keyword%")
    List<Song> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findTopPlayedSongs();
    
    @Query("SELECT s FROM Song s ORDER BY s.createdAt DESC")
    List<Song> findLatestSongs();
} 