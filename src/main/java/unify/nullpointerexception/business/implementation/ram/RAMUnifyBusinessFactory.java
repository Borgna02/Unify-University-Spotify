package unify.nullpointerexception.business.implementation.ram;


import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;



public class RAMUnifyBusinessFactory extends UnifyBusinessFactory {

    private UserService userService;
	private MusicService musicService;
	private ArtistsService artistsService;
	private AlbumService albumService;
	private PlaylistService playlistService;
    private GenreService genreService;
    private ArtistPicturesService artistPicturesService;

    public RAMUnifyBusinessFactory(){

        
        artistsService = new RAMArtistsService();
        genreService = new RAMGenreService();
        musicService = new RAMMusicService(artistsService, genreService);
        albumService = new RAMAlbumService(artistsService, musicService, genreService);
        playlistService = new RAMPlaylistService();
        userService = new RAMUserService(playlistService);
        artistPicturesService = new RAMArtistPicturesService();

    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public MusicService getMusicService() {
        return musicService;
    }

    @Override
    public ArtistsService getArtistsService() {
        return artistsService;
    }

    @Override
    public AlbumService getAlbumService() {
        return albumService;
    }

    @Override
    public PlaylistService getPlaylistService() {
        
        return playlistService;
    }

    @Override
    public GenreService getGenreService() {
        return genreService;
    }

    @Override
    public ArtistPicturesService getArtistPicturesService() {
        return artistPicturesService;
    }

    
} 
