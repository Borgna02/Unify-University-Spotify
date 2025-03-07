package unify.nullpointerexception.business.implementation.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.domain.Genre;

public class RAMGenreService implements GenreService {

    
    private List<Genre> genres = new ArrayList<>();
    private byte[] defaultCover = {};
    private Map<Integer, byte[]> covers = new HashMap<>();
    private int id;
    public RAMGenreService(){

        //defaultCover.setData(data);

        Genre elettronica = new Genre();
        elettronica.setId(id);
        elettronica.setName("Elettronica");
        byte[] coverElettronica = {/*dati*/};
        //coverElettronica.setData(data);
        
        genres.add(elettronica);
        covers.put(id, coverElettronica);
        id++;

        Genre pop = new Genre();
        pop.setId(id);
        pop.setName("Pop");
        byte[] coverPop = {/*dati*/};
        //coverPop.setData(data);

        genres.add(pop);
        covers.put(id, coverPop);
        id++;

        Genre liscio = new Genre();
        liscio.setId(id);
        liscio.setName("Liscio");
        byte[] coverLiscio = {/*dati*/};
        //coverLiscio.setData(data);

        genres.add(liscio);
        covers.put(id, coverLiscio);
        id++;
    }

    @Override
    public Genre findGenreById(Integer id) throws BusinessException {
        
        for (Genre genre : genres) {
            if(genre.getId() == id) return genre;
        }
        return new Genre();
    }

    @Override
    public List<Genre> getAllGenres() throws BusinessException {

        return genres;
    }

    @Override
    public List<String> getAllGenresNames() throws BusinessException {
        List<String> genresNames = new ArrayList<String>();

        for (Genre genre : genres) {
            genresNames.add(genre.getName());
        }

        return genresNames;
    }

    @Override
    public Genre findGenreByName(String genreName) {
        
        for (Genre genre : genres) {
            if((genre.getName()).equals(genreName)) return genre;
        }
        
        return new Genre();
    }

    @Override
    public List<Genre> findGenresByNamePrefix(String prefix) {

        prefix = prefix.toLowerCase();
        List<Genre> result = new ArrayList<>();
        for (Genre genre : genres) {
            if(genre.getName().toLowerCase().contains(prefix)) result.add(genre);
        }
        return result;
    }

    @Override
    public byte[] fetchCover(Genre genre) throws BusinessException {
        
        if(covers.containsKey(genre.getId()))
            return covers.get(genre.getId());

        return defaultCover;
    }

    @Override
    public void setCover(Genre genre, byte[] cover) throws BusinessException {
        covers.put(genre.getId(), cover);
    }
    
}
