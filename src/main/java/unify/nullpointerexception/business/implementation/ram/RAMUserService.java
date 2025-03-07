package unify.nullpointerexception.business.implementation.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.User;

public class RAMUserService implements UserService {

	private PlaylistService playlistService;

	private List<User> users = new ArrayList<>();
	private Map<Integer, byte[]> avatars = new HashMap<>();
	private static int id;

	public RAMUserService(PlaylistService playlistService){

		this.playlistService = playlistService;


		User admin = new User();
		admin.setId(id++);
		admin.setEmail("");
		admin.setUsername("admin");
		admin.setPassword("");
		admin.setAdmin(true);
		avatars.put(admin.getId(), new byte[1]);
		
		users.add(admin);
		
	}

	@Override
	public User authenticate(String email, String password) throws BusinessException {
		
		for (User user : users) {
			if(user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password))
				return user;
		}
		return new User();

	}

	@Override
	public void createUser(User utente, byte[] avatar) throws BusinessException{

		for (User user : users) {
			if(user.getEmail().equalsIgnoreCase(utente.getEmail()))
				throw new BusinessException("email già utilizzata");
		}
		
		utente.setId(id++);
		users.add(utente);

		if(avatar != null) 
			avatars.put(utente.getId(), avatar);

	}

	@Override
	public User findUserById(Integer id) throws BusinessException {
		
		return users.get(id);
	}


	@Override
	public void editUser(User user, byte[] avatar) throws BusinessException {
		
		for (User oldUser : users){
            if (oldUser.equals(user)){
				users.remove(oldUser);
				users.add(user);
				setAvatar(user, avatar);
				return;
            }
		                
        }
		
	}

	@Override
	public void deleteUser(User user) throws BusinessException {
		
		for (Playlist playlist : playlistService.findAllPlaylistsByUser(user)) {
			playlistService.deletePlaylist(playlist);
		}
		
		users.remove(user);

		
	}

	@Override
	public List<User> findUsersByEmailPrefix(String emailPrefix) throws BusinessException {
		emailPrefix = emailPrefix.toLowerCase();

		List<User> result = new ArrayList<>();

		for (User user : users) {

			if(user.getEmail().contains(emailPrefix)) result.add(user);
		}

		return result;
	}

	@Override
	public List<User> getAllUsers() throws BusinessException {
		return users;
	}

	@Override
	public byte[] fetchAvatar(User user) throws BusinessException {
		return avatars.get(user.getId());
	}

	@Override
	public void setAvatar(User user, byte[] avatar) throws BusinessException {
		
		avatars.put(user.getId(), avatar);
		
	}
}
