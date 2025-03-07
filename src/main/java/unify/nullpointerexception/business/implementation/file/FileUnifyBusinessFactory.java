package unify.nullpointerexception.business.implementation.file;

import java.io.File;

import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;

public class FileUnifyBusinessFactory extends UnifyBusinessFactory {

	private UserService userService;
	private MusicService musicService;
	private ArtistsService artistsService;
	private AlbumService albumService;
	private PlaylistService playlistService;
	private GenreService genreService;
	private ArtistPicturesService artistPicturesService;

	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources"
			+ File.separator + "files";
	private static final String UTENTI_FILE_NAME = REPOSITORY_BASE + File.separator + "elencoUtenti.txt";
	private static final String CANZONI_FILE_NAME = REPOSITORY_BASE + File.separator + "canzoni.txt";
	private static final String ALBUM_FILE_NAME = REPOSITORY_BASE + File.separator + "album.txt";
	private static final String ARTISTS_FILE_NAME = REPOSITORY_BASE + File.separator + "artists.txt";
	private static final String PLAYLIST_FILE_NAME = REPOSITORY_BASE + File.separator + "playlist.txt";
	private static final String PICTURES_FILE_NAME = REPOSITORY_BASE + File.separator + "artistsPictures.txt";
	private static final String GENRES_FILE_NAME = REPOSITORY_BASE + File.separator + "genres.txt";
	private static final String SONGS_FILE_NAME = REPOSITORY_BASE + File.separator + "canzoni.txt";

	public FileUnifyBusinessFactory() {

		genreService = new FileGenreService(GENRES_FILE_NAME);
		artistPicturesService = new FileArtistPicturesService(PICTURES_FILE_NAME);
		artistsService = new FileArtistsService(ARTISTS_FILE_NAME, SONGS_FILE_NAME, PICTURES_FILE_NAME, artistPicturesService, musicService);
		musicService = new FileMusicService(CANZONI_FILE_NAME, ALBUM_FILE_NAME, PLAYLIST_FILE_NAME, genreService, artistsService);
		albumService = new FileAlbumService(ALBUM_FILE_NAME, musicService, genreService, artistsService);
		playlistService = new FilePlaylistService(PLAYLIST_FILE_NAME, userService, musicService);
		userService = new FileUserService(UTENTI_FILE_NAME, playlistService);
	
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