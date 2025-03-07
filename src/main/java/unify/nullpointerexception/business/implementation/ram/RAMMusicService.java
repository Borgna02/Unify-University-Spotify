package unify.nullpointerexception.business.implementation.ram;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.Song;

public class RAMMusicService implements MusicService {

	private static List<Song> songs = new ArrayList<>();
	private static Map<Integer, byte[]> covers = new HashMap<>();
	private static byte[] defaultCover = {};
	private static Map<Integer, String> lyrics = new HashMap<>();
	private static Map<Integer, byte[]> tracks = new HashMap<>();
	private static int id;

	public RAMMusicService(ArtistsService artistsService, GenreService genreService){}


	@Override
	public List<Song> findAllMusicByArtist(Artist artist ) throws BusinessException {

		List<Song> result = new ArrayList<>();

		for (Song song : songs) {
			for (Artist a : song.getArtists()) {
				if (artist.equals(a)) result.add(song);
			}
			
		}

		return result;
	}

	@Override
	public List<Song> findAllMusicByGenreName( String genreName ) throws BusinessException {

		List<Song> result = new ArrayList<>();

		for (Song song : songs) {
			if(song.getGenre().getName().equals(genreName)) result.add(song);
		}

		return result;
	}

	@Override
	public Song findMusicById(Integer id) throws BusinessException {
		
		for (Song song : songs) {
			if(song.getId() == id) return song;
		}
		return new Song();
	}


	@Override
	public List<Song> findAllMusicByPrefix(String prefix) throws BusinessException {

		prefix = prefix.toLowerCase();
		List<Song> result = new ArrayList<>();

		for (Song song : songs) {
			if(song.getTitle().toLowerCase().contains(prefix)) result.add(song);
		}

		return result;
	}

	@Override
	public List<Song> findAllMusicByGenrePrefix(String genrePrefix) throws BusinessException {

		genrePrefix = genrePrefix.toLowerCase();
		List<Song> result = new ArrayList<>();

		for (Song song : songs) {
			if(song.getGenre().getName().toLowerCase().contains(genrePrefix)) result.add(song);
		}

		return result;
	}

	@Override
	public List<Song> getAllSongs() throws BusinessException {
		return songs;
	}




	@Override
	public byte[] fetchCover(Song song) throws BusinessException {
		
		if(covers.containsKey(song.getId())) 
			return covers.get(song.getId());

		return defaultCover;
	}

	@Override
    public void setCover(Song song, byte[] cover) throws BusinessException{
		covers.put(song.getId(), cover);
    }

	@Override
	public String fetchLyrics(Song song) throws BusinessException {

		if(lyrics.containsKey(song.getId()))
			return lyrics.get(song.getId());

		return new String();
	}

	@Override
    public void setLyrics (Song song, String text) throws BusinessException{

		lyrics.put(song.getId(), text);
    }

	@Override
	public InputStream fetchTrack(Song song) throws BusinessException {
		return new ByteArrayInputStream(tracks.get(song.getId()));
	}

	@Override
    public void setTrack (Song song, byte[] audio) throws BusinessException{

		tracks.put(song.getId(), audio);
    }

	@Override
	public void createSong(Song song, String text, byte[] newTrack, byte[] newSongCover) throws BusinessException {

		song.setId(id);
		songs.add(song);
		setLyrics(song, text);
		setTrack(song, newTrack);
		setCover(song, newSongCover);
		id++;
	}

	@Override
	public void editSong(Song song, String text, byte[] newTrack, byte[] newSongCover) throws BusinessException {

		for (Song oldSong : songs) {
            if(oldSong.equals(song)){
                songs.remove(oldSong);
                songs.add(song);
				if (text != null) setLyrics(song, text);
				if (newTrack != null) setTrack(song, newTrack);
				if (newSongCover != null) setCover(song, newSongCover);
				break;
            }
        }
		
	}



	@Override
	public void deleteSong(Song song) throws BusinessException {
		
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		PlaylistService playlistService = factory.getPlaylistService();

		for (Playlist playlist : playlistService.getAllPlaylists()) {
			if (playlist.getSongs().contains(song)){
				playlist.getSongs().remove(song);
			}
			
		}
		songs.remove(song);
		lyrics.remove(song.getId());
		covers.remove(song.getId());
		tracks.remove(song.getId());


	}
	

	@Override
    public void removeArtistFromSong (Song song, Artist artist) throws BusinessException{
        song.getArtists().remove(artist);
        if (song.getArtists().isEmpty()) deleteSong(song);
    }


	
}
