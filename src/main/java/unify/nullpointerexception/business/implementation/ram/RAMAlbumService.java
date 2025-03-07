package unify.nullpointerexception.business.implementation.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;

public class RAMAlbumService implements AlbumService {

    private static List<Album> albumList = new ArrayList<>();
    private static byte[] defaultCover = {};
    private static Map<Integer,byte[]> covers = new HashMap<>();
    private static int id;

    public RAMAlbumService(ArtistsService artistsService, MusicService musicService, GenreService genreService){}
    
    @Override
    public Album findAlbumById(Integer id) throws BusinessException {
        
        for (Album album : albumList) {
            if(album.getId() == id) return album;
        }

        return new Album();
    }

    @Override
    public List<Album> findAllAlbumByArtist(Artist artist) throws BusinessException {
        
        List<Album> result = new ArrayList<>();

        for (Album album : albumList) {
            if(album.getArtist().equals(artist)) result.add(album);
        }
        return result;
    }

    @Override
    public List<Album> findAllAlbumByPrefix(String prefix) throws BusinessException {

        prefix = prefix.toLowerCase();
        List<Album> result = new ArrayList<>();

        for (Album album : albumList) {
            if(album.getName().toLowerCase().contains(prefix)) result.add(album);
        }
        return result;
    }

    @Override
    public List<Album> findAllAlbumByGenrePrefix(String prefix) throws BusinessException {

        prefix = prefix.toLowerCase();
        List<Album> result = new ArrayList<>();

        for (Album album : albumList) {
            if(album.getGenre().getName().toLowerCase().contains(prefix)) result.add(album);
        }
        return result;
    }

	@Override
	public byte[] fetchCover(Album album) throws BusinessException {

		if(covers.containsKey(album.getId()))
            return covers.get(album.getId());

        return defaultCover;
	}
    @Override
    public void setCover(Album album, byte[] cover) throws BusinessException {

        covers.put(album.getId(), cover);
        
    }
    @Override
    public void createAlbum(Album newAlbum, byte[] newCover) throws BusinessException {
        newAlbum.setId(id);

        albumList.add(newAlbum);
        covers.put(id, newCover);

        id++;
    }
    @Override
    public void editAlbum(Album album, byte[] cover) throws BusinessException {
        
        for (Album oldAlbum : albumList) {

            if(oldAlbum.equals(album)){

                albumList.remove(oldAlbum);
                albumList.add(album);
                setCover(album, cover);
                return;
            }
        }
        
    }
    @Override
    public void deleteAlbum(Album album) throws BusinessException {

        albumList.remove(album);
        covers.remove(album.getId());
        
    }

    @Override
    public List<Album> findAllAlbumBySong(Song song) throws BusinessException {

        List<Album> res = new ArrayList<>();

        for (Album album : albumList) {
            if(album.getSongs().contains(song))
                res.add(album);
        }
        
        return res;
    }
    
}
