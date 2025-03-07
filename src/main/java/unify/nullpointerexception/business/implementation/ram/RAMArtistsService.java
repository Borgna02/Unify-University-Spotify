package unify.nullpointerexception.business.implementation.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;
import unify.nullpointerexception.domain.Song;

public class RAMArtistsService implements ArtistsService{

    private List<Artist> artists = new ArrayList<>();

    private byte[] defaultAvatar = {};
    private Map<Integer, byte[]> avatars = new HashMap<>();
    private List<ArtistPicture> pictures = new ArrayList<>();

    private Map<Integer, String> biographies = new HashMap<>();

   

    private int idArtist;

    @Override
    public Artist findArtistById(Integer id) throws BusinessException {
        for (Artist artist : artists) {
            if(artist.getId() == id) return artist;
        }
        return new Artist();
    }

    @Override
    public List<Artist> findArtistByNamePrefix(String namePrefix) throws BusinessException {

        namePrefix = namePrefix.toLowerCase();
        List<Artist> result = new ArrayList<>();

        for (Artist artist : artists) {
            if(artist.getStageName().toLowerCase().contains(namePrefix)) result.add(artist);
        }

        return result;
    }

    @Override
    public List<Artist> getAllArtists() throws BusinessException {
       
        return artists;
    }

    @Override
    public String getBiography(Artist artist) {

        if(biographies.containsKey(artist.getId()))
            return biographies.get(artist.getId());

        return new String();
    }

    @Override
    public void setBiography(Artist artist, String newBio) {
        biographies.put(artist.getId(), newBio);        
    }

    @Override
    public byte[] fetchAvatar(Artist artist) throws BusinessException {

        if(avatars.containsKey(artist.getId()))
            return avatars.get(artist.getId());

        return defaultAvatar;
    }

    @Override
    public void setAvatar(Artist artist, byte[] avatar) {
        avatars.put(artist.getId(), avatar);        
    }

    @Override
    public void createArtist(Artist newArtist, String newBio, byte[] newAvatar) throws BusinessException {

        newArtist.setId(idArtist);

        artists.add(newArtist);
        setAvatar(newArtist, newAvatar);
        setBiography(newArtist, newBio);
        idArtist++;
    }

    @Override
    public void editArtist(Artist artist, String bio, byte[] avatar) throws BusinessException {

        for (Artist oldArtist : artists) {
            if(oldArtist.equals(artist)){
                artists.remove(oldArtist);
                artists.add(artist);
                setAvatar(artist, avatar);
                setBiography(artist, bio);
                return;
            }
        }
        
    }

    @Override
    public void deleteArtist(Artist artist) throws BusinessException {

        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        ArtistPicturesService artistsPictureService = factory.getArtistPicturesService();
        MusicService musicService = factory.getMusicService();
        AlbumService albumService = factory.getAlbumService();
        
        artists.remove(artist);
        avatars.remove(artist.getId());
        List<Song> songs = new ArrayList<Song>(musicService.findAllMusicByArtist(artist));
        for (Song song : songs) {
            musicService.removeArtistFromSong(song, artist);
        }
        List<Album> albums = new ArrayList<Album>(albumService.findAllAlbumByArtist(artist));
        for (Album album : albums) {
            albumService.deleteAlbum(album);
        }
        
        pictures.removeAll(artistsPictureService.findAllPicturesByArtist(artist));
        
    }


}
