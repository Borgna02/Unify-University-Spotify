package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.domain.Genre;

public class FileGenreService implements GenreService {

    private final String COVER_PATH = "src/main/resources/img/genresCovers/";
    private final String COVER_EXT = ".png";
    private int ID_INDEX = 0, NAME_INDEX = 1;
    private byte[] defaultCover;
    private String genreFilePath;

    public FileGenreService(String genreFilePath) {
        this.genreFilePath = genreFilePath;

        File f = new File(COVER_PATH + "default_cover" + COVER_EXT);
        if (f.exists()) {

            try {
                defaultCover = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Genre findGenreById(Integer id) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(genreFilePath);
            for (String[] colonne : fileData.getRows()) {

                // id ; nome_arte
                if (Integer.parseInt(colonne[ID_INDEX]) == id) {
                    return new Genre(id, colonne[NAME_INDEX]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return null;
    }

    @Override
    public Genre findGenreByName(String genreName) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(genreFilePath);
            for (String[] colonne : fileData.getRows()) {

                // id ; nome_arte
                if (colonne[NAME_INDEX].equals(genreName))
                    return new Genre(Integer.parseInt(colonne[ID_INDEX]), genreName);

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return null;
    }

    @Override
    public List<Genre> getAllGenres() throws BusinessException {
        List<Genre> genres = new ArrayList<Genre>();

        try {
            FileData fileData = FileUtility.readAllRows(genreFilePath);
            for (String[] columns : fileData.getRows()) {
                // id ; nomeGenere
                genres.add(new Genre(Integer.parseInt(columns[ID_INDEX]), columns[NAME_INDEX]));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return genres;
    }

    @Override
    public List<String> getAllGenresNames() throws BusinessException {
        List<String> genresNames = new ArrayList<String>();

        try {
            FileData fileData = FileUtility.readAllRows(genreFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nomeGenere
                genresNames.add(columns[NAME_INDEX]);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return genresNames;
    }

    @Override
    public List<Genre> findGenresByNamePrefix(String prefix) throws BusinessException {

        List<Genre> result = new ArrayList<Genre>();

        try {
            FileData fileData = FileUtility.readAllRows(genreFilePath);
            for (String[] colonne : fileData.getRows()) {
                // id ; nome_arte
                if (colonne[NAME_INDEX].toLowerCase().contains(prefix.toLowerCase()))
                    result.add(new Genre(Integer.parseInt(colonne[ID_INDEX]), colonne[NAME_INDEX]));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
        return result;
    }

    @Override
    public byte[] fetchCover(Genre genre) throws BusinessException {

        byte[] cover = defaultCover;
        String coverPath = COVER_PATH + genre.getId() + COVER_EXT;
        File f = new File(coverPath);
        if (f.exists()) {

            try {
                cover = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException(e);
            }
        }
        return cover;
    }

    @Override
    public void setCover(Genre genre, byte[] cover) throws BusinessException {

        File f = new File(COVER_PATH + genre.getId() + COVER_EXT);

        try {
            Files.write(f.toPath(), cover, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

}
