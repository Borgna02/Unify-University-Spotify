package unify.nullpointerexception.business;

import java.io.InputStream;
import java.util.List;

import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;

public interface MusicService {
   
    void createSong(Song song, String text, byte [] newTrack, byte[] newSongCover) throws BusinessException;
    void editSong(Song song, String text, byte [] newTrack, byte[] newSongCover) throws BusinessException;
    void deleteSong(Song song) throws BusinessException;

    List<Song> findAllMusicByArtist(Artist artist ) throws BusinessException;
    List<Song> findAllMusicByGenreName(String genre ) throws BusinessException;
    List<Song> findAllMusicByPrefix(String prefix ) throws BusinessException;
    List<Song> findAllMusicByGenrePrefix(String genrePrefix ) throws BusinessException;
    Song findMusicById(Integer id) throws BusinessException;
    List<Song> getAllSongs() throws BusinessException;

    String fetchLyrics(Song song) throws BusinessException;
    byte[] fetchCover(Song song) throws BusinessException;

    InputStream fetchTrack(Song song) throws BusinessException;
    
    void setTrack (Song song, byte[] audio) throws BusinessException;
    void setCover(Song song, byte[] cover) throws BusinessException;
    void setLyrics (Song song, String text) throws BusinessException;

    void removeArtistFromSong (Song song, Artist artist) throws BusinessException;
}
