package unify.nullpointerexception.business.implementation.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;
import unify.nullpointerexception.domain.Song;

public class FileArtistsService implements ArtistsService {

    private String artistsFilePath, songsFilePath, picturesFilePath;
    private final String AVATAR_PATH = "src/main/resources/img/artists_avatars/";
    private final String AVATAR_EXT = ".jpeg";
    private final String PICTURES_EXT = ".jpeg";
    private final String BIOGRAPHIES_PATH = "src/main/resources/files/biographies/";
    private final String PICTURES_PATH = "src/main/resources/files/pictures/";
    private int ID_INDEX = 0, STAGE_NAME_INDEX = 1, ARTIST_ID_INDEX = 4;

    private byte[] defaultAvatar;

    private ArtistPicturesService artistsPictureService;

    private int newId = -1;
    private MusicService musicService;

    public FileArtistsService(String artistsFilePath, String songsFilePath, String picturesFilePath,
            ArtistPicturesService artistsPictureService, MusicService musicService) {
        this.artistsFilePath = artistsFilePath;
        this.songsFilePath = songsFilePath;
        this.picturesFilePath = picturesFilePath;
        this.artistsPictureService = artistsPictureService;
        this.musicService = musicService;

        File f = new File("src/main/resources/img/default_user.png");
        if (f.exists()) {

            try {
                defaultAvatar = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // inizializzo newId all'id dell'ultima riga del file + 1
            FileData fileData = FileUtility.readAllRows(artistsFilePath);
            int rowsNumber = fileData.getRows().size();
            newId = Integer.parseInt(fileData.getRows().get(rowsNumber - 1)[ID_INDEX]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Artist findArtistById(Integer id) throws BusinessException {

        try {
            FileData fileData = FileUtility.readAllRows(artistsFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome_arte
                if (Integer.parseInt(columns[ID_INDEX]) == id)
                    return new Artist(id, columns[STAGE_NAME_INDEX]);

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
        return null;

    }

    @Override
    public List<Artist> findArtistByNamePrefix(String namePrefix) throws BusinessException {

        List<Artist> result = new ArrayList<>();

        try {
            FileData fileData = FileUtility.readAllRows(artistsFilePath);
            for (String[] columns : fileData.getRows()) {

                // id ; nome_arte
                if (columns[STAGE_NAME_INDEX].toLowerCase().contains(namePrefix.toLowerCase()))
                    result.add(new Artist(Integer.parseInt(columns[ID_INDEX]), columns[STAGE_NAME_INDEX]));

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
        return result;

    }

    @Override
    public List<Artist> getAllArtists() throws BusinessException {
        List<Artist> result = new ArrayList<>();
        try {
            FileData fileData = FileUtility.readAllRows(artistsFilePath);
            for (String[] columns : fileData.getRows())
                result.add(new Artist(Integer.parseInt(columns[ID_INDEX]), columns[STAGE_NAME_INDEX]));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void createArtist(Artist newArtist, String newBio, byte[] newAvatar) throws BusinessException {

        newArtist.setId(++newId);

        if (newAvatar != null)
            setAvatar(newArtist, newAvatar);
        if (newBio != null)
            setBiography(newArtist, newBio);

        String newRow = newId + ";" + newArtist.getStageName() + "\n";
        FileUtility.appendRow(artistsFilePath, newRow);

    }

    @Override
    public void editArtist(Artist artist, String bio, byte[] avatar) throws BusinessException {

        if (avatar != null)
            setAvatar(artist, avatar);
        if (bio != null)
            setBiography(artist, bio);

        // stringa da salvare sul file
        String newRow = artist.getId() + ";" + artist.getStageName() + "\n";
        FileUtility.editRowById(artistsFilePath, artist.getId(), newRow);

    }

    @Override
    public void deleteArtist(Artist artist) throws BusinessException {

        // eliminazione della riga dell'artista sul file
        FileUtility.deleteRowById(artistsFilePath, artist.getId());

        // eliminazione dell'avatar dell'artista
        FileUtility.deleteFile(AVATAR_PATH + artist.getId() + AVATAR_EXT);

        // cancellazione idArtista nel file canzoni

        FileUtility.deleteFromIndexColumnIfEqual(songsFilePath, ARTIST_ID_INDEX, artist.getId().toString());

        try {
            FileData fileData = FileUtility.readAllRows(songsFilePath);
            for (String[] columns : fileData.getRows()) {

                // se la canzone non ha più artisti, elimino la canzone
                if (columns.length <= 4) {
                    musicService.deleteSong(new Song(Integer.parseInt(columns[0]), null, null, null));
                }

            }

        } catch (IOException e) {

            e.printStackTrace();
            throw new BusinessException(e);
        }

        // eliminazione delle foto ufficiali dell'artista
        for (ArtistPicture picture : artistsPictureService.findAllPicturesByArtist(artist)) {

            FileUtility.deleteRowById(picturesFilePath, picture.getId());
            FileUtility.deleteFile(PICTURES_PATH + picture.getId() + PICTURES_EXT);

        }

        // eliminazione biografia artista
        FileUtility.deleteFile(BIOGRAPHIES_PATH + artist.getId() + ".txt");

    }

    @Override
    public byte[] fetchAvatar(Artist artist) throws BusinessException {
        byte[] avatar = defaultAvatar;
        String avatarPath = AVATAR_PATH + artist.getId() + AVATAR_EXT;
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
    public String getBiography(Artist artist) {
        File file = new File(BIOGRAPHIES_PATH + artist.getId() + ".txt");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                fileInputStream.close();
                return new String(data, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    public void setBiography(Artist artist, String newBio) {
        File bioFile = new File(BIOGRAPHIES_PATH + artist.getId() + ".txt");
        try {
            Files.write(bioFile.toPath(), newBio.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAvatar(Artist artist, byte[] avatar) throws BusinessException {
        File f = new File(AVATAR_PATH + artist.getId() + AVATAR_EXT);
        try {
            Files.write(f.toPath(), avatar, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(e);
        }
    }

}
