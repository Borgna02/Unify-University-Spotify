package unify.nullpointerexception.business;

import java.util.List;

import unify.nullpointerexception.domain.User;

public interface UserService {

	User findUserById(Integer id) throws BusinessException;

	List<User> findUsersByEmailPrefix(String emailPrefix) throws BusinessException;

	List<User> getAllUsers() throws BusinessException;

	User authenticate(String username, String password) throws BusinessException;

	void createUser(User user, byte[] avatar) throws BusinessException;

	void editUser(User user, byte[] avatar) throws BusinessException;

	void deleteUser(User user) throws BusinessException;

	byte[] fetchAvatar(User user) throws BusinessException;
	
	void setAvatar(User user, byte[] avatar) throws BusinessException;

}
