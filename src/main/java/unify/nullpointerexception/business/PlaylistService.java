package unify.nullpointerexception.business;

import java.util.List;

import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.User;

public interface PlaylistService {

	List<Playlist> findAllPlaylistsByUser(User user) throws BusinessException;

	Playlist findPlaylistById(int id) throws BusinessException;

	void createPlaylist(Playlist playlist) throws BusinessException;


	void editPlaylist(Playlist playlist) throws BusinessException;

	void deletePlaylist(Playlist playlist) throws BusinessException;

	List<Playlist> getAllPlaylists() throws BusinessException;

}
