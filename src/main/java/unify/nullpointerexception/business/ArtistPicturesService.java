package unify.nullpointerexception.business;

import java.util.List;

import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;

public interface ArtistPicturesService {

    List<ArtistPicture> findAllPicturesByArtist(Artist artist) throws BusinessException;
    void createArtistPicture(Artist artist, ArtistPicture newPicture) throws BusinessException;
    void setArtistPicture(Artist artist, ArtistPicture newPicture) throws BusinessException;
    void deleteArtistPicture(ArtistPicture artistPicture) throws BusinessException;

}