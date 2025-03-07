package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;

public class FileArtistPicturesService implements ArtistPicturesService {

    private String picturesFilePath;
    private final String PICTURES_PATH = "src/main/resources/img/artists_pictures/";
    private final String PICTURES_EXT = ".png";
    private int ID_INDEX = 0, CAPTION_INDEX = 1;

    private int newId = -1;

    public FileArtistPicturesService(String picturesFilePath) {

        this.picturesFilePath = picturesFilePath;

        try {
            // inizializzo newId all'id dell'ultima riga del file + 1
            FileData fileData = FileUtility.readAllRows(picturesFilePath);
            int rowsNumber = fileData.getRows().size();
            newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ArtistPicture> findAllPicturesByArtist(Artist artist) throws BusinessException {
        List<ArtistPicture> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(picturesFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; didascalia ; id_artista
                if (Integer.parseInt(columns[2]) == artist.getId()) {

                    String path = PICTURES_PATH + columns[ID_INDEX] + PICTURES_EXT;
                    File f = new File(path);
                    if (f.exists())
                        result.add(new ArtistPicture(Integer.parseInt(columns[ID_INDEX]),
                                Files.readAllBytes(f.toPath()), artist, columns[CAPTION_INDEX]));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

        return result;

    }

    private String pictureToRow(ArtistPicture picture) {

        String row = picture.getId() + FileUtility.COLUMN_SEPARATOR + picture.getCaption()
                + FileUtility.COLUMN_SEPARATOR
                + picture.getArtist().getId() + '\n';

        return row;
    }

    @Override
    public void createArtistPicture(Artist artist, ArtistPicture newPicture) throws BusinessException {

        newPicture.setId(++newId);
        newPicture.setArtist(artist);
        FileUtility.appendRow(picturesFilePath, pictureToRow(newPicture)); // aggiungo i dati della nuova foto al file

        File img = new File(PICTURES_PATH + newPicture.getId() + PICTURES_EXT);

        try {
            // sovrascrivo/creo il file immagine
            Files.write(img.toPath(), newPicture.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }

    }

    @Override
    public void setArtistPicture(Artist artist, ArtistPicture picture) throws BusinessException {

        // sovrascrivo la vecchia riga
        FileUtility.editRowById(picturesFilePath, picture.getId(), pictureToRow(picture));

        File img = new File(PICTURES_PATH + picture.getId() + PICTURES_EXT);

        try {
            // sovrascrivo/creo il file immagine
            Files.write(img.toPath(), picture.getData(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

    @Override
    public void deleteArtistPicture(ArtistPicture artistPicture) throws BusinessException {

        // elimino la stringa dal file delle immagini e il file dell'immagine stessa
        FileUtility.deleteRowById(picturesFilePath, artistPicture.getId());
        FileUtility.deleteFile(PICTURES_PATH + artistPicture.getId() + PICTURES_EXT);
    }

}
