package unify.nullpointerexception.business;

import java.util.List;

import unify.nullpointerexception.domain.Artist;

public interface ArtistsService {
    Artist findArtistById(Integer id) throws BusinessException;
    List<Artist> findArtistByNamePrefix(String namePrefix) throws BusinessException;
    List<Artist> getAllArtists() throws BusinessException;

    void createArtist(Artist newArtist, String newBio, byte[] newAvatar) throws BusinessException;
    void editArtist(Artist artist, String bio, byte[] avatar) throws BusinessException;
    void deleteArtist(Artist artist) throws BusinessException;  //una volta rimpiazzato il servizio, eliminare le foto dell'artista 
    String getBiography(Artist artist);
    void setBiography(Artist artist, String newBio);
    void setAvatar(Artist artist, byte[] avatar) throws BusinessException;
    byte[] fetchAvatar(Artist artist) throws BusinessException;
}