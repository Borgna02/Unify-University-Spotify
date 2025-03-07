package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.User;

public class FileUserService implements UserService {

	private PlaylistService playlistService;
	private byte[] defaultAvatar;
	private String usersFilePath;

	private static final String AVATARS_PATH = "src/main/resources/img/avatars/";
	private static final String AVATARS_EXT = ".png";
	private static final String DEFAULT_USER_PATH = AVATARS_PATH + "default_user" + AVATARS_EXT;
	private final int ID_INDEX = 0, NAME_INDEX = 1, EMAIL_INDEX = 2, PASSWORD_INDEX = 3, IS_ADMIN_INDEX = 4;

	private int newId = -1;

	public FileUserService(String usersFilePath, PlaylistService playlistService) {

		this.playlistService = playlistService;
		this.usersFilePath = usersFilePath;

		File f = new File(DEFAULT_USER_PATH);
		if (f.exists()) {

			try {
				defaultAvatar = Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {

			// inizializzo newId all'id dell'ultima riga del file + 1
			FileData fileData = FileUtility.readAllRows(usersFilePath);
			int rowsNumber = fileData.getRows().size();
			newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String userToRow(User user) {
		return user.getId() + FileUtility.COLUMN_SEPARATOR + user.getUsername() + FileUtility.COLUMN_SEPARATOR
				+ user.getEmail() + FileUtility.COLUMN_SEPARATOR + user.getPassword() + FileUtility.COLUMN_SEPARATOR
				+ user.isAdmin() + '\n';
	}

	@Override
	public void createUser(User user, byte[] avatar) throws BusinessException {

		if (FileUtility.isDataAlreadyInARow(user.getEmail(), EMAIL_INDEX, usersFilePath))
			throw new BusinessException("email già utilizzata");

		if (user.getPassword().indexOf(FileUtility.COLUMN_SEPARATOR) > 0)
			throw new BusinessException("la password non può contenere \'" + FileUtility.COLUMN_SEPARATOR + "\'");

		user.setId(++newId);
		FileUtility.appendRow(usersFilePath, userToRow(user));

		if (avatar != null)
			setAvatar(user, avatar);

	}

	@Override
	public void editUser(User newUser, byte[] newAvatar) throws BusinessException {

		if (newAvatar != null)
			setAvatar(newUser, newAvatar);

		FileUtility.editRowById(usersFilePath, newUser.getId(), userToRow(newUser));

	}

	@Override
	public User authenticate(String email, String password) throws BusinessException {

		try {

			FileData fileData = FileUtility.readAllRows(usersFilePath);

			for (String[] columns : fileData.getRows()) {
				if (columns[EMAIL_INDEX].equals(email) && columns[PASSWORD_INDEX].equals(password))
					return new User(Integer.parseInt(columns[ID_INDEX]), columns[NAME_INDEX], email, password,
							Boolean.parseBoolean(columns[IS_ADMIN_INDEX]));

			}

			throw new BusinessException("Utente inesistente");

		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}

	@Override
	public User findUserById(Integer id) throws BusinessException {

		try {

			FileData fileData = FileUtility.readAllRows(usersFilePath);
			for (String[] columns : fileData.getRows()) {
				if (columns[ID_INDEX].equals(Integer.toString(id)))
					return new User(id, columns[NAME_INDEX], columns[EMAIL_INDEX], columns[PASSWORD_INDEX],
							Boolean.parseBoolean(columns[IS_ADMIN_INDEX]));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
		return null;

	}

	@Override
	public List<User> findUsersByEmailPrefix(String emailPrefix) throws BusinessException {

		List<User> result = new ArrayList<>();

		try {
			FileData fileData = FileUtility.readAllRows(usersFilePath);
			for (String[] columns : fileData.getRows()) {
				if (columns[EMAIL_INDEX].toLowerCase().contains(emailPrefix.toLowerCase()))
					result.add(new User(Integer.parseInt(columns[ID_INDEX]), columns[NAME_INDEX], columns[EMAIL_INDEX],
							columns[PASSWORD_INDEX], Boolean.parseBoolean(columns[IS_ADMIN_INDEX])));
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
		return result;
	}

	@Override
	public List<User> getAllUsers() throws BusinessException {

		List<User> result = new ArrayList<>();

		try {
			FileData fileData = FileUtility.readAllRows(usersFilePath);
			for (String[] columns : fileData.getRows())
				result.add(new User(Integer.parseInt(columns[ID_INDEX]), columns[NAME_INDEX], columns[EMAIL_INDEX],
						columns[PASSWORD_INDEX], Boolean.parseBoolean(columns[IS_ADMIN_INDEX])));

		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
		return result;
	}

	@Override
	public void deleteUser(User user) throws BusinessException {

		// elimina le playlist dell'utente
		for (Playlist playlist : playlistService.findAllPlaylistsByUser(user))
			playlistService.deletePlaylist(playlist);

		// elimina l'immagine dell'avatar
		FileUtility.deleteFile(AVATARS_PATH + user.getId() + AVATARS_EXT);

		// rimuove la riga dell'utente dal file
		FileUtility.deleteRowById(usersFilePath, user.getId());

	}

	@Override
	public byte[] fetchAvatar(User user) throws BusinessException {
		byte[] avatar = defaultAvatar;
		String avatarPath = AVATARS_PATH + user.getId() + AVATARS_EXT;
		File f = new File(avatarPath);
		if (f.exists()) {

			try {
				avatar = Files.readAllBytes(f.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new BusinessException(e);
			}
		}
		return avatar;
	}

	@Override
	public void setAvatar(User user, byte[] avatar) throws BusinessException {

		File f = new File(AVATARS_PATH + user.getId() + AVATARS_EXT);

		try {
			Files.write(f.toPath(), avatar, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}

	}

}
