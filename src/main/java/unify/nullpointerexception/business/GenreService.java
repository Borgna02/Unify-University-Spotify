package unify.nullpointerexception.business;
import java.util.List;

import unify.nullpointerexception.domain.Genre;

public interface GenreService {
    Genre findGenreById(Integer id) throws BusinessException;
    Genre findGenreByName(String genreName) throws BusinessException;
    List<Genre> getAllGenres() throws BusinessException;
    List<String> getAllGenresNames() throws BusinessException;
    List<Genre> findGenresByNamePrefix(String prefix) throws BusinessException;
    byte[] fetchCover(Genre genre) throws BusinessException;
    void setCover(Genre genre, byte[] cover) throws BusinessException;
    
}
