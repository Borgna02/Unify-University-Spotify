package unify.nullpointerexception.business;

import java.util.List;

import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;

public interface AlbumService {

    Album findAlbumById(Integer id) throws BusinessException;
    List<Album> findAllAlbumByArtist(Artist artist) throws BusinessException;
    List<Album> findAllAlbumByPrefix(String prefix) throws BusinessException;
    List<Album> findAllAlbumByGenrePrefix(String prefix) throws BusinessException;
    List<Album> findAllAlbumBySong(Song song) throws BusinessException;
    byte[] fetchCover(Album album) throws BusinessException;
    void setCover(Album album, byte[] cover) throws BusinessException;
    void createAlbum(Album newAlbum, byte[] newCover) throws BusinessException;
    void editAlbum(Album album, byte[] cover) throws BusinessException;
    void deleteAlbum(Album album) throws BusinessException;
    

}
