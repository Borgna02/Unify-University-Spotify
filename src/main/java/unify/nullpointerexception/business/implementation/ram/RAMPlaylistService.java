package unify.nullpointerexception.business.implementation.ram;

import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.User;

import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.PlaylistService;

public class RAMPlaylistService implements PlaylistService {

    private int id;
    private List<Playlist> playlists = new ArrayList<>(); 

    @Override
    public List<Playlist> findAllPlaylistsByUser(User user) throws BusinessException {
        
        List<Playlist> result = new ArrayList<>();

        for (Playlist playlist : playlists) {

            if(playlist.getCreator().equals(user)) result.add(playlist);
        }

        return result;
    }

    @Override
    public Playlist findPlaylistById(int id) throws BusinessException {

        for (Playlist playlist : playlists) {

            if(playlist.getId().equals(id)) return playlist;
        }

        return new Playlist();
    }

    @Override
    public void createPlaylist(Playlist playlist) throws BusinessException {
        
        playlist.setId(id++);
        playlists.add(playlist);
        
    }

    @Override
    public void editPlaylist(Playlist playlist) throws BusinessException {

        for (Playlist oldPlaylist : playlists) {

            if(oldPlaylist.equals(playlist)){

                playlists.remove(oldPlaylist);
                playlists.add(playlist);
                return;
            }
        }
        
    }

    @Override
    public void deletePlaylist(Playlist playlist) throws BusinessException {
        
        playlists.remove(playlist);
        
    }

    @Override
    public List<Playlist> getAllPlaylists(){
        return playlists;
    }
    
}
