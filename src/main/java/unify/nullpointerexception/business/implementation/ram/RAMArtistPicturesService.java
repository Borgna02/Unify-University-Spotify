package unify.nullpointerexception.business.implementation.ram;

import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;

public class RAMArtistPicturesService implements ArtistPicturesService {

    private List<ArtistPicture> pictures = new ArrayList<>();

    private int idPicture;

    public RAMArtistPicturesService() {
    }

    @Override
    public List<ArtistPicture> findAllPicturesByArtist(Artist artist) throws BusinessException {

        List<ArtistPicture> result = new ArrayList<>();

        for (ArtistPicture picture : pictures) {
            if (picture.getArtist().equals(artist))
                result.add(picture);
        }

        return result;
    }

    @Override
    public void createArtistPicture(Artist artist, ArtistPicture newPicture) throws BusinessException {
        newPicture.setId(idPicture++);
        newPicture.setArtist(artist);
        pictures.add(newPicture);

    }

    @Override
    public void setArtistPicture(Artist artist, ArtistPicture newPicture) throws BusinessException {

        newPicture.setArtist(artist);

        for (ArtistPicture oldPicture : pictures) {

            if (oldPicture.equals(newPicture)) {
                pictures.remove(oldPicture);
                pictures.add(newPicture);
                return;
            }
        }
    }

    @Override
    public void deleteArtistPicture(ArtistPicture artistPicture) throws BusinessException {

        pictures.remove(artistPicture);
    }

}
